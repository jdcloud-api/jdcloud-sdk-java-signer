package com.jdcloud.apigateway.signature;

import java.util.List;
import java.util.Map;

public class SignRequest {
    private String host;
    private String path;
    private String httpMethod;
    private Map<String, String> headers;
    private String signedHeadersString;
    private String canonicalHeaders;
    private Map<String, String> signedHeaders;
    private String queryParametersString;
    private Map<String, List<String>> queryParameters;
    private String body;
    private String algorithm;
    private String assessKey;
    private String secretKey;
    private String credentialScope;
    private String signature;
    private String reqTime;
    private String date;
    private String region;
    private String service;
    private String contentSha256;

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the httpMethod
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param httpMethod
     *            the httpMethod to set
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * @return the signedHeaders
     */
    public Map<String, String> getSignedHeaders() {
        return signedHeaders;
    }

    /**
     * @param signedHeaders
     *            the signedHeaders to set
     */
    public void setSignedHeaders(Map<String, String> signedHeaders) {
        this.signedHeaders = signedHeaders;
    }

    /**
     * @return the queryParameters
     */
    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    /**
     * @param queryParameters
     *            the queryParameters to set
     */
    public void setQueryParameters(Map<String, List<String>> queryParameters) {
        this.queryParameters = queryParameters;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body
     *            the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm
     *            the algorithm to set
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return the assessKey
     */
    public String getAssessKey() {
        return assessKey;
    }

    /**
     * @param assessKey
     *            the assessKey to set
     */
    public void setAssessKey(String assessKey) {
        this.assessKey = assessKey;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * @param secretKey
     *            the secretKey to set
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * @return the credentialScope
     */
    public String getCredentialScope() {
        return credentialScope;
    }

    /**
     * @param credentialScope
     *            the credentialScope to set
     */
    public void setCredentialScope(String credentialScope) {
        this.credentialScope = credentialScope;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the queryParametersString
     */
    public String getQueryParametersString() {
        return queryParametersString;
    }

    /**
     * @param queryParametersString
     *            the queryParametersString to set
     */
    public void setQueryParametersString(String queryParametersString) {
        this.queryParametersString = queryParametersString;
    }

    /**
     * @return the signedHeadersString
     */
    public String getSignedHeadersString() {
        return signedHeadersString;
    }

    /**
     * @param signedHeadersString the signedHeadersString to set
     */
    public void setSignedHeadersString(String signedHeadersString) {
        this.signedHeadersString = signedHeadersString;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the reqTime
     */
    public String getReqTime() {
        return reqTime;
    }

    /**
     * @param reqTime the reqTime to set
     */
    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    /**
     * @return the canonicalHeaders
     */
    public String getCanonicalHeaders() {
        return canonicalHeaders;
    }

    /**
     * @param canonicalHeaders the canonicalHeaders to set
     */
    public void setCanonicalHeaders(String canonicalHeaders) {
        this.canonicalHeaders = canonicalHeaders;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the contentSha256
     */
    public String getContentSha256() {
        return contentSha256;
    }

    /**
     * @param contentSha256 the contentSha256 to set
     */
    public void setContentSha256(String contentSha256) {
        this.contentSha256 = contentSha256;
    }

}
