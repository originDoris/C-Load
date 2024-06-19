package com.cl.framework.plugin.jooq.util.query.tree;

/**
 * 查询叶子节点
 * @author xhz
 */
public interface QLeaf extends QNode {

    /**
     * 当前节点的字段信息
     */
    String field();

    /**
     * 当前节点的值
     */
    Object value();
}
