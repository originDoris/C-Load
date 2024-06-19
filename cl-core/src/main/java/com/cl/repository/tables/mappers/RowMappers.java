package com.cl.repository.tables.mappers;

import io.vertx.sqlclient.Row;
import java.util.function.Function;

public class RowMappers {

        private RowMappers(){}

        public static Function<Row,com.cl.repository.tables.pojos.ClCodeConfig> getClCodeConfigMapper() {
                return row -> {
                        com.cl.repository.tables.pojos.ClCodeConfig pojo = new com.cl.repository.tables.pojos.ClCodeConfig();
                        pojo.setId(row.getLong("id"));
                        pojo.setGmtCreate(row.getLocalDateTime("gmt_create"));
                        pojo.setGmtModified(row.getLocalDateTime("gmt_modified"));
                        pojo.setCreator(row.getString("creator"));
                        pojo.setEditor(row.getString("editor"));
                        pojo.setDsl(row.getString("dsl"));
                        pojo.setContent(row.getString("content"));
                        pojo.setStatus(row.getString("status"));
                        pojo.setIsDelete(row.getShort("is_delete"));
                        pojo.setTenantId(row.getLong("tenant_id"));
                        pojo.setCodeName(row.getString("code_name"));
                        pojo.setDesc(row.getString("desc"));
                        return pojo;
                };
        }

}
