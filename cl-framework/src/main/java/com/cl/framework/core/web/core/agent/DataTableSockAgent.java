package com.cl.framework.core.web.core.agent;

import com.cl.framework.core.annotation.Agent;
import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.vertx.VertxLauncher;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;

/**
 * 默认处理WebSocket的 Agent
 * @author xhz
 */
@Agent(type = ServerType.SOCK)
public class DataTableSockAgent extends AbstractVerticle {
    @Override
    public void start() {
        com.cl.framework.core.runtime.DataTableConfig.getSockOpts().forEach((port, option) -> {
            final HttpServer server = VertxLauncher.getVertx().createHttpServer(option);

            final Router router = Router.router(this.vertx);

            server.requestHandler(router).listen().subscribe();
        });
    }
}
