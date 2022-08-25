### update log:
### 2021.6.19 fix readstring bug
### 2021.6.21 add file compare
# 11.28 cv2 region screen shot
import sys
import hashlib
import functools
import filecmp
import re
import os
from warnings import warn
import base64
from sys import platform
import shutil
# from tendo import singleton
# global utils , from Util
from pathlib import Path

SCREEN_SHOT_BASE_DIR = 'tmp/'
# 可以判断数字
DESKTOP = r'C:/Users/1/Desktop/'
WORKSPACE = DESKTOP + r'workspace/'
WORKSPACEINPUT = WORKSPACE + 'input/'
WORKSPACEOUTPUT = WORKSPACE + 'output/'
DOMAIN = 'kuroweb.tk'
# 分割长度 last:50
SPLIT = 40

MINUTE_MILLIS = 60000


def init_cross_platform_variables():
    ensure_dir(DESKTOP)
    ensure_dir(WORKSPACE)
    ensure_dir(WORKSPACEINPUT)
    ensure_dir(WORKSPACEOUTPUT)
    if platform == "linux" or platform == "linux2":
        # linux
        pass
    elif platform == "win32" or platform == "win64":
        pass


from aip import AipOcr

""" 你的 APPID AK SK """
APP_ID = '18945603'
API_KEY = 'mRU4KX0VG5UNGmqptK94m9mG'
SECRET_KEY = 'oovGS8Hn3o44C817PONPBkIqtFiepX4l'

client = AipOcr(APP_ID, API_KEY, SECRET_KEY)


# bad implementation use log_print instead
def log(s):
    print(s)

    with open('ml_logs.txt', 'a+', encoding='utf-8') as f:
        f.write(s)
        f.close()


def json_decode(json):
    encoded = json
    decoded = {}
    for i in encoded:
        if type(encoded[i]) == str:
            decoded[base64decode(str(i))] = base64decode((encoded[i]))
        else:
            decoded[base64decode(str(i))] = encoded[i]
    print(decoded)
    return decoded


def json_encode(json):
    x = json
    encoded = {}
    for i in x:
        if type(x[i]) == str:
            encoded[base64encode(str(i))] = base64encode((x[i]))
        else:
            encoded[base64encode(str(i))] = x[i]
    print(encoded)
    return encoded


def windows_file_name(file_name='default'):
    x = re.sub(r'[\\/:*?"<>|\'\n$]', '', file_name)
    x = x[0:30]
    return x


def get_file_size(path):
    return os.path.getsize(path)


def base64encode(s):
    en = (base64.encodebytes(bytes(s, encoding='utf-8')).decode('utf-8')).replace('\n', '')
    return en


def exists(path):
    return os.path.exists(path)


def extract_url_param_dict(full_path_info):
    dic = {}
    param_container = re.findall(r'(?<=\?).*', full_path_info)
    if param_container:
        param = param_container[0]
        params = param.split('&')
        for par in params:
            key, value = par.split('=', maxsplit=1)
            dic[key] = url_decode(value)
    return dic


def extract_url_path_list(full_path_info):
    """
    # on request http://127.0.0.1:8000/test/w/
    # this function /test/w/
    :param full_path_info:
    :return: a list of vars length > 1
    """
    rt = []
    # print(r.path)
    # print(r.get_full_path_info())
    full_path_info = re.findall(r'[^?]*', full_path_info)[0]
    panthers = str(full_path_info).split('/')
    for i in panthers:
        if i:
            rt.append(i)
    return rt


def log_print(*objs):
    error_log(*objs, output_file='ml_log.txt')
    # def error_log(*objs):
    #     write_string_append('errors.txt', str(datetime.datetime.now()) + ': ' + str(' '.join([str(obj) for obj in objs])) + '\n')


def datetime_now():
    return str(datetime.datetime.now())


def base64decode(s):
    de = (base64.decodebytes(bytes(s, encoding='utf-8')).decode('utf-8'))
    return de


def time_():
    return time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())


