INSERT INTO users (id, login, passwd, name, roles) VALUES (1, 'anonymous', '', 'anonymousUser', 'ROLE_ANONYMOUS');
INSERT INTO users (id, login, passwd, name, roles) VALUES (2, 'system', '', 'System', 'ROLE_ANONYMOUS,ROLE_ADMIN');

INSERT INTO labels (id, parent_id, name, number) VALUES (300, 0, 'responsible', 0);
INSERT INTO labels (id, parent_id, name, number) VALUES (313, 300, 'Iwona', 20);
INSERT INTO labels (id, parent_id, name, number) VALUES (308, 300, 'Martin', 0);
INSERT INTO labels (id, parent_id, name, number) VALUES (302, 300, 'Marcel', 10);
INSERT INTO labels (id, parent_id, name, number) VALUES (301, 300, 'Radek', 10);
INSERT INTO labels (id, parent_id, name, number) VALUES (303, 300, 'Robert', 20);
INSERT INTO labels (id, parent_id, name, number) VALUES (304, 300, 'David', 10);
INSERT INTO labels (id, parent_id, name, number) VALUES (305, 300, 'Karel', 0);
INSERT INTO labels (id, parent_id, name, number) VALUES (306, 300, 'Peta', 10);
INSERT INTO labels (id, parent_id, name, number) VALUES (309, 300, 'Mirek', 10);
INSERT INTO labels (id, parent_id, name, number) VALUES (311, 300, 'Gra�yna', 10);


