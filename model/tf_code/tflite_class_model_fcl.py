import numpy as np

# record start time
import pandas as pd
from keras import layers
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
import matplotlib.pyplot as plt
from tool import wine_data_loader, tflite_converter
import cfg

act = 'tanh'
# inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
inp = __next = layers.Input(shape=[1000], name='input')
# inp = __next = layers.Input(shape=[720 * 480], name='input')
# inp = __next = layers.Input(shape=[1280 ], name='input')
# __next = layers.Activation(act)(__next)
# __next = layers.Dense(1000, activation=act)(__next)
# __next = layers.Dropout(.2)(__next)

# max_h = layers.Lambda(lambda x: tf.reshape(tf.reduce_max(x, axis=1), (-1, 1)))(__next)
# max_h = layers.Dense(1, activation=act)(max_h)
# max_h = layers.Dense(1, activation=act)(max_h)
#
# max_h_x = layers.Lambda(lambda x: tf.reshape(tf.argmax(x, axis=1) / 1000, (-1, 1)))(__next)
# max_h_x = layers.Dense(1, activation=act)(max_h_x)
# max_h_x = layers.Dense(1, activation=act)(max_h_x)

__next = layers.Dense(800, activation=act)(__next)
# __next = layers.Dropout(.2)(__next)

__next = layers.Dense(800, activation=act)(__next)
# __next = layers.Dropout(.2)(__next)

# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
__next = layers.Dense(100, activation=act)(__next)
# __next = layers.Dense(2, activation=act)(__next)
output = __next = layers.Dense(cfg.classify_count, activation='softmax')(__next)

model = keras.Model(inputs=[inp], outputs=[output])
model.summary()
# opt = tf.keras.optimizers.SGD(learning_rate=0.01)
model.compile(
    # optimizer=opt,
    optimizer='adam',
    loss='categorical_crossentropy',
    # loss='mse',
    # metric s=['val_mse']
    metrics=['categorical_accuracy']
)

import random


def shuffle_along_axis(a, axis):
    idx = np.random.rand(*a.shape).argsort(axis=axis)
    return np.take_along_axis(a, idx, axis=axis)


class WineDataAugmentation:
    def __init__(self, xs: np.array, ys: np.array):
        self.xs = xs
        self.ys = ys

    def generate(self, size=2):
        new_size = self.xs.shape[0] * size
        # numpy拷贝xs和ys到size倍

        xs = np.repeat(self.xs, size, axis=0)
        ys = np.repeat(self.ys, size, axis=0)

        # xs [batch * 1000]
        # 对xs中的第一个维度随机乘以0.1-1.0之间的数
        xs = xs * np.random.uniform(0.9, 1.1, size=(new_size, 1))

        # 加入-300~300的噪音
        # noise = np.random.random((new_size, self.xs.shape[1]))
        # noise = noise * 10000
        # print(noise)
        # print(np.ptp(noise, axis=1).shape)
        # print(np.ptp(noise, axis=1))
        # return
        # xs = xs + noise
        # # 每个样本减去自己的最小值
        # xs = xs - np.min(xs, axis=1).reshape(-1, 1)
        # dbg(xs.shape)

        for i in range(xs.shape[0]):
            this_tensor = xs[i]
            # r= random [-200,200]
            r = random.randint(-50, 50)
            # r=random.randint(-500,500)
            # dbg(r)
            if r < 0:
                r = -r
                xs[i][:1000 - r] = this_tensor[r:1000]
                xs[i][1000 - r:] = this_tensor[1000 - r - 1]
            else:
                xs[i][r:1000] = this_tensor[:1000 - r]
                xs[i][:r] = this_tensor[r]
        # xs=xs[0:50]
        # xs=xs[50:100]
        # xs=xs[0:100]
        # dbg('0',xs[:,:5])
        # xs=shuffle_along_axis(xs, axis=0)
        # dbg('1',xs[:,:5])
        return xs, ys


x_all_pre, y_all_pre = wine_data_loader.get_data3()
# x_all_pre, y_all_pre = wine_data_loader.get_data5()


# x_all_augmented, y_all_augmented = WineDataAugmentation(x_all_pre, y_all_pre).generate(1)
x_all_augmented, y_all_augmented = (x_all_pre, y_all_pre)


# x_all_augmented.shape
x, y, x_test, y_test = wine_data_loader.split(x_all_augmented, y_all_augmented)

print(x.shape, x_test.shape)
# print(x[0])
# print(y[0])
history = model.fit(x, y,
                    validation_data=(x_test, y_test),
                    # epochs=50,
                    # epochs=20,
                    epochs=200,
                    # epochs=1000,
                    # epochs=1,
                    # epochs=10,
                    )

tflite_converter.save_model_as_tflite(model)
model.save('model/tf_fcl.h5')
# history.history是个字典：列表的数据
pd.DataFrame(history.history).plot(figsize=(8, 5))
# plt.grid(True)
# plt.gca().set_ylim(0, 0.1)
plt.show()
# print(model.layers)
# print(model.layers[1].get_weights()[0].shape)
input_neuron_attention = (np.average(model.layers[1].get_weights()[0], axis=1))
# sft = softmax(input_neuron_attention)
# sft=np.abs(input_neuron_attention)
sft = input_neuron_attention
plt.plot([i for i in range(len(sft))], sft, 'r-')
plt.show()
