CREATE TABLE IF NOT EXISTS public.packages (
    id                  UUID                            DEFAULT uuid_generate_v4()  NOT NULL,
    type                SMALLINT                        NOT NULL,
    root_id             UUID                            NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()   NOT NULL,

    CONSTRAINT PK_packages_id
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.base_contents (
    id                  UUID                            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(64)                     NOT NULL,
    description         VARCHAR(120)                    NOT NULL,
    category            SMALLINT                        NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE        DEFAULT now()               NOT NULL,
    user_id             UUID                            NOT NULL,
    package_id          UUID                            NULL,

    CONSTRAINT PK_base_contents_id
    PRIMARY KEY(id),
    CONSTRAINT CHECK_base_contents_package_id_and_category
    CHECK (
        (package_id IS NULL AND category = 0::SMALLINT) OR
        (package_id IS NOT NULL AND category != 0::SMALLINT)
    )
);

ALTER TABLE public.packages
ADD CONSTRAINT FK_packages_root_id
FOREIGN KEY (root_id)
REFERENCES public.base_contents(id)
ON DELETE CASCADE;

ALTER TABLE public.base_contents
ADD CONSTRAINT FK_base_contents_package_id
FOREIGN KEY (package_id)
REFERENCES public.packages(id)
ON DELETE CASCADE;

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
