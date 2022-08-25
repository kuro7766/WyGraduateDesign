from lib import mysql_helper

from lib import http_response
from lib.decorators import *


@path_route('all_wines')
def query(request):
    json_str = mysql_helper.exec_sql('''
        select * from wine_db.wine_type
        ''', single_result_detection=False)

    return http_response.HttpCrossDomainResponse(json_str)
