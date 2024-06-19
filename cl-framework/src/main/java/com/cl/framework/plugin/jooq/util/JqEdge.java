package com.cl.framework.plugin.jooq.util;


import org.apache.commons.lang3.StringUtils;

/*
 * Helper for join
 */
class JqEdge {
    private transient String fromTable;
    private transient String fromField;
    private transient String toTable;
    private transient String toField;

    JqEdge() {
    }

    void setFrom(final String table, final String field) {
        this.fromTable = table;
        this.fromField = StringUtils.isNotBlank(field) ? field : "key";
    }

    void setTo(final String table, final String field) {
        this.toTable = table;
        this.toField = StringUtils.isNotBlank(field) ? field : "key";
    }

    String getFromTable() {
        return this.fromTable;
    }

    String getFromField() {
        return this.fromField;
    }

    String getToTable() {
        return this.toTable;
    }

    String getToField() {
        return this.toField;
    }

    @Override
    public String toString() {
        return "JqEdge{" +
                "fromTable='" + this.fromTable + '\'' +
                ", fromField='" + this.fromField + '\'' +
                ", toTable='" + this.toTable + '\'' +
                ", toField='" + this.toField + '\'' +
                '}';
    }
}
