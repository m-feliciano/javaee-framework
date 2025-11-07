create table tb_user
(
    id         varchar(36)                               not null
        primary key,
    login      varchar(255)                              not null
        unique,
    password   varchar(255)                              not null,
    status     varchar(1) default 'A'::character varying not null
        constraint tb_user_status_check
            check ((status)::text = ANY
                   ((ARRAY ['A'::character varying, 'I'::character varying, 'X'::character varying])::text[])),
    image_url  text,
    config     text,
    created_at timestamp  default CURRENT_TIMESTAMP      not null,
    updated_at timestamp  default CURRENT_TIMESTAMP      not null
);

alter table tb_user
    owner to postgres;

create index idx_user_login
    on tb_user (login);

create index idx_user_status
    on tb_user (status);

create index idx_user_created_at
    on tb_user (created_at);

create table tb_category
(
    id         varchar(36)                               not null
        primary key,
    name       varchar(255)                              not null,
    status     varchar(1) default 'A'::character varying not null
        constraint tb_category_status_check
            check ((status)::text = ANY
                   ((ARRAY ['A'::character varying, 'I'::character varying, 'X'::character varying])::text[])),
    user_id    varchar(36)                               not null
        references tb_user,
    created_at timestamp  default CURRENT_TIMESTAMP      not null,
    updated_at timestamp  default CURRENT_TIMESTAMP      not null
);

alter table tb_category
    owner to postgres;

create index idx_category_user_id
    on tb_category (user_id);

create index idx_category_status
    on tb_category (status);

create index idx_category_name_user
    on tb_category (name, user_id);

create index idx_category_created_at
    on tb_category (created_at);

create table tb_product
(
    id            varchar(36)                               not null
        primary key,
    name          varchar(100)                              not null,
    description   text,
    url_img       text,
    register_date date       default CURRENT_DATE           not null,
    price         numeric(19, 2)                            not null
        constraint tb_product_price_check
            check (price >= (0)::numeric),
    status        varchar(1) default 'A'::character varying not null
        constraint tb_product_status_check
            check ((status)::text = ANY
                   ((ARRAY ['A'::character varying, 'I'::character varying, 'X'::character varying])::text[])),
    user_id       varchar(36)                               not null
        references tb_user,
    category_id   varchar(36)
        references tb_category,
    created_at    timestamp  default CURRENT_TIMESTAMP      not null,
    updated_at    timestamp  default CURRENT_TIMESTAMP      not null
);

alter table tb_product
    owner to postgres;

create index idx_product_user_id
    on tb_product (user_id);

create index idx_product_category_id
    on tb_product (category_id);

create index idx_product_status
    on tb_product (status);

create index idx_product_register_date
    on tb_product (register_date);

create index idx_product_price
    on tb_product (price);

create index idx_product_name
    on tb_product (name);

create index idx_product_composite_user_status
    on tb_product (user_id, status);

create index idx_product_composite_category_status
    on tb_product (category_id, status);

create index idx_product_created_at
    on tb_product (created_at);

create table tb_inventory
(
    id          varchar(36)                               not null
        primary key,
    quantity    integer                                   not null
        constraint tb_inventory_quantity_check
            check (quantity >= 0),
    description text,
    status      varchar(1) default 'A'::character varying not null
        constraint tb_inventory_status_check
            check ((status)::text = ANY
                   ((ARRAY ['A'::character varying, 'I'::character varying, 'X'::character varying])::text[])),
    user_id     varchar(36)                               not null
        references tb_user,
    product_id  varchar(36)
        references tb_product,
    created_at  timestamp  default CURRENT_TIMESTAMP      not null,
    updated_at  timestamp  default CURRENT_TIMESTAMP      not null
);

alter table tb_inventory
    owner to postgres;

create index idx_inventory_user_id
    on tb_inventory (user_id);

create index idx_inventory_product_id
    on tb_inventory (product_id);

create index idx_inventory_status
    on tb_inventory (status);

create index idx_inventory_quantity
    on tb_inventory (quantity);

create index idx_inventory_composite_user_product
    on tb_inventory (user_id, product_id);

