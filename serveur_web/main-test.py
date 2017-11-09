
import json, random
from flask import Flask, request,g
from flask_cors import CORS, cross_origin
from db import Db
import base64
import googleapiclient.discovery
from google.oauth2 import service_account

app = Flask(__name__)
app.debug = True
CORS(app)

@app.route('/inscription', methods=['post'])
def ajout_inscrit():
    db = Db()                               #Ouverture de la connection avec la base de donnée.
    data = request.get_json()               #Récupération de l'objet Json.
    verif = db.select("SELECT * FROM equipe where e_name = '%s';" %(data['id_joueur']))

    #Si la taille de mon élement est vide alors cet intitulé n'est pas dans la base.
    if (len(verif) != 0):
        print('Log - Utilisateur déjà dans la base')
        db.close()
        return json.dumps("Ce nom est déja utilisé"), 400, {'Content-Type': 'application/json'}

    else:
        response = {"objet": ["arm", "dog", "screen"]}
        print('Log - Création utilisateur dans la base')
        db.execute("INSERT INTO equipe(e_id ,e_name) VALUES (NULL,'%s');" %(data['id_joueur']))
        db.close()
        return json.dumps(response), 201, {'Content-Type': 'application/json'}

@app.route('/postimage', methods=['post'])
def envoyerimagegoogle():
    db = Db()

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
    print(label)
    response = {"response": "Photo analysee"}
    var = 'c'
    objets = ["arm", "dog", "screen"]

    UPDATE `objet` SET `o_found` = '1' WHERE `objet`.`obj_id` = 4;

    for objet in range(0, len(objets)):
        if objets[objet] == label:
            db.execute('UPDATE inscrit Set etat =%s WHERE id_joueur = %s;', ('a', data['name']))
            var = 'a'
            break
    if var == 'c':
        db.execute('UPDATE inscrit Set etat =%s WHERE id_joueur = %s;', ('b', data['name']))

    db.close()

    return json.dumps(response), 201, {'Content-Type': 'application/json'}