def screen_shot(target=''):
    name = ''
    if (target):
        name = target
    else:
        name = str(time.time())
        try:
            os.remove(f'{SCREEN_SHOT_BASE_DIR + str(name)}.png')
        except:
            pass
    ensure_dir(SCREEN_SHOT_BASE_DIR)
    os.system(f'ffmpeg -f gdigrab -i desktop -frames:v 1 {SCREEN_SHOT_BASE_DIR + str(name)}.png')
    return SCREEN_SHOT_BASE_DIR + str(name) + '.png'


def high_light_string(string_highlight, full_str):
    if (full_str):
        return full_str.replace(string_highlight, '\033[1;31;40m' + string_highlight + '\033[0m')
    else:
        return '\033[1;31;40m' + string_highlight + '\033[0m'


def get_screen_string():
    return ''.join(get_screen_string_list())


def get_screen_string_list():
    oo = ocr(screen_shot())
    s = []
    for i in oo['words_result']:
        s.append(i['words'])
    return s


def pic_to_string(pic_path):
    oo = ocr(pic_path)
    s = []
    # print(oo)
    if oo.__contains__('words_result'):
        for i in oo['words_result']:
            s.append(i['words'])
    return ''.join(s)


def region_ocr(l, t, r, b):
    return pic_to_string(region_screenshot(l, t, r, b))


def region_screenshot(l, t, r, b):
    # from PIL import Image
    import cv2
    output = 'tmp/quick/0.png'
    ensure_dir('tmp/quick')
    pic = screen_shot()

    img = cv2.imread(pic)
    crop_img = img[int(t):int(b), int(l):int(r)]
    cv2.imwrite(output, crop_img)
    return output
    # im = Image.open(pic)
    # region_chat = im.crop((l, t, r, b))
    # region_chat.save(output)
    # return output


def ocr(file_path):
    """ 读取图片 """

    def get_file_content():
        with open(file_path, 'rb') as fp:
            return fp.read()

    try:
        image = get_file_content()
        """ 调用通用文字识别, 图片参数为本地图片 """
        client.basicGeneral(image)

        """ 如果有可选参数 """
        options = {}
        options["language_type"] = "CHN_ENG"
        options["detect_direction"] = "true"
        options["detect_language"] = "true"
        options["probability"] = "true"

        """ 带参数调用通用文字识别, 图片参数为本地图片 """
        return client.basicGeneral(image, options)
    except Exception as e:
        print(e)
        time.sleep(1)
        return ocr(file_path)


def date_string():
    return str(datetime.datetime.now())[:10]


def compare_map(x, y):
    shared_items = {k: x[k] for k in x if k in y and x[k] == y[k]}
    return shared_items


def same_map(x, y):
    if len(x) != len(y):
        return False
    r = compare_map(x, y)
    if len(r) == len(x):
        return True
    return False


# deprecated! just check file path list same
def check_dir_same(file_path):
    import os
    data_path = 'var/file_state_dict.obj'
    ensure_dir('var')
    last_capture = {}

    if os.path.exists('var/file_state_dict.obj'):
        last_capture = read_obj(data_path)

    current = {}
    for file in getAllFileRecursively(file_path):
        sts = os.stat(file)
        modify = sts.st_mtime
        current[file] = modify

    x = same_map(current, last_capture)

    write_obj(data_path, current)
    return x


def cmp_file(a, b):
    return filecmp.cmp(a, b, shallow=False)


def cmp_dir(a, b):
    # parent1=parent_dir(a)
    # parent2=parent_dir(b)
    dir1s, files1 = getAllFiles_Dir_Files_tuple(a)
    dir1s_name = [path_to_filename(i) for i in dir1s]
    files1_name = [path_to_filename(i) for i in files1]
    dir2s, files2 = getAllFiles_Dir_Files_tuple(b)
    dir2s_name = [path_to_filename(i) for i in dir2s]
    files2_name = [path_to_filename(i) for i in files2]
    # print(dir1s_name,dir2s_name)
    # print(files1_name,files2_name)
    res = cmp_list(dir1s_name, dir2s_name)
    res1 = cmp_list(files1_name, files2_name)
    # print(res,res1)
    if res and res1:
        for f_name in files1_name:
            f1 = os.path.join(a, f_name)
            f2 = os.path.join(b, f_name)
            res_f = filecmp.cmp(f1, f2, shallow=True)
            # print(f1,f2)
            # print(filecmp.cmp(f1,f2))
            if res_f:
                pass
            else:
                # print('rt')
                return False

        for d_name in dir1s_name:
            # print(d_name)
            d1 = os.path.join(a, d_name)
            d2 = os.path.join(b, d_name)
            # print(d1,d2)
            res_d = cmp_dir(d1, d2)
            if res_d:
                pass
            else:
                return False

        return True
    else:
        return False


