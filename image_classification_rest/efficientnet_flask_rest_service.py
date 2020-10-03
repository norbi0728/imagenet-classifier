from flask import Flask, jsonify, request
import tensorflow as tf
import numpy as np

def file_reader(file_name):
  for line in open(file_name, "r"):
    yield line

credentials_gen = file_reader("credentials.txt")
email = next(credentials_gen).replace('\n', "")
password = next(credentials_gen).replace('\n', "")

model = tf.keras.applications.EfficientNetB7()

input_height = model.input_shape[1]
input_width = model.input_shape[2]



app = Flask(__name__)

@app.route("/prediction", methods=['POST'])
def predict_image_class():
	data = request.files['image'] #at this point data is a FileStorage object and to get the byte data out of it, it's read() method needs to be called like in case of open() function
	data = data.read()
	image = tf.image.decode_jpeg(data)
	image = tf.image.resize(image, (input_height, input_width)).numpy()
	image = image.reshape(1, image.shape[0], image.shape[1], image.shape[2])
	image = tf.keras.applications.efficientnet.preprocess_input(image)
	pred = model.predict(image)
	pred = tf.keras.applications.efficientnet.decode_predictions(pred) #example label: 
																		#[[('n02107142', 'Doberman', 0.8203237),
																		#('n02107312', 'miniature_pinscher', 0.0028763188),
																		#('n02109047', 'Great_Dane', 0.0012315626),
																		#('n02089078', 'black-and-tan_coonhound', 0.0011360319),
																		#('n02106550', 'Rottweiler', 0.0011076091)]]
	label = pred[0][0][1] # Dobermann in the above example
	confidence = pred[0][0][2] # 0.8203237
	print(label, confidence)
	return label + ';' + str(confidence)

@app.route("/credentials")
def get_credentials():
	return email + ';' + password

@app.route("/classes")
def get_classes():
	to_send = ""
	possible_predictions = tf.keras.applications.efficientnet.decode_predictions(np.arange(1000).reshape(1, 1000), top=1000) #shows the possible labels with additional data which I don't need
	for item in possible_predictions[0]:
	  to_send += ';' + item[1]
	return to_send[1:]

if __name__ == '__main__':
	app.run(debug=False,host='0.0.0.0')