package com.cl.framework.plugin.jooq.util.query.tree;

/**
 * 顶级查询节点
 * @author xhz
 */
public interface QNode {

    /**
     * 查询操作符号
     */
    QOp op();

    /**
     * 当前层级
     */
    QNode level(Integer level);

    /**
     * 是否为叶子节点
     */
    boolean isLeaf();
}
