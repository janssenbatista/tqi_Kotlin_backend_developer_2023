CREATE TABLE IF NOT EXISTS tb_shopping_carts (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tb_users (id)
);