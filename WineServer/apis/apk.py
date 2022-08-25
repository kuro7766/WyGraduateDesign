import os.path

from lib import mylib

from wsgiref.util import FileWrapper

from lib.http_response import *


def update(request):
    if request.GET.get('version') and os.path.exists('data/version.txt'):
        version = request.GET.get('version')
        print(version ,mylib.read_string('data/version.txt'))
        if version != mylib.read_string('data/version.txt'):
            return HttpCrossDomainJsonResponse({'code': True, 'msg': '发现新版本'})

    return HttpCrossDomainJsonResponse({'code': False, 'msg': '已经是最新版本'})

def app(request):
    filename = "data/app.apk"
    wrapper = FileWrapper(open(filename))
    response = HttpCrossDomainResponse(wrapper, content_type='text/plain')
    response['Content-Disposition'] = 'attachment; filename=%s' % os.path.basename(filename)
    response['Content-Length'] = os.path.getsize(filename)
    return response


# @re_path_route('test/(\w{2,4})')
# def test(request,args):
#     return HttpResponse(f'your args , {args}')



# @path_route('aac')
# def aadfa(request):
#     # a=1/0
#     return HttpResponse('test')

