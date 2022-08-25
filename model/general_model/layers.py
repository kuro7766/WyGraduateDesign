
from torch import nn

class LambdaLayer(nn.Module):
    def __init__(self, lambd):
        super(LambdaLayer, self).__init__()
        self.lambd = lambd

    def forward(self, x):
        # dbg('LambdaLayer', x.shape)
        return self.lambd(x)