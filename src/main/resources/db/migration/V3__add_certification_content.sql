CREATE TABLE IF NOT EXISTS public.certifications (
    id                                  UUID     DEFAULT     uuid_generate_v4()     NOT NULL,
    code                                VARCHAR(64)                                 NULL,
    link                                VARCHAR(255)                                NULL,
    image_url                           VARCHAR(255)                                NULL,
    issued_at                           TIMESTAMP   WITH TIME ZONE                  NOT NULL,
    base_content_id                     UUID                                        NOT NULL,

    CONSTRAINT PK_certifications_id
    PRIMARY KEY(id)
);

ALTER TABLE public.certifications
ADD CONSTRAINT FK_certifications_base_content_id
FOREIGN KEY(base_content_id)
REFERENCES public.base_contents(id)
ON DELETE CASCADE;

ALTER TABLE public.certifications
ADD CONSTRAINT UQ_certifications_base_content_id
UNIQUE(base_content_id);
