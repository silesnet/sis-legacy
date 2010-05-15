CREATE INDEX audit_history_index ON audit_items (history_id);

CREATE INDEX audit_label_index ON audit_items (history_type_label_id);

CREATE INDEX audit_user_index ON audit_items (user_id);

CREATE INDEX bill_index ON bill_items (bill_id);

CREATE INDEX customer_index ON bills (customer_id);

CREATE INDEX customer_index2 ON services (customer_id);

CREATE INDEX invoicing_index ON bills (invoicing_id);

CREATE INDEX label_parent_index ON labels (parent_id);

CREATE INDEX mac_prefix ON vendors (mac_prefix);
