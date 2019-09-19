
# 简介 #


  欢迎使用京东云开发者Java签名工具。 

# 环境准备 #
1. 京东云Java SDK适用于jdk7及以上版本。
2. 在开始调用京东云open API之前，需提前在京东云用户中心账户管理下的[AccessKey管理页面](https://uc.jdcloud.com/accesskey/index)申请accesskey和secretKey密钥对（简称AK/SK）。AK/SK信息请妥善保管，如果遗失可能会造成非法用户使用此信息操作您在云上的资源，给你造成数据和财产损失。

# SDK使用方法 #

编译代码并引用
 
# 调用SDK #
Java SDK的调用主要分为4步：

1. 设置accessKey和secretKey
2. 创建Client
3. 设置请求参数
4. 执行请求得到响应

以下是查询单个云主机实例详情的调用示例

```java
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

```
