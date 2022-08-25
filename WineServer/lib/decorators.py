from django.conf import settings
from importlib import import_module
# from django.utils.importlib import import_module
from django.conf.urls import url


# def URL(path):
#     # print('initilizing decorator')
#     path = r'^%s$' % path[1:]  # Add delimiters and remove opening slash
#
#     def decorator(view):
#
#         urls = import_module(settings.ROOT_URLCONF)
#         urls.urlpatterns += url('', url(path, view))
#         # _decorator_urls += path(path, view)
#
#         return view
#
#     return decorator
from django.http import HttpResponse

from django.urls import path,re_path

URLS = []
URLS_d = {}


def path_route(url_path=''):
    # def wrapper(func):
    #     path_name = path or func.__name__
    #     URLS.append(
    #         djpath(path_name, func)
    #     )
    #
    #     path_func = URLS_d.get(path_name, None)
    #     if path_func:
    #         print(path_func, '<>', func)
    #         raise Exception('THE same path')
    #     URLS_d[path_name] = func
    #     ### above is not important ####

    def decorator(view):
        # urls = import_module(settings.ROOT_URLCONF)
        print(url_path,'->',view)

        URLS.append(path(url_path, view))
        # _decorator_urls += path(path, view)

        return view
    # return wrapper
    return decorator

def re_path_route(url_path=''):
    # def wrapper(func):
    #     path_name = path or func.__name__
    #     URLS.append(
    #         djpath(path_name, func)
    #     )
    #
    #     path_func = URLS_d.get(path_name, None)
    #     if path_func:
    #         print(path_func, '<>', func)
    #         raise Exception('THE same path')
    #     URLS_d[path_name] = func
    #     ### above is not important ####

    def decorator(view):
        # urls = import_module(settings.ROOT_URLCONF)
        print(url_path,'->',view)

        URLS.append(re_path(url_path, view))
        # _decorator_urls += path(path, view)

        return view
    # return wrapper
    return decorator
