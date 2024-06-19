package com.cl.framework.plugin.jooq.generate.builder;

import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;

@FunctionalInterface
interface OverwriteDAOComponent {

    public void overwrite(SchemaDefinition schema, JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType);

}
