/*
 * This file is generated by jOOQ.
 */
package com.cl.repository.tables.interfaces;


import com.cl.framework.plugin.jooq.generate.VertxPojo;

import java.io.Serializable;
import java.time.LocalDateTime;


import static com.cl.framework.plugin.jooq.generate.VertxPojo.*;
/**
 * 代码配置
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public interface IClCodeConfig extends VertxPojo, Serializable {

    /**
     * Setter for <code>public.cl_code_config.id</code>.
     */
    public IClCodeConfig setId(Long value);

    /**
     * Getter for <code>public.cl_code_config.id</code>.
     */
    public Long getId();

    /**
     * Setter for <code>public.cl_code_config.gmt_create</code>. 创建时间
     */
    public IClCodeConfig setGmtCreate(LocalDateTime value);

    /**
     * Getter for <code>public.cl_code_config.gmt_create</code>. 创建时间
     */
    public LocalDateTime getGmtCreate();

    /**
     * Setter for <code>public.cl_code_config.gmt_modified</code>. 修改时间
     */
    public IClCodeConfig setGmtModified(LocalDateTime value);

    /**
     * Getter for <code>public.cl_code_config.gmt_modified</code>. 修改时间
     */
    public LocalDateTime getGmtModified();

    /**
     * Setter for <code>public.cl_code_config.creator</code>. 创建人
     */
    public IClCodeConfig setCreator(String value);

    /**
     * Getter for <code>public.cl_code_config.creator</code>. 创建人
     */
    public String getCreator();

    /**
     * Setter for <code>public.cl_code_config.editor</code>. 创建人
     */
    public IClCodeConfig setEditor(String value);

    /**
     * Getter for <code>public.cl_code_config.editor</code>. 创建人
     */
    public String getEditor();

    /**
     * Setter for <code>public.cl_code_config.dsl</code>. 语言类型
     */
    public IClCodeConfig setDsl(String value);

    /**
     * Getter for <code>public.cl_code_config.dsl</code>. 语言类型
     */
    public String getDsl();

    /**
     * Setter for <code>public.cl_code_config.content</code>. 代码内容
     */
    public IClCodeConfig setContent(String value);

    /**
     * Getter for <code>public.cl_code_config.content</code>. 代码内容
     */
    public String getContent();

    /**
     * Setter for <code>public.cl_code_config.status</code>. publish ,
     * normal，disable
     */
    public IClCodeConfig setStatus(String value);

    /**
     * Getter for <code>public.cl_code_config.status</code>. publish ,
     * normal，disable
     */
    public String getStatus();

    /**
     * Setter for <code>public.cl_code_config.is_delete</code>. 0 否 1 是
     */
    public IClCodeConfig setIsDelete(Short value);

    /**
     * Getter for <code>public.cl_code_config.is_delete</code>. 0 否 1 是
     */
    public Short getIsDelete();

    /**
     * Setter for <code>public.cl_code_config.tenant_id</code>. 租户id
     */
    public IClCodeConfig setTenantId(Long value);

    /**
     * Getter for <code>public.cl_code_config.tenant_id</code>. 租户id
     */
    public Long getTenantId();

    /**
     * Setter for <code>public.cl_code_config.code_name</code>.
     */
    public IClCodeConfig setCodeName(String value);

    /**
     * Getter for <code>public.cl_code_config.code_name</code>.
     */
    public String getCodeName();

    /**
     * Setter for <code>public.cl_code_config.desc</code>.
     */
    public IClCodeConfig setDesc(String value);

    /**
     * Getter for <code>public.cl_code_config.desc</code>.
     */
    public String getDesc();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IClCodeConfig
     */
    public void from(IClCodeConfig from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IClCodeConfig
     */
    public <E extends IClCodeConfig> E into(E into);

        @Override
        public default IClCodeConfig fromJson(io.vertx.core.json.JsonObject json) {
                setOrThrow(this::setId,json::getLong,"id","java.lang.Long");
                setOrThrow(this::setGmtCreate,key -> {String s = json.getString(key); return s==null?null:java.time.LocalDateTime.parse(s);},"gmt_create","java.time.LocalDateTime");
                setOrThrow(this::setGmtModified,key -> {String s = json.getString(key); return s==null?null:java.time.LocalDateTime.parse(s);},"gmt_modified","java.time.LocalDateTime");
                setOrThrow(this::setCreator,json::getString,"creator","java.lang.String");
                setOrThrow(this::setEditor,json::getString,"editor","java.lang.String");
                setOrThrow(this::setDsl,json::getString,"dsl","java.lang.String");
                setOrThrow(this::setContent,json::getString,"content","java.lang.String");
                setOrThrow(this::setStatus,json::getString,"status","java.lang.String");
                setOrThrow(this::setIsDelete,key -> {Integer i = json.getInteger(key); return i==null?null:i.shortValue();},"is_delete","java.lang.Short");
                setOrThrow(this::setTenantId,json::getLong,"tenant_id","java.lang.Long");
                setOrThrow(this::setCodeName,json::getString,"code_name","java.lang.String");
                setOrThrow(this::setDesc,json::getString,"desc","java.lang.String");
                return this;
        }


        @Override
        public default io.vertx.core.json.JsonObject toJson() {
                io.vertx.core.json.JsonObject json = new io.vertx.core.json.JsonObject();
                json.put("id",getId());
                json.put("gmt_create",getGmtCreate()==null?null:getGmtCreate().toString());
                json.put("gmt_modified",getGmtModified()==null?null:getGmtModified().toString());
                json.put("creator",getCreator());
                json.put("editor",getEditor());
                json.put("dsl",getDsl());
                json.put("content",getContent());
                json.put("status",getStatus());
                json.put("is_delete",getIsDelete());
                json.put("tenant_id",getTenantId());
                json.put("code_name",getCodeName());
                json.put("desc",getDesc());
                return json;
        }

}
