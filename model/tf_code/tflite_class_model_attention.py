import time

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

act = 'sigmoid'
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

# att = layers.Dense(1000, activation='softmax')(__next)
# __next = layers.Dropout(.2)(__next)
# __next= layers.Multiply()([inp, att])

# __next = layers.Dense(1000, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(500, activation=act)(__next)
# __next = layers.Dense(100, activation=act)(__next)
# __next = layers.Dense(2, activation=act)(__next)
output = __next = layers.Dense(cfg.classify_count,activation='softmax',use_bias=False)(__next)

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
x, y, x_test, y_test = wine_data_loader.split(*wine_data_loader.get_data3())
print(x.shape, x_test.shape)
# print(x[0])
# print(y[0])
history = model.fit(x, y,
                    validation_data=(x_test, y_test),
                    # epochs=50,
                    # epochs=20,
                    epochs=100,
                    # epochs=30,
                    # epochs=1,
                    # epochs=10,
                    )

start=time.time()
predictions = model.predict(x_test)
end=time.time()
print('predict time:',end-start)
print(predictions[:4])
tflite_converter.save_model_as_tflite(model)
# history.history是个字典：列表的数据
pd.DataFrame(history.history).plot(figsize=(8, 5))
# plt.grid(True)
# plt.gca().set_ylim(0, 0.1)
plt.show()
print(model.layers)

model.save('model/tf_fcl.h5')

# print(model.layers[1].get_weights()[0].shape)
input_neuron_attention = (np.average(model.layers[1].get_weights()[0], axis=1))
sft = input_neuron_attention
# sft = np.abs(input_neuron_attention)
plt.plot([i for i in range(len(sft))], sft, 'r.')
plt.show()
