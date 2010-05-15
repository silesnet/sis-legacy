CREATE INDEX audit_history_index ON audit_items USING btree (history_id);

CREATE INDEX audit_label_index ON audit_items USING btree (history_type_label_id);

CREATE INDEX audit_user_index ON audit_items USING btree (user_id);

CREATE INDEX bill_index ON bill_items USING btree (bill_id);

CREATE INDEX customer_index ON bills USING btree (customer_id);

CREATE INDEX customer_index2 ON services USING btree (customer_id);

CREATE INDEX invoicing_index ON bills USING btree (invoicing_id);

CREATE INDEX label_parent_index ON labels USING btree (parent_id);

CREATE INDEX mac_prefix ON vendors USING btree (mac_prefix);
