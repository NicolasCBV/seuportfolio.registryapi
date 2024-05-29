CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
SET TIMEZONE="UTC";

CREATE TABLE IF NOT EXISTS public.users (
	id              UUID                            DEFAULT uuid_generate_v4()  NOT NULL,
    full_name       VARCHAR(255)                    NOT NULL,
    description     VARCHAR(120)                    NULL,
    email           VARCHAR(320)                    NOT NULL,
    password        CHAR(60)                        NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
  
    CONSTRAINT PK_users_id
    PRIMARY KEY(id)
);

ALTER TABLE public.users
ADD CONSTRAINT UQ_users_email
UNIQUE(email);

