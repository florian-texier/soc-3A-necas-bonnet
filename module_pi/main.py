from sense_hat import SenseHat
import requests
import time
from flask import Flask
valeur = 50
sense = SenseHat()
while  (1) :
	etats = requests.get('http://polar-bayou-90643.herokuapp.com/lireetats')

	r = requests.get('http://polar-bayou-90643.herokuapp.com/resetetats')

	etats=etats.json()['objet']
	for i in range(0,len(etats)):
		if (etats[i]['etat'] == 'imgsuccess') : 
			sense.show_message('%s'%(etats[i]['id']), text_colour=(0,255,0))
			sense.show_message(":)",text_colour=(0, 255, 0))

		if (etats[i]['etat'] =='imgfail') :
			sense.show_message('%s'%(etats[i]['id']), text_colour=(0,0,255))
			sense.show_message(":(",text_colour=(0, 0, 255))
		print(etats[i])
	print(' ')
	time.sleep(10)

