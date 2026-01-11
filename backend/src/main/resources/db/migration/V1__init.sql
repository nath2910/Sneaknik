--
-- PostgreSQL database dump
--

\restrict xU2pfVWuNVwJvpxc4N2NVHFU9M7nuvu0PNHhJyOXgDWEDVYEcLxMIi107aX7tOy

-- Dumped from database version 18.1 (Debian 18.1-1.pgdg12+2)
-- Dumped by pg_dump version 18.1 (Debian 18.1-1.pgdg13+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: tableauventes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tableauventes (
    nom_item character varying(255),
    prix_retail numeric(38,2),
    prix_resell numeric(38,2),
    id integer NOT NULL,
    date_achat date,
    user_id bigint,
    description character varying(255),
    categorie character varying(255),
    date_vente date,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


--
-- Name: tableauventes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tableauventes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: tableauventes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tableauventes_id_seq OWNED BY public.tableauventes.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(200) NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    password character varying(200),
    provider character varying(20) DEFAULT 'LOCAL'::character varying,
    provider_id character varying(255),
    email_verified boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    picture_url text
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: tableauventes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tableauventes ALTER COLUMN id SET DEFAULT nextval('public.tableauventes_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: tableauventes tableauventes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tableauventes
    ADD CONSTRAINT tableauventes_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_email_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_unique UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_users_provider_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_provider_id ON public.users USING btree (provider, provider_id);


--
-- Name: idx_users_provider_provider_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_users_provider_provider_id ON public.users USING btree (provider, provider_id);


--
-- Name: tableauventes fk_tableauventes_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tableauventes
    ADD CONSTRAINT fk_tableauventes_user FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

\unrestrict xU2pfVWuNVwJvpxc4N2NVHFU9M7nuvu0PNHhJyOXgDWEDVYEcLxMIi107aX7tOy