# b to a add and delete
def cmp_dir_add_delete_tuple(a, b, depth=0):
    add = []
    delete = []
    public_dir_name = []
    public_file_name = []
    dir1s, files1 = getAllFiles_Dir_Files_tuple(a)
    dir1s_name = [path_to_filename(i) for i in dir1s]
    files1_name = [path_to_filename(i) for i in files1]
    dir2s, files2 = getAllFiles_Dir_Files_tuple(b)
    dir2s_name = [path_to_filename(i) for i in dir2s]
    files2_name = [path_to_filename(i) for i in files2]

    res = sub(dir1s_name, dir2s_name)
    res_rev = sub(dir2s_name, dir1s_name)
    public_dir_name = set(dir1s_name).intersection(set(dir2s_name))
    delete.extend(os.path.join(a, res_item) for res_item in res)
    add.extend(os.path.join(b, res_rev_item) for res_rev_item in res_rev)

    res1 = sub(files1_name, files2_name)
    res1_rev = sub(files2_name, files1_name)
    public_file_name = set(files1_name).intersection(set(files2_name))
    delete.extend(os.path.join(a, res_item) for res_item in res1)
    add.extend(os.path.join(b, res_rev_item) for res_rev_item in res1_rev)

    for f_name in public_file_name:
        f1 = os.path.join(a, f_name)
        f2 = os.path.join(b, f_name)
        res_f = filecmp.cmp(f1, f2, shallow=False)
        # print(f1,f2)
        # print(filecmp.cmp(f1,f2))
        if res_f:
            pass
        else:
            delete.append(f1)
            add.append(f2)

    for d_name in public_dir_name:
        # print(d_name)
        d1 = os.path.join(a, d_name)
        d2 = os.path.join(b, d_name)
        res_d = cmp_dir_add_delete_tuple(d1, d2, depth=depth + 1)
        delete.extend(res_d[1])
        add.extend(res_d[0])

    if depth == 0:
        log_print('add:', (add))
        log_print('del', (delete))

    return [add, delete]


def cmp_list(a: list, b: list):
    sa = set(a)
    sb = set(b)
    return (not sa.difference(sb)) and (not sb.difference(sa))
    # return functools.reduce(lambda x, y : x and y, map(lambda p, q: p == q,a,b), True)
    # return set(a).difference(set(b))


def sub(a: list, b: list):
    return set(a).difference(b)


def get_baidu_recommended(keyword):
    url = f'https://www.baidu.com/sugrec?pre=1&p=3&ie=utf-8&json=1&prod=pc&from=pc_web&wd={keyword}&req=2&bs={keyword}&csor=2&pwd={keyword}&_={int(time.time() * 1000)}'
    import requests
    import json
    r = requests.get(url)
    # print(r.text)
    return json.loads(r.text)


def error_log(*objs, output_file='errors.txt'):
    write_string_append(output_file,
                        str(datetime.datetime.now()) + ': ' + str(' '.join([str(obj) for obj in objs])) + '\n')


def get_google_recommended(keyword):
    url = f'https://www.google.com.hk/complete/search?q={keyword}&cp=5&client=psy-ab&xssi=t&gs_ri=gws-wiz&hl=zh-CN&authuser=0&dpr=1.25'
    # url = f'https://www.baidu.com/sugrec?pre=1&p=3&ie=utf-8&json=1&prod=pc&from=pc_web&wd={keyword}&req=2&bs={keyword}&csor=2&pwd={keyword}&_={int(time.time() * 1000)}'
    import requests
    import json
    r = requests.get(url)
    # print(r.text)
    return json.loads(r.text[4:])
    # return json.loads(r.text)


