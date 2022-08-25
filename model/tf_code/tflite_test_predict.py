import numpy as np
import sklearn

from mpl_toolkits.mplot3d import Axes3D
from sklearn.neighbors import KNeighborsClassifier
# record start time
import time
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis as LDA
import os
from sklearn.decomposition import PCA
import matplotlib.pyplot as plt
import pandas as pd
import random
from matplotlib.pyplot import cm
from keras import layers
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras
import tensorflow as tf

model = keras.models.load_model('test_model.h5')
model.summary()
y=model.predict(np.arange(100).reshape((-1,2,2)))
print(y)