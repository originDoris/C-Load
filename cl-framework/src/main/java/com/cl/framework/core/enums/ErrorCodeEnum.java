package com.cl.framework.core.enums;

import lombok.Getter;

/**
 * @author xhz
 * @Description: 公共异常代码枚举
 */
@Getter
public enum ErrorCodeEnum {
    PARAM_ERROR_CODE(400, "参数错误"),
    API_EXEC_ERROR_CODE(4100, "API运行错误"),
    SUCCESS(200, "请求正常"),
    NO_CONTENT(204, "No Content"),
    NOT_LOGIN(401, "未登录"),
    DATASOURCE_EXEC_ERROR_CODE(4200, "DATASOURCE运行错误"),
    NO_PERMISSION(10001,"没权限"),
    LICENSE_EXPIRED_ERROR(10002, "license过期"),

    INTERNAL_ERROR(500, "服务异常"),
    START_UP_FAILURE(5001, "服务启动失败"),
    NO_ARG_CONSTRUCTOR(4001, "缺少无参构造方法!"),
    NOT_PUBLIC_CLASS(4002, "非Public类！"),
    PATH_ANNO_IS_EMPTY(4003, "path注解为空！"),
    METHOD_IS_NULL(4004, "method is null！"),
    FILTER_SPECIFICATION_ERROR(4005, "过滤器配置错误！"),
    FILTER_ORDER_ERROR(4006, "FilterOrder配置错误！"),
    FILTER_INIT_ERROR(4007, "Filter初始化错误！"),
    ADDRESS_METHOD_ERROR(4008, "方法格式错误！"),
    ADDRESS_NOT_EXIST_ERROR(4009, "address 不存在！"),
    EVENT_ACTION_NON_ERROR(4010, "event action 不能为空！"),
    FILE_NOT_EXIST_ERROR(4011, "文件不存在！"),
    ANNOTATION_REPEAT_ERROR(4012, "注解重复异常！"),
    PARAM_ANNOTATION_ERROR(4013, "参数注解配置异常！"),
    MEDIA_NOT_SUPPORT_ERROR(4014, "不支持的media"),
    METHOD_MUST_HAS_RETURN(4015, "此方法必须有非Void返回值！"),
    WORKER_MISSING_ERROR(4016, "没有找到对应的Address ！"),
    VERTX_CALLBACK_ERROR(4017, "vertx 回调失败！"),
    AGENT_DUPLICATED_ERROR(4018, "agent 配置重复了！"),
    WORKER_ARGUMENT_ERROR(4019, "worker 方法参数错误！"),
    ASYNC_SIGNATURE_ERROR(4020, "异步方法验证错误！"),
    INVOKER_NULL_ERROR(4021, "未找到对应的invoker!"),
    RESOLVER_NULL_ERROR(4022, "resolver 不能为空！"),
    RESOLVER_TYPE_ERROR(4033, "resolver类型错误！"),
    WALL_DUPLICATED_ERROR(4034, "wall配置重复！"),
    WALL_KEY_MISSING_ERROR(4035, "wall key 不能为空！"),
    WALL_METHOD_MULTI_ERROR(4036, "wall 方法重复！"),
    FILTER_CONTEXT_ERROR(4037, "filter context 不能为空！"),
    QUALIFIER_MISSED_ERROR(4038, "缺少qualifier 注解！"),
    NAMED_IMPL_ERROR(4039, "实现类必须使用@Named注解！"),
    NAME_NOT_FOUND_ERROR(4040, "@Named对应的类 不存在！"),
    PLUGIN_SPECIFICATION_ERROR(4041, "plugin 不符合规范！"),
    CONFIG_KEY_MISSING_ERROR(4042, "config key missing！"),
    JOOQ_FIELD_MISSING_ERROR(4043, "jooq field missing！"),
    JOOQ_MERGE_ERROR(4044, "jooq merge error！"),
    JOOQ_QUERY_KEY_TYPE_ERROR(4045, "query key type error！"),
    JOOQ_QUERY_META_NULL_ERROR(4046, "query meta null error！"),
    JOOQ_PAGER_INVALID_ERROR(4047, "page invalid error！"),
    JOOQ_SIZE_INVALID_ERROR(4048, "size invalid error！"),
    JOOQ_OP_UN_SUPPORT_ERROR(4049, "jooq op error!"),
    JOOQ_COND_FIELD_ERROR(4050, "jooq condition field error!"),
    JOOQ_COND_CLAUSE_ERROR(4051, "jooq condition clause error!"),
    JOOQ_CLASS_INVALID_ERROR(4052, "jooq class invalid error!"),
    CYCLIC_DEPENDENCE_ERROR(4053, "存在循环依赖！"),
    JOB_ON_MISSING_ERROR(4054, "job 对应的on不存在！"),
    CONTRACT_FIELD_ERROR(4055, "contract field 数量错误！"),
    INVOKING_SPEC_ERROR(4056,"invoking spec error！"),
    JOB_METHOD_ERROR(4057,"job method error！"),

    ;
    private final Integer code;

    private final String message;


    ErrorCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
