-- Table: public.todos

-- DROP TABLE IF EXISTS public.todos;

CREATE TABLE IF NOT EXISTS public.todos
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    user_id integer NOT NULL,
    begin_time timestamp without time zone,
    planned_end_time timestamp without time zone,
    actual_end_time timestamp without time zone,
    current_amount integer,
    total_amount integer,
    description "char",
    create_time timestamp without time zone NOT NULL,
    update_time timestamp without time zone NOT NULL,
    name "char" NOT NULL,
    CONSTRAINT todo_pk PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.todos
    OWNER to postgres;