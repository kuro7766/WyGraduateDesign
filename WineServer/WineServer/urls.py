"""WineServer URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.urls import path

# >>> 这里必须要把需要的文件import进来，即使ide里是灰色，否则不会添加到urlpatterns
from apis import file_server, apk,database_api,predicts,app_data

# from apis import *
from lib.decorators import URLS

urlpatterns = [
    # path('admin/', admin.site.urls),
    path(r'wineapi', file_server.wine_file_upload),
    # path(r'all_wines', database_api.query),
#     appdownload
    path(r'appdownload', apk.app),
#     update
    path(r'update', apk.update),
]

# 注解部分的url
urlpatterns.extend(URLS)

# print(urlpatterns)
print('--- registered routes ---\n',(urlpatterns))