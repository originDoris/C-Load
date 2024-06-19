package com.cl.framework.plugin.jooq.shared.postgres;

import org.jooq.Converter;

import java.util.function.Function;

/**
 * 讲PG类型转换为用户类型
 * @author xhz
 */
public interface RowConverter<P,U> extends Converter<P,U> {

    default U fromRow(Function<String,P> fromRow, String columnName){
        return from(fromRow.apply(columnName));
    }

}
