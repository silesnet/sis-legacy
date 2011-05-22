ALTER TABLE bills
    ADD CONSTRAINT bills_pkey PRIMARY KEY (id);

ALTER TABLE customers
    ADD CONSTRAINT customers_history_id_key UNIQUE (history_id);

ALTER TABLE customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);

ALTER TABLE invoicings
    ADD CONSTRAINT invoicings_history_id_key UNIQUE (history_id);

ALTER TABLE invoicings
    ADD CONSTRAINT invoicings_pkey PRIMARY KEY (id);

ALTER TABLE services
    ADD CONSTRAINT services_pkey PRIMARY KEY (id);

ALTER TABLE settings
    ADD CONSTRAINT settings_name_key UNIQUE (name);

ALTER TABLE settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);

ALTER TABLE audit_items
    ADD CONSTRAINT sis_history_pkey PRIMARY KEY (id);

ALTER TABLE labels
    ADD CONSTRAINT sis_label_pkey PRIMARY KEY (id);

ALTER TABLE users
    ADD CONSTRAINT sis_user_login_key UNIQUE (login);

ALTER TABLE users
    ADD CONSTRAINT sis_user_name_key UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT sis_user_pkey PRIMARY KEY (id);

ALTER TABLE bill_items
    ADD CONSTRAINT fk20690d88e58fea1d FOREIGN KEY (bill_id) REFERENCES bills(id);

ALTER TABLE audit_items
    ADD CONSTRAINT fk36bc565cafae52d1 FOREIGN KEY (history_type_label_id) REFERENCES labels(id);

ALTER TABLE audit_items
    ADD CONSTRAINT fk36bc565ce35fa39d FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE services
    ADD CONSTRAINT fk5235105e31f2d3d FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE customers
    ADD CONSTRAINT fk600e7c55793cd404 FOREIGN KEY (shire_id) REFERENCES labels(id);

ALTER TABLE customers
    ADD CONSTRAINT fk600e7c558a6d2c95 FOREIGN KEY (responsible_id) REFERENCES labels(id);
