#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
import sys

import global_variants
from lib import mylib, mysql_helper
from tests import a


def main():
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'WineServer.settings')
    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)


# language=sql
def initialize_db():
    mysql_helper.exec_sql('''
    create table if not exists wine_type (
        id int auto_increment primary key,
        wine_name varchar(255),
        wine_degree double,
        file_path varchar(255),
        image_path varchar(255),
        intensity_num int,
        device_name varchar(255),
        extra_info varchar(255)
    )
    ''')

    mysql_helper.exec_sql(
        '''CREATE TABLE if not exists wine (id int,name varchar(255),description varchar(255),img varchar(255),device_id varchar(255))''')
    mysql_helper.exec_sql(
        '''CREATE TABLE if not exists wine_curve (id int,curve_file varchar(255),curve_desc varchar(255),wine_id int,device_id varchar(255))''')
    mysql_helper.exec_sql(
        '''CREATE TABLE if not exists wine_smell_type (
        id int,name varchar(255),description varchar(255),device_id varchar(255)
        )
        ''')
    mysql_helper.exec_sql(
        '''CREATE TABLE if not exists wine_smell_type_map (id int,wine_id int,type_id int,device_id varchar(255))''')

    mysql_helper.exec_sql(
        '''CREATE TABLE if not exists wine_curve_result (curve_id int,degree varchar(255),type varchar(255),is_error int,device_id varchar(255))''')


if __name__ == '__main__':
    mylib.ensure_dir(global_variants.wine_data_path)
    mylib.ensure_dir('./data/')

    initialize_db()
    main()
