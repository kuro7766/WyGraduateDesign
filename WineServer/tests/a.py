import time

import requests
import os, shutil
from lib import mylib, mysql_helper
import sys

DOMAIN = '39.100.158.75'


def file_uploader():
    files = {'txt_file': open('tests/001-1637157394258.txt', 'rb')
        , 'image_file': open('tests/2021-12-17 12.26.49.jpg', 'rb')}
    values = {'wine_name': '茅台', 'wine_degree': 20, 'intensity_num': 10,
              'device_name': 'gpry2000'}
    r = requests.post(f'http://{DOMAIN}:9998/wineapi', files=files,
                      data=values)


def request_snapshot():
    mylib.ensure_dir('tmp')

    # save r as a file
    i = 0
    # start time
    start_time = time.time()
    while i < 270:
        # with open('snapshot.jpg', 'wb') as f:
        #     f.write(r.content)
        r = requests.get(f'http://192.168.137.62:8083/?action=snapshot',
                         headers={'Cache-Control': 'no-cache'})
        # with open(f'tmp/snapshot{i}.jpg', 'wb') as f:
        #     f.write(r.content)
        i += 1
        # print(i)

    print(f'{time.time() - start_time} for 270 images')


def mysql_test():
    # # language=SQL

    while True:
        # # language=SQL
        mysql_helper.exec_sql('''
        insert into test_table(b) values('hello')
        ''')

    # language=SQL
    # mysql_helper.exec_sql('''
    # create database if not exists test_db
    # ''')
