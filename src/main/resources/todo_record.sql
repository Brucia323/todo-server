-- Table: public.todo_record

-- DROP TABLE IF EXISTS public.todo_record;

CREATE TABLE IF NOT EXISTS public.todo_record
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    user_id integer NOT NULL,
    todo_id integer NOT NULL,
    amount integer NOT NULL,
    create_time timestamp without time zone NOT NULL,
    update_time timestamp without time zone NOT NULL,
    CONSTRAINT todo_record_pk PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.todo_record
    OWNER to postgres;