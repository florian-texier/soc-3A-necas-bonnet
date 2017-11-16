# -*- coding: utf-8 -*-
import json, random
import time
import requests
from flask import Flask, request,g
from flask_cors import CORS, cross_origin
from db import Db
import base64
import googleapiclient.discovery
from google.oauth2 import service_account

app = Flask(__name__)
app.debug = True
CORS(app)


################### ROUTES POUR LA RASPBERRY PI ################### 

# ROUTE POUR LIRE CE QUE DOIS AFFICHER LA RASPBERRY
@app.route('/lireetats')
def etat():
    db = Db() 						        # Ouverture connexion vers BDD
    etats = db.select("SELECT * FROM equipe;") # Je récupère les informations de de ma table équipe
    db.close() 						        # Fermeture connexion vers BDD
    tableau_etats =[]					    # Tableau qui contiendra des objets Json => {'id':nomdelequipe,'etat':etat}

    for i in range(0,len(etats)):	        # Pour chaque équipe inscrite dans ma BDD :
        # Je formate les données en JSon comme détaillé ligne 23
        monjson = {'id':etats[i]['e_name'],'etat':etats[i]['e_etat']}
        tableau_etats.append(monjson)		# Je rajoute l'objet à mon tableau

    reponseaenvoyer ={'objet':tableau_etats}# J'encapsule mon tableau dans un objet JSon

    # Je retourne mon objet JSon complet
    return json.dumps(reponseaenvoyer), 201, {'Content-Type': 'application/json'} 


# ROUTE POUR METTRE TOUT LES ETATS A NOTHINGTOSHOW
@app.route('/resetetats')
def resetetat():
    db = Db() 						        # Ouverture connexion vers BDD
    etats = db.select("SELECT * FROM equipe;")# Je récupère les informations de de ma table équipe

    for etat in range(0,len(etats)):        # Pour chaque équipe inscrite dans ma BDD
        # Je change l'état de l'équipe avec "nothingtoshow"
        db.execute('UPDATE equipe Set e_etat=%s WHERE e_name= %s;',('nothingtoshow',etats[etat]['e_name']))

    db.close()						                #Fermeture connexion vers BDD

    #Je retourne mon objet JSon complet
    return json.dumps("{'response': 'reset_etat_OK'}"), 201, {'Content-Type': 'application/json'}

####################################################################




############ ROUTES POUR RESET/VISUALISER LA BASE ##################

#RESET LA BASE
@app.route('/reset',methods=['GET'])
def reset():
    db = Db()						#Ouverture connexion vers BDD
    db.executeFile("db.sql")		#Execution du fichier db.sql pour vider la base de donnée
    db.close()						#Fermeture connexion vers BDD
    return json.dumps("{'response': 'reset_bdd_OK'}"), 201, {'Content-Type': 'application/json'}



#AFFICHER TOUTES LES INFORMATIONS DANS LA TABLE EQUIPE
@app.route('/inscrit', methods=['GET'])
def get_users():
    db = Db()						#Ouverture connexion vers BDD

    informations = db.select('SELECT * FROM equipe;')	#Je récupère les informations de ma table équipe

    db.close()						#Fermeture connexion vers BDD
    response ={'liste_inscrit':informations}
    #Je retourne les informations de ma table équipe
    return json.dumps(informations), 201, {'Content-Type': 'application/json'}

#AFFICHER TOUS LES OBJETS DANS BASE
@app.route('/objets', methods=['GET'])
def get_objets():
    db = Db()						#Ouverture connexion vers BDD
    informations = db.select('SELECT * FROM objet;')	#Je récupère les informations de ma table objet
    db.close()						#Fermeture connexion vers BDD
    response ={'liste_objets':informations}
    #Je retourne les informations de ma table équipe
    return json.dumps(response), 201, {'Content-Type': 'application/json'}

