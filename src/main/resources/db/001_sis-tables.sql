
CREATE SEQUENCE audit_item_id_seq
    START WITH 1
    INCREMENT BY 1;

-- just for HSQLDB testing!
create table dual_audit_item_id_seq (zero integer);
insert into dual_audit_item_id_seq values (0);

-- just for HSQLDB testing!
CREATE ALIAS TRANSLATE FOR "cz.silesnet.util.HsqlSupport.translate";

CREATE TABLE audit_items (
    id bigint NOT NULL,
    history_id bigint NOT NULL,
    history_type_label_id bigint,
    user_id bigint,
    time_stamp timestamp NOT NULL,
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
    billing_date timestamp,
    purge_date timestamp,
    customer_id bigint NOT NULL,
    period_from timestamp NOT NULL,
    period_to timestamp NOT NULL,
    vat integer,
    hash_code character varying(50) NOT NULL,
    is_confirmed boolean,
    is_sent boolean,
    is_delivered boolean,
    is_archived boolean,
    deliver_by_mail boolean,
    customer_name character varying(80),
    invoicing_id bigint,
    synchronized timestamp
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
    connection_spot character varying(100),
    inserted_on timestamp NOT NULL,
    frequency integer,
    lastly_billed timestamp,
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
    format integer,
    deliver_signed boolean,
    symbol character varying(20),
    updated timestamp,
    synchronized timestamp,
    account_no character varying(17),
    bank_no character varying(4),
    variable integer
);

CREATE TABLE invoicings (
    id bigint NOT NULL,
    history_id bigint NOT NULL,
    name character varying(80) NOT NULL,
    country integer,
    invoicing_date timestamp,
    numberingbase character varying(15)
);

CREATE TABLE labels (
    id bigint NOT NULL,
    parent_id bigint,
    name character varying(255) NOT NULL,
    number smallint
);

CREATE TABLE services (
    id bigint NOT NULL,
    customer_id bigint,
    period_from timestamp NOT NULL,
    period_to timestamp,
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

CREATE TABLE newservices
(
  customer_id integer DEFAULT 0,
  customer character varying(90),
  technician character varying(10),
  id integer,
  name character varying(20),
  download smallint,
  upload smallint,
  price smallint,
  period_from date,
  billing_on date
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
    mac_prefix varchar(10) NOT NULL,
    vendor varchar(50) NOT NULL
);
