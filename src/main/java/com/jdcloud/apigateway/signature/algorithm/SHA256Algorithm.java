package com.jdcloud.apigateway.signature.algorithm;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jdcloud.apigateway.signature.SignRequest;
import com.jdcloud.sdk.auth.Credentials;
import com.jdcloud.sdk.auth.SessionCredentials;
import com.jdcloud.sdk.auth.sign.AbstractSigner;
import com.jdcloud.sdk.auth.sign.SigningAlgorithm;
import com.jdcloud.sdk.constant.ParameterConstant;
import com.jdcloud.sdk.http.SdkHttpFullRequest;
import com.jdcloud.sdk.http.SdkHttpFullRequest.Builder;
import com.jdcloud.sdk.utils.BinaryUtils;
import com.jdcloud.sdk.utils.StringUtils;

public class SHA256Algorithm extends AbstractSigner implements ISignAlgorithm {

    private static Logger LOGGER = LoggerFactory.getLogger(SHA256Algorithm.class);
    
    @Override
    public String doSign(SignRequest signRequest) {
        handleHeaders(signRequest);
        String signString = buildSignString(signRequest);
        LOGGER.info("---[ STRING TO SIGN ]---");
        LOGGER.info(signString);
        final byte[] signingKey = buildSigningKey(signRequest);
        final byte[] signature = sign(signString.getBytes(Charset.forName("UTF-8")), signingKey,
                SigningAlgorithm.HmacSHA256);
        return BinaryUtils.toHex(signature);
    }

    public String buildSignString(SignRequest signRequest) {
        return new StringBuilder(signRequest.getAlgorithm()).append(ParameterConstant.LINE_SEPARATOR)
                .append(signRequest.getReqTime()).append(ParameterConstant.LINE_SEPARATOR)
                .append(signRequest.getCredentialScope()).append(ParameterConstant.LINE_SEPARATOR)
                .append(buildHashedCanonicalRequest(signRequest)).toString();
    }

    /**
     * 方法描述：计算参与签名的秘钥验证
     * 
     * @param credentials
     * @param dateStamp
     * @param regionName
     * @param serviceName
     * @return
     * @author lixuenan3
     * @date 2018年3月22日 下午5:57:52
     */
    private byte[] buildSigningKey(SignRequest signRequest) {
        byte[] kSecret = ("JDCLOUD2" + signRequest.getSecretKey()).getBytes(Charset.forName("UTF-8"));
        byte[] kDate = sign(signRequest.getDate(), kSecret, SigningAlgorithm.HmacSHA256);
        byte[] kRegion = sign(signRequest.getRegion(), kDate, SigningAlgorithm.HmacSHA256);
        byte[] kService = sign(signRequest.getService(), kRegion, SigningAlgorithm.HmacSHA256);
        return sign(ParameterConstant.JDCLOUD_TERMINATOR, kService, SigningAlgorithm.HmacSHA256);
    }

    private String buildHashedCanonicalRequest(SignRequest signRequest) {
        String queryString = getCanonicalizedQueryString(signRequest.getQueryParameters());
        
        String bodySha256;
        if(signRequest.getContentSha256() != null) {
            bodySha256 = signRequest.getContentSha256();
        } else {
            bodySha256 = BinaryUtils.toHex(hash(
                    new ByteArrayInputStream((signRequest.getBody() == null ? "" : signRequest.getBody()).getBytes())));
        }
        
        String canonicalRequest = new StringBuilder(signRequest.getHttpMethod())
                .append(ParameterConstant.LINE_SEPARATOR).append(getCanonicalizedResourcePath(signRequest.getPath(), false)) // TODO
                .append(ParameterConstant.LINE_SEPARATOR).append(queryString).append(ParameterConstant.LINE_SEPARATOR)
                .append(signRequest.getCanonicalHeaders()).append(ParameterConstant.LINE_SEPARATOR)
                .append(signRequest.getSignedHeadersString()).append(ParameterConstant.LINE_SEPARATOR)
                .append(bodySha256).toString();
        LOGGER.info("---[ CANONICAL STRING  ]---");
        LOGGER.info(canonicalRequest);
        return BinaryUtils.toHex(hash(canonicalRequest));
    }

    private void handleHeaders(SignRequest signRequest) {
        Map<String, String> headers = signRequest.getHeaders();
        String authorization = headers.get("authorization");// TODO
        if (StringUtils.isBlank(authorization)) {
            throw new RuntimeException("header:authorization不能为空");
        }
        signRequest.setAlgorithm(getAlgorithm(authorization));
        String signedHeadersString = getSignedHeadersString(authorization);
        signRequest.setSignedHeadersString(signedHeadersString);
        Map<String, String> signedHeaders = buildSignedHeaders(signRequest, signedHeadersString, headers);
        signRequest.setSignedHeaders(signedHeaders);
        signRequest.setCanonicalHeaders(buildCanonicalHeaders(signedHeaders));
        handleCredential(signRequest, authorization);
        signRequest.setSignature(getSignature(authorization));
        signRequest.setReqTime(getReqTime(headers));
        signRequest.setContentSha256(getContentSha256(headers));
    }

    private String buildCanonicalHeaders(Map<String, String> signedHeaders) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : signedHeaders.entrySet()) {
            builder.append(entry.getKey()).append(":").append(entry.getValue())
                    .append(ParameterConstant.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    private String getReqTime(Map<String, String> headers) {
        return headers.get("x-jdcloud-date");// TODO
    }
    
    /**
     * 方法描述：如果x-jdcloud-content-sha256参与签名，则取该header的value直接作为bodySha256的值
     * @param headers
     * @return 
     * @author lixuenan3
     * @date 2018年12月18日 下午8:01:10
     */
    private String getContentSha256(Map<String, String> headers) {
        return headers.get("x-jdcloud-content-sha256");// TODO
    }

    /**
     * 方法描述：获取签名
     * 
     * @param authorization
     * @return
     * @author lixuenan3
     * @date 2018年11月9日 下午6:21:37
     */
    private String getSignature(String authorization) {
        String signature = authorization.substring(authorization.indexOf("Signature=")).replace("Signature=", "");
        return signature;
    }

    /**
     * 方法描述：获取签名算法
     * 
     * @param authorization
     * @return
     * @author lixuenan3
     * @date 2018年11月9日 下午6:21:44
     */
    private static String getAlgorithm(String authorization) {
        return authorization.substring(0, authorization.indexOf("Credential=") - 1);
    }

    /**
     * 方法描述：处理证书信息
     * 
     * @param signRequest
     * @param authorization
     * @author lixuenan3
     * @date 2018年11月9日 下午6:21:51
     */
    private void handleCredential(SignRequest signRequest, String authorization) {
        String credential = authorization
                .substring(authorization.indexOf("Credential="), authorization.indexOf("SignedHeaders=") - 2)
                .replace("Credential=", "");
        // Credential=636B856DCF14D467D313CCB0C0E2B21C/20181107/cn-north-1/xapw9ul97y4p/jdcloud2_request
        String[] credentialArray = credential.split("/");
        if (credentialArray == null || credentialArray.length == 0) {
            throw new RuntimeException();// TODO
        }
        signRequest.setAssessKey(credentialArray[0]);
        signRequest.setDate(credentialArray[1]);
        signRequest.setRegion(credentialArray[2]);
        signRequest.setService(credentialArray[3]);
        signRequest.setCredentialScope(getCredentialScope(credential));
    }

    /**
     * 方法描述：从证书中获取证书范围
     * 
     * @param credential
     * @return
     * @author lixuenan3
     * @date 2018年11月9日 下午6:22:23
     */
    private String getCredentialScope(String credential) {
        return credential.substring(credential.indexOf("/") + 1);
    }

    /**
     * 方法描述：获取参与签名的header的key
     * 
     * @param authorization
     * @return
     * @author lixuenan3
     * @date 2018年11月9日 下午6:22:42
     */
    private String getSignedHeadersString(String authorization) {
        return authorization.substring(authorization.indexOf("SignedHeaders="), authorization.indexOf("Signature=") - 2)
                .replace("SignedHeaders=", "");
    }

    /**
     * 方法描述：构造参与签名的header的key value
     * 
     * @param signedHeadersString
     * @param headers
     * @return
     * @author lixuenan3
     * @date 2018年11月9日 下午6:26:44
     */
    private Map<String, String> buildSignedHeaders(SignRequest signRequest, String signedHeadersString, Map<String, String> headers) {
        Map<String, String> signedHeaders = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(signedHeadersString)) {
            String[] signedHeaderKeys = signedHeadersString.split(";");
            if (signedHeaderKeys == null || signedHeaderKeys.length == 0) {
                throw new RuntimeException();// TODO
            }
            for (String signedHeaderKey : signedHeaderKeys) {
                signedHeaders.put(signedHeaderKey, headers.get(signedHeaderKey));
            }
        }
        return signedHeaders;
    }

    @Override
    public SdkHttpFullRequest sign(Builder builder, Credentials credentials) {
        return null;
    }

    @Override
    protected void addSessionCredentials(Builder mutableRequest, SessionCredentials credentials) {

    }

    public static void main(String[] args) {
        String authorization = "JDCLOUD2-HMAC-SHA256 Credential=636B856DCF14D467D313CCB0C0E2B21C/20181107/cn-north-1/xapw9ul97y4p/jdcloud2_request, SignedHeaders=content-type;x-jdcloud-date;x-jdcloud-nonce, Signature=d30baab05fa480cefb75cedc87b32d59664ea56a700b7f0f4a1e541037d15484";
        String algorithm = getAlgorithm(authorization);
        System.out.println("***" + algorithm + "***");
        String credential = authorization
                .substring(authorization.indexOf("Credential="), authorization.indexOf("SignedHeaders=") - 2)
                .replace("Credential=", "");
        System.out.println("***" + credential + "***");
        System.out.println(credential.substring(0, credential.indexOf("/")));
        System.out.println(credential.substring(credential.indexOf("/") + 1));
        String signedHeaders = authorization
                .substring(authorization.indexOf("SignedHeaders="), authorization.indexOf("Signature=") - 2)
                .replace("SignedHeaders=", "");
        System.out.println("***" + signedHeaders + "***");
        String signature = authorization.substring(authorization.indexOf("Signature=")).replace("Signature=", "");
        System.out.println("***" + signature + "***");

    }
}
