from sense_hat import SenseHat

# -*- coding: utf-8 -*-
import json, random
from flask import Flask, request
from flask_cors import CORS, cross_origin
from db import Db

app = Flask(__name__)
app.debug = True
CORS(app)

#Vider la base
@app.route('/reset',methods=['GET'])
def reset():
	sense = SenseHat()
	sense.show_message(":)",text_colour=(255, 0, 0))
	sense.show_message(":x",text_colour=(0, 255, 0))
	sense.show_message(":(",text_colour=(0, 0, 255))
	return "Reset done."

