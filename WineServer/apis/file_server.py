import hashlib
import time
import traceback
import urllib

import global_variants
from lib import mylib, mysql_helper
from django.http import HttpRequest, HttpResponseNotFound
from lib import http_response
import os
import global_variants as gv

base_path = global_variants.wine_data_path


# 白酒上传工具请求参数
# params['wine_name'], params['wine_degree'] 白酒名称和白酒度数
# r.POST['intensity_num'],r.POST['device_name']  激光强度和对应的设备
# txt_file = request.FILES['txt_file']  曲线的数据文件
# image_file = request.FILES['image_file']  曲线对应的白酒图片

def wine_file_upload(r: HttpRequest):
    request = r

    # url必须先unquote转码，不然会出现意外情况
    params = mylib.extract_url_param_dict(urllib.parse.unquote(r.get_full_path()))

    try:
        if r.method == 'POST':

            mylib.ensure_dir(base_path)

            # mylib.exec_sql('''
            # insert into wine_type values (null,?,?,?,?)
            # ''')
            txt_file = request.FILES['txt_file']
            image_file = request.FILES['image_file']
            timestamp = int(time.time() * 1000000)
            txt_file_name = '{}.{}'.format(timestamp, 'txt')

            md5 = hashlib.md5()
            for chunk in image_file.chunks():
                md5.update(chunk)
            md5sum = md5.hexdigest()
            image_file_name = '{}.{}'.format(md5sum, 'jpg')

            with open(os.path.join(base_path, txt_file_name), 'wb+') as destination:
                for chunk in txt_file.chunks():
                    destination.write(chunk)

            # 手机相机拍摄的图片较大，上传100张占用大量存储，md5去重
            if not os.path.exists(os.path.join(base_path, image_file_name)):
                with open(os.path.join(base_path, image_file_name), 'wb+') as destination:
                    for chunk in image_file.chunks():
                        destination.write(chunk)

            with gv.sqlite_lock:
                print(r.POST['wine_name'])
                mysql_helper.exec_sql('''
                insert into wine_db.wine_type(wine_name,wine_degree,file_path,image_path,intensity_num,device_name) 
                values (?,?,?,?,?,?)
                ''', r.POST['wine_name'], r.POST['wine_degree'], txt_file_name, image_file_name,
                                      r.POST['intensity_num'],
                                      r.POST['device_name'])

            return http_response.HttpOk()
    except Exception as e:
        print(e)
        with open('errors_detailed.txt', 'a+') as err_f:
            err_f.write('\n' + mylib.datetime_now())
            traceback.print_exc(file=err_f)
            err_f.close()
        mylib.error_log(str(e))

    return http_response.HttpFailure()

from lib.decorators import path_route

@path_route('upload/file/(.*)')
def file_upload(request: HttpRequest,file_name):
    # request.FILES['file']

    txt_file = request.FILES['file']
    
    # timestamp = int(time.time() * 1000000)
    # txt_file_name = '{}.{}'.format(timestamp, 'txt')

    txt_file_name = file_name


    with open(os.path.join(base_path, txt_file_name), 'wb+') as destination:
        for chunk in txt_file.chunks():
            destination.write(chunk)

    print(txt_file_name)
    return http_response.HttpOk()