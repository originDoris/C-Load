package com.cl.framework.plugin.jooq.shared;

import org.jooq.Attachable;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.function.Function;

/**
 * 用于执行插入，更新，删除 sql
 *
 * @author xhz
 */
public interface BasicQueryExecutor<EXECUTE> extends Attachable {

    /**
     * 执行操作并返回数据
     */
    EXECUTE execute(Function<DSLContext, ? extends Query> queryFunction);


    default void release(){
        detach();
    }
}
