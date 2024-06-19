package com.cl.framework.plugin.jooq.generate.builder;


@FunctionalInterface
interface RenderDAOInterfaceComponent {

    String renderDAOInterface(String rType, String pType, String tType);

}
