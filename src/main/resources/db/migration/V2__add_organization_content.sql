CREATE TABLE IF NOT EXISTS public.organizations (
    id                  UUID            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(64)     NOT NULL,
    description         VARCHAR(120)    NOT NULL,
    created_at          DATE            DEFAULT now()               NOT NULL,
    updated_at          DATE            DEFAULT now()               NOT NULL,
    user_id             UUID            NOT NULL,

    CONSTRAINT PK_organizations_id
    PRIMARY KEY(id)
);

ALTER TABLE public.organizations
ADD CONSTRAINT FK_organizations_user_id
FOREIGN KEY(user_id)
REFERENCES public.users(id)
ON DELETE CASCADE;

ALTER TABLE public.organizations
ADD CONSTRAINT UQ_organizations_name_user_id
UNIQUE(name, user_id);

CREATE TABLE IF NOT EXISTS public.organization_tags (
    id                  UUID            DEFAULT uuid_generate_v4()  NOT NULL,
    name                VARCHAR(60)     NOT NULL,
    created_at          DATE            DEFAULT now()               NOT NULL,
    organization_id     UUID            NOT NULL,

    CONSTRAINT PK_organization_tags_id
    PRIMARY KEY(id)
);

ALTER TABLE public.organization_tags
ADD CONSTRAINT FK_organization_tags_organization_id
FOREIGN KEY(organization_id)
REFERENCES public.organizations(id)
ON DELETE CASCADE;

ALTER TABLE public.organization_tags
ADD CONSTRAINT UQ_organization_tags_organization_id_name
UNIQUE(name, organization_id);
