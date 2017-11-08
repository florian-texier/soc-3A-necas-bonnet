from sense_hat import SenseHat

from flask import Flask

app = Flask(__name__)


@app.route('/')
def index():
	sense.show_message(":)",text_colour=(255, 0, 0))
	sense.show_message(":x",text_colour=(0, 255, 0))
	sense.show_message(":(",text_colour=(0, 0, 255))
	return 'Hello world'


if __name__ == '__main__':
	app.run(debug=True, host='0.0.0.0')


