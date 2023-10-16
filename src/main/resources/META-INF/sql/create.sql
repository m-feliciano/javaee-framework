
    create table tb_category (
       id  bigserial not null,
        name varchar(255),
        status varchar(1),
        primary key (id)
    );

    create table tb_inventory (
       id  bigserial not null,
        description varchar(255),
        quantity int4,
        status varchar(1),
        product int8,
        primary key (id)
    );


    create table tb_product (
       id  bigserial not null,
        description TEXT,
        name varchar(100),
        price numeric(19, 2),
        created_at date,
        status varchar(1),
        url_img TEXT,
        category_id int8,
        user_id int8,
        primary key (id)
    );


    create table tb_user (
       id  bigserial not null,
        image_url TEXT,
        login varchar(255),
        password varchar(255),
        status varchar(1),
        primary key (id)
    );


    create table user_perfis (
       user_id int8 not null,
       perfis int4
    );

    alter table tb_user
       add constraint pk_tb_user_id unique (login);


    alter table tb_inventory
       add constraint fk_tb_inventory_product
       foreign key (product)
       references tb_product;


    alter table tb_product
       add constraint fk_tb_product_tb_category
       foreign key (category_id)
       references tb_category;

    alter table tb_product
       add constraint fk_tb_product_tb_user
       foreign key (user_id)
       references tb_user;


    alter table user_perfis
       add constraint fk_tb_user_perfis
       foreign key (user_id)
        references tb_user;