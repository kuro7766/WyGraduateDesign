from tool.dbg import dbg


class ParameterDict:
    def __init__(self):
        self.seed = 2021
        self.kfold = 10
        self.epoch = 2
        self.gpus = 1
        self.resume_from_checkpoint = None
        self.output_dim = 1
        self.batch_size = 16
        self.shuffle = True
        self.num_workers = 4
        self.lr = 1e-4
        self.loss = 'nn.BCEWithLogitsLoss'
        self.train_test_split = 0.8
        self.use_gpu = True
