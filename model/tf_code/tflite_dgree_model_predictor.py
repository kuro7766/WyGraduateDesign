import numpy as np
# residual block
# https://stackoverflow.com/questions/64792460/how-to-code-a-residual-block-using-two-layers-of-a-basic-cnn-algorithm-built-wit
from tensorflow import keras

# record start time
from tool import ml, wine_tool, data_loader

data = [{"name": "#1", "degree": 50.88},
        {"name": "#2", "degree": 47.83},
        {"name": "#3", "degree": 44.96},
        {"name": "#4", "degree": 41.36},
        {"name": "#5", "degree": 37.22},
        {"name": "#6", "degree": 33.50},
        {"name": "#7", "degree": 26.80},
        {"name": "#8", "degree": 19.30},
        {"name": "#9", "degree": 11.58},
        {"name": "#10", "degree": 6.95}]
last_peak = ml.read_obj('../peak.obj')
print('last_peak:', last_peak)
for item in data:
    test_set = np.zeros(0)
    for f in ml.getAllFiles(f'data2/data#101_{item["name"]}/10/'):
        test_set = np.append(test_set,
                             (np.array(wine_tool.col2data_to_float_list(f), dtype=np.float)))

    test_set = data_loader.normalization_manually(test_set,
                                                  # 低度数
                                                  # 0.0, 59675.0
                                                  # 高度数
                                                  # 0.0, 87279.0
                                                  # 0.0, 112914.0
                                                  0.0, last_peak
                                                  )

    v = None
    for head in ['model_1.h5',]:
    # for head in ['model_1.h5', 'model.h5']:
    # for head in ['model.h5']:
    # for head in ['model_test_1.h5']:
        prediction = keras.models.load_model(head).predict(
            test_set.reshape(
                (-1, 1000)))
        if v is None:
            v = prediction
        else:
            v = np.append(v, prediction, axis=1)

    v = v * 100
    # print(v[:10])

    weighted_out = np.average(v, axis=1, weights=[1.0 / v.shape[1] for i in range(v.shape[1])])
    # print(weighted_out)
    # print(np.mean(weighted_out),np.mean(prediction),np.mean(prediction2))
    # print(v)
    print(item['name'], ([np.average(v[:, col]) for col in range(v.shape[1])]), '[average] :', np.average(weighted_out),
          '[truth] :', item['degree'], '[error] :', np.abs(np.average(weighted_out) - item['degree']))
# print(np.average(v, axis=1))
# print('should be ', y[0])
# model.save('test_model.h5')
# tflite_converter.convert('test_model.h5')
# tflite_converter.save_model_as_tflite(model)

# history.history是个字典：列表的数据
# pd.DataFrame(history.history).plot(figsize=(8, 5))
# plt.grid(True)
# plt.gca().set_ylim(0, 0.1)
# plt.show()
