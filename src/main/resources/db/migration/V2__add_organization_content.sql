CREATE TABLE IF NOT EXISTS public.base_contents (
    id                  UUID            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(64)     NOT NULL,
    description         VARCHAR(120)    NOT NULL,
    category            SMALLINT        NOT NULL,
    created_at          DATE            DEFAULT now()               NOT NULL,
    updated_at          DATE            DEFAULT now()               NOT NULL,
    user_id             UUID            NOT NULL,

    CONSTRAINT PK_organizations_id
    PRIMARY KEY(id)
);

ALTER TABLE public.base_contents
ADD CONSTRAINT FK_base_contents_user_id
FOREIGN KEY(user_id)
REFERENCES public.users(id)
ON DELETE CASCADE;

ALTER TABLE public.base_contents
ADD CONSTRAINT UQ_base_contents_name_user_id
UNIQUE(name, user_id);

CREATE TABLE IF NOT EXISTS public.tags (
    id                  UUID            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(60)     NOT NULL,
    created_at          DATE            DEFAULT now()               NOT NULL,
    base_content_id     UUID            NOT NULL,

    CONSTRAINT PK_tags_id
    PRIMARY KEY(id)
);

ALTER TABLE public.tags
ADD CONSTRAINT FK_tags_base_content_id
FOREIGN KEY(base_content_id)
REFERENCES public.base_contents(id)
ON DELETE CASCADE;

ALTER TABLE public.tags
ADD CONSTRAINT UQ_tags_name_base_content_id
UNIQUE(name, base_content_id);
