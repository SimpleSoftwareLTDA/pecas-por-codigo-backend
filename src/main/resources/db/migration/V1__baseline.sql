--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

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

DROP DATABASE IF EXISTS "novo-pecas-online";
--
-- Name: novo-pecas-online; Type: DATABASE; Schema: -; Owner: pecas
--

CREATE DATABASE "novo-pecas-online" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


ALTER DATABASE "novo-pecas-online" OWNER TO pecas;

\connect -reuse-previous=on "dbname='novo-pecas-online'"

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
-- Name: pg_stat_statements; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_stat_statements WITH SCHEMA public;


--
-- Name: EXTENSION pg_stat_statements; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_stat_statements IS 'track planning and execution statistics of all SQL statements executed';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.address (
    id integer NOT NULL,
    street character varying(255) NOT NULL,
    city character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    country character varying(255) NOT NULL,
    state_id integer NOT NULL
);


ALTER TABLE public.address OWNER TO neondb_owner;

--
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.address_id_seq OWNER TO neondb_owner;

--
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.address_id_seq OWNED BY public.address.id;


--
-- Name: authorities; Type: TABLE; Schema: public; Owner: pecas
--

CREATE TABLE public.authorities (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    authority character varying(50) NOT NULL
);


ALTER TABLE public.authorities OWNER TO pecas;

--
-- Name: authorities_id_seq; Type: SEQUENCE; Schema: public; Owner: pecas
--

CREATE SEQUENCE public.authorities_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.authorities_id_seq OWNER TO pecas;

--
-- Name: brand; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.brand (
    id integer NOT NULL,
    brand_name character varying(255) NOT NULL
);


ALTER TABLE public.brand OWNER TO neondb_owner;

--
-- Name: brand_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.brand_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.brand_id_seq OWNER TO neondb_owner;

--
-- Name: brand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.brand_id_seq OWNED BY public.brand.id;


--
-- Name: brazilian_state; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.brazilian_state (
    id integer NOT NULL,
    state_code character varying(255) NOT NULL,
    state_name character varying(255) NOT NULL
);


ALTER TABLE public.brazilian_state OWNER TO neondb_owner;

--
-- Name: brazilian_state_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.brazilian_state_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.brazilian_state_id_seq OWNER TO neondb_owner;

--
-- Name: brazilian_state_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.brazilian_state_id_seq OWNED BY public.brazilian_state.id;


