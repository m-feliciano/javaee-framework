-- =====================================================
-- DROP FOREIGN KEYS
-- =====================================================

ALTER TABLE tb_category DROP CONSTRAINT IF EXISTS tb_category_user_id_fkey;

ALTER TABLE tb_product DROP CONSTRAINT IF EXISTS tb_product_user_id_fkey;
ALTER TABLE tb_product DROP CONSTRAINT IF EXISTS tb_product_category_id_fkey;

ALTER TABLE tb_inventory DROP CONSTRAINT IF EXISTS tb_inventory_user_id_fkey;
ALTER TABLE tb_inventory DROP CONSTRAINT IF EXISTS tb_inventory_product_id_fkey;

ALTER TABLE user_perfis DROP CONSTRAINT IF EXISTS user_perfis_user_id_fkey;
ALTER TABLE user_perfis DROP CONSTRAINT IF EXISTS user_perfis_perfil_id_fkey;

ALTER TABLE tb_refresh_token DROP CONSTRAINT IF EXISTS tb_refresh_token_user_id_fkey;
ALTER TABLE tb_refresh_token DROP CONSTRAINT IF EXISTS tb_refresh_token_replaced_by_fkey;

ALTER TABLE tb_user_activity_log DROP CONSTRAINT IF EXISTS fk_activity_log_user;

ALTER TABLE tb_file_image DROP CONSTRAINT IF EXISTS fk_product;
ALTER TABLE tb_file_image DROP CONSTRAINT IF EXISTS fk_user;

-- =====================================================
-- ALTER PRIMARY KEYS
-- =====================================================

ALTER TABLE tb_user ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_category ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_product ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_inventory ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_refresh_token ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_user_activity_log ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_file_image ALTER COLUMN id TYPE uuid USING id::uuid;
ALTER TABLE tb_confirmation_token ALTER COLUMN id TYPE uuid USING id::uuid;

-- =====================================================
-- ALTER FK COLUMNS
-- =====================================================

ALTER TABLE tb_category ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

ALTER TABLE tb_product ALTER COLUMN user_id TYPE uuid USING user_id::uuid;
ALTER TABLE tb_product ALTER COLUMN category_id TYPE uuid USING category_id::uuid;

ALTER TABLE tb_inventory ALTER COLUMN user_id TYPE uuid USING user_id::uuid;
ALTER TABLE tb_inventory ALTER COLUMN product_id TYPE uuid USING product_id::uuid;

ALTER TABLE user_perfis ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

ALTER TABLE tb_refresh_token ALTER COLUMN user_id TYPE uuid USING user_id::uuid;
ALTER TABLE tb_refresh_token ALTER COLUMN replaced_by TYPE uuid USING replaced_by::uuid;

ALTER TABLE tb_confirmation_token ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

ALTER TABLE tb_user_activity_log ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

ALTER TABLE tb_file_image ALTER COLUMN product_id TYPE uuid USING product_id::uuid;
ALTER TABLE tb_file_image ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

-- =====================================================
-- RECREATE FOREIGN KEYS
-- =====================================================

ALTER TABLE tb_category
    ADD CONSTRAINT tb_category_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE tb_product
    ADD CONSTRAINT tb_product_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE tb_product
    ADD CONSTRAINT tb_product_category_id_fkey
        FOREIGN KEY (category_id) REFERENCES tb_category (id);

ALTER TABLE tb_inventory
    ADD CONSTRAINT tb_inventory_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE tb_inventory
    ADD CONSTRAINT tb_inventory_product_id_fkey
        FOREIGN KEY (product_id) REFERENCES tb_product (id);

ALTER TABLE user_perfis
    ADD CONSTRAINT user_perfis_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE user_perfis
    ADD CONSTRAINT user_perfis_perfil_id_fkey
        FOREIGN KEY (perfil_id) REFERENCES tb_perfil (id);

ALTER TABLE tb_refresh_token
    ADD CONSTRAINT tb_refresh_token_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE tb_refresh_token
    ADD CONSTRAINT tb_refresh_token_replaced_by_fkey
        FOREIGN KEY (replaced_by) REFERENCES tb_refresh_token (id);

ALTER TABLE tb_user_activity_log
    ADD CONSTRAINT fk_activity_log_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id);

ALTER TABLE tb_file_image
    ADD CONSTRAINT fk_product
        FOREIGN KEY (product_id) REFERENCES tb_product (id)
            ON DELETE CASCADE;

ALTER TABLE tb_file_image
    ADD CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id)
            ON DELETE SET NULL;

-- =====================================================
-- DEFAULT UUID v7
-- =====================================================

ALTER TABLE tb_user ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_category ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_product ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_inventory ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_file_image ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_refresh_token ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_user_activity_log ALTER COLUMN id SET DEFAULT uuidv7();
ALTER TABLE tb_confirmation_token ALTER COLUMN id SET DEFAULT uuidv7();

-- =====================================================
-- FINAL
-- =====================================================

ANALYZE;
