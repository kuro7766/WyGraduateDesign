import tool.tflite_converter as tflite_converter
from pytorch_classify import classify_trainer
from pytorch_classify import classify_trainer

if __name__ == '__main__':
    from tf_code import tools_light_anl
    # from tf_code import tflite_class_model_attention    # 这一行不能去掉，会报错，因为主程序没有写到main函数

    # 主要函数
    # from tf_code import tflite_class_model_fcl

    # tflite_converter.convert('model/smell.h5')

    # from tool import wine_data_loader
    # x,y = wine_data_loader.get_data5()
    # print(x.shape,y.shape)


    # classify_trainer.main()

    # from tool import tflite_converter
    #
    # # torch转换成onnx
    # tflite_converter.torch_pth_to_onnx(classify_trainer.MlpClassifier(), 'model/torch_model_mlp_class_15.pth', (1, 1000))
    # tflite_converter.onnx_to_tflite()

    # from tf_code import tflite_degree_model_for_torch_verification
    # import tensorflow as tf
    # print("Num GPUs Available: ", len(tf.config.list_physical_devices('GPU')))
    # from tf_code import tflite_degree_model_gpu_test
    # from machine_learnings import svctest