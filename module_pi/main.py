from sense_hat import SenseHat
import requests
import time
from flask import Flask

sense = SenseHat()
while  (1) :
	#Je fais une requête pour récupérer tous les états des équipes 
	etats = requests.get('http://polar-bayou-90643.herokuapp.com/lireetats')
	
	#Je fais une requête pour reset les états des équipes (Pour ne pas re-afficher)
	r = requests.get('http://polar-bayou-90643.herokuapp.com/resetetats')

	#Je stocke le tableau d'états
	etats=etats.json()['objet']
	
	#Pour chaque état, si j'ai un succés ou un echec et j'affiche sur la Pi :
	for i in range(0,len(etats)):
		if (etats[i]['etat'] == 'imgsuccess') : 
			sense.show_message('%s'%(etats[i]['id']), text_colour=(0,255,0))
			sense.show_message(":)",text_colour=(0, 255, 0))

		if (etats[i]['etat'] =='imgfail') :
			sense.show_message('%s'%(etats[i]['id']), text_colour=(0,0,255))
			sense.show_message(":(",text_colour=(0, 0, 255))
		print(etats[i])
	print(' ')
	
	#Je fais un sleep pour ne pas harceler de manière violente le serveur
	time.sleep(10)

