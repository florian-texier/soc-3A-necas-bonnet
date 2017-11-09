from sense_hat import SenseHat
import requests
import time
from flask import Flask
valeur = 50
sense = SenseHat()
while  (1) :
	etats = requests.get('http://polar-bayou-90643.herokuapp.com/lireetat')
	t = requests.get('http://polar-bayou-90643.herokuapp.com/resetetat')
	print(etats.json())
	toto = etats.json()
	print(toto['objet'][1])
	etats=toto['objet']
	for etat in range(0,len(etats)):
		if (etats[etat] == 'a') : 
			sense.show_message('%s'%(etat), text_colour=(0,255,0))
			sense.show_message(":)",text_colour=(0, 255, 0))

		if (etats[etat] =='b') :
			sense.show_message('%s'%(etat), text_colour=(0,0,255))
			sense.show_message(":(",text_colour=(0, 0, 255))
		print(etats[etat])
	time.sleep(1)

