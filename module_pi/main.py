from sense_hat import SenseHat
import requests
import time
from flask import Flask
valeur = 50
sense = SenseHat()
while  (1) :
	etats = requests.get('http://polar-bayou-90643.herokuapp.com/lireetat')
	t = requests.get('http://polar-bayou-90643.herokuapp.com/resetetat')
	toto = etats.json()
	etats=toto['objet']
	for etat in range(0,len(etats)):
		if (etats[etat]['etat'] == 'a') : 
			sense.show_message('%s'%(etats[etat]['id']), text_colour=(0,255,0))
			sense.show_message(":)",text_colour=(0, 255, 0))

		if (etats[etat]['etat'] =='b') :
			sense.show_message('%s'%(etats[etat]['id']), text_colour=(0,0,255))
			sense.show_message(":(",text_colour=(0, 0, 255))
		print(etats[etat])
	print(' ')
	time.sleep(10)

