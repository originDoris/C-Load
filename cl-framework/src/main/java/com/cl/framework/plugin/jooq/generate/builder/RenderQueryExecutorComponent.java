package com.cl.framework.plugin.jooq.generate.builder;

@FunctionalInterface
interface RenderQueryExecutorComponent {

    String renderQueryExecutor(String rType, String pType, String tType);

}
