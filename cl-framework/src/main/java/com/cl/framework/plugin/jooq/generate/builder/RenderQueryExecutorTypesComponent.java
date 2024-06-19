package com.cl.framework.plugin.jooq.generate.builder;

interface RenderQueryExecutorTypesComponent {

    String renderFindOneType(String pType);

    String renderFindManyType(String pType);

    String renderExecType();

    String renderInsertReturningType(String tType);


}
