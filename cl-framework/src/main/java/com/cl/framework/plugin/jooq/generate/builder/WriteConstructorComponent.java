package com.cl.framework.plugin.jooq.generate.builder;

import org.jooq.codegen.JavaWriter;

/**
 * 写入构造函数组件
 * @author xhz
 */
@FunctionalInterface
interface WriteConstructorComponent {

    void writeConstructor(JavaWriter out, String className, String tableIdentifier, String tableRecord, String pType, String tType, String schema);
}
