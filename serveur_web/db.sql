DROP TABLE IF EXISTS objet CASCADE;
DROP TABLE IF EXISTS equipe CASCADE;


CREATE TABLE equipe(
	e_id   SERIAL NOT NULL ,
	e_name VARCHAR (25)  ,
	e_etat VARCHAR (25)  ,
	CONSTRAINT prk_constraint_equipe PRIMARY KEY (e_id)
);


CREATE TABLE objet(
	o_id     SERIAL NOT NULL ,
	o_name   VARCHAR (25)  ,
	o_found  VARCHAR (25)   ,
	o_coordx VARCHAR (25)  ,
	o_coordy VARCHAR (25)  ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_objet PRIMARY KEY (o_id)
);

CREATE TABLE public.image(
	i_id     SERIAL NOT NULL ,
	i_base64 VARCHAR (2000)   ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_image PRIMARY KEY (i_id)
);

ALTER TABLE public.image ADD CONSTRAINT FK_image_e_id FOREIGN KEY (e_id) REFERENCES public.equipe(e_id);


ALTER TABLE objet ADD CONSTRAINT FK_objet_e_id FOREIGN KEY (e_id) REFERENCES equipe(e_id);


INSERT INTO equipe (e_name, e_etat) VALUES ('Flo', 'nothingtoshow');
INSERT INTO equipe (e_name, e_etat) VALUES ('Jer', 'nothingtoshow');

INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('arm', 'false', '0', '0', '1');
INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('dog', 'false', '0', '0', '1');
INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('screen', 'false', '0', '0', '1');
INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('arm', 'false', '0', '0', '2');
INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('dog', 'false', '0', '0', '2');
INSERT INTO objet (o_name, o_found, o_coordx, o_coordy, e_id) VALUES ('screen', 'false', '0', '0', '2');
