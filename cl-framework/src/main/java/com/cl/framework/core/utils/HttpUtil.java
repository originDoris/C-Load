package com.cl.framework.core.utils;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.ext.web.client.HttpRequest;
import io.vertx.rxjava3.ext.web.client.HttpResponse;
import io.vertx.rxjava3.ext.web.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xhz
 * @Description: 发送http请求
 */
public class HttpUtil {
    private static WebClient webClient;
    private static HttpUtil instance;



    private HttpUtil(Vertx vertx) {
        webClient = WebClient.create(vertx);
    }

    public static HttpUtil getInstance(Vertx vertx) {
        if (instance == null) {
            instance = new HttpUtil(vertx);
        }
        return instance;
    }


    public Single<HttpResponse<Buffer>> get(URI url, MultiMap headers, HashMap<String, String> queryParam,Long timeout) {
        HttpRequest<Buffer> request = createHttpRequest(url, headers, "get", timeout);
        addQueryParams(request, queryParam);
        return request.putHeaders(headers).rxSend();
    }

    private HttpRequest<Buffer> createHttpRequest(URI url, MultiMap headers, String method,Long timeout) {
        HttpRequest<Buffer> bufferHttpRequest;
        if (url.getPort() >= 0 && url.getPort() <= 65535) {
            switch (method) {
                case "get":
                    bufferHttpRequest = webClient.get(url.getPort(), url.getHost(), url.getPath());
                    break;
                case "post":
                    bufferHttpRequest = webClient.post(url.getPort(), url.getHost(), url.getPath());
                    break;
                case "put":
                    bufferHttpRequest = webClient.put(url.getPort(), url.getHost(), url.getPath());
                    break;
                case "delete":
                    bufferHttpRequest = webClient.delete(url.getPort(), url.getHost(), url.getPath());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method");
            }
        } else {
            switch (method) {
                case "get":
                    bufferHttpRequest = webClient.get(url.getHost(), url.getPath());
                    break;
                case "post":
                    bufferHttpRequest = webClient.post(url.getHost(), url.getPath());
                    break;
                case "put":
                    bufferHttpRequest = webClient.put(url.getHost(), url.getPath());
                    break;
                case "delete":
                    bufferHttpRequest = webClient.delete(url.getHost(), url.getPath());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method");
            }
        }
        return bufferHttpRequest.timeout(timeout == null ? 10000 : timeout * 1000);
    }

    private void addQueryParams(HttpRequest<Buffer> bufferHttpRequest, HashMap<String, String> queryParam) {
        if (queryParam != null && !queryParam.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                bufferHttpRequest.addQueryParam(entry.getKey(), entry.getValue());
            }
        }
    }


    public Single<HttpResponse<Buffer>> post(URI url, Object body, MultiMap headers, HashMap<String,String> queryParam,Long timeout) {
        HttpRequest<Buffer> request = createHttpRequest(url, headers, "post",timeout);
        addQueryParams(request, queryParam);
        return request
                .putHeaders(headers)
                .rxSendJson(body);
    }


    public Single<HttpResponse<Buffer>> put(URI url, Object body, MultiMap headers , HashMap<String,String> queryParam,Long timeout) {
        HttpRequest<Buffer> request = createHttpRequest(url, headers, "put",timeout);
        addQueryParams(request, queryParam);
        return request
                .putHeaders(headers)
                .rxSendJson(body);
    }

    public Single<HttpResponse<Buffer>> delete(URI url, MultiMap headers , HashMap<String,String> queryParam,Long timeout) {
        HttpRequest<Buffer> request = createHttpRequest(url, headers, "delete",timeout);
        addQueryParams(request, queryParam);
        return request
                .putHeaders(headers)
                .rxSend();
    }


    public static URI appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);
        String newQuery = oldUri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }
        return new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());
    }


    public static URI getUrl(String apiPath,String apiHost,String apiProtocol) throws URISyntaxException {
        return new URI(apiProtocol + "://" + apiHost + apiPath);
    }


}
