from sense_hat import SenseHat
import requests
import time
from flask import Flask
valeur = 50
sense = SenseHat()
while  (1) :
	r = requests.get('http://polar-bayou-90643.herokuapp.com/test')
	print(r.text)
	if (r.text == 'a') : 
		sense.show_message(":)",text_colour=(0, 255, 0))

	if (r.text =='b') :
		sense.show_message(":(",text_colour=(0, 0, 255))
	if(r.text =='c'):
		sense.show_message(":x",text_colour=(255,0,0))
	time.sleep(1)