#AFFICHER LES OBJETS D'UNE EQUIPE
@app.route('/objets/<int:id>', methods=['GET'])
def get_objets_equipe(id):
    db = Db()						#Ouverture connexion vers BDD
    informations = db.select("SELECT * FROM objet WHERE e_id = '%s';" % (id))	#Je récupère les informations de ma table objet
    db.close()						#Fermeture connexion vers BDD
    response ={'liste_objets':informations}
    #Je retourne les informations de ma table équipe
    return json.dumps(response), 201, {'Content-Type': 'application/json'}

####################################################################


@app.route('/images',methods=['GET'])
def shownbequipes():
    print('open db')
    db = Db()  # Ouverture connexion vers BDD
    informations = db.select('SELECT * FROM image;')  # Je récupère les informations de ma table équipe
    db.close()
    print('return')
    return json.dumps(informations), 201, {'Content-Type': 'application/json'}

@app.route('/images/<int:id>', methods=['GET'])
def get_images_equipe(id):
    db = Db()						#Ouverture connexion vers BDD
    informations = db.select("SELECT * FROM image WHERE e_id = '%s';" % (id))	#Je récupère les informations de ma table objet
    db.close()						#Fermeture connexion vers BDD
    response ={'liste_images':informations}
    #Je retourne les informations de ma table équipe
    return json.dumps(response), 201, {'Content-Type': 'application/json'}



@app.route('/newcontainer', methods=['post'])
def ajout_container():
    data = request.get_json()
    ip = 'http://' + data['ip'] + ':5000'
    print(data)
    db = Db()
    db.execute("UPDATE equipe SET e_ip = ('%s') WHERE e_ip IS NULL;" % (ip))
    db.close()
    return '200', 200, {'Content-Type': 'application/json'}

@app.route('/objup/<int:id>', methods=['get'])
def incrobjpoints(id):
    data = request.get_json()
    db = Db()
    points = db.select("SELECT o_points FROM objet where o_id = '%s';" % (id))
    point = points[0]['o_points']
    point += 1
    db.execute("UPDATE objet SET o_points =%s WHERE o_id =%s;" % (point, id))
    db.close()
    return '200', 200, {'Content-Type': 'application/json'}

@app.route('/objdown/<int:id>', methods=['get'])
def decrobjpoints(id):
    data = request.get_json()
    print(data)
    db = Db()
    points = db.select("SELECT o_points FROM objet where o_id = '%s';" % (id))
    point = points[0]['o_points']
    point -= 1
    if (point < 0):
        point = 0
    db.execute("UPDATE objet SET o_points =%s WHERE o_id =%s;" % (point, id))
    db.close()
    return '200', 200, {'Content-Type': 'application/json'}


############ INSCRIPTION ET AFFECTATION DE MISSION #################
@app.route('/inscription', methods=['post'])
def ajout_inscrit():
    db = Db()                       #Ouverture de la connection avec la BDD
    data = request.get_json()       #Récupération de la requète
    print(data)

    #Je recherche dans la base si une équipe a déjà ce nom
    resultat_recherche_equipe = db.select("SELECT * FROM equipe where e_name = '%s';" % (data['nom_equipe']))

    #Si la taille de mon élement est vide alors cet intitulé n'est pas dans la base
    if (len(resultat_recherche_equipe) != 0):
        print('Log - Utilisateur déjà dans la base')
        db.close()			     #Fermeture de la connection avec la BDD
        return json.dumps({'response': 'utilisateur_existant'}), 400, {'Content-Type': 'application/json'}

    if (len(resultat_recherche_equipe) == 0):
        #J'ajoute l'équipe dans la BDD avec en paramètre son nom et son état par défaut qui est nothingtoshow.
        db.execute("INSERT INTO equipe(e_name,e_etat) VALUES ('%s','%s');"%(data['nom_equipe'],'nothingtoshow'))
        # Je refais une recherche dans la base avec le nom de l'équipe pour rappatrier l'id
        resultat_recherche_equipe = db.select("SELECT * FROM equipe where e_name = '%s';" % (data['nom_equipe']))
        print('Log - Création utilisateur dans la base')

        listeobjetacree=['arm','dog','screen']
        #J'ajoute dans la base des objets à trouver pour l'équipe crée
        for objet in listeobjetacree:
            db.execute("INSERT INTO objet(o_name, o_found, o_points, e_id) VALUES ('%s','%s','%s','%s');"%(objet, 'false',0, resultat_recherche_equipe[0]['e_id']))

        #Je recupère les objets à trouver pour les équipes et je renvois ça au client android
        req = db.select("SELECT * FROM objet where e_id = '%s';" % (resultat_recherche_equipe[0]['e_id']))
        #print(response)
        response = {"objet":req}
        db.close() 			 #Fermeture de la connection avec la BDD
        requests.post('http://172.30.1.154:8080/v1-webhooks/endpoint?key=PEKtcWFHQfKud5mqtSUFRVtibxuGn0bDJJOnd2Az&projectId=1a116')
        return json.dumps(response), 200, {'Content-Type': 'application/json'}

