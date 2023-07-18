CREATE TABLE IF NOT EXISTS tb_products (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    measurement_unit VARCHAR(5) NOT NULL,
    unit_price DECIMAL(10,4) NOT NULL CHECK (unit_price > 0.0),
    quantity_in_stock INT NOT NULL CHECK (quantity_in_stock >= 0),
    category_id SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_cat_id FOREIGN KEY(category_id) REFERENCES tb_categories(id)
);