package com.cl.framework.core.options;

import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DataTable 配置信息
 *
 * @author xhz
 */
@Data
public class DataTableOptions {

    public static final String TOKEN_KEY = "X-DataTable-Token";

    public static final String TENANT_CODE = "X-DataTable-TenantCode";

    public static final String SERVER_RUN_PATH = "^/DataTable/server/.*";

    public static final String SERVER_RUN_URI = "/dc/DataTable/server/";

    public static final String SERVER_APP_USER_TOKEN = "User-Token";
    public static final String SERVER_APP_ACCESS_KEY = "Access-Key";
    public static final String SERVER_APP_SECRET_KEY = "Secret-Key";
    public static final String REDIS_TOKEN_KEY = "APP_INFO_";
    public static final boolean CAPTCHA = true;
    public static final List<String> EXCLUDE_AUTH_PATH = new ArrayList<>();

    private Long dataCubeTenantCode;

    private String path;

    private String tokenHeaderKey;

    private String sm2PublicKey;

    private String sm2PrivateKey;

    private String tenantCode;

    private String serverDomain;

    private String serverRunPath;

    private String serverAppUserToken;

    private String serverAppAccessKey;

    private String serverAppSecretKey;

    private String serverRedisKey;

    private List<String> excludeAuthPath;

    private String serverRunUri;

    private Integer wsPort;

    private String license;

    private Boolean captcha;

    private Boolean clusteredSession;

    private String sessionName;

    public DataTableOptions() {
        tokenHeaderKey = TOKEN_KEY;
        serverRunPath = SERVER_RUN_PATH;
        serverAppUserToken = SERVER_APP_USER_TOKEN;
        serverAppAccessKey = SERVER_APP_ACCESS_KEY;
        serverAppSecretKey = SERVER_APP_SECRET_KEY;
        serverRedisKey = REDIS_TOKEN_KEY;
        excludeAuthPath = EXCLUDE_AUTH_PATH;
        serverRunUri = SERVER_RUN_URI;
        captcha = CAPTCHA;
        clusteredSession = false;
    }

    public DataTableOptions(final com.cl.framework.core.options.DataTableOptions other) {
        this.path = other.getPath();
        this.tokenHeaderKey = other.getTokenHeaderKey();
        this.sm2PublicKey = other.getSm2PublicKey();
        this.sm2PrivateKey = other.getSm2PrivateKey();
        this.tenantCode = other.getTenantCode();
        this.serverDomain = other.getServerDomain();
        this.serverRunPath = other.getServerRunPath();
        this.serverAppAccessKey = other.getServerAppAccessKey();
        this.serverAppSecretKey = other.getServerAppSecretKey();
        this.serverAppUserToken = other.getServerAppUserToken();
        this.serverRedisKey = other.getServerRedisKey();
        this.excludeAuthPath = other.getExcludeAuthPath();
        this.serverRunUri = other.getServerRunUri();
        this.license = other.getLicense();
        this.captcha = other.getCaptcha();
        this.clusteredSession = other.getClusteredSession();
        this.sessionName = other.getSessionName();
    }


    public DataTableOptions(final JsonObject json) {
        this();
        com.cl.framework.core.options.converter.DataTableOptionsConverter.fromJson(json, this);
    }


}
