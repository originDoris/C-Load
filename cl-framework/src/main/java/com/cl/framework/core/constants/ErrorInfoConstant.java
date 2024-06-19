package com.cl.framework.core.constants;

/**
 * 错误信息常量
 *
 * @author xhz
 */
public class ErrorInfoConstant {

    public static final String INTERNAL_ERROR = "服务内部异常, class:{0}, message:{1}";

    public static final String NIL_MSG = "出现空的数据流异常 {0}, caused = {1}.";

    public static final String START_UP_CLASS_NULL_MSG = "虽然输入的class 不重要，但是容器不允许class为空！";

    public static final String START_UP_ANNO_IS_NULL = "输入的Class 必须用@{0} 修饰！";

    public static final String SCAN_PACKAGE_ERROR = "扫描包出现错误 thread-name:{0} pack:{1}";

    public static final String NO_ARG_CONSTRUCTOR_ERROR = "{0} 缺少无参构造函数！";

    public static final String NOT_PUBLIC_ERROR = "{0} 必须用public修饰！";

    public static final String FILTER_SPECIFICATION_ERROR = "Filter :{0} 必须是 {1} 的子类，并且使用@WebFilter 注解修饰！";

    public static final String FILTER_ORDER_ERROR = "{0} FilterOrder 不能小于0";

    public static final String FILTER_INIT_ERROR = "{0} Filter初始化错误 ！";
    public static final String ADDRESS_METHOD_NOT_VOID_ERROR = "Method: {0},方法格式错误，返回值必须是Void！";
    public static final String ADDRESS_METHOD_IS_VOID_ERROR = "Method: {0},方法格式错误，返回值不能是Void！";

    public static final String ADDRESS_NOT_EXIST_ERROR = "Address:{0} 不存在！";

    public static final String FIELD_SERIALIZABLE_ERROR = "字段序列化失败，字段类型：{0}, 字段内容:{1}";

    public static final String FILE_NOT_EXIST_ERROR = "文件：{0} 不存在, class:{1}";

    public static final String EVENT_ACTION_NON_ERROR = "event action 不能为空！event:{0}";

    public static final String ANNOTATION_REPEAT_ERROR = "注解重复异常，annotation：{0} methodName:{1} occurs:{2}";

    public static final String MEDIA_NOT_SUPPORT_ERROR = "不支持的Media type:{0} medias:{1}";

    public static final String METHOD_MUST_HAS_RETURN = "此方法必须有非Void返回值, class:{0} method:{1}";

    public static final String WORKER_MISSING_ERROR = "没有找到对应的Address :{0},Class:{1} !";

    public static final String AGENT_DUPLICATED_ERROR = "agent 配置重复了, ServerType:{0}，size:{1}，{2} ";

    public static final String WORKER_ARGUMENT_ERROR = "worker 组件方法参数错误，class：{0}, method:{1}";

    public static final String ASYNC_SIGNATURE_ERROR = "异步方法验证错误, class:{0}, returnType:{1}, paramType:{2}";

    public static final String INVOKER_NULL_ERROR = "未找到对应的invoker, class:{0}, returnType:{1}, paramCls:{2}";

    public static final String RESOLVER_NULL_ERROR = "resolver 不能为空！";
    public static final String RESOLVER_TYPE_ERROR = "resolver 类型错误，class:{0}！";


    public static final String WALL_DUPLICATED_ERROR = "wall配置重复！ values:{0}!";

    public static final String WALL_KEY_MISSING_ERROR = "wall key 不能为空！ key:{0}，config:{1}!";

    public static final String WALL_METHOD_MULTI_ERROR = "wall method 重复！methodName:{0}, class:{1}";

    public static final String QUALIFIER_MISSED_ERROR = "缺少qualifier 注解！class:{0},fieldName:{1},fieldClass:{2}";

    public static final String NAMED_IMPL_ERROR = "实现类必须使用@Named注解! class:{0},names:{1},fieldType:{2}";

    public static final String NAMED_NOT_FOUND = "@Named对应的类 不存在！class:{0}, names:{1},value:{2}";

    public static final String PLUGIN_SPECIFICATION_ERROR = "plugin 不符合规范！class:{0}, name:{1}";

    public static final String CONFIG_KEY_MISSING_ERROR = "config key missing！class:{0}, key:{1}";

    public static final String JOOQ_FIELD_MISSING_ERROR = "jooq field missing！class:{0}, field:{1}, value:{2}";

    public static final String JOOQ_QUERY_KEY_TYPE_ERROR = "jooq query key type error ! target:{0}, key:{1}, type:{2}, class:{3}";
    public static final String JOOQ_OP_UN_SUPPORT_ERROR = "jooq op error! op:{0}";

    public static final String JOOQ_COND_FIELD_ERROR = "jooq condition field error! targetField:{0}";
    public static final String JOOQ_COND_CLAUSE_ERROR = "jooq condition clause error! name:{0}, type:{1}, targetField:{2}";

    public static final String JOOQ_CLASS_INVALID_ERROR = "jooq class invalid error ! class:{0}";
    public static final String JOB_ON_MISSING_ERROR = "job 对应的on不存在！class:{0}";

    public static final String CONTRACT_FIELD_ERROR = "contract field 数量错误！class:{0}, fieldType:{1}, instance:{2},size:{3}";

    public static final String INVOKING_SPEC_ERROR = "invoking spec error class:{0},method:{1}";

    public static final String JOB_METHOD_ERROR = "job method error class:{0},mission:{1}";
}
