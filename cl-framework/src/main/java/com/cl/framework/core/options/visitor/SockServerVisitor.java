package com.cl.framework.core.options.visitor;

import com.cl.framework.core.enums.ServerType;

/**
 * Sock服务配置
 *
 * @author xhz
 */
public class SockServerVisitor extends HttpServerVisitor {
    @Override
    public ServerType getType() {
        return ServerType.SOCK;
    }
}
