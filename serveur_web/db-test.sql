#------------------------------------------------------------
#        Script MySQL.
#------------------------------------------------------------


#------------------------------------------------------------
# Table: equipe
#------------------------------------------------------------

CREATE TABLE equipe(
        e_id   int (11) Auto_increment  NOT NULL ,
        e_name Varchar (25) ,
        PRIMARY KEY (e_id )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: objet
#------------------------------------------------------------

CREATE TABLE objet(
        obj_id  int (11) Auto_increment  NOT NULL ,
        o_name  Varchar (25) ,
        o_found Bool ,
        o_coordx Varchar (25) ,
        o_coordy Varchar (25) ,
        e_id    Int ,
        PRIMARY KEY (obj_id )
)ENGINE=InnoDB;

ALTER TABLE objet ADD CONSTRAINT FK_objet_e_id FOREIGN KEY (e_id) REFERENCES equipe(e_id);

INSERT INTO `equipe` (`e_id`, `e_name`) VALUES (NULL, 'Flo');
INSERT INTO `equipe` (`e_id`, `e_name`) VALUES (NULL, 'Jer');

INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'arm', '0', '0', '0', '1');
INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'dog', '0', '0', '0', '1');
INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'screen', '0', '0', '0', '1');
INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'arm', '0', '0', '0', '2');
INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'dog', '0', '0', '0', '2');
INSERT INTO `objet` (`obj_id`, `o_name`, `o_found`, `o_coordx`, o_coordy, `e_id`) VALUES (NULL, 'screen', '0', '0', '0', '2');



