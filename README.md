# Scavenger Hunt Necas Bonnet
Notre fabuleux GitHub pour le projet de parcours 3A.

## Authors

* **Jérome Bonnet** - *Le blond*
* **Florian Necas** - *Le brun*

# Pré-requis
- Un PC Hote Rancher
- Un PC Serveur avec Docker
- Une Raspberry Pi + Pi HAT 
- Un téléphone Android
- Le git https://github.com/jeromebonnet/soc-3A-necas-bonnet

# Installation

## PC Rancher serveur : 
- Déployer une stack de l'image fnecasimerir/swarvision avec le docker-compose situé dans le dossier swarmCompose

## PC Serveur :
- Déployer une image Docker de fnecasimerir/superviscav
- Déployer une image postgresql avec comme mot de passe : example | user : postgres | db : postgres
- Télécharger les fichiers du git et executer :


    pip install -r serveur_web/requirements.txt
    python serveur_web/main.py


## Raspberry
- Uploader les fichiers du dossier Git /module_pi et executer


    pip install sense_hat
    pip install requests
    python3 main.py


## Android
- Build & Launch the app and play :)

## Développé avec

* [Flask](http://flask.pocoo.org/) - Framework web utilisé
* [Bulma](https://bulma.io/) - Meilleur framework CSS ayant jamais existé
* [Docker](https://www.docker.com/) - Des containers, encore des containers
* [Rancher](http://rancher.com/) - Swarm de containers, encore des containers
* [Nginx](https://nginx.org/en/) - Serveur du superviseur
* [Postgresql](https://www.postgresql.org/?&) - Pour la BDD, bien évidemment
* [Volley](https://developer.android.com/training/volley/index.html) - Parce qu'Android aussi communique

# Documentation API

La documentation API est disponible sur [ce wiki](http://fnecas.ovh:3000/doku.php?id=wiki:scavhunt)