def scanf():
    inputs = []
    while True:
        inputs.append(input())
        if inputs[len(inputs) - 1] == '0':
            inputs.pop(len(inputs) - 1)
            break
    s = ''
    for i in inputs:
        s += i + "\n"

    return s[:-1]


class waiting:
    def __init__(self, size):
        import time
        self.start = time.time()
        self.full_task_count = size
        self.progress = 0

    def countup(self):
        self.progress += 1

    def countup_print(self):
        self.countup()
        print((time.time() - self.start) * (self.full_task_count / self.progress))


def read_input():
    return read_string('../input/input.txt')


def random_pick(container, count):
    # group_of_items = {1, 2, 3, 4}  # a sequence or set will work here.
    # num_to_select = 2  # set the number to select here.
    import random
    return random.sample(container, min(len(container), count))


import urllib.parse


def url_encode(s):
    return urllib.parse.quote_plus(s)


def url_decode(s):
    return urllib.parse.unquote_plus(s)


def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        pass

    try:
        import unicodedata
        unicodedata.numeric(s)
        return True
    except (TypeError, ValueError):
        pass
    return False


def bigCamelClass(bigcamel):
    return bigcamel[0].upper() + bigcamel[1:]


def bigCamel(s):
    rt = ''
    nextUpper = False
    for j in range(len(s)):
        i = str(s[j])
        if (ord(i[0]) > 91 and i != '_'):
            if nextUpper:
                rt += i.upper()
                nextUpper = False
            else:
                rt += i
        elif i == '_':
            nextUpper = True
        else:
            rt += i
            pass
    return rt


def upperString(smallcamel):
    rt = ''
    for i in smallcamel:
        if (i != '_'):
            rt += i.upper()
        else:
            rt += '_'
    return rt


def smallCamel(s):
    rt = ''
    index = 0
    for j in range(len(s)):
        i = str(s[j])
        if (ord(i[0]) < 91):
            if (index != 0):
                rt += '_'
                rt += i.lower()
            else:
                rt += i.lower()
        else:
            rt += i
        index += 1
    return rt


def find_first(string, pattern):
    """
    没找到返回0
    :param string:
    :param pattern:
    :return:
    """
    regList = re.findall(pattern, string)
    if (len(regList) != 0):
        return regList[0]
    else:
        return 0


def random(low, up):
    import random as r
    return r.randint(low, up)


def getAllFileRecursively(path, all_files=None, sort=1):
    """
    using '/' instead of '\' is suggested, it's ok to append '/' at the end
    of the file path string
    :param sort:
    :param path:
    :param all_files:
    :return:
    """
    if all_files is None:
        all_files = []
    # 首先遍历当前目录所有文件及文件夹
    file_list = os.listdir(path)
    # print(file_list)
    # 准备循环判断每个元素是否是文件夹还是文件，是文件的话，把名称传入list，是文件夹的话，递归
    for file in file_list:
        # 利用os.path.join()方法取得路径全名，并存入cur_path变量，否则每次只能遍历一层目录
        cur_path = os.path.join(path, file)
        # 判断是否是文件夹
        if os.path.isdir(cur_path):
            getAllFileRecursively(cur_path, all_files)
        else:
            all_files.append(re.sub(r'//|\\', '/', path + '/' + file))

    if sort == 1:
        all_files.sort(key=lambda x: os.path.getctime(x))
    return all_files


import pickle


def remove_if_exists(path):
    if os.path.exists(path):
        os.remove(path)


# 获取当前文件夹所有文件，包括目录
def getAllFiles(path, sort=1):
    list = []
    file_list = os.listdir(path)

    for file in file_list:
        list.append(os.path.join(path, file))
    if sort == 1:
        list.sort(key=lambda x: os.path.getctime(x))
    return list


