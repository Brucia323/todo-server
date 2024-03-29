-- Table: public.todo

-- DROP TABLE IF EXISTS public.todo;

CREATE TABLE IF NOT EXISTS public.todo
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    user_id integer NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    begin_date date,
    planned_end_date date,
    actual_end_date date,
    current_amount integer,
    total_amount integer NOT NULL,
    description text COLLATE pg_catalog."default",
    create_time timestamp without time zone NOT NULL,
    update_time timestamp without time zone NOT NULL,
    CONSTRAINT todo_pk PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.todo
    OWNER to postgres;