--
-- Name: category; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.category (
    category_id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.category OWNER TO neondb_owner;

--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.category_category_id_seq OWNER TO neondb_owner;

--
-- Name: category_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.category_category_id_seq OWNED BY public.category.category_id;


--
-- Name: contact; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.contact (
    id integer NOT NULL,
    items_email character varying(255) NOT NULL,
    items_phone character varying(255) NOT NULL,
    stock_email character varying(255),
    billing_email character varying(255),
    nf_email character varying(255),
    seller_name character varying(255),
    site character varying(255),
    whatsapp character varying(255),
    items_whatsapp character varying(255)
);


ALTER TABLE public.contact OWNER TO neondb_owner;

--
-- Name: contact_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.contact_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.contact_id_seq OWNER TO neondb_owner;

--
-- Name: contact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.contact_id_seq OWNED BY public.contact.id;


--
-- Name: description; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.description (
    id integer NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.description OWNER TO neondb_owner;

--
-- Name: description_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.description_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.description_id_seq OWNER TO neondb_owner;

--
-- Name: description_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.description_id_seq OWNED BY public.description.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: pecas
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO pecas;

--
-- Name: item; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.item (
    id integer NOT NULL,
    manufacturer character varying(255),
    code character varying(255) NOT NULL,
    price_in_cents bigint,
    description character varying(255),
    update_date timestamp(6) without time zone NOT NULL,
    hash character varying(255),
    category_id integer
);


ALTER TABLE public.item OWNER TO neondb_owner;

--
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.item_id_seq OWNER TO neondb_owner;

--
-- Name: item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.item_id_seq OWNED BY public.item.id;


--
-- Name: plan; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.plan (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    price_in_cents bigint NOT NULL,
    stock boolean DEFAULT false,
    quote boolean DEFAULT false,
    small_banner boolean DEFAULT false,
    big_banner boolean DEFAULT false
);


ALTER TABLE public.plan OWNER TO neondb_owner;

--
-- Name: plan_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.plan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.plan_id_seq OWNER TO neondb_owner;

--
-- Name: plan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.plan_id_seq OWNED BY public.plan.id;


--
-- Name: signature; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.signature (
    id bigint NOT NULL,
    payment_day integer NOT NULL,
    supplier_id integer NOT NULL,
    plan_id integer NOT NULL,
    big_banner_url character varying(255),
    small_banner_url character varying(255),
    status character varying(255) DEFAULT 'ACTIVE'::character varying NOT NULL
);


ALTER TABLE public.signature OWNER TO neondb_owner;

--
-- Name: signature_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.signature_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.signature_id_seq OWNER TO neondb_owner;

--
-- Name: signature_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.signature_id_seq OWNED BY public.signature.id;


--
-- Name: stock; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.stock (
    id bigint NOT NULL,
    quantity integer NOT NULL,
    supplier_id integer NOT NULL,
    piece_id integer NOT NULL
);


ALTER TABLE public.stock OWNER TO neondb_owner;

--
-- Name: stock_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.stock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.stock_id_seq OWNER TO neondb_owner;

--
-- Name: stock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.stock_id_seq OWNED BY public.stock.id;


--
-- Name: supplier; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.supplier (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    social_name character varying(255) NOT NULL,
    supplier_original_link character varying(255),
    description_id integer,
    brand_id integer,
    cnpj character varying(255) NOT NULL,
    state_subscription character varying(255),
    address_id integer NOT NULL,
    contact_id integer NOT NULL,
    asaas_id character varying(255),
    token_id bigint
);


ALTER TABLE public.supplier OWNER TO neondb_owner;

--
-- Name: supplier_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.supplier_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.supplier_id_seq OWNER TO neondb_owner;

--
-- Name: supplier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.supplier_id_seq OWNED BY public.supplier.id;


--
-- Name: tokens; Type: TABLE; Schema: public; Owner: pecas
--

CREATE TABLE public.tokens (
    id bigint NOT NULL,
    created_at timestamp(6) with time zone,
    token character varying(255),
    username character varying(255),
    supplier_id integer
);


ALTER TABLE public.tokens OWNER TO pecas;

--
-- Name: tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: pecas
--

CREATE SEQUENCE public.tokens_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tokens_id_seq OWNER TO pecas;

--
-- Name: users; Type: TABLE; Schema: public; Owner: pecas
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    password text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    unlocked boolean NOT NULL,
    enabled boolean NOT NULL
);


ALTER TABLE public.users OWNER TO pecas;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: pecas
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO pecas;

--
-- Name: address id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.address ALTER COLUMN id SET DEFAULT nextval('public.address_id_seq'::regclass);


--
-- Name: brand id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brand ALTER COLUMN id SET DEFAULT nextval('public.brand_id_seq'::regclass);


--
-- Name: brazilian_state id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brazilian_state ALTER COLUMN id SET DEFAULT nextval('public.brazilian_state_id_seq'::regclass);


--
-- Name: category category_id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.category ALTER COLUMN category_id SET DEFAULT nextval('public.category_category_id_seq'::regclass);


--
-- Name: contact id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.contact ALTER COLUMN id SET DEFAULT nextval('public.contact_id_seq'::regclass);


--
-- Name: description id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.description ALTER COLUMN id SET DEFAULT nextval('public.description_id_seq'::regclass);


--
-- Name: item id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.item ALTER COLUMN id SET DEFAULT nextval('public.item_id_seq'::regclass);


--
-- Name: plan id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.plan ALTER COLUMN id SET DEFAULT nextval('public.plan_id_seq'::regclass);


--
-- Name: signature id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.signature ALTER COLUMN id SET DEFAULT nextval('public.signature_id_seq'::regclass);


--
-- Name: stock id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.stock ALTER COLUMN id SET DEFAULT nextval('public.stock_id_seq'::regclass);


--
-- Name: supplier id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier ALTER COLUMN id SET DEFAULT nextval('public.supplier_id_seq'::regclass);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: authorities authorities_pkey; Type: CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_pkey PRIMARY KEY (id);


--
-- Name: brand brand_brand_name_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_brand_name_key UNIQUE (brand_name);


--
-- Name: brand brand_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brand
    ADD CONSTRAINT brand_pkey PRIMARY KEY (id);


--
-- Name: brazilian_state brazilian_state_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brazilian_state
    ADD CONSTRAINT brazilian_state_pkey PRIMARY KEY (id);


--
-- Name: brazilian_state brazilian_state_state_code_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brazilian_state
    ADD CONSTRAINT brazilian_state_state_code_key UNIQUE (state_code);


--
-- Name: brazilian_state brazilian_state_state_name_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.brazilian_state
    ADD CONSTRAINT brazilian_state_state_name_key UNIQUE (state_name);


--
-- Name: category category_name_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_name_key UNIQUE (name);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: contact contact_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.contact
    ADD CONSTRAINT contact_pkey PRIMARY KEY (id);


--
-- Name: description description_description_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.description
    ADD CONSTRAINT description_description_key UNIQUE (description);


--
-- Name: description description_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.description
    ADD CONSTRAINT description_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- Name: plan plan_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY (id);


--
-- Name: signature signature_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.signature
    ADD CONSTRAINT signature_pkey PRIMARY KEY (id);


--
-- Name: signature signature_supplier_id_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.signature
    ADD CONSTRAINT signature_supplier_id_key UNIQUE (supplier_id);


--
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (id);


--
-- Name: supplier supplier_address_id_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_address_id_key UNIQUE (address_id);


--
-- Name: supplier supplier_asaas_id_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_asaas_id_key UNIQUE (asaas_id);


--
-- Name: supplier supplier_contact_id_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_contact_id_key UNIQUE (contact_id);


--
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);


