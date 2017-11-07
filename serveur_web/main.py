# -*- coding: utf-8 -*-
import json, random
from flask import Flask, request
from flask_cors import CORS, cross_origin
from db import Db

app = Flask(__name__)
app.debug = True
CORS(app)

#Vider la base
@app.route('/reset',methods=['GET'])
def reset():
    db = Db()
    db.executeFile("db.sql")
    db.close()
    return "Reset done."

#Liste de tous les utilisateurs
@app.route('/inscrit', methods=['GET'])
def get_users():
    db = Db()
    informations = db.select('SELECT * FROM inscrit;')
    db.close()
    return json.dumps(informations), 201, {'Content-Type': 'application/json'}


@app.route('/inscription', methods=['post'])
def ajout_inscrit():
    db = Db()                               #Ouverture de la connection avec la base de donnée.
    data = request.get_json()               #Récupération de l'objet Json.
    verif = db.select("SELECT * FROM inscrit where id_joueur = '%s';" % (data['id_joueur']))

    #Si la taille de mon élement est vide alors cet intitulé n'est pas dans la base.
    if (len(verif) != 0):
        print('Log - Utilisateur déjà dans la base')
        db.close()
        return json.dumps("Ce nom est déja utilisé"), 400, {'Content-Type': 'application/json'}

    else:
        print('Log - Création utilisateur dans la base')
        db.execute("INSERT INTO inscrit(id_joueur) VALUES ('%s');"%(data['id_joueur']))
        db.close()
        return json.dumps('OK'), 201, {'Content-Type': 'application/json'}

##### En cours #####
@app.route('/gettimage', methods=['get'])
def envoyerimagegoogle():
    #nomfichier = chemin + "/" + str(name)
    #return send_file(nomfichier), 200, {'Content-Type': 'image/jpeg'}
    #Post vers google
    return 0

if __name__ == "__main__":
  app.run()