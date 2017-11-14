# 06/11
## Preparation
* Paramètrage PostgreSQL + Heroku.
* Liaison dépot Github et Heroku pour déploiement rapide. 
* Serveur basique de départ avec routes suivante :
	* Afficher les inscrits.
	* Ajouter un inscrit + gestion en cas de inscrit déjà présent dans la base.
	* Reset la base de donnée.
* Recherches Kubernetes

* Mise en place du detecteur de beacon
* Tentative d'afficher le beacon sur la mainActivity

# 07/11
## Android
* Modification du serveur Web :
	* Actualisation et tests de la route pour effectuer une inscription
	* Premier pas d'Intégration serveur/android OK : J'appuis sur un bouton de l'interface (téléphone), le téléphone envoi un POST, l'user si il n'existe pas est ajouté dans la base (verifié) et le serveur renvoi un JSON OK.
* Application Android :
	* Ajout d'une permission INTERNET dans le manifest. (Pb rencontré : PERMISSION DENIED)
	* Ajout bouton sur l'interface
	* Connecter le bouton à la fonction de Post un user(Fait par Flo)
	* Tests

* Mise à jour de la main activity pour qu'elle contienne le barcode & beacon
* Communique avec le serveur heroku (creation d'un utilisateur)

## Backend
* Matin - Tenter desespérement de faire fonctionner l'authentification vers google. (Sans Succés)
* Google Cloud Vision (enfin!) mis en place

# 08/11

## Android

* Modification du serveur Web :
	* Post Image fonctionnel, le téléphone envoi l'image au serveur, le serveur l'envoi à Google et renvoi la réponse au téléphone.
	* Shared preferences pour le nom.
	* List View a faire.

	
## Backend 

* Petit serveur sur la Pi + prise en main du Sense Hate (led) (|| A REMPLACER PAR UN AUTRE MOYEN POUR AFFICHER SI LA PHOTO EST OK OU NON||)
* Ajout 21h03 : Changement sur la Pi : Je fais des requête get vers mon serveur pour récupérer un état PB => Variable globale qui zozotte.



# 09/11

	* Route sur lireetats des joueurs
	* Route pour reset etats des joueurs
	
* Modification base de donnee :
	* Ajout column etat


	* Script qui recupere les états des équipes, fonctionne comme une queue, affiche si il y a un état à afficher et je reset les états des équipes
	
*Android :
	* Envoi des coordonees GPS
	* Reception liste mission json

* Yeah, on a une demi demo pour demain. :)

# 10/11
* Modification du serveur Web :
	* Beaucoup de modification sur le serveur + BDD (Correction variables,ajouts de commentaire, correction format json, nouvelles routes)
	* Pb rencontré qui m'a pris plusieurs heures à trouver => Formatage JSON en cause, pas de soucis coté serveur mais le client n'affichait pas d'erreur 
	(Mais ça avait quand même un impact...)
* Modification Raspberry Pi :
	* Modification des noms de variables et suppression des fameux mono caractere comme variable
*Android :
	* Appareil photo en cours + corrections divers
* Docker : 
	* On a commencé à paramètrer... à suivre
	
# 13/11 :
* Modification du serveur Web :
	* Serveur repassé en local  sur machine physique + BDD seulemet hebergé sur Heroku (difficulutés avec Post gres, si temps, on la repassera aussi en local)
* Docker :
	* Conteneur avec la route pour post l'image sur google vision OK
* Android :
	* Ajustement par rapport aux changements d'adresses
	* Correction objets JSON
	
#14/11 :
* Superviseur
	* Script JavaScript + HTML à nu :
		* Affichage Nombre d'équipes
		* Afficher les noms des équipes
		* Afficher les photos PAR équipes
* Serveur web :
	* Modification BDD pour les images
	* Ajustement code pour stockage et envoi base64
	* Routes /images et /images/numeroequipe crées

* Android :
	* Finaliation des modules d'inscription et post images

A GARDER : 
Tuto activer beacon sur Raspberry => https://learn.adafruit.com/pibeacon-ibeacon-with-a-raspberry-pi?view=all
Créer une application web Docker Python et PostgreSQL dans Azure => https://docs.microsoft.com/fr-fr/azure/app-service/containers/tutorial-docker-python-postgresql-app
Quelle différence entre Docker et Kubernetes ? => http://www.lemagit.fr/article/Quelle-difference-entre-Docker-et-Kubernetes
