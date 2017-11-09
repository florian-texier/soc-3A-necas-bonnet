DROP TABLE IF EXISTS inscrit CASCADE;
CREATE TABLE inscrit(
	id_joueur varchar(50) NOT NULL PRIMARY KEY,
	etat varchar(50),
	x int,
	y int
);




INSERT INTO inscrit(id_joueur,etat,x,y) VALUES ('un chat la','c',1,2);
