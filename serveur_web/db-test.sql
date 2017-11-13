------------------------------------------------------------
--        Script Postgre 
------------------------------------------------------------



------------------------------------------------------------
-- Table: equipe
------------------------------------------------------------
CREATE TABLE public.equipe(
	e_id   SERIAL NOT NULL ,
	e_name VARCHAR (25)  ,
	e_etat VARCHAR (25)  ,
	CONSTRAINT prk_constraint_equipe PRIMARY KEY (e_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: objet
------------------------------------------------------------
CREATE TABLE public.objet(
	o_id     SERIAL NOT NULL ,
	o_name   VARCHAR (25)  ,
	o_found  BOOL   ,
	o_coordx VARCHAR (25)  ,
	o_coordy VARCHAR (25)  ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_objet PRIMARY KEY (o_id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: image
------------------------------------------------------------
CREATE TABLE public.image(
	i_id     SERIAL NOT NULL ,
	i_base64 VARCHAR (2000)   ,
	e_id     INT   ,
	CONSTRAINT prk_constraint_image PRIMARY KEY (i_id)
)WITHOUT OIDS;



ALTER TABLE public.objet ADD CONSTRAINT FK_objet_e_id FOREIGN KEY (e_id) REFERENCES public.equipe(e_id);
ALTER TABLE public.image ADD CONSTRAINT FK_image_e_id FOREIGN KEY (e_id) REFERENCES public.equipe(e_id);


INSERT INTO equipe (e_id, e_name) VALUES (NULL, 'Flo', 'c');
INSERT INTO equipe (e_id, e_name) VALUES (NULL, 'Jer', 'c');

INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'arm', '0', '0', '0', '1');
INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'dog', '0', '0', '0', '1');
INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'screen', '0', '0', '0', '1');
INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'arm', '0', '0', '0', '2');
INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'dog', '0', '0', '0', '2');
INSERT INTO objet (obj_id, o_name, o_found, o_coordx, o_coordy, e_id) VALUES (NULL, 'screen', '0', '0', '0', '2');



