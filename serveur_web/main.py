# -*- coding: utf-8 -*-
import json, random
from flask import Flask, request
from flask_cors import CORS, cross_origin
from db import Db
import base64
import googleapiclient.discovery
from google.oauth2 import service_account

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



#Pour s'inscrire
@app.route('/inscription', methods=['post'])
def ajout_inscrit():
    db = Db()                               #Ouverture de la connection avec la base de donnée.
    data = request.get_json()               #Récupération de l'objet Json.
    verif = db.select("SELECT * FROM inscrit where id_joueur = '%s';" % (data['id_joueur']))

    response ={"objet":["chien","bouteille","ecran"]}

    #Si la taille de mon élement est vide alors cet intitulé n'est pas dans la base.
    if (len(verif) != 0):
        print('Log - Utilisateur déjà dans la base')
        db.close()
        return json.dumps("Ce nom est déja utilisé"), 400, {'Content-Type': 'application/json'}

    else:
        print('Log - Création utilisateur dans la base')
        db.execute("INSERT INTO inscrit(id_joueur) VALUES ('%s');"%(data['id_joueur']))
        db.close()
        return json.dumps(response), 201, {'Content-Type': 'application/json'}



#Poster une image
@app.route('/postimage', methods=['post'])
def envoyerimagegoogle():
  
    # [START authenticate]
    credentials = service_account.Credentials.from_service_account_file('credentials.json')
    service = googleapiclient.discovery.build('vision', 'v1', credentials=credentials)
    # [END authenticate]

    data = request.get_json()
    image_content = data['image']

    # [START construct_request]
    service_request = service.images().annotate(body={
        'requests': [{
            'image': {
                'content': image_content.decode('UTF-8')
                },
                'features': [{
                    'type': 'LABEL_DETECTION',
                    'maxResults': 1
                }]
            }]
    })
    response = service_request.execute()
    label = response['responses'][0]['labelAnnotations'][0]['description']

    print(response)

    response ={"response":"Photo analysee"}
    status = 0
    return json.dumps(response), 201, {'Content-Type': 'application/json'}


if __name__ == "__main__":
  app.run()
