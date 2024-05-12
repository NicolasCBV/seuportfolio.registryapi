CREATE TABLE IF NOT EXISTS public.base_contents (
    id                  UUID                            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(64)                     NOT NULL,
    description         VARCHAR(120)                    NOT NULL,
    category            SMALLINT                        NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    user_id             UUID                            NOT NULL,

    CONSTRAINT PK_base_contents_id
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
    id                  UUID                            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(60)                     NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    base_content_id     UUID                            NOT NULL,

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

CREATE TABLE IF NOT EXISTS public.organization_aditional_infos (
    id                  UUID            DEFAULT uuid_generate_v4()      NOT NULL,
    site_url            VARCHAR(255)    NULL,
    base_content_id     UUID            NOT NULL,

    CONSTRAINT PK_organization_aditional_infos_id
    PRIMARY KEY(id)
);

ALTER TABLE public.organization_aditional_infos
ADD CONSTRAINT FK_organization_aditional_infos_id_base_content_id
FOREIGN KEY(base_content_id)
REFERENCES public.base_contents(id)
ON DELETE CASCADE;
