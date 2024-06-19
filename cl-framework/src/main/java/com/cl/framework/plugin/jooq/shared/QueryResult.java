package com.cl.framework.plugin.jooq.shared;

import org.jooq.Field;

import java.util.List;
import java.util.stream.Stream;

/**
 * QueryResult
 *
 * @author xhz
 */
public interface QueryResult {

    <T> T get(Field<T> field);

    <T> T get(int index, Class<T> type);

    <T> T get(String columnName, Class<T> type);

    <T> T unwrap();

    boolean hasResults();

    List<QueryResult> asList();

    Stream<QueryResult> stream();
}
