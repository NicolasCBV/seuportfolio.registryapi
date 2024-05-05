CREATE TABLE IF NOT EXISTS public.users (
	id UUID NOT NULL,
    full_name varchar(255) NOT NULL,
    description varchar(120) NULL,
    email varchar(320) NOT NULL,
    password char(60) NOT NULL,
    created_at date NOT NULL,
    updated_at date NOT NULL,
  
    CONSTRAINT id
    PRIMARY KEY(id)
);

ALTER TABLE public.users
ADD CONSTRAINT "UQ_users_email"
UNIQUE(email);

