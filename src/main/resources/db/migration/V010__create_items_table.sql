CREATE TABLE IF NOT EXISTS tb_items (
    id UUID NOT NULL PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity SMALLINT NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT unq_cart_id_product_id UNIQUE(shopping_cart_id, product_id),
    CONSTRAINT fk_shopping_cart_id FOREIGN KEY (shopping_cart_id) REFERENCES tb_shopping_carts (id),
    CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES tb_products (id)
);