def getAllFiles_NoDirectory(path, sort=1):
    list = []
    file_list = os.listdir(path)
    # 准备循环判断每个元素是否是文件夹还是文件，是文件的话，把名称传入list，是文件夹的话，递归
    for file in file_list:
        if not os.path.isdir(os.path.join(path, file)):
            list.append(os.path.join(path, file))
    if sort == 1:
        list.sort(key=lambda x: os.path.getctime(x))
    return list


def getAllFiles_Dir_Files_tuple(path, sort=1):
    list = []
    dirs = []
    file_list = os.listdir(path)
    # 准备循环判断每个元素是否是文件夹还是文件，是文件的话，把名称传入list，是文件夹的话，递归
    for file in file_list:
        if not os.path.isdir(os.path.join(path, file)):
            list.append(os.path.join(path, file))
        else:
            dirs.append(os.path.join(path, file))
    if sort == 1:
        list.sort(key=lambda x: os.path.getctime(x))
    return (dirs, list)


def clean_windows_output_dir():
    ensure_dir(WORKSPACEOUTPUT)
    for f in getAllFiles(WORKSPACEOUTPUT):
        delete_dir(f)


def clean_windows_input_dir():
    ensure_dir(WORKSPACEINPUT)
    for f in getAllFiles(WORKSPACEINPUT):
        delete_dir(f)


# todo
def file_name_sort_to_int():
    pass


def pick_file_from_input_to_output_and_delete(suffix=None):
    if suffix is None:
        suffix = ['.jpg']
    for pdf in getAllFiles(WORKSPACEINPUT):
        for sfx in suffix:
            if pdf.endswith(sfx):
                shutil.move(pdf, WORKSPACEOUTPUT)
                continue


def pick_file_from_output_to_input_and_delete(suffix=None):
    if suffix is None:
        suffix = ['.jpg']
    for pdf in getAllFiles(WORKSPACEOUTPUT):
        for sfx in suffix:
            if pdf.endswith(sfx):
                shutil.move(pdf, WORKSPACEINPUT)
                continue


# remember /a like path is forbidden , change it to a
def path_join(*args):
    path = ''
    index = 0
    for i in args:
        if index > 0:
            if i.startswith('/') or i.startswith('\\'):
                i = i[1:]
        path = os.path.join(path, i)
        index += 1
    if path.endswith('/') or path.endswith('\\'):
        return path[:-1]
    return path


def split_long_word_to_lines(word, length):
    ipre = 0
    i = length
    s = ''
    while (i < len(word)):
        s += word[ipre:i]
        s += '\n'
        ipre = i
        i += length
    s += word[ipre:]
    return s


def similarity(s1, s2):
    longer = max(len(s1), len(s2))
    return 1 - (edit_distance(s1, s2) / longer)


def edit_distance(s1, s2):
    m = len(s1)
    n = len(s2)
    states = []
    for i in range(0, m + 1):
        tmp = [i]
        for j in range(1, n + 1):
            tmp.append(0)
        states.append(tmp)
    for i in range(0, n + 1):
        states[0][i] = i

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if (s1[i - 1] == s2[j - 1]):
                states[i][j] = states[i - 1][j - 1]
            else:
                states[i][j] = min(states[i - 1][j - 1], min(states[i - 1][j], states[i][j - 1])) + 1
    return states[m][n]


def write_string(path, str):
    with open(path, 'w+', encoding='utf-8') as f:
        f.write(str)
        f.close()
        Path(path).touch()


def write_string_append(path, str):
    with open(path, 'a+', encoding='utf-8') as f:
        f.write(str)
        f.close()
        Path(path).touch()


def read_string(path, default_string=''):
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8', errors='ignore') as f:
            s = f.read()
            f.close()
            return s
    else:
        return default_string


def read_json(path):
    s = read_string(path, '{}')
    return json.loads(s)


def write_json(path, j):
    write_string(path, json.dumps(j))


def write_byte(path, str):
    with open(path, 'wb+') as f:
        f.write(str)
        f.close()


def read_byte(path):
    with open(path, 'rb') as f:
        s = f.read()
        f.close()
        return s


def write_obj(path, obj3):
    obj3 = pickle.dumps(obj3)
    with open(path, 'wb')as f:
        f.write(obj3)
        f.close()


