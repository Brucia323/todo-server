-- Table: public.users

-- DROP TABLE IF EXISTS public.users;

CREATE TABLE IF NOT EXISTS public.users
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    name "char" NOT NULL,
    email "char" NOT NULL,
    password_hash "char" NOT NULL,
    time_per_week "char",
    create_time timestamp without time zone NOT NULL,
    update_time timestamp without time zone NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY (id),
    CONSTRAINT email UNIQUE (email)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;