create index idx_inventory_created_at
    on tb_inventory (created_at);

create table tb_perfil
(
    id   integer
        primary key,
    name varchar(50) not null
        unique
);

alter table tb_perfil
    owner to postgres;

create table user_perfis
(
    user_id   varchar(36) not null
        references tb_user,
    perfil_id integer     not null
        references tb_perfil,
    primary key (user_id, perfil_id)
);

alter table user_perfis
    owner to postgres;

create index idx_user_perfis_user_id
    on user_perfis (user_id);

create index idx_user_perfis_perfil_id
    on user_perfis (perfil_id);

create function update_updated_at_column() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

alter function update_updated_at_column() owner to postgres;

create trigger update_tb_user_updated_at
    before update
    on tb_user
    for each row
execute procedure update_updated_at_column();

create trigger update_tb_category_updated_at
    before update
    on tb_category
    for each row
execute procedure update_updated_at_column();

create trigger update_tb_product_updated_at
    before update
    on tb_product
    for each row
execute procedure update_updated_at_column();

create trigger update_tb_inventory_updated_at
    before update
    on tb_inventory
    for each row
execute procedure update_updated_at_column();

create table tb_user_activity_log
(
    id                 varchar(36)  not null
        primary key,
    user_id            varchar(36)  not null,
    action             varchar(100) not null,
    entity_type        varchar(50),
    entity_id          varchar(36),
    status             varchar(20)  not null,
    request_payload    text,
    response_payload   text,
    error_message      text,
    http_status_code   integer,
    http_method        varchar(10),
    endpoint           varchar(255),
    ip_address         varchar(45),
    correlation_id     varchar(50),
    execution_time_ms  bigint,
    timestamp          timestamp default CURRENT_TIMESTAMP not null,
    user_agent         varchar(500),
    constraint fk_activity_log_user
        foreign key (user_id) references tb_user (id)
);

alter table tb_user_activity_log owner to postgres;

create index idx_activity_log_user_id on tb_user_activity_log (user_id);
create index idx_activity_log_timestamp on tb_user_activity_log (timestamp desc);
create index idx_activity_log_action on tb_user_activity_log (action);
create index idx_activity_log_status on tb_user_activity_log (status);
create index idx_activity_log_correlation_id on tb_user_activity_log (correlation_id);

-- =====================================
-- DADOS INICIAIS
-- =====================================

insert into tb_perfil (id, name) values (1, 'ADMIN');
insert into tb_perfil (id, name) values (2, 'USER');
insert into tb_perfil (id, name) values (3, 'MANAGER');
insert into tb_perfil (id, name) values (4, 'GUEST');

-- =====================================
-- ÍNDICES OTIMIZADOS (UUID-compatíveis)
-- =====================================

-- TB_PRODUCT
CREATE INDEX idx_product_user_status ON tb_product(user_id, status);
CREATE INDEX idx_product_category_status ON tb_product(category_id, status);
CREATE INDEX idx_product_name_status ON tb_product(name, status);
CREATE INDEX idx_product_description_status ON tb_product(description, status);
CREATE INDEX idx_product_composite ON tb_product(user_id, category_id, status);

-- TB_CATEGORY
CREATE INDEX idx_category_user_status ON tb_category(user_id, status);
CREATE INDEX idx_category_name_user_status ON tb_category(name, user_id, status);

-- TB_INVENTORY
CREATE INDEX idx_inventory_user_status ON tb_inventory(user_id, status);
CREATE INDEX idx_inventory_product_user_status ON tb_inventory(product_id, user_id, status);
CREATE INDEX idx_inventory_description_status ON tb_inventory(description, status);

-- TB_USER
CREATE INDEX idx_user_auth ON tb_user(login, password, status);
CREATE INDEX idx_user_perfis_composite ON user_perfis(user_id, perfil_id);

-- Atualizar estatísticas
ANALYZE tb_product;
ANALYZE tb_category;
ANALYZE tb_inventory;
ANALYZE tb_user;
ANALYZE user_perfis;
ANALYZE tb_perfil;
