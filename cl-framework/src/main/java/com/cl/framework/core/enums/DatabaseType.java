package com.cl.framework.core.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 支持的数据库类型
 * @author xhz
 */

public enum DatabaseType {
    MYSQL5,
    MYSQL8,
    MYSQL,
    ORACLE,
    ORACLE11,
    ORACLE12,
    POSTGRESQL,;


    public static Optional<DatabaseType> get(String code) {
        return Arrays.stream(DatabaseType.values()).filter(databaseType -> databaseType.name().equals(code)).findFirst();
    }
}
