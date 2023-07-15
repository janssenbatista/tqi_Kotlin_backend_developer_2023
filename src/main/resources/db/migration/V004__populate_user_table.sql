-- CREATE ADMIN USER WITH EMAIL admin@jumarket.com.br AND PASSWORD admin123
INSERT INTO tb_users(
    id,
    name,
    email,
    password,
    role_id,
    created_at,
    updated_at)
VALUES (
    gen_random_uuid(),
    'Admin',
    'admin@jumarket.com.br',
    '$2a$12$cWDf/QgLzaqO8qnj3RyYCOC7CjKlmnJZO932SoK9KEMc8s.a0dPOu',
    1,
    now(),
    now()
);

-- CREATE EMPLOYEE USER WITH EMAIL employee@jumarket.com.br AND PASSWORD employee123
INSERT INTO tb_users(
    id,
    name,
    email,
    password,
    role_id,
    created_at,
    updated_at)
VALUES (
    gen_random_uuid(),
    'Employee',
    'employee@jumarket.com.br',
    '$2a$12$/1ioAqp3fThE8uJ./cYxv.7.WM.tG1o82EP7QbnkYW668HQb4qOwC',
    2,
    now(),
    now()
);