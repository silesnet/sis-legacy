
CREATE SEQUENCE audit_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

CREATE TABLE audit_items (
    id bigint NOT NULL,
    history_id bigint NOT NULL,
    history_type_label_id bigint,
    user_id bigint,
    time_stamp timestamp without time zone NOT NULL,
    field_name character varying(255) NOT NULL,
    old_value character varying(255),
    new_value character varying(255)
);

CREATE TABLE bill_items (
    bill_id bigint NOT NULL,
    text character varying(100),
    amount real,
    price integer,
    is_display_unit boolean
);

CREATE TABLE bills (
    id bigint NOT NULL,
    number character varying(15),
    billing_date timestamp without time zone,
    purge_date timestamp without time zone,
    customer_id bigint NOT NULL,
    period_from timestamp without time zone NOT NULL,
    period_to timestamp without time zone NOT NULL,
    vat integer,
    hash_code character varying(50) NOT NULL,
    is_confirmed boolean,
    is_sent boolean,
    is_delivered boolean,
    is_archived boolean,
    deliver_by_mail boolean,
    customer_name character varying(80),
    invoicing_id bigint,
    synchronized timestamp without time zone
);

CREATE TABLE customers (
    id bigint NOT NULL,
    history_id bigint NOT NULL,
    public_id character varying(20) NOT NULL,
    name character varying(80) NOT NULL,
    supplementary_name character varying(40),
    street character varying(40),
    city character varying(40),
    postal_code character varying(10),
    country integer,
    email character varying(50),
    dic character varying(20),
    contract_no character varying(50) NOT NULL,
    connection_spot character varying(100),
    inserted_on timestamp without time zone NOT NULL,
    frequency integer,
    lastly_billed timestamp without time zone,
    is_billed_after boolean,
    deliver_by_email boolean,
    deliver_copy_email character varying(100),
    deliver_by_mail boolean,
    is_auto_billing boolean,
    info character varying(150),
    contact_name character varying(50),
    phone character varying(60),
    is_active boolean,
    status integer,
    shire_id bigint,
    responsible_id bigint,
    format integer,
    deliver_signed boolean,
    symbol character varying(20),
    updated timestamp without time zone,
    synchronized timestamp without time zone,
    account_no character varying(17),
    bank_no character varying(4),
    variable integer
);

CREATE TABLE invoicings (
    id bigint NOT NULL,
    history_id bigint NOT NULL,
    name character varying(80) NOT NULL,
    country integer,
    invoicing_date timestamp without time zone,
    numberingbase character varying(15)
);

CREATE TABLE labels (
    id bigint NOT NULL,
    parent_id bigint,
    name character varying(255) NOT NULL,
    number smallint
);

CREATE TABLE nodes (
    id bigint NOT NULL,
    class_type character varying(30) NOT NULL,
    parent_id bigint,
    name character varying(50) NOT NULL,
    info character varying(200),
    active boolean,
    history_id bigint NOT NULL,
    type integer,
    frequency integer,
    mac character varying(30),
    custom_vendor character varying(50),
    domain_lid bigint,
    route character varying(50),
    ip character varying(50),
    wep character varying(255),
    ssid character varying(50),
    mac_authorization boolean,
    channel integer,
    polarization integer
);

CREATE TABLE services (
    id bigint NOT NULL,
    customer_id bigint,
    period_from timestamp without time zone NOT NULL,
    period_to timestamp without time zone,
    name character varying(70) NOT NULL,
    price integer NOT NULL,
    frequency integer,
    download integer,
    upload integer,
    is_aggregated boolean,
    info character varying(150),
    replace_id bigint,
    additionalname character varying(50),
    bps character(1)
);

CREATE TABLE settings (
    id bigint NOT NULL,
    name character varying(50) NOT NULL,
    value character varying(250)
);

CREATE TABLE users (
    id bigint NOT NULL,
    login character varying(255) NOT NULL,
    passwd character varying(255),
    name character varying(255) NOT NULL,
    roles character varying(255)
);

CREATE TABLE vendors (
    mac_prefix text NOT NULL,
    vendor text NOT NULL
);