def minute(seconds):
    return str(seconds // 60) + '分' + str(seconds % 60) + '秒'


def ffmpegThread(url, save):
    os.system(
        f'ffmpeg -protocol_whitelist file,http,https,tcp,tls,crypto -i {url} -c copy -bsf:a aac_adtstoasc {save}.mp4')


from threading import Thread


def ffmpeg(url, save=''):
    if not save:
        save = format_windows_name(url)
    thread_03 = Thread(target=ffmpegThread, args=(url, save,))
    thread_03.start()


def format_windows_name(origin):
    return windows_file_name(origin)


def delete_dir(dir):
    if os.path.exists(dir):
        if os.path.isdir(dir):
            shutil.rmtree(dir)
        else:
            os.remove(dir)


def delete_dir_if_exists(dir):
    if os.path.exists(dir):
        if os.path.isdir(dir):
            shutil.rmtree(dir)
        else:
            os.remove(dir)


from pathlib import Path


def path_to_filename(path):
    return os.path.basename(path)
    # return re.findall(r'(?<=[\\/]).*?$', path)[0]


def parent_dir(path):
    path = Path(path)
    return str(path.parent)


def get_file_dir(path):
    return os.path.dirname(os.path.realpath(path))


def current_millis_13():
    return int(time.time() * 1000)


from shutil import copyfile


def copy_file(src, dst):
    copyfile(src, dst)


def scanf_lines():
    inputs = []
    while True:
        inputs.append(input())
        if inputs[len(inputs) - 1] == '0':
            inputs.pop(len(inputs) - 1)
            break
    s = ''
    for i in inputs:
        s += i + "\n"
    return s


def read_obj(path):
    if not os.path.exists(path):
        return None
    f = open(path, "rb")
    try:
        obj = pickle.load(f)
    except:
        pass
    f.close()
    return obj


def ffmpeg_command_mp4_to_mp3(path_from, path_to):
    os.system(f'ffmpeg -i {path_from} -f mp3 {path_to}')
    return path_to


def execCmdWithResult(cmd):
    r = os.popen(cmd)
    text = r.read()
    r.close()
    return text


def ensure_dir(dir):
    try:
        os.makedirs(dir)
    except OSError:
        pass


# def ffmpeg_command_video_merge(folder):
def ffmpeg_command_mp3_mp4_to_pcm(path):
    import os
    os.system(f'ffmpeg -y  -i {path}  -acodec pcm_s16le -f s16le -ac 1 -ar 16000 {path}.pcm')
    return f'{path}.pcm'


def ffmpeg_command_video_split(path, to_folder, timeGap):
    length = int(re.findall(('[\d]+'), getLength(path))[0])
    i = 1
    ensure_dir(to_folder)
    list = []
    while (i - 1) * timeGap < length:
        file = os.path.join(to_folder, str(i))
        list.append(f'{file}.mp4')
        os.system(f'ffmpeg -ss {(i - 1) * timeGap} -i {path} -c copy -t {timeGap} {file}.mp4')
        i += 1
    return list


def getLength(input_video):
    cmd = 'ffprobe -i %s -show_entries format=duration -v quiet -of csv="p=0"' % input_video
    output = os.popen(cmd, 'r')
    output = output.read()
    return output


def pinyin_list(s):
    import pypinyin
    return pypinyin.lazy_pinyin(s, pypinyin.Style.NORMAL)


def pinyin_str(s):
    rt = ''
    for i in pinyin_list(s):
        rt += i
    rt = re.sub(symbols, '', rt)
    return rt


symbols = r'[,，。、\.\)\(]'

import os
import time
import datetime


def time2localString(time_sj=time.time() * 1000):  # 传入参数
    time_sj /= 1000
    data_sj = time.localtime(time_sj)
    time_str = time.strftime("%Y-%m-%d %H:%M:%S", data_sj)  # 时间戳转换正常时间
    return time_str  # 返回日期，格式为str


def currentMillicon():
    t = time.time()
    # print(t)  # 原始时间数据
    # print(int(t))  # 秒级时间戳
    return int(round(t * 1000))
    # print(int(round(t * 1000)))  # 毫秒级时间戳
    # print(int(round(t * 1000000)))  # 微秒级时间戳


def swipeY(a):
    y1 = 500
    y2 = y1 + a
    os.system(f'adb shell input swipe 0 {y2} 0 {y1}')


def tap(x, y):
    os.system(f'adb shell input tap {x} {y}')


def getUi(fileCache):
    if (not os.path.exists(fileCache)):
        os.mkdir(fileCache)
    os.system(f'adb shell uiautomator dump /sdcard/ui.xml')
    os.system(f'adb pull /sdcard/ui.xml {fileCache}')


def back():
    os.system('adb shell input keyevent 4')


def rename(file_path, new_name):
    dir = os.path.dirname(file_path)
    os.rename(file_path, os.path.join(dir, new_name))


# deprecated!
def move(file_path, new_path):
    import shutil
    warn('\nthis move is actually copy method and is extremly buggy!\n')
    shutil.copy(file_path, new_path)


def clear_output():
    for f in getAllFiles(WORKSPACEOUTPUT):
        delete_dir(f)


def copy_force(file_path, new_path):
    # log_print('1'+file_path)
    if os.path.exists(new_path):
        # log_print('2'+new_path)

        delete_dir(new_path)
    if os.path.isdir(file_path):
        # log_print('3'+file_path)

        shutil.copytree(file_path, new_path)  # 这一行出bug
    else:
        # log_print('4'+file_path+','+new_path)

        move(file_path, new_path)


import sqlite3
import json

sqlite_path = 'default.db3'
db_instance = [None, None, None]


# 0 db
# 1 cursor 

# syntax
# https://sqlite.org/lang_createtrigger.html
# https://www.sqlite.org/lang_createtable.html
def exec_sql(
        sql_str
        , *params, json_str=True, single_result_detection=True):
    sql_script = sql_str

    db = sqlite3.connect(sqlite_path, check_same_thread=False)

    db.row_factory = sqlite3.Row  # This enables column access by name: row['column_name']

    # https://stackoverflow.com/questions/15856976/transactions-with-python-sqlite3
    db.isolation_level = None

    cursor = db.cursor()

    l_result = None

    try:
        rows = cursor.execute(sql_script, params).fetchall()
    except sqlite3.Error as err:
        rows = []
        error_log('exec_sql:' + str(err))
        # return Response(False, None)

    db.commit()

    db.close()

    l_result = [dict(ix) for ix in rows]
    if single_result_detection and len(l_result) == 1:
        l_result = l_result[0]

    if json_str:
        success_rt = json.dumps(l_result)  # CREATE JSON
    else:
        success_rt = l_result
    return success_rt


def sqlite_commit_and_disconnect():
    db_instance[1].execute("commit")
    db_instance[1].close()
    db_instance[0].close()
    db_instance[0] = None
    db_instance[1] = None


# 对于发生了变更则必须commit，否则另一个cursor实例查不到修改的数据
def sqlite_commit_and_begin():
    db_instance[1].execute("commit")
    db_instance[1].execute("begin")


def sqlite_connect_and_begin():
    db_instance[0] = sqlite3.connect(sqlite_path, check_same_thread=False)
    db_instance[0].row_factory = sqlite3.Row  # This enables column access by name: row['column_name']
    db_instance[0].isolation_level = None
    db_instance[1] = db_instance[0].cursor()
    db_instance[1].execute("begin")


def exec_sql_quick(
        sql_str
        , *params, json_str=True, single_result_detection=True):
    sql_script = sql_str

    # https://stackoverflow.com/questions/15856976/transactions-with-python-sqlite3

    cursor = db_instance[1]

    l_result = None

    try:
        rows = cursor.execute(sql_script, params).fetchall()
    except sqlite3.Error as err:
        rows = []
        error_log('exec_sql:' + str(err))
        # return Response(False, None)

    # db.commit()

    # db.close()

    l_result = [dict(ix) for ix in rows]
    if single_result_detection and len(l_result) == 1:
        l_result = l_result[0]

    if json_str:
        success_rt = json.dumps(l_result)  # CREATE JSON
    else:
        success_rt = l_result
    return success_rt


def _handle_sql_rows_to_dict(rows, json_str=True, single_result_detection=True):
    l_result = [dict(ix) for ix in rows]
    if single_result_detection and len(l_result) == 1:
        l_result = l_result[0]

    if json_str:
        success_rt = json.dumps(l_result)  # CREATE JSON
    else:
        success_rt = l_result
    return success_rt


# 应该尽量减少connect动作
# use this if you have huge data inserts
def exec_transaction(sql_list: list, results=None, json_str=True, single_result_detection=True):
    sql = sqlite3.connect(sqlite_path)
    sql.row_factory = sqlite3.Row  # This enables column access by name: row['column_name']

    sql.isolation_level = None
    c = sql.cursor()
    c.execute("begin")
    last_sql = ''
    index = -1
    if results is None:
        results = {}
    try:
        for _sql in sql_list:
            index += 1
            # print(_sql)
            last_sql = _sql

            if isinstance(_sql, str):
                # print('str')
                rows = c.execute(_sql).fetchall()
                if rows:
                    results[index] = _handle_sql_rows_to_dict(rows, json_str=json_str,
                                                              single_result_detection=single_result_detection)
                continue

            if len(_sql) == 0:
                pass
            elif len(_sql) == 1:
                rows = c.execute(_sql[0]).fetchall()
                if rows:
                    results[index] = _handle_sql_rows_to_dict(rows, json_str=json_str,
                                                              single_result_detection=single_result_detection)
            else:
                # print(*_sql[1:])
                rows = c.execute(_sql[0], _sql[1:]).fetchall()
                if rows:
                    results[index] = _handle_sql_rows_to_dict(rows, json_str=json_str,
                                                              single_result_detection=single_result_detection)
        c.execute("commit")
    except sql.Error as err:
        print("failed!")
        c.execute("rollback")
        error_log('exec_transaction:' + str(last_sql) + str(err))

    return results


def test(fun, count=100):
    start = time.time()
    for i in range(count):
        fun()
    print(time.time() - start)


def file_md5(file):
    # BUF_SIZE is totally arbitrary, change for your app!
    BUF_SIZE = 65536  # lets read stuff in 64kb chunks!

    md5 = hashlib.md5()

    with open(file, 'rb') as f:
        while True:
            data = f.read(BUF_SIZE)
            if not data:
                break
            md5.update(data)

    return md5.hexdigest()


def file_size(file):
    return Path(file).stat().st_size


import traceback


def trace_print(file_name='error_ml_traceprint.txt'):
    with open(file_name, 'a+') as err_f:
        err_f.write('\n' + datetime_now())
        traceback.print_exc(file=err_f)
        err_f.close()


class Debugger:
    debug_mode = True

    @property
    def _debug(self):
        return Debugger.debug_mode

    def __init__(self) -> None:
        self._logFilterConfigs = {
            'enableTagOnly': False,  # 只允许有tag的打印，其余全部过滤
            'enabledTagName': ''  # 允许打印的tag名称

            # 以下暂不可用
            ,
            'disableByRegex': False,
            'disabledRegex': '',

            'enableByRegex': False,
            'enabledRegex': ''
        }

        self.colorTable = {
            'black': '\x1B[30m',
            'red': '\x1B[31m',
            'green': '\x1B[32m',
            'yellow': '\x1B[33m',
            'blue': '\x1B[34m',
            'magenta': '\x1B[35m',
            'cyan': '\x1B[36m',
            'white': '\x1B[37m',
            'reset': '\x1B[0m',
        }

    def log(self, msg, tag=''):
        if (self._debug and
                (not self._logFilterConfigs['enableTagOnly'] or
                 self._logFilterConfigs['enableTagOnly'] and tag and
                 tag == self._logFilterConfigs['enabledTagName'])):
            print('\x1B[35m[DebugTool]\x1B[0m :', "" if not tag else "\x1B[32m<" + str(tag) + ">\x1B[0m"
                  , f'{msg}')

    def coloredString(self, msg, color):
        return f'{self.colorTable[color]}{msg}' + '\x1B[0m'
