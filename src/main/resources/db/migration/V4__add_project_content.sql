CREATE TABLE IF NOT EXISTS public.projects (
    id                  UUID            DEFAULT uuid_generate_v4()      NOT NULL,
    image_url           VARCHAR(255)    NULL,
    state               SMALLINT        NOT NULL,
    base_content_id     UUID            NOT NULL,

    CONSTRAINT PK_projects_id
    PRIMARY KEY(id),
    CONSTRAINT CHECK_projects_state
    CHECK (state = 0 OR state = 1)
);

ALTER TABLE projects
ADD CONSTRAINT FK_projects_base_content_id
FOREIGN KEY (base_content_id)
REFERENCES public.base_contents(id)
ON DELETE CASCADE;
