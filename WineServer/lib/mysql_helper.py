import json
from lib import mylib as ml
import datetime
import dbutils.pooled_db as pdb
import pymysql

# mysql
# ? query is not supported

# don't close , otherwise the connection will be really slow
_configs = {
    'host': '39.100.158.75',
    'user': 'root',
    'passwd': 'sq12345678',
    'db': 'wine_db'
}


pools = []


def _get_pool() -> pdb.PooledDB:
    if not pools or not pools[0]:
        pools.append(pdb.PooledDB(creator=pymysql,
                                  maxconnections=0,  # 连接池允许的最大连接数，0和None表示不限制连接数
                                  mincached=0,  # 初始化时，链接池中至少创建的空闲的链接，0表示不创建
                                  maxcached=0,  # 链接池中最多闲置的链接，0和None不限制
                                  maxusage=0,  # 一个链接最多被重复使用的次数，None表示无限制
                                  blocking=True,  # 连接池中如果没有可用连接后，是否阻塞等待。True，等待；False，不等待然后报错
                                  port=3306,
                                  host=_configs['host'], user=_configs['user'], passwd=_configs['passwd'],
                                  db=_configs['db'],
                                  use_unicode=True,
                                  charset='utf8')
                     )
    return pools[0]


import re


def _script_wrapper(s):
    s2 = re.sub(r'(?<=\W)\?(?=([\W]|$))', '%s', s)
    print(s, '\n... converted to ...\n', s2)
    return s2


def _get_db():
    db = _get_pool().connection()

    # db = pymysql.connect(host=_configs['host'], user=_configs['user'], passwd=_configs['passwd'], db=_configs['db'])
    return db


def exec_sql(
        sql_str
        , *params, json_str=True, single_result_detection=True):
    sql_script = sql_str

    db = _get_db()

    # https://stackoverflow.com/questions/15856976/transactions-with-python-sqlite3
    db.isolation_level = None

    cursor = db.cursor(pymysql.cursors.DictCursor)

    l_result = None
    try:
        cursor.execute(_script_wrapper(sql_script), params)
        rows = cursor.fetchall()

        db.commit()
        # db.close()
    except Exception as err:
        rows = []
        ml.error_log(sql_script + '\n' + str(err) + '\n')
        # return Response(False, None)

    l_result = rows
    if single_result_detection and len(l_result) == 1:
        l_result = l_result[0]

    if json_str:
        success_rt = json.dumps(l_result)  # CREATE JSON
    else:
        success_rt = l_result
    return success_rt
