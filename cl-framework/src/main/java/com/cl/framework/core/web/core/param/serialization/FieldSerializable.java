package com.cl.framework.core.web.core.param.serialization;

/**
 * 字段序列化
 *
 * @author xhz
 */
public interface FieldSerializable {

    Object from(Class<?> type, String literal);


    <T> Object from(T input);
}
