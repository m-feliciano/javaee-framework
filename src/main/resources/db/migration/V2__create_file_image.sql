CREATE TABLE tb_file_image
(
    id         CHAR(36) PRIMARY KEY,
    file_name  VARCHAR(100),
    file_type  VARCHAR(50),
    uri        TEXT,
    status     CHAR(1)   default 'A',
    product_id CHAR(36),
    user_id    CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES tb_product (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES tb_user (id)
            ON DELETE SET NULL
);

CREATE INDEX idx_file_image_file_name ON tb_file_image (file_name);
CREATE INDEX idx_file_image_product_id ON tb_file_image (product_id);
CREATE INDEX idx_file_image_product_id_status ON tb_file_image (product_id, status);
CREATE INDEX idx_file_image_file_name_status ON tb_file_image (file_name, status);

ALTER TABLE tb_user
    DROP COLUMN image_url;
ALTER TABLE tb_product
    DROP COLUMN url_img;

