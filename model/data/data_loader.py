# -- coding: utf-8 --
import os
import random

import numpy as np


def curve_loader(path):
    """
    简易版的读取曲线
    :param path: 文件夹路径
    :return:
    """
    files = os.listdir(path)
    curves = []
    labels = []
    for file in files:
        label = float(file.strip().split('-')[0])
        # print(label)
        labels.append(label - 1)
        position = path + '/' + file
        with open(position, 'r', encoding='utf-8') as f:
            value = []
            for line in f.readlines():
                value.append(float(line.split(' ')[-1].strip()))
            print(len(value))
            print(value)
            curves.append(value)
    print(np.array(curves).shape)
    X = np.array(curves)
    # X = data_normal_2d(X)
    X = normalization(X)
    X = np.expand_dims(X, 2)
    Y = np.array(labels)
    # X = X.type(torch.FloatTensor)
    # Y = Y.type(torch.FloatTensor).unsqueeze(1)
    # X = Variable(X)
    # Y = Variable(Y)
    return X, Y


def curve_loader2(path, test_flag=0, train_ratio=0.7):
    """
    简易版的读取曲线
    :param path: 文件夹路径
    :return:
    """
    files = os.listdir(path)
    curves = []
    labels = []
    for file in files:
        # if not file.endswith('.txt'):
            # X, Y = curve_loader2()
        label = int(file.strip().split('-')[0])
        # print(label)
        labels.append(label)
        position = path + '/' + file
        with open(position, 'r', encoding='utf-8') as f:
            value = []
            for line in f.readlines():
                value.append(float(line.split(' ')[-1].strip()))
            print(len(value))
            print(value)
            curves.append(value)
    print(np.array(curves).shape)
    X = np.array(curves)
    Y = np.array(labels)
    X = normalization(X)
    train_X = []
    train_Y = []
    test_X = []
    test_Y = []
    if test_flag == 1:
        train_size = int(len(X) * train_ratio)
        train_index = random.sample(range(0, len(X)), train_size)
        for i in range(len(X)):
            if i in train_index:
                train_X.append(X[i])
                train_Y.append(Y[i])
            else:
                test_X.append(X[i])
                test_Y.append(Y[i])
        # train_X = torch.from_numpy(np.array(train_X))
        # train_Y = torch.from_numpy(np.array(train_Y))
        # train_X = train_X.unsqueeze(1)
        # train_X = np.expand_dims(train_X, 2)
        # train_X = train_X.type(torch.FloatTensor)
        # train_Y = train_Y.type(torch.FloatTensor).unsqueeze(1)

        # test_X = torch.from_numpy(np.array(test_X))
        # test_X = normalization(test_X)
        # test_Y = torch.from_numpy(np.array(test_Y))
        # test_X = test_X.unsqueeze(1)
        # test_X = np.expand_dims(test_X, 2)
        # test_X = test_X.type(torch.FloatTensor)
        # test_Y = test_Y.type(torch.FloatTensor).unsqueeze(1)
        # return Variable(train_X), Variable(train_Y), Variable(test_X), Variable(test_Y)
        return train_X, train_Y, test_X, test_Y
    else:
        train_X = X
        train_Y = Y
        # train_X = torch.from_numpy(np.array(train_X))
        # train_X = normalization(train_X)
        # train_Y = torch.from_numpy(Y)
        # train_X = train_X.unsqueeze(1)
        # train_X = np.expand_dims(train_X, 2)
        # train_X = train_X.type(torch.FloatTensor)
        # train_Y = train_Y.type(torch.FloatTensor).unsqueeze(1)
        return train_X, train_Y,


def data_normal_2d(orign_data, dim="col"):
    if dim == "col":
        dim = 1
        d_min = np.min(orign_data, axis=dim)[0]
        for idx, j in d_min:
            if j < 0:
                orign_data[idx, :] += np.abs(d_min[idx])
                d_min = np.min(orign_data, axis=dim)[0]
    else:
        dim = 0
        d_min = np.min(orign_data, axis=dim)[0]
        for idx, j in enumerate(d_min):
            if j < 0:
                orign_data[idx, :] += np.abs(d_min[idx])
                d_min = np.min(orign_data, dim=dim)[0]
    d_max = np.max(orign_data, axis=dim)[0]
    dst = d_max - d_min
    if d_min.shape[0] == orign_data.shape[0]:
        d_min = d_min.unsqueeze(1)
        dst = dst.unsqueeze(1)
    else:
        d_min = d_min.unsqueeze(0)
        dst = dst.unsqueeze(0)
    norm_data = np.sub(orign_data, d_min).true_divide(dst)
    return norm_data


def normalization(data):
    _range = np.max(data) - np.min(data)
    return (data - np.min(data)) / _range


if __name__ == '__main__':
    a, b = curve_loader2(path='./curve')
    print(111)
