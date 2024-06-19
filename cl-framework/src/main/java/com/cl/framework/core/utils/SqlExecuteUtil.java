package com.cl.framework.core.utils;

import com.cl.framework.core.domain.BasePage;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.functions.Function;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.jdbcclient.JDBCPool;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.*;
import io.vertx.rxjava3.sqlclient.templates.RowMapper;
import io.vertx.rxjava3.sqlclient.templates.SqlTemplate;
import io.vertx.rxjava3.sqlclient.templates.TupleMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author xhz
 * @Description: sql执行工具类
 */
@Slf4j
public class SqlExecuteUtil {

    public static <T> Single<T> executeUpdate(T param, String sql, TupleMapper<T> tTupleMapper, SqlClient sqlClient) {
        log.info("executeUpdate.sql :{}", sql);
        return SqlTemplate.forUpdate(sqlClient, sql)
                .mapFrom(tTupleMapper)
                .rxExecute(param)
                .map(result -> param);
    }


    public static Single<HashMap<String, Object>> executeUpdate(HashMap<String, Object> param, String sql, SqlClient sqlClient) {
        log.info("executeUpdate.sql :{}", sql);
        return SqlTemplate.forUpdate(sqlClient, sql)
                .rxExecute(param)
                .map(result -> param);
    }


    public static Single<Boolean> executeUpdate(String sql, SqlClient sqlClient, Map<String, Object> map) {
        log.info("executeUpdate.sql :{}", sql);
        return SqlTemplate.forUpdate(sqlClient, sql)
                .execute(map)
                .map(data -> true);
    }

    public static <T> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, TupleMapper<T> tTupleMapper, T data) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapTo(rowMapper)
                .mapFrom(tTupleMapper)
                .rxExecute(data)
                .map(rowSet ->
                        StreamSupport.stream(rowSet.spliterator(), false)
                                .collect(Collectors.toList())
                );

    }

    public static <T extends Serializable> Single<Optional<T>> executeQueryOne(String sql, SqlClient sqlClient, Function<Row, T> mapper, JsonObject data) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            RowIterator<Row> iterator = rowSet.iterator();
                            boolean b = iterator.hasNext();
                            if (b) {
                                Row next = rowSet.iterator().next();
                                T apply = mapper.apply(next);
                                return Optional.of(apply);
                            }
                            return Optional.empty();
                        }
                );
    }

    public static <T extends Serializable> Single<BasePage<T>> executeQuery(String sql, SqlClient sqlClient, Function<Row,T> mapper, JsonObject data, BasePage<T> basePage) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            ArrayList<T> ts = new ArrayList<>();
                            for (Row row : rowSet) {
                                ts.add(mapper.apply(row));
                            }
                            basePage.setList(ts);
                            return basePage;
                        }
                );

    }

    public static <T extends Serializable> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, JsonObject data, List<T> list) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapTo(rowMapper)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            for (T row : rowSet) {
                                list.add(row);
                            }
                            return list;
                        }
                );

    }

    public static <T extends Serializable> Single<List<T>> executeQueryList(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, JsonObject data) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapTo(rowMapper)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            List<T> collect = StreamSupport.stream(rowSet.spliterator(), false)
                                    .collect(Collectors.toList());
                            return collect;
                        }
                );
    }


    public static <T> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, Function<Row, T> mapper, JsonObject data, List<T> list) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            for (Row row : rowSet) {
                                list.add(mapper.apply(row));
                            }

                            return list;
                        }
                );
    }

    public static <T> Single<Optional<T>> executeGetOne(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, TupleMapper<T> tTupleMapper, T data) {
        log.info("executeGetOne.sql :{}", sql);
        return executeQuery(sql, sqlClient, rowMapper, tTupleMapper, data)
                .map(result -> {
                    if (result != null && !result.isEmpty()) {
                        return Optional.of(result.get(0));
                    }
                    return Optional.empty();
                });

    }


    public static <T> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, JsonObject data) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapTo(rowMapper)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet ->
                        StreamSupport.stream(rowSet.spliterator(), false)
                                .collect(Collectors.toList())
                );
    }

    public static <T> Single<Optional<T>> executeGetOne(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, JsonObject data) {
        log.info("executeGetOne.sql :{}", sql);
        return executeQuery(sql, sqlClient, rowMapper, data)
                .map(result -> {
                    if (result != null && !result.isEmpty()) {
                        return Optional.of(result.get(0));
                    }
                    return Optional.empty();
                });
    }


    public static <T extends Serializable> Single<BasePage<T>> executeQuery(String sql, SqlClient sqlClient, RowMapper<T> rowMapper, JsonObject data, BasePage<T> basePage) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapTo(rowMapper)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                            List<T> collect = StreamSupport.stream(rowSet.spliterator(), false)
                                    .collect(Collectors.toList());
                            basePage.setList(collect);
                            return basePage;
                        }
                );
    }


    public static <T> Single<T> executeQuery(String sql, SqlClient sqlClient, Function<? super RowSet<Row>, T> mapper) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .rxExecute(new HashMap<>())
                .map(mapper);
    }

    public static <T> Single<T> executeQuery(String sql, SqlClient sqlClient, Function<RowSet<Row>, T> mapper, JsonObject data) {
        log.info("executeQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(mapper);
    }


    public static <T> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, Function<Row, T> rowMapper, ArrayList<T> resultList) {
        log.info("executeCountQuery.sql :{}", sql);
        return sqlClient
                .preparedQuery(sql)
                .execute()
                .map(rowSet -> {
                    for (Row row : rowSet) {
                        T result = rowMapper.apply(row);
                        resultList.add(result);
                    }
                    return resultList;
                });
    }


    public static <T> Single<List<T>> executeQuery(String sql, SqlClient sqlClient, Function<Row, T> rowMapper, JsonObject data, ArrayList<T> resultList) {
        log.info("executeCountQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rowSet -> {
                    for (Row row : rowSet) {
                        T result = rowMapper.apply(row);
                        resultList.add(result);
                    }
                    return resultList;
                });
    }


    public static Single<Integer> executeCountQuery(String sql, SqlClient sqlClient, JsonObject data, String key) {
        log.info("executeCountQuery.sql :{}", sql);
        return SqlTemplate.forQuery(sqlClient, sql)
                .mapFrom(TupleMapper.jsonObject())
                .rxExecute(data)
                .map(rows -> {
                    int i = rows.rowCount();
                    if (i > 0) {
                        return rows.iterator().next().getInteger(key);
                    }
                    return 0;
                });
    }


    public static <T> Single<List<T>> executeQueryList(String sql, SqlClient sqlClient, Function<Row, T> mapper) {
        log.info("executeQueryList.sql :{}", sql);
        List<T> ts = new ArrayList<>();
        return executeQuery(sql, sqlClient, mapper, null, ts);
    }


}
