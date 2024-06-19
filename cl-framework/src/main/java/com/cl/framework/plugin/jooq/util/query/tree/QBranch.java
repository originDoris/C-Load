package com.cl.framework.plugin.jooq.util.query.tree;

import java.util.Set;

/**
 * 查询分支节点
 * @author xhz
 */
public interface QBranch extends QNode {

    Set<QNode> nodes();

    QBranch add(QNode node);
}
