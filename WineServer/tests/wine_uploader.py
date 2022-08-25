import requests
import os, shutil

import sys
import argparse

DOMAIN = '127.0.0.1'

if __name__ == '__main__':
    domain = DOMAIN
    # parse args using argparse
    parser = argparse.ArgumentParser('假酒上传工具', add_help=False)
    parser.add_argument('--wine_name', help='wine name')
    parser.add_argument('--wine_degree', help='wine degree')
    parser.add_argument('--intensity_num', help='intensity num')
    parser.add_argument('--device_name', help='device name')

    parser.add_argument('--txt_file', help='txt file path')
    parser.add_argument('--image_file', help='image file path')

    parser.add_argument('--domain', help='wine server domain')
    parser.add_argument('--port', help='wine server port', required=False, default='8888')
    args, unparsed = parser.parse_known_args()

    domain = args.domain if args.domain else DOMAIN
    port = args.port

    param_error = False
    if not os.path.exists(args.image_file):
        print('error: image file', args.image_file, 'not exists !!!')
        param_error = True

    if not param_error:
        files = {'txt_file': open(args.txt_file, 'rb')
            , 'image_file': open(args.image_file, 'rb')}
        values = {'wine_name': args.wine_name, 'wine_degree': args.wine_degree, 'intensity_num': args.intensity_num,
                  'device_name': args.device_name}

        r = requests.post(f'http://{domain}:{port}/wineapi', files=files,
                          data=values)

        print('response code:', r.status_code)
