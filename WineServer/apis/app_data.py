# // python
# part

from lib.decorators import re_path_route, path_route
import json
import lib.mysql_helper as helper
import traceback
from lib.http_response import HttpCrossDomainResponse

@path_route('upload/wine')
def upload_wine(request):
    try:
        if request.method == 'POST':
            data = request.body.decode('utf-8')
            data = json.loads(data)
            print(data)
            id = data['id']
            name = data['name']
            desc = data['desc']
            img = data['img']
            device_id = data['device_id']
            # device_id = 1

            print('args',id, name, desc, img, device_id)
            helper.exec_sql('insert into wine_db.wine(id,name,description,img,device_id) values(?,?,?,?,?)',
                            id, name, desc, img, device_id)
            return HttpCrossDomainResponse('success')
    except Exception as e:
        print(traceback.format_exc())


@path_route('upload/wine_curve')
def upload_wine_curve(request):
    try:
        if request.method == 'POST':
            data = request.body.decode('utf-8')
            data = json.loads(data)
            id = data['id']
            curve_file = data['curve_file']
            curve_desc = data['curve_desc']
            wine_id = data['wine_id']
            device_id = data['device_id']

            helper.exec_sql('insert into wine_curve(id,curve_file,curve_desc,wine_id,device_id) values(%s,%s,%s,%s,%s)',
                            id, curve_file, curve_desc, wine_id, device_id)
            return HttpCrossDomainResponse('success')
    except Exception as e:
        print(traceback.format_exc())


@path_route('upload/wine_type')
def upload_wine_type(request):
    try:
        if request.method == 'POST':
            data = request.body.decode('utf-8')
            data = json.loads(data)
            id = data['id']
            name = data['name']
            desc = data['desc']
            device_id = data['device_id']

            helper.exec_sql('insert into wine_smell_type(id,name,description,device_id) values(%s,%s,%s,%s)',
                            id, name, desc, device_id)
            return HttpCrossDomainResponse('success')
    except Exception as e:
        print(traceback.format_exc())


@path_route('upload/wine_type_map')
def upload_wine_type_map(request):
    try:
        if request.method == 'POST':
            data = request.body.decode('utf-8')
            data = json.loads(data)
            id = data['id']
            wine_id = data['wine_id']
            type_id = data['type_id']
            device_id = data['device_id']

            helper.exec_sql('insert into wine_smell_type_map(id,wine_id,type_id,device_id) values(%s,%s,%s,%s)',
                            id, wine_id, type_id, device_id)
            return HttpCrossDomainResponse('success')
    except Exception as e:
        print(traceback.format_exc())


@path_route('upload/wine_curve_result')
def upload_wine_curve_result(request):
    try:
        if request.method == 'POST':
            data = request.body.decode('utf-8')
            data = json.loads(data)
            # id = data['id']
            curve_id = data['curve_id']
            degree = data['degree']
            type = data['type']
            is_error = data['is_error']
            device_id = data['device_id']
            print('----------curve-----------', curve_id, degree, type, is_error, device_id)
            helper.exec_sql(
                'insert into wine_curve_result(curve_id,degree,type,is_error,device_id) values(%s,%s,%s,%s,%s,%s)',
                 curve_id, degree, type, is_error, device_id)
            return HttpCrossDomainResponse('success')
    except Exception as e:
        print(traceback.format_exc())

