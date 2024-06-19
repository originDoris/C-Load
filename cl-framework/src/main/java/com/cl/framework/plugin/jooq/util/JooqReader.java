package com.cl.framework.plugin.jooq.util;

import com.cl.framework.core.domain.BasePage;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.plugin.jooq.util.condition.JooqCond;
import com.cl.framework.plugin.jooq.util.query.Inquiry;
import com.cl.framework.plugin.jooq.util.query.Pager;
import com.fasterxml.jackson.core.type.TypeReference;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * jooq读取工具类
 *
 * @author xhz
 */
public class JooqReader {


   public static  <T, R extends Record> Single<BasePage<T>> searchPagination(final Table<R> table, final DSLContext dslContext, final Inquiry inquiry, final Function<String, Field> fnAnalyze, RecordMapper<R, T> mapper) {
     return Single.defer(() -> {
         BasePage<T> basePage = new BasePage<T>();
           return count(table, dslContext, fnAnalyze, inquiry).flatMap(integer -> {
               basePage.setPageSize(inquiry.getPager().getSize());
               basePage.setPageNo(inquiry.getPager().getPage());
               basePage.setCount(integer);
               if (integer == 0) {
                   basePage.setList(new ArrayList<>());
                   return Single.just(basePage);
               }
               return searchInternal(table, dslContext, inquiry, fnAnalyze, mapper)
                       .map(list -> {
                           basePage.setList(list);
                           return basePage;
                       });
           });
       });
    }


    static <R extends Record>  Single<Integer> count(final Table<R> table, final DSLContext dslContext, final Function<String, Field> fnAnalyze,final Inquiry inquiry) {
        return count(table, dslContext, fnAnalyze, null == inquiry.getCriteria() ? new JsonObject() : inquiry.getCriteria().toJson());
    }

    static < R extends Record> Single<Integer> count(final Table<R> table, final DSLContext dslContext, final Function<String, Field> fnAnalyze, final JsonObject filters) {
        return Single.just(null == filters ? dslContext.fetchCount(table) : dslContext.fetchCount(table, JooqCond.transform(filters, fnAnalyze)));
    }

    @SuppressWarnings("all")
    public static <T, R extends Record> Single<List<T>> searchInternal(final Table<R> table, final DSLContext dslContext, final Inquiry inquiry, final Function<String, Field> fnAnalyze, RecordMapper<R, T> mapper) {
        return Single.defer(() -> {
            SelectWhereStep started;
            Set<String> projection = inquiry.getProjection();
            if (projection == null || projection.isEmpty()) {
                started  = dslContext.selectFrom(table);
            }else{
                List<Field<Object>> collect = projection.stream().map(DSL::field).collect(Collectors.toList());
                started = dslContext.select(collect).from(table);
            }
            SelectConditionStep conditionStep = null;
            if (null != inquiry.getCriteria()) {
                final Condition condition = JooqCond.transform(inquiry.getCriteria().toJson(), fnAnalyze);
                conditionStep = started.where(condition);
            }

            SelectSeekStepN selectStep = null;
            if (null != inquiry.getSorter()) {
                final List<OrderField> orders = JooqCond.orderBy(inquiry.getSorter(), fnAnalyze, null);
                if (null == conditionStep) {
                    selectStep = started.orderBy(orders);
                } else {
                    selectStep = conditionStep.orderBy(orders);
                }
            }

            SelectWithTiesAfterOffsetStep pagerStep = null;
            if (null != inquiry.getPager()) {
                final Pager pager = inquiry.getPager();
                if (null == selectStep && null == conditionStep) {
                    pagerStep = started.offset(pager.getStart()).limit(pager.getSize());
                } else if (null == selectStep) {
                    pagerStep = conditionStep.offset(pager.getStart()).limit(pager.getSize());
                } else {
                    pagerStep = selectStep.offset(pager.getStart()).limit(pager.getSize());
                }
            }
            if (null != pagerStep) {
                return toResult(pagerStep.fetch(mapper));
            }
            if (null != selectStep) {
                return toResult(selectStep.fetch(mapper));
            }
            if (null != conditionStep) {
                return toResult(conditionStep.fetch(mapper));
            }
            return toResult(started.fetch(mapper));

        });

    }


    public static <T, R extends Record> Single<List<T>> searchInternal(final String table, final DSLContext dslContext, final Inquiry inquiry, final Function<String, Field> fnAnalyze, RecordMapper<R, T> mapper) {
        return Single.defer(() -> {
            final Set<String> projectionSet = inquiry.getProjection();
            SelectWhereStep started;
            if (projectionSet == null || projectionSet.isEmpty()) {
                started  = dslContext.selectFrom(table);
            }else{
                List<Field<Object>> collect = projectionSet.stream().map(DSL::field).collect(Collectors.toList());
                started = dslContext.select(collect).from(table);
            }
            SelectConditionStep conditionStep = null;
            if (null != inquiry.getCriteria()) {
                final Condition condition = JooqCond.transform(inquiry.getCriteria().toJson(), fnAnalyze);
                conditionStep = started.where(condition);
            }


            SelectSeekStepN selectStep = null;
            if (null != inquiry.getSorter()) {
                final List<OrderField> orders = JooqCond.orderBy(inquiry.getSorter(), fnAnalyze, null);
                if (null == conditionStep) {
                    selectStep = started.orderBy(orders);
                } else {
                    selectStep = conditionStep.orderBy(orders);
                }
            }

            SelectWithTiesAfterOffsetStep pagerStep = null;
            if (null != inquiry.getPager()) {
                final Pager pager = inquiry.getPager();
                if (null == selectStep && null == conditionStep) {
                    pagerStep = started.offset(pager.getStart()).limit(pager.getSize());
                } else if (null == selectStep) {
                    pagerStep = conditionStep.offset(pager.getStart()).limit(pager.getSize());
                } else {
                    pagerStep = selectStep.offset(pager.getStart()).limit(pager.getSize());
                }
            }


            if (null != pagerStep) {
                return toResult(pagerStep.fetch(mapper));
            }
            if (null != selectStep) {
                return toResult(selectStep.fetch(mapper));
            }
            if (null != conditionStep) {
                return toResult(conditionStep.fetch(mapper));
            }
            return toResult(started.fetch(mapper));

        });

    }



    public static <T> Single<List<T>> toResult(List<T> list) {
        return Single.just(list);

    }
}
