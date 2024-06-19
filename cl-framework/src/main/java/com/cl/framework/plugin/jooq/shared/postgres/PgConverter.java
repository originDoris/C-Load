package com.cl.framework.plugin.jooq.shared.postgres;

import org.jooq.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 该类是为了将pg类型和Java类型进行转换，比如jsonB类型
 * @author xhz
 */
public interface PgConverter<P,T,U> extends Converter<T,U> {


    /**
     * @return convert from the PGClient-type to the user-type using a {@link RowConverter}
     */
    RowConverter<P,U> rowConverter();
}
