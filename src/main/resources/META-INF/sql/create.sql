create table tb_user
(
    id        varchar(100),  -- UUID
    image_url TEXT,
    login     varchar(255) unique,
    password  varchar(255),
    status    varchar(1),
    config    TEXT
);

create table tb_category
(
    id      varchar(100),   -- UUID
    name    varchar(255),
    status  varchar(1),
    user_id varchar(100) not null    -- UUID FK
);

create table tb_product
(
    id            varchar(100),  -- UUID
    description   TEXT,
    name          varchar(100),
    price         numeric(19, 2),
    register_date date,
    status        varchar(1),
    url_img       TEXT,
    category_id   varchar(100),           -- UUID FK (nullable)
    user_id       varchar(100) not null   -- UUID FK
);

create table tb_inventory
(
    id          varchar(100),  -- UUID
    description TEXT,
    quantity    int4,
    status      varchar(1),
    product_id  varchar(100),           -- UUID FK
    user_id     varchar(100) not null   -- UUID FK
);

create table tb_perfil
(
    id   bigserial primary key,      -- Só este usa incremental
    name varchar(50) not null
);

create table user_perfis
(
    user_id   varchar(100) not null,  -- UUID FK
    perfil_id int8 not null           -- BIGINT FK
);

-- =====================================
-- PRIMARY KEYS
-- =====================================

alter table tb_user add constraint pk_tb_user_id primary key (id);
alter table tb_category add constraint pk_tb_category_id primary key (id);
alter table tb_product add constraint pk_tb_product_id primary key (id);
alter table tb_inventory add constraint pk_tb_inventory_id primary key (id);

-- =====================================
-- FOREIGN KEYS (CORRIGIDAS)
-- =====================================

alter table tb_category
    add constraint fk_tb_category_tb_user
        foreign key (user_id) references tb_user(id);

alter table tb_product
    add constraint fk_tb_product_tb_category
        foreign key (category_id) references tb_category(id);

alter table tb_product
    add constraint fk_tb_product_tb_user
        foreign key (user_id) references tb_user(id);

alter table tb_inventory
    add constraint fk_tb_inventory_tb_product
        foreign key (product_id) references tb_product(id);

alter table tb_inventory
    add constraint fk_tb_inventory_tb_user
        foreign key (user_id) references tb_user(id);

alter table user_perfis
    add constraint fk_tb_user_perfis_tb_user
        foreign key (user_id) references tb_user(id);

alter table user_perfis
    add constraint fk_tb_user_perfis_tb_perfil
        foreign key (perfil_id) references tb_perfil(id);

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
CREATE INDEX idx_user_login ON tb_user(login);
CREATE INDEX idx_user_status ON tb_user(status);

-- USER_PERFIS
CREATE INDEX idx_user_perfis_user_id ON user_perfis(user_id);
CREATE INDEX idx_user_perfis_perfil_id ON user_perfis(perfil_id);
CREATE INDEX idx_user_perfis_composite ON user_perfis(user_id, perfil_id);

-- Atualizar estatísticas
ANALYZE tb_product;
ANALYZE tb_category;
ANALYZE tb_inventory;
ANALYZE tb_user;
ANALYZE user_perfis;
ANALYZE tb_perfil;