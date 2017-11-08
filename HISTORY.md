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

A GARDER : 
Tuto activer beacon sur Raspberry => https://learn.adafruit.com/pibeacon-ibeacon-with-a-raspberry-pi?view=all
Créer une application web Docker Python et PostgreSQL dans Azure => https://docs.microsoft.com/fr-fr/azure/app-service/containers/tutorial-docker-python-postgresql-app
Quelle différence entre Docker et Kubernetes ? => http://www.lemagit.fr/article/Quelle-difference-entre-Docker-et-Kubernetes
