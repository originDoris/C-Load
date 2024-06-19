package com.cl.framework.core.enums;

/**
 * 框架默认支持的服务类型
 * @author xhz
 */

public enum ServerType {
    HTTP("http"),
    SOCK("sock"),
    API("api");

    private transient final String literal;

    ServerType(final String literal) {
        this.literal = literal;
    }

    public String key() {
        return this.literal;
    }

    public boolean match(final String literal) {
        return this.literal.equals(literal);
    }
}
