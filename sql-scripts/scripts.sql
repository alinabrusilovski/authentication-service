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

ALTER TABLE "users"
ALTER COLUMN created TYPE TIMESTAMP WITH TIME ZONE USING created AT TIME ZONE 'UTC';

ALTER TABLE "users"
ALTER COLUMN updated TYPE TIMESTAMP WITH TIME ZONE USING updated AT TIME ZONE 'UTC';

ALTER TABLE "users"
ALTER COLUMN created SET NOT NULL;

ALTER TABLE "users"
ALTER COLUMN updated SET NOT NULL;

ALTER TABLE "users"
ALTER COLUMN deleted SET NOT NULL;

ALTER TABLE "users"
ALTER COLUMN created SET DEFAULT NOW();

ALTER TABLE "users"
ALTER COLUMN updated SET DEFAULT NOW();

ALTER TABLE "users"
ALTER COLUMN deleted SET DEFAULT FALSE;

//Создание функции для обновления updated
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

//Создание триггера на обновление
CREATE TRIGGER set_updated
BEFORE UPDATE ON "users"
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

ALTER TABLE scope
ALTER COLUMN description TYPE VARCHAR(255);

ALTER TABLE user_scope DROP CONSTRAINT user_scope_user_id_fkey;
ALTER TABLE user_scope DROP CONSTRAINT user_scope_scope_id_fkey;

ALTER TABLE user_scope
ADD CONSTRAINT user_scope_user_id_fkey FOREIGN KEY (user_id) REFERENCES "users"(id);

ALTER TABLE user_scope
ADD CONSTRAINT user_scope_scope_id_fkey FOREIGN KEY (scope_id) REFERENCES scope(id);

ALTER TABLE users
ADD COLUMN refresh_token VARCHAR(1000) UNIQUE,
ADD COLUMN refresh_token_expired TIMESTAMP;
CREATE INDEX idx_refresh_token ON users(refresh_token);
