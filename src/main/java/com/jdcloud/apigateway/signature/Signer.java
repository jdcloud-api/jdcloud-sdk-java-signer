package com.jdcloud.apigateway.signature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.jdcloud.apigateway.signature.algorithm.ISignAlgorithm;
import com.jdcloud.apigateway.signature.algorithm.ISignAlgorithm.ApiGatewayAlgorithm;
import com.jdcloud.apigateway.signature.algorithm.ISignAlgorithm.StaticSignAlgorithm;
import org.apache.commons.lang3.StringUtils;

public class Signer {

    private ISignAlgorithm signAlgorithm;

    public Signer(ApiGatewayAlgorithm apiGatewayAlgorithm) {
        signAlgorithm = StaticSignAlgorithm.builder(apiGatewayAlgorithm);
    }

    public String sign(String secretKey, String host, String path, String httpMethod, Map<String, String> headers,
            String queryParameters, String body) {
        if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(host) || StringUtils.isBlank(path)
                || StringUtils.isBlank(httpMethod) || headers == null || headers.size() == 0) {
            throw new RuntimeException("wrong paramters input");
        }
        SignRequest signRequest = new SignRequest();
        signRequest.setSecretKey(secretKey);
        signRequest.setHost(host);
        signRequest.setPath(path);
        signRequest.setHttpMethod(httpMethod);
        signRequest.setHeaders(headers);
        signRequest.setQueryParametersString(queryParameters);
        signRequest.setQueryParameters(buildQueryParameters(queryParameters));
        signRequest.setBody(body);
        return signAlgorithm.doSign(signRequest);
    }

    public static void main(String[] args) {
        String secretKey = "secret key";
        String host = "192.168.182.82:5888";
        String uri = "/test/httpCode";
        String method = "GET";
        String queryString = "code=200";
        String body = "this is body";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-jdcloud-nonce", "1263de8a-9adf-4626-ade0-f25b608d3a55");
        headers.put("x-jdcloud-date", "20181114T085919Z");
        headers.put("authorization", "JDCLOUD2-HMAC-SHA256 Credential=636B856DCF14D467D313CCB0C0E2B21C/20181114/cn-north-1/xapw9ul97y4p/jdcloud2_request, SignedHeaders=content-type;x-jdcloud-date;x-jdcloud-nonce, Signature=9b085f63d7fd1583f4fe20356c5770da9dffb51ea66fcdbd45e8a0c8b0a7dbfa");
        headers.put("content-type", "application/json");
        Signer signer = new Signer(ApiGatewayAlgorithm.SHA256);
        String signature = signer.sign(secretKey, host, uri, method, headers, queryString, body);
        System.out.println(signature);
    }

    public boolean validate(HttpServletRequest request, String secretKey) {
        return validate(secretKey, getHost(request), getPath(request), getHttpMethod(request), getHeaders(request), getQueryParameters(request), getBody(request));
    }
    
    private String getHost(HttpServletRequest request) {
        return request.getHeader("host");
    }
    private String getPath(HttpServletRequest request) {
        return request.getRequestURI();
        }
    
    private String getHttpMethod(HttpServletRequest request) {
        return request.getMethod();
    }
    
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }
    
    private Map<String, String[]> getQueryParameters(HttpServletRequest request) {
         return request.getParameterMap();
    }
    
    private String getBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
    
    public boolean validate(String secretKey, String host, String path, String httpMethod, Map<String, String> headers,
            Map<String, String[]> queryParameters, String body) {
        if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(host) || StringUtils.isBlank(path)
                || StringUtils.isBlank(httpMethod) || headers == null || headers.size() == 0) {
            throw new RuntimeException("wrong paramters input");// TODO
        }
        SignRequest signRequest = new SignRequest();
        signRequest.setSecretKey(secretKey);
        signRequest.setHost(host);
        signRequest.setPath(path);
        signRequest.setHttpMethod(httpMethod);
        signRequest.setHeaders(headers);
        signRequest.setQueryParameters(buildQueryParameters(queryParameters));
        signRequest.setBody(body);
        return signAlgorithm.doSign(signRequest).equals(signRequest.getSignature());
    }

    private Map<String, List<String>> buildQueryParameters(String queryParameters) {
        Map<String, List<String>> queryParametersMaps = new HashMap<>();
        if(StringUtils.isNotBlank(queryParameters)) {
            String[] params = queryParameters.split("&");
            for (String param : params) {
                if (StringUtils.isBlank(param))
                    continue;
                String[] kv = param.split("=");
                if (kv.length != 2)
                    throw new RuntimeException("wrong paramters input");
                List<String> values = queryParametersMaps.get(kv[0]);
                if (values == null)
                    values = new ArrayList<>();
                values.add(kv[1]);
                queryParametersMaps.put(kv[0], values);
            }
        }
        return queryParametersMaps;
    }

    private Map<String, List<String>> buildQueryParameters(Map<String, String[]> queryParameters) {
        Map<String, List<String>> queryParametersMaps = new HashMap<>();
        if(queryParameters != null && queryParameters.size() > 0) {
            for(Map.Entry<String, String[]> entry : queryParameters.entrySet()) {
                queryParametersMaps.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        }
        return queryParametersMaps;
    }

}
