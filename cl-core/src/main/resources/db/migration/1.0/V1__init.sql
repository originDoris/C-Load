create table if not exists public.cl_code_config
(
    id             bigint                                          not null
    constraint cl_code_config_pk
    primary key,
    gmt_create     timestamp                                       not null,
    gmt_modified   timestamp,
    creator        varchar(50),
    editor         varchar(50),
    dsl            varchar(50)                                     not null,
    content        text                                            not null,
    status         varchar(20) default 'normal'::character varying not null,
    is_delete      smallint    default 0                           not null,
    tenant_id      bigint,
    code_name      varchar(50),
    "desc"         varchar(255),
    server_address varchar(50)                                     not null,
    method_name    varchar(50)                                     not null
    );

comment on table public.cl_code_config is '代码配置';

comment on column public.cl_code_config.gmt_create is '创建时间';

comment on column public.cl_code_config.gmt_modified is '修改时间';

comment on column public.cl_code_config.creator is '创建人';

comment on column public.cl_code_config.editor is '创建人';

comment on column public.cl_code_config.dsl is '语言类型';

comment on column public.cl_code_config.content is '代码内容';

comment on column public.cl_code_config.status is 'publish , normal，disable';

comment on column public.cl_code_config.is_delete is '0 否 1 是';

comment on column public.cl_code_config.tenant_id is '租户id';

comment on column public.cl_code_config.server_address is '服务地址 唯一';

comment on column public.cl_code_config.method_name is '方法名';

alter table public.cl_code_config
    owner to postgres;

create index if not exists cl_code_config_dsl_index
    on public.cl_code_config (dsl);

create index if not exists cl_code_config_status_index
    on public.cl_code_config (status);

create unique index if not exists cl_code_config_server_address_tenant_id_uindex
    on public.cl_code_config (server_address, tenant_id);

