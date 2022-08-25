import torch


class SingleGpuTensorWrapper:
    def __init__(self, tensor, use_gpu=True):
        self.tensor = tensor.cuda() if use_gpu else tensor
