import torch.nn


class SingleCudaTrainer:
    def __init__(self, model: torch.nn.Module, use_gpu=True):
        self.model = model
        self.use_gpu = use_gpu

        if self.use_gpu:
            self.model=self.model.cuda()
