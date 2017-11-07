from google.cloud import vision
import google.oauth2.credentials


credentials = google.oauth2.oauth2.credentials.Credentials('access_token',refresh_token='refresh_token',token_uri='token_uri',client_id='client_id',client_secret='client_secret')


#client = Client(credentials=creds)

#client = vision.Client()

autorisation = vision.from_service_account_json('C:/Users/Jerome/Desktop/try-apis-bfc045e18c6d.json')
client = vision.ImageAnnotatorClient(credentials=credentials)
response = client.annotate_image({ 'image': {'source': {'image_uri': 'http://dinoanimals.pl/wp-content/uploads/2013/04/Lama-lamy-DinoAnimals.pl-1.jpg'}},'features': [{'type': vision.enums.Feature.Type.FACE_DETECTION}],})
print(len(response.annotations))



#client = vision.Client()
#credentials = service_account.Credentials.from_service_account_file('C:/Users/Jerome/Desktop/try-apis-bfc045e18c6d.json')
#scoped_credentials = credentials.with_scopes(['https://www.googleapis.com/auth/cloud-platform'])
#credentials = credentials(scoped_credentials)

#client = datastore.Client()
#client = from_service_account_json(' C:/Users/Jerome/Desktop/try-apis-bfc045e18c6d.json')
#GOOGLE_APPLICATION_CREDENTIALS='C:/Users/Jerome/Desktop/try-apis-bfc045e18c6d.json'

