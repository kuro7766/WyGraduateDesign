import json

import numpy as np

# record start time
import pandas as pd
from keras import layers
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
import tensorflow as tf
import matplotlib.pyplot as plt
from tool import ml, wine_tool, data_loader,wine_data_loader

act = 'sigmoid'
# inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
inp = __next = layers.Input(shape=[1000], name='input')
# inp = __next = layers.Input(shape=[720 * 480], name='input')
# inp = __next = layers.Input(shape=[1280 ], name='input')
# __next = layers.Activation(act)(__next)
# __next = layers.Dense(1000, activation=act)(__next)
# __next = layers.Dropout(.2)(__next)
__next = layers.LayerNormalization()(__next)
max_h = layers.Lambda(lambda x: tf.reshape(tf.reduce_max(x, axis=1), (-1, 1)))(__next)
max_h = layers.Dense(1, activation=act)(max_h)
max_h = layers.Dense(1, activation=act)(max_h)

max_h_x = layers.Lambda(lambda x: tf.reshape(tf.argmax(x, axis=1) / 1000, (-1, 1)))(__next)
max_h_x = layers.Dense(1, activation=act)(max_h_x)
max_h_x = layers.Dense(1, activation=act)(max_h_x)

__next = layers.Dense(800, activation=act)(__next)

# __next = layers.Dense(500, activation=act)(__next)
__next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
__next = layers.Dense(100, activation=act)(__next)
__next = layers.Dense(2, activation=act)(__next)
# __next = layers.Concatenate()([__next, max_h, max_h_x])
__next = layers.Dense(4, activation=act)(__next)
__next = layers.Dense(4, activation=act)(__next)
output = __next = layers.Dense(1, )(__next)
j = json.loads(ml.read_string('./Wine.json'))

# __next = layers.Conv2D(32, (3, 3), padding='same', activation='relu', name='conv1')(input_img)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Conv2D(64, (3, 3), padding='same', activation='relu', name='conv2')(__next)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Conv2D(128, (3, 3), padding='same', activation='relu', name='conv3')(__next)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Flatten()(__next)
# __next = layers.Dense(400, activation='sigmoid')(__next)
# output = layers.Dense(200, activation='softmax', name='type')(__next)
# __next = tf.reshape(__next, [-1,1280, 720, 10])
# __next = __next.reshape([-1, 1280, 720, 1])
# __next = __next[:, :,0] + __next[:, :,1]
# output = __next = tf.reduce_mean(__next, axis=1)
# tf.where(output > 0, output, 0)
# tf.reduce_mean()

model = keras.Model(inputs=[inp], outputs=[output])
model.summary()
# opt = tf.keras.optimizers.SGD(learning_rate=0.01)
model.compile(
    # optimizer=opt,
    optimizer='adam',
    # loss='categorical_crossentropy',
    loss='mse',
    # metrics=['val_mse']
)

# data = np.arange(1280 * 720 * 10).reshape((-1, 1280 * 720*10))
# print(data)
x = np.zeros(0, dtype=np.float)
y = np.zeros(0)

# for f in ml.getAllFiles('data2/data#101/10/'):
#     x = np.append(x, np.array(wine_tool.col2data_to_float_list(f), dtype=np.float))
#     y = np.append(y, 53.0)
#
# for f in ml.getAllFiles('data2/data#101_#1/10/'):
#     x = np.append(x, np.array(wine_tool.col2data_to_float_list(f), dtype=np.float))
#     y = np.append(y, 50.88)
#
# for f in ml.getAllFiles('data2/data#101_#10/10/'):
#     # print(f)
#     x = np.append(x, np.array(wine_tool.col2data_to_float_list(f), dtype=np.float))
#     y = np.append(y, 6.95)

for i, item in enumerate(j[0]['items']):
    degree = item['degree']
    for f in ml.getAllFiles(f'data2/data#102_{item["name"]}/10'):
        x = np.append(x, wine_tool.col2data_to_float_list(f))
        y = np.append(y, degree)

for i, item in enumerate(j[1]['items']):
    if i + 1 <= 5:
    # if i + 1 > 5:
        # if i + 1 == 3:
        continue
    degree = item['degree']
    for f in ml.getAllFiles(f'data2/data#101_{item["name"]}/10'):
        x = np.append(x, wine_tool.col2data_to_float_list(f))
        y = np.append(y, degree)

for f2 in ml.getAllFiles('./data/intensity10/GPYR0029_group_1/'):
    degree = 36
    if not ml.path_to_filename(f2) == '#2':
        continue

    print(f2)
    for f in ml.getAllFiles(f2 + '/10'):
        # print(f)
        # for f in ml.getAllFiles(f):
        x = np.append(x, wine_tool.col2data_to_float_list(f))
        if len(wine_tool.col2data_to_float_list(f)) == 0:
            print(f)
        y = np.append(y, degree)

x = x.reshape((-1, 1000))
x, wine_min, wine_range = data_loader.normalization(x)
y = y / 100

history = model.fit(x, y,
                    # validation_data=(X_test, Y_test)
                    # epochs=50,
                    # epochs=20,
                    epochs=100,
                    )

# prediction = model.predict(x[0].reshape((-1, 1000)))
model.save('model.h5')

test_set = np.zeros(0)
for f in ml.getAllFiles('./data2/data#101_#3/10/'):
    test_set = np.append(test_set,
                         (np.array(wine_tool.col2data_to_float_list(f), dtype=np.float)))

test_set = data_loader.normalization_manually(test_set, wine_min, wine_range)
prediction = model.predict(
    test_set.reshape(
        (-1, 1000)))
# / div
# print(prediction * 100)
# print('-' * 30, )
print(wine_min, ',', wine_range)

# print('should be ', y[0])
# model.save('test_model.h5')
# tflite_converter.convert('test_model.h5')
# tflite_converter.save_model_as_tflite(model)

# history.history是个字典：列表的数据
pd.DataFrame(history.history).plot(figsize=(8, 5))
# plt.grid(True)
# plt.gca().set_ylim(0, 0.1)
plt.show()


# (1000, 500)
# print(model.layers[1].get_weights()[0].shape)
# correct solution:
def softmax(x):
    """Compute softmax values for each sets of scores in x."""
    e_x = np.exp(x - np.max(x))
    return e_x / e_x.sum(axis=0)  # only difference


input_neuron_attention = (np.average(model.layers[1].get_weights()[0], axis=1))
# sft = softmax(input_neuron_attention)
# sft=np.abs(input_neuron_attention)
sft = input_neuron_attention
plt.plot([i for i in range(len(sft))], sft, 'r-')
plt.show()
