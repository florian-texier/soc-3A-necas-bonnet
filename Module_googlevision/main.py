# [START import_libraries]
import argparse
import base64

import googleapiclient.discovery
from google.oauth2 import service_account
# [END import_libraries]


def main(photo_file):
    """Run a label request on a single image"""

    # [START authenticate]
    credentials = service_account.Credentials.from_service_account_file('project-1a95fbdfa238.json')
    service = googleapiclient.discovery.build('vision', 'v1', credentials=credentials)
    # [END authenticate]

    # [START construct_request]
    with open(photo_file, 'rb') as image:
        image_content = base64.b64encode(image.read())
        service_request = service.images().annotate(body={
            'requests': [{
                'image': {
                    'content': image_content.decode('UTF-8')
                },
                'features': [{
                    'type': 'LABEL_DETECTION',
                    'maxResults': 1
                }]
            }]
        })
        # [END construct_request]
        # [START parse_response]
        response = service_request.execute()
        label = response['responses'][0]['labelAnnotations'][0]['description']
        print('Found label: %s for %s' % (label, photo_file))
        # [END parse_response]


# [START run_application]
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('image_file', help='The image you\'d like to label.')
    args = parser.parse_args()
    main(args.image_file)
# [END run_application]