####################################################################


#Poster une image
@app.route('/postimage', methods=['post'])
def envoyerimagegoogle():

    datas = request.get_json()		#Recuperation de la requete du client android
    db = Db()  # Ouverture de la connection avec la BDD

    resultat_recherche_equipe = db.select("SELECT * FROM equipe where e_name = '%s';" % (datas['nom_equipe']))
    resultat_id_equipe = resultat_recherche_equipe[0]['e_id']
    resultat_IP_equipe = resultat_recherche_equipe[0]['e_ip']

    print(resultat_IP_equipe)
    toSend = json.dumps({'image':datas['image']})
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    response = requests.post(resultat_IP_equipe + '/analyse', data=toSend, headers=headers)
    dataVision = response.json()


    label = dataVision['label']
    print(label)
    imageIN = datas['image']
    #Pour les logs j'affiche ce qu'à déterminé GOOGLE VISION




    try:
        db.execute("INSERT INTO image(i_coordx, i_coordy, i_base64, e_id) VALUES ('%s','%s','%s','%s');"%(datas['lat'], datas['long'], datas['image'],resultat_id_equipe))
    except:
        db.execute("INSERT INTO image(i_base64, e_id) VALUES ('%s','%s');" % (imageIN, resultat_id_equipe))

    #Je recupère les objets à trouver pour les équipes et je renvois ça au client android
    objetsdelequipe = db.select("SELECT * FROM objet where e_id = '%s';" % (resultat_id_equipe))
    print(objetsdelequipe)

    var_sortie = 0			#Je met la variable temporaire à zero, elle me servira à voir si l'image doit passer à fail ou non.

    for i in range(0,len(objetsdelequipe)):	#Pour chaque objet de l'équipe :

    #SI l'objet correspond à ce qu'à déterminé google, j'update l'état de mon équipe avec imgsucess.
	#Ensuite je sort de ma boucle
        if objetsdelequipe[i]['o_name'] == label :
           if objetsdelequipe[i]['o_found']=='false':
      
                db.execute('UPDATE equipe Set e_etat =%s WHERE e_name = %s;',('imgsuccess',datas['nom_equipe']))
                #RAJOUTER L'UPDATE DE X Y Z ETC
                db.execute('UPDATE objet Set o_found =%s, o_points =%s WHERE e_id = %s AND o_name=%s;',('true',1,resultat_id_equipe,label))
                var_sortie = 1
		response ={"response":"Photo analysee"}
           else :		
		response ={"response":"Objet déjà trouvé !"}
           break

    #SI l'objet ne correspond pas à un objet de ma liste
    if var_sortie == 0 :
        db.execute('UPDATE equipe Set e_etat =%s WHERE e_name = %s;',('imgfail',datas['nom_equipe']))
        response ={"response":"Photo analysee"}

    db.close()				#Fermeture de la connection avec la BDD
    return json.dumps(response), 201, {'Content-Type': 'application/json'}




if __name__ == "__main__":
  app.run(host="0.0.0.0", port=5000)
