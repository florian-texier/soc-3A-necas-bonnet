from sense_hat import SenseHat
import requests
import time
from flask import Flask

adresseserveur="http://172.30.0.147:5000"
sense = SenseHat()
while  (1) :
	#Je fais une requête pour récupérer tous les états des équipes 
	etats = requests.get(adresseserveur+'/lireetats')

	#Je fais une requête pour reset les états des équipes (Pour ne pas re-afficher)
	r = requests.get(adresseserveur+'/resetetats')

	#Je stocke la reponse et la json dumps
	temporaire=etats.json()

	#Je  stocke uniquement le tableau d'états
	etats = temporaire['objet']

	#Pour chaque état, si j'ai un succés ou un echec et j'affiche sur la Pi :
	for i in range(0,len(etats)):
		if (etats[i]['etat'] == 'imgsuccess') : 
			sense.show_message('%s'%(etats[i]['id']), text_colour=(0,255,0))
			sense.show_message(":)",text_colour=(0, 255, 0))

		if (etats[i]['etat'] =='imgfail') :
			sense.show_message('%s'%(etats[i]['id']), text_colour=(255,0,0))
			sense.show_message(":(",text_colour=(255, 0, 0))
		print(etats[i])
	print(' ')

	#Je fais un sleep pour ne pas harceler de manière violente le serveur
	time.sleep(5)

