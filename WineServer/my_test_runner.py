import json

import global_variants
from tests import a
from lib import mylib, mysql_helper

if __name__ == '__main__':
    # a.file_uploader()
    # a.request_snapshot()
    # a.mysql_test()
    # language=sql
    # print(mysql_helper.exec_sql('''
    # select SUBSTRING(wine_type.image_path,0,16) as img,1 from wine_db.wine_type
    # '''))
    # print(mysql_helper.exec_sql('''
    # select * from wine_db.wine_type
    # ''',single_result_detection=False))
    import requests
    # r=requests.get(f'http://{global_variants.domain}:{global_variants.port}/update?version=1.0.0')
    # r=requests.get(f'http://{global_variants.domain}:{global_variants.port}/aac')
    l = [i for i in range(1000)]
    print(l)
    r=requests.post(f'http://{global_variants.domain}:{global_variants.port}/predict', data={
        'curve': json.dumps({'data':l})
    })
    print(r.text)