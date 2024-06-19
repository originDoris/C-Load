
package com.cl.framework.plugin.jooq.shared.postgres;

import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author xhz
 */
public class NumericToLongConverter implements PgConverter<BigInteger, Numeric,BigInteger>{

    private static final IdentityRowConverter<BigInteger> identityConverter = new IdentityRowConverter<>(BigInteger.class);

    private static NumericToLongConverter INSTANCE;
    public static NumericToLongConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new NumericToLongConverter() : INSTANCE;
    }

    @Override
    public BigInteger from(Numeric databaseObject) {
        return databaseObject == null ? null : BigInteger.valueOf(databaseObject.longValue());
    }

    @Override
    public Numeric to(BigInteger userObject) {
        return userObject == null ? null : Numeric.create(userObject.longValue());
    }

    @Override
    public Class<BigInteger> toType() {
        return BigInteger.class;
    }

    @Override
    public Class<Numeric> fromType() {
        return Numeric.class;
    }

    @Override
    public RowConverter<BigInteger, BigInteger> rowConverter() {

        return identityConverter;
    }
}
