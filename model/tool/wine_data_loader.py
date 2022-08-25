import numpy as np
from sklearn import model_selection
# record start time
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
from typing import List, Tuple

from tool import ml, wine_tool, data_loader



class WineDataAugmentation:
    def __init__(self, xs: np.array, ys: np.array):
        self.xs = xs
        self.ys = ys

    def generate(self, size=2):
        new_size = self.xs.shape[0] * size
        # numpy拷贝xs和ys到size倍

        xs=np.repeat(self.xs, size, axis=0)
        ys=np.repeat(self.ys, size, axis=0)

        # 加入-300~300的噪音
        noise = np.random.normal(0, 1, (new_size, self.xs.shape[1]))
        noise = noise * 1000
        xs = xs + noise
        # 每个样本减去自己的最小值
        xs = xs - np.min(xs, axis=1).reshape(-1, 1)

        return xs, ys


# data = np.arange(1280 * 720 * 10).reshape((-1, 1280 * 720*10))
# print(data)
# 这是一个分类任务，使用独热编码。
def get_data3(use_onehot=True):
    x = np.zeros(0, dtype=np.float)
    y = np.zeros(0)
    dirs = ml.getAllFiles('./data3')
    for d in dirs:
        # print(d)
        cls = int(d.split('#')[-1])
        # print(cls)
        path = f'{d}/10/'
        files = ml.getAllFileRecursively(path)
        for f in files:
            x = np.append(x, wine_tool.col2data_to_float_list(f))
            y = np.append(y, cls)

    x = x.reshape((-1, 1000))
    # x, wine_min, wine_range = data_loader.normalization(x)
    if use_onehot:
        y = keras.utils.to_categorical(y, num_classes=15)
    # print('load params:',x.shape,y.shape,wine_min,wine_range)

    return x, y.astype(int)  # 一个很严重的错误，onehot标签没有转换成int，分类任务要注意


# data = np.arange(1280 * 720 * 10).reshape((-1, 1280 * 720*10))
# print(data)
# 这是一个分类任务，使用独热编码。
def get_data5(use_onehot=True):
    x = np.zeros(0, dtype=np.float)
    y = np.zeros(0)
    dirs = ml.getAllFiles('./data5')
    for d in dirs:
        print(d)
        cls = int(d[-1])
        # print(cls)
        path = f'{d}/'
        files = ml.getAllFileRecursively(path)
        for f in files:
            x = np.append(x, wine_tool.col2data_to_float_list(f))
            y = np.append(y, cls)

    x = x.reshape((-1, 1000))
    # x, wine_min, wine_range = data_loader.normalization(x)
    if use_onehot:
        y = keras.utils.to_categorical(y, num_classes=3)
    # print('load params:',x.shape,y.shape,wine_min,wine_range)

    return x, y.astype(int)  #

# print(x.shape,y.shape)

def split(x, y, rate=0.2):
    x_train, x_test, y_train, y_test = model_selection.train_test_split(x, y, test_size=rate,random_state=20)
    return x_train, y_train, x_test, y_test


if __name__ == '__main__':
    x, y = get_data5()

    print(x.shape, y.shape)
