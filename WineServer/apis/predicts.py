from django.http import HttpRequest
from lib.decorators import *

import numpy as np

# record start time
# residual block
from tensorflow import keras
from lib.synctool import synchronized
from lib.http_response import HttpCrossDomainJsonResponse
import json

# class NumpyEncoder(json.JSONEncoder):
#     def default(self, obj):
#         if isinstance(obj, np.ndarray):
#             return obj.tolist()
#         return json.JSONEncoder.default(self, obj)
#
# a = np.array([[1, 2, 3], [4, 5, 6]])
# print(a.shape)
# json_dump = json.dumps({'a': a, 'aa': [2, (2, 3, 4), a], 'bb': [2]},
#                        cls=NumpyEncoder)
# print(json_dump)


@synchronized('predict')
def predict(model,data):
    model = keras.models.load_model(model)
    # model.summary()
    # y = model.predict(np.arange(100).reshape((-1, 2, 2)))
    # print('data',data)
    y = model.predict(np.array(data).reshape((-1,1000)))

    return y

@path_route('predict')
def model(req:HttpRequest):
    data = json.loads(req.POST['curve'])

    data = data['data']
    print(data)

    y = predict('model/smell.h5',data)

    print('y',y)
    return HttpCrossDomainJsonResponse({'result':y.tolist()})