--
-- Name: tokens tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: pecas
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_address_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_address_id ON public.address USING btree (id);


--
-- Name: idx_brazilian_state_code; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_brazilian_state_code ON public.brazilian_state USING btree (state_code);


--
-- Name: idx_brazilian_state_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_brazilian_state_id ON public.brazilian_state USING btree (id);


--
-- Name: idx_contact_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_contact_id ON public.contact USING btree (id);


--
-- Name: idx_item; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_item ON public.item USING btree (id);


--
-- Name: idx_item_hash; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_item_hash ON public.item USING btree (hash);


--
-- Name: idx_stock_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_stock_id ON public.stock USING btree (id);


--
-- Name: idx_stock_piece_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_stock_piece_id ON public.stock USING btree (piece_id);


--
-- Name: idx_stock_supp_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_stock_supp_id ON public.stock USING btree (supplier_id);


--
-- Name: idx_supplier_cnpj; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_supplier_cnpj ON public.supplier USING btree (cnpj);


--
-- Name: idx_supplier_id; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_supplier_id ON public.supplier USING btree (id);


--
-- Name: idx_supplier_original_link; Type: INDEX; Schema: public; Owner: neondb_owner
--

CREATE INDEX idx_supplier_original_link ON public.supplier USING btree (supplier_original_link);


--
-- Name: tokens_token_idx; Type: INDEX; Schema: public; Owner: pecas
--

CREATE UNIQUE INDEX tokens_token_idx ON public.tokens USING btree (token);


--
-- Name: users_username_idx; Type: INDEX; Schema: public; Owner: pecas
--

CREATE UNIQUE INDEX users_username_idx ON public.users USING btree (username);


--
-- Name: item fk2n9w8d0dp4bsfra9dcg0046l4; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT fk2n9w8d0dp4bsfra9dcg0046l4 FOREIGN KEY (category_id) REFERENCES public.category(category_id);


--
-- Name: tokens fk5hmc1bspcblb9laenc80bm8av; Type: FK CONSTRAINT; Schema: public; Owner: pecas
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT fk5hmc1bspcblb9laenc80bm8av FOREIGN KEY (supplier_id) REFERENCES public.supplier(id);


--
-- Name: supplier fk_address; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES public.address(id) ON DELETE CASCADE;


--
-- Name: supplier fk_brand; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES public.brand(id);


--
-- Name: supplier fk_contact; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk_contact FOREIGN KEY (contact_id) REFERENCES public.contact(id) ON DELETE CASCADE;


--
-- Name: supplier fk_description; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk_description FOREIGN KEY (description_id) REFERENCES public.description(id);


--
-- Name: stock fk_piece; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT fk_piece FOREIGN KEY (piece_id) REFERENCES public.item(id) ON DELETE CASCADE;


--
-- Name: signature fk_plan; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.signature
    ADD CONSTRAINT fk_plan FOREIGN KEY (plan_id) REFERENCES public.plan(id) ON DELETE CASCADE;


--
-- Name: address fk_state; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT fk_state FOREIGN KEY (state_id) REFERENCES public.brazilian_state(id) ON DELETE CASCADE;


--
-- Name: signature fk_supplier; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.signature
    ADD CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES public.supplier(id) ON DELETE CASCADE;


--
-- Name: stock fk_supplier_stock; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT fk_supplier_stock FOREIGN KEY (supplier_id) REFERENCES public.supplier(id) ON DELETE CASCADE;


--
-- Name: supplier fk_supplier_token; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT fk_supplier_token FOREIGN KEY (token_id) REFERENCES public.tokens(id) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

