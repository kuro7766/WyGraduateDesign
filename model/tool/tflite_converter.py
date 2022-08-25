import tensorflow as tf
from torch.autograd import Variable

import torch.onnx
import torchvision
import torch
import onnx


def convert(model_path):
    model = tf.keras.models.load_model(model_path)
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    open("model/converted_model.tflite", "wb").write(tflite_model)


def save_model_as_tflite(model):
    # model = tf.keras.models.load_model(model_path)
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    open("model/converted_model.tflite", "wb").write(tflite_model)


def torch_pth_to_onnx(model: torch.nn.Module, pth_path, shape_test: tuple):
    state_dict = torch.load(pth_path)
    model.load_state_dict(state_dict)
    torch.onnx.export(model, torch.randn(shape_test), "model/converting-tmp.onnx")


def onnx_to_tflite():
    onnx_model = onnx.load("model/converting-tmp.onnx")
    from onnx_tf.backend import prepare

    tf_rep = prepare(onnx_model)
    tf_rep.export_graph("model/converted_model")

    # Convert the model
    converter = tf.lite.TFLiteConverter.from_saved_model("model/converted_model")
    tflite_model = converter.convert()

    # Save the model
    with open("model/converted_model.tflite", 'wb') as f:
        f.write(tflite_model)


def torch_model_to_tflite(model: torch.nn.Module):
    torch.save(model.state_dict(), 'model/converting-tmp.pth')

    # torch转换成onnx
    torch_pth_to_onnx(type(model)(), 'model/converting-tmp.pth', (1, 1000))
    onnx_to_tflite()
