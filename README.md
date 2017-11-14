# soc-3A-necas-bonnet
Notre fabuleux GitHub pour le projet de parcours 3A.

## Authors

* **Jérome Bonnet** - *Le blond*
* **Florian Necas** - *Le brun*

# Sommaire : 
	* Serveur-web
	* Rasbperry-pi
	* Android 	(A FAIRE)
	* Docker 	(A FAIRE)
	* Front 	(A FAIRE)
	* Trucs compliqués 


# Serveur-web sur Heroku 
	* Installation :
		* Installed add-ons : POST-SQL 
		* Pushez notre code et c'est bon.
		* Faite votrenomdesite.herokuapp.com/reset
		
	*Routes :
		* /lireetats (GET) 	- Utilisé par la raspberry-pi 
			* Fonction : Retourne un tableau d'états avec des objets de types JSON 
			* Reponse : [{'id':nomdelequipe,'etat':etat}]
			
		* /resetetats(GET)	- Utilisé par la raspberry-pi 
			* Fonction : Remet les états de toutes les équipes à "nothingtoshow" 
			* Reponse : {'response': 'reset_etat_OK'}
			
		* /reset (GET)
			* Fonction : Reset la base de donnée 
			* Reponse : {'response': 'reset_bdd_OK'}
			
		* /inscrit (GET)
			* Fonction : Retourne les équipes incrites + les informations associées
			* Reponse : [{"e_id": 1, "e_name": "Flo", "e_etat": "nothingtoshow"}, {"e_id": 2, "e_name": "Jer", "e_etat": "nothingtoshow"}]
		
		* /objets (GET)
			* Fonction : Retourne tous les objets de toutes les équipes + les informations associées
			* Reponse : {"liste_objets": [{"o_name": "arm", "o_coordx": "0", "o_coordy": "0", "o_id": 1, "e_id": 1, "o_image": null, "o_found": "false"}]}
			
		* /objets/<int:id> (GET)
			* Fonction : Retourne les objets d'UNE équipe
			* Reponse : {"liste_objets": [{"o_name": "arm", "o_coordx": "0", "o_coordy": "0", "o_id": 1, "e_id": 1, "o_image": null, "o_found": "false"}]}
			
		* /inscription (POST)
			* Fonction : Ajout d'une équipe dans la base + création de ses objets
			* Reponse : {"liste_objets": [{"o_name": "arm", "o_coordx": "0", "o_coordy": "0", "o_id": 1, "e_id": 1, "o_image": null, "o_found": "false"}]}
			
		* /images (GET)
			* Fonction : Afficher toutes les photos de toutes les équipes
			* Response : {"e_id": 2, "i_coordy": "2/1,50/1,58/1", "i_coordx": "42/1,40/1,32/1", "i_id": 1, {"i_base64": DONNEES}
			
		* /images/<id:equipe> (GET)
			* Fonction : Afficher toutes les photos d'UNE SEULE Equipe
			* Response : {"e_id": 2, "i_coordy": "2/1,50/1,58/1", "i_coordx": "42/1,40/1,32/1", "i_id": 1, {"i_base64": DONNEES}
		
* Rasbperry-pi 
	* Pré-requis :
		* Installer Python3
		* Librairie à avoir : sense_hat,requests,time

	* Utilisation :
		* Se connecter sur votre Rasbperry-pi en SSH
		* Excuter => python3 main.py
		
	* Fonctionnement :
		* Requête pour récupérer tous les états des équipes 
		* Requête pour reset les états des équipes (Pour ne pas re-afficher plusieurs fois)
		* Pour chaque état, si j'ai un succés ou un echec et j'affiche sur le sensor har
		* Un sleep pour ne pas harceler de manière violente le serveur

# Android

* [Android Image Upload](https://github.com/akrajilwar/Android-Image-Upload/) 
* Utilisation de cette partie en supprimant le multipart form et en faisant une requete JSON à la place.
* Envoi de l'image en base 64

* Utilisation du code de Pierre Grabolosa.

# Docker 

* Simple serveur Flask avec une route POST /analyse, qui renvoie le premier label détecté.


# Trucs compliqués 
* Affichier l'image en base64 en JS :  $("<img>", {"src": "data:image/png;base64," + lapetiteimage,"width": "250px", "height": "250px"}).appendTo("#photo_equipe");
	
	
	