GRANT ALL PRIVILEGES ON DATABASE auth_service TO auth_service;

CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    second_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created DATE,
    updated DATE,
    deleted BOOLEAN
);

ALTER TABLE public.user
    OWNER to auth_service;

CREATE TABLE scope (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

ALTER TABLE public.scope
    OWNER to auth_service;

CREATE TABLE user_scope (
    user_id INT NOT NULL,
    scope_id INT NOT NULL,
    PRIMARY KEY (user_id, scope_id),
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (scope_id) REFERENCES scope(id) ON DELETE CASCADE
);

ALTER TABLE public.user_scope
    OWNER to auth_service;

SELECT setval('user_id_seq', (SELECT MAX(id) FROM "user"));

SELECT setval('scope_id_seq', (SELECT MAX(id) FROM "scope"));

CREATE UNIQUE INDEX idx_user_email_unique ON "user" (email);