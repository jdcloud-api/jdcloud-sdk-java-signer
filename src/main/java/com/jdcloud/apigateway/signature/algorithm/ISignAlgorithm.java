package com.jdcloud.apigateway.signature.algorithm;

import com.jdcloud.apigateway.signature.SignRequest;

public interface ISignAlgorithm {

    public static final String SEPARATOR = "/";

    public enum ApiGatewayAlgorithm {
        SHA256
    }

    String doSign(SignRequest signRequest);

    static class StaticSignAlgorithm {
        public static ISignAlgorithm builder(ApiGatewayAlgorithm algorithm) {
            switch (algorithm) {
            case SHA256:
                return new SHA256Algorithm();
            default:
                break;
            }
            return new SHA256Algorithm();
        }
    }
}
