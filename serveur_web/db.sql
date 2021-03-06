DROP TABLE IF EXISTS objet CASCADE;
DROP TABLE IF EXISTS image CASCADE;
DROP TABLE IF EXISTS equipe CASCADE;
DROP TABLE IF EXISTS beacon CASCADE;


CREATE TABLE equipe(
	e_id   SERIAL NOT NULL ,
	e_name VARCHAR (25)  ,
	e_etat VARCHAR (25)  ,
	e_ip   VARCHAR (25)  ,
	CONSTRAINT prk_constraint_equipe PRIMARY KEY (e_id)
);


CREATE TABLE objet(
	o_id     SERIAL NOT NULL ,
	o_name   VARCHAR (25)  ,
	o_found  VARCHAR (25)  ,
	o_points INT   ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_objet PRIMARY KEY (o_id)
);

CREATE TABLE image(
	i_id     SERIAL NOT NULL ,
	i_base64 TEXT   ,
	i_coordx VARCHAR (25)  ,
	i_coordy VARCHAR (25)  ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_image PRIMARY KEY (i_id)
);

CREATE TABLE beacon(
	b_id     SERIAL NOT NULL ,
	b_uid VARCHAR (25)
);



ALTER TABLE public.image ADD CONSTRAINT FK_image_e_id FOREIGN KEY (e_id) REFERENCES public.equipe(e_id);


ALTER TABLE objet ADD CONSTRAINT FK_objet_e_id FOREIGN KEY (e_id) REFERENCES equipe(e_id);


INSERT INTO equipe (e_name, e_etat, e_ip) VALUES ('Flo', 'nothingtoshow', 'http://172.18.0.2:5000');

INSERT INTO objet (o_name, o_found, o_points, e_id) VALUES ('arm', 'false',0, '1');
INSERT INTO objet (o_name, o_found, o_points, e_id) VALUES ('dog', 'false',0,'1');
INSERT INTO objet (o_name, o_found, o_points, e_id) VALUES ('screen', 'false',0,'1');

INSERT INTO beacon (b_uid) VALUES ('0xdeadbeef1ee7cafebabe');
INSERT INTO beacon (b_uid) VALUES ('0xc0ffee0ff1c3');
INSERT INTO beacon (b_uid) VALUES ('0xc0ffee0ff1ce');