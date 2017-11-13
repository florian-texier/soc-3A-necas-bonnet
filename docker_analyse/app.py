import json
from flask import Flask, request,g
from flask_cors import CORS, cross_origin
from redis import Redis
import googleapiclient.discovery
from google.oauth2 import service_account

app = Flask(__name__)
redis = Redis(host='redis', port=6379)

@app.route('/')
def hello():
    count = redis.incr('hits')
    return 'Hello World! I have been seen {} times.\n'.format(count)

@app.route('/analyse', methods=['post'])
def send_image():


    credentials = service_account.Credentials.from_service_account_file('credentials.json')
    service = googleapiclient.discovery.build('vision', 'v1', credentials=credentials)

    data = request.get_json()		#Recuperation de la requete du client android


    image_content = data['image']	#Je stock dans la variable l'image


    #Construction du JSON qui va être envoyer à Google Vision
    service_request = service.images().annotate(body={
        'requests': [{
            'image': {
                'content': image_content
                },
                'features': [{
                    'type': 'LABEL_DETECTION',
                    'maxResults': 1
                }]
            }]
    })

    # Envoi et récupération de la réponse de Google VIsion
    response = service_request.execute()

    # Stockage intitulé déterminé par google vision
    label = response['responses'][0]['labelAnnotations'][0]['description']
    print(label)                    #Pour les logs j'affiche ce qu'à déterminé GOOGLE VISION

    response ={"label": label}

    return json.dumps(response), 201, {'Content-Type': 'application/json'}

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)
