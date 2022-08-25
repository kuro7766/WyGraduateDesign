# record start time
from keras import layers
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
import tensorflow as tf
from tool import tflite_converter

# inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
# inp = __next = layers.Input(shape=[720 * 480], name='input')
# inp = __next = layers.Input(shape=[1280 ], name='input')

# __next = layers.Conv2D(32, (3, 3), padding='same', activation='relu', name='conv1')(input_img)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Conv2D(64, (3, 3), padding='same', activation='relu', name='conv2')(__next)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Conv2D(128, (3, 3), padding='same', activation='relu', name='conv3')(__next)
# __next = layers.MaxPooling2D((2, 2))(__next)
# __next = layers.Flatten()(__next)
# __next = layers.Dense(400, activation='tanh')(__next)
# output = layers.Dense(200, activation='softmax', name='type')(__next)
# __next = tf.reshape(__next, [-1,1280, 720, 10])
# __next = __next.reshape([-1, 1280, 720, 1])
# __next = __next[:, :,0] + __next[:, :,1]
# output = __next = tf.reduce_mean(__next, axis=1)
# tf.where(output > 0, output, 0)
# tf.reduce_mean()
output=tf.reduce_sum(__next, axis=1)
model = keras.Model(inputs=[inp], outputs=[output])
model.summary()
model.compile(optimizer='adam',
              loss='categorical_crossentropy',
              metrics=['accuracy'])

# data = np.arange(1280 * 720 * 10).reshape((-1, 1280 * 720*10))
# print(data)

# model.fit(data, np.zeros(shape=(len(data), 10)), epochs=1)

# prediction = model.predict(data)
# print(prediction)

# model.save('test_model.h5')
# tflite_converter.convert('test_model.h5')
tflite_converter.save_model_as_tflite(model)
