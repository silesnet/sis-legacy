ALTER TABLE ONLY bills
    ADD CONSTRAINT bills_pkey PRIMARY KEY (id);

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_history_id_key UNIQUE (history_id);

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);

ALTER TABLE ONLY invoicings
    ADD CONSTRAINT invoicings_history_id_key UNIQUE (history_id);

ALTER TABLE ONLY invoicings
    ADD CONSTRAINT invoicings_pkey PRIMARY KEY (id);

ALTER TABLE ONLY nodes
    ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);

ALTER TABLE ONLY services
    ADD CONSTRAINT services_pkey PRIMARY KEY (id);

ALTER TABLE ONLY settings
    ADD CONSTRAINT settings_name_key UNIQUE (name);

ALTER TABLE ONLY settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);

ALTER TABLE ONLY audit_items
    ADD CONSTRAINT sis_history_pkey PRIMARY KEY (id);

ALTER TABLE ONLY labels
    ADD CONSTRAINT sis_label_pkey PRIMARY KEY (id);

ALTER TABLE ONLY users
    ADD CONSTRAINT sis_user_login_key UNIQUE (login);

ALTER TABLE ONLY users
    ADD CONSTRAINT sis_user_name_key UNIQUE (name);

ALTER TABLE ONLY users
    ADD CONSTRAINT sis_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY bill_items
    ADD CONSTRAINT fk20690d88e58fea1d FOREIGN KEY (bill_id) REFERENCES bills(id);

ALTER TABLE ONLY audit_items
    ADD CONSTRAINT fk36bc565cafae52d1 FOREIGN KEY (history_type_label_id) REFERENCES labels(id);

ALTER TABLE ONLY audit_items
    ADD CONSTRAINT fk36bc565ce35fa39d FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE ONLY services
    ADD CONSTRAINT fk5235105e31f2d3d FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE ONLY customers
    ADD CONSTRAINT fk600e7c55793cd404 FOREIGN KEY (shire_id) REFERENCES labels(id);

ALTER TABLE ONLY customers
    ADD CONSTRAINT fk600e7c558a6d2c95 FOREIGN KEY (responsible_id) REFERENCES labels(id);

ALTER TABLE ONLY nodes
    ADD CONSTRAINT fk64212b14dcb921d FOREIGN KEY (domain_lid) REFERENCES labels(id);

ALTER TABLE ONLY audit_items
    ADD CONSTRAINT fk73691b32afae52d1 FOREIGN KEY (history_type_label_id) REFERENCES labels(id);

ALTER TABLE ONLY audit_items
    ADD CONSTRAINT fk73691b32e35fa39d FOREIGN KEY (user_id) REFERENCES users(id);
