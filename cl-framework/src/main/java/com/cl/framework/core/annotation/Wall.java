package com.cl.framework.core.annotation;


import java.lang.annotation.*;

/**
 * 鉴权注解
 * 1. 401 Response
 * 2. 403 Response
 * @author xhz
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Wall {
    /**
     * 需要鉴权的路径
     */
    String path() default "/*";

    String value();

    /**
     * wall顺序,每个wall必须不同
     */
    int order() default 0;

    /**
     * 当前授权方式是否是自定义的
     * 1. 默认值为false,这意味着必须实现带有@Authenticate注释的AuthHandler方法
     * 2. 如果默认值为true,则必须按照以下方式实现phase方法:
     * 使用@Phase注解
     * Phase(HEADER): JsonObject parse(MultiMap headers, JsonObject body)
     * Phase(AUTHORIZE): void authorize(JsonObject, AsyncHandler<User>)
     * Phase(ACCESS): User isAuthorized(String, Handler<AsyncResult<Boolean>> resultHandler)
     */
    boolean define() default false;
}
