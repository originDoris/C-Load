package com.cl.framework.core.options;

import com.cl.framework.core.options.converter.ScConfigOptionsConverter;
import com.cl.framework.core.utils.secure.ScCondition;
import io.vertx.core.json.JsonObject;
import lombok.Data;

/**
 * 用户鉴权的安全配置
 *
 * @author xhz
 */
@Data
public class ScConfigOptions {

    /**
     * 1. User
     * 2. Role
     * 3. Group
     * 4. Permission
     * 5. Action
     * 6. Resource
     */
    private ScCondition condition;

    /**
     * code过期时间 秒
     */
    private Integer codeExpired;

    /**
     * code长度
     */
    private Integer codeLength;

    /**
     * code 会话池
     */
    private String codePool;

    /**
     * token 过期时间 ( ms )
     */
    private Long tokenExpired;

    /**
     * Token session pool
     */
    private String tokenPool;

    /**
     * 是否启用用户组
     */
    private Boolean supportGroup = Boolean.FALSE;

    /**
     * 是否启用缓存
     */
    private Boolean supportSecondary = Boolean.FALSE;

    /**
     *  是否启用多应用
     */
    private Boolean supportMultiApp = Boolean.TRUE;

    /**
     * 缓存角色
     */
    private String permissionPool;


    public ScConfigOptions() {
    }


    public ScConfigOptions(JsonObject jsonObject) {
        ScConfigOptionsConverter.fromJson(jsonObject, this);
    }

}
