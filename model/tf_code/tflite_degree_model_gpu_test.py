#%%

import os

# os.environ['CUDA_VISIBLE_DEVICES'] = '-1'

#%%

import json

import numpy as np

# record start time
import pandas as pd
import torch
from IPython.core.display import display
from keras import layers
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
import tensorflow as tf
import matplotlib.pyplot as plt
from torch.utils.data import DataLoader

from general_model.grow_numpy import GrowableNumpyArray
from general_model.list_batch_loader import ListBatchLoadDataset
from model_helper.single_gpu_dataloader import SingleGpuTensorWrapper
from pytorch_regression.regression_params import RegressionParams
from tool import ml, wine_tool, data_loader,wine_data_loader
from tool.dbg import dbg

#%%

if tf.test.gpu_device_name():
    print('GPU found')
else:
    print("No GPU found")
print("Num GPUs Available: ", len(tf.config.list_physical_devices('GPU')))


#%%

wine_json=ml.read_json("Wine.json")

class RegressionTrainer:
    def __init__(self):
        self.param=RegressionParams()
        self.param.batch_size=64
        self.param.epoch=1
        self.param.lr=1e-9
        self.param.cnn_stride=1
        self.param.cnn_size=3
        self.param.pool_size=2
        self.param.lr_decay=0.99


    def load(self):

        self.x = GrowableNumpyArray(dtype=np.float,grow_speed=16)
        self.y = GrowableNumpyArray(dtype=np.float,grow_speed=16)

        for wine in wine_json:
            base_wine_path='data4/'+wine['id']
            dbg(wine['name'])
            if not os.path.exists(base_wine_path):
                dbg('not exists:', base_wine_path)
                continue
            # load data
            dirs = ml.getAllFiles(f'{base_wine_path}')
            for d in dirs:
                path=d
                files = ml.getAllFileRecursively(path)
                for f in files:
                    self.x.update(wine_tool.col2data_to_float_list(f))
                    self.y.add(wine['degree'])
                    # self.x = np.append(self.x, wine_tool.col2data_to_float_list(f))
                    # self.y = np.append(self.y, wine['degree'])

            # load item_data
            for wine_item in wine['items']:
                sub_wine_path=base_wine_path+'_'+wine_item['name']

                dirs = ml.getAllFiles(f'{sub_wine_path}')
                dbg(wine_item)
                for d in dirs:
                    path=d
                    files = ml.getAllFileRecursively(path)
                    for f in files[:100]:
                        self.x.update(wine_tool.col2data_to_float_list(f))
                        self.y.add(wine_item['degree'])
                        # self.x = np.append(self.x, wine_tool.col2data_to_float_list(f))
                        # self.y = np.append(self.y, wine_item['degree'])

            break

        self.x = self.x.finalize()
        self.x = self.x.reshape((-1, 1000))


        self.y=self.y.finalize()


        plt.plot(sorted(self.y))
        plt.plot(self.y)


        # normalize

        self.y=self.y/100


        layer_normed_x=torch.nn.LayerNorm([1000])(torch.tensor(self.x,dtype=torch.float))
        i=0
        while i<3:
            indice=i
            plt.plot(layer_normed_x[indice].detach().numpy(),label=self.y[indice])
            plt.legend()
            plt.show()
            i+=1

        self.x,self.y,self.x1,self.y1=wine_data_loader.split(self.x,self.y,1-self.param.train_test_split)

        # wine_dataset = ListBatchLoadDataset(self.x.tensor, self.y.tensor)
        # self.dataloader = DataLoader(wine_dataset, batch_size=self.param.batch_size,
        #                         shuffle=True, num_workers=0)



trainer=RegressionTrainer()
trainer.load()
dbg(trainer.x.shape,trainer.y.shape)

#%%

# tf.debugging.set_log_device_placement(False)
#%% md

#%%


# act = 'tanh'
# # inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
# inp = __next = layers.Input(shape=[1000], name='input')
# __next = layers.Dense(800, activation=act)(__next)
# __next = layers.Dense(800, activation=act)(__next)
# __next = layers.Dense(800, activation=act)(__next)
# __next = layers.Dense(104, activation=act)(__next)
# __next = layers.Dense(10, activation=act)(__next)
# output = __next = layers.Dense(1, )(__next)
# model = keras.Model(inputs=[inp], outputs=[output])
# model.summary()
# # opt = tf.keras.optimizers.Adam(lr=1e-4)
# opt = tf.keras.optimizers.Adam(lr=1e-6)
# model.compile(
#     optimizer=opt,
#     # optimizer='adam',
#     loss='mse',
#
# )

#%%
act = 'tanh'
config=[
    ('conv',2,2),
    ('pool',2),
    ('conv',2,2),
    ('pool',2),
]
# inp = __next = layers.Input(shape=[1280 * 720*10], name='input')
inp = __next = layers.Input(shape=[1000], name='input')
__next=layers.Reshape(target_shape=[1000,1], name='reshape')(__next)

for c in config:
    if c[0]=='conv':
        __next = layers.Conv1D(filters=c[1], kernel_size=c[2], activation=act)(__next)
    elif c[0]=='pool':
        __next = layers.MaxPool1D(pool_size=c[1])(__next)

__next = layers.Flatten()(__next)
output = __next = layers.Dense(1, )(__next)
model = keras.Model(inputs=[inp], outputs=[output])
model.summary()
# opt = tf.keras.optimizers.Adam(lr=1e-4)
opt = tf.keras.optimizers.Adam(lr=1e-6)
model.compile(
    optimizer=opt,
    # optimizer='adam',
    loss='mse',
)
#%%

history = model.fit(trainer.x, trainer.y,
                    # validation_data=(X_test, Y_test)
                    # epochs=50,
                    epochs=100,
                    # epochs=500,
                    # epochs=300,
                    )


pd.DataFrame(history.history).plot(figsize=(8, 5))
# plt.grid(True)
# plt.gca().set_ylim(0, 5e-4)
plt.show()


prediction = model.predict(trainer.x1)
dict = {'predict': prediction[-6:, 0],
        'truth': trainer.y1[-6:],
        'diff': (prediction[-6:, 0] - trainer.y1[-6:])
        }
df = pd.DataFrame(dict)

# displaying the DataFrame
display(df.T)



#%%

# 输出误差小于2.5度的百分比
dif=prediction.reshape(-1,)-trainer.y1
np.sum((dif<0.025))/len(dif)*100

#%%



#%%