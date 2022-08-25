from collections import OrderedDict

import torch.nn as nn

import torch
from torch import nn
import numpy as np
import matplotlib.pyplot as plt

import random
import torch.nn.functional as F
from general_model.layers import LambdaLayer
from tool.dbg import dbg
from tool import wine_data_loader
from model_helper.singlecudatrainer import SingleCudaTrainer
from model_helper.single_gpu_dataloader import SingleGpuTensorWrapper


class MlpClassifier(nn.Module):

    def __init__(self):
        super().__init__()

        # self.layers = torch.nn.Sequential(
        #     OrderedDict([
        #         # layernorm
        #         ('layernorm1', nn.LayerNorm([1000])),
        #         # ('layernorm1', nn.BatchNorm1d(1000)),
        #         ("h1", torch.nn.LazyLinear(1000)),
        #         ("act1", torch.nn.Sigmoid()),
        #         # ("h2", torch.nn.LazyLinear(1000)),
        #         # ("act2", torch.nn.Sigmoid()),
        #         ("h4", torch.nn.LazyLinear(15)),
        #         ("softmax", torch.nn.Softmax(dim=1))
        #     ]
        #     )
        # )

        self.layers = torch.nn.Sequential(
            OrderedDict([
                # layernorm
                ('layernorm1', nn.LayerNorm([1000])),
                # reshape to (batch_size, 1, 1000)
                ('reshape1', LambdaLayer(lambda x: x.view(x.shape[0], 1, -1))),
                # ('layernorm1', nn.BatchNorm1d(1000)),
                ("conv1", torch.nn.Conv1d(1, 16, kernel_size=3, stride=1, padding=1)),
                ("flat1", LambdaLayer(lambda x: x.view(x.shape[0], -1))),
                ("h1", torch.nn.LazyLinear(1000)),
                ("act1", torch.nn.Tanh()),

                # ("h2", torch.nn.Linear(1000,1000)),
                # ("act2", torch.nn.Sigmoid()),
                ("h4", torch.nn.Linear(1000, 15)),
                ("softmax", torch.nn.Softmax(dim=1))
            ]
            )
        )

    def forward(self, x):
        x = self.layers(x)
        # dbg(x.shape)
        return x


def main():
    x, y = wine_data_loader.get_data3(use_onehot=False)

    # dbg(y)
    x, y, x1, y1 = wine_data_loader.split(x, y, 0.3)
    use_gpu = True

    m = MlpClassifier()

    trainer = SingleCudaTrainer(m, use_gpu)

    optimizer = torch.optim.Adam(trainer.model.parameters(), lr=0.0001)  # optimize all cnn parameters

    loss_func = nn.CrossEntropyLoss()

    x = SingleGpuTensorWrapper(torch.from_numpy(x).float(), use_gpu=use_gpu)
    y = SingleGpuTensorWrapper(torch.from_numpy(y).long(), use_gpu=use_gpu)
    x1 = SingleGpuTensorWrapper(torch.from_numpy(x1).float(), use_gpu=use_gpu)
    y1 = SingleGpuTensorWrapper(torch.from_numpy(y1).long(), use_gpu=use_gpu)

    # dbg(x.tensor.device)
    # curve = torch.nn.LayerNorm([1000])(x.tensor)
    # dbg(torch.mean(curve[0]), torch.std_mean(curve[0]))

    # dbg('yshape',y.tensor.shape)

    # epoch = 1
    loss_list = []
    acc_list = []
    # epoch = 20000
    epoch = 200
    for it in range(epoch):
        # dbg('before predict:')
        predict = trainer.model(x.tensor)
        # dbg('ok at it:', it)
        loss = loss_func(predict, y.tensor)  # calculate loss

        loss_list.append(loss.item())
        optimizer.zero_grad()  # clear gradients for this training step
        loss.backward()  # backpropagation, compute gradients
        optimizer.step()  # apply gradients

        if it % 10 == 0:
            # 在循环里只打印三行可以在控制台没有抖动输出
            # dbg(predict[10],y.tensor[10])
            # dbg('predict', predict[:2].cpu().data.numpy(),'real', y.tensor[:2].cpu().data.numpy())
            # dbg('predict', predict.cpu().data.numpy())

            # dbg('iteration:', it, 'loss:', loss.item())
            # dbg('accuracy', np.mean(np.argmax(predict.cpu().data.numpy(), axis=1) == y.tensor.cpu().data.numpy()))
            dbg(str(it), ',validation accuracy',
                np.mean(np.argmax(trainer.model(x1.tensor).cpu().data.numpy(), axis=1) == y1.tensor.cpu().data.numpy()))

            # dbg('val test')
            predict = trainer.model(x1.tensor)
            dbg(np.argmax(predict[:50].cpu().data.numpy()[:15], axis=1))
            dbg(y1.tensor[:50].cpu().data.numpy()[:15])
            plt.plot(loss_list)
            dbg('last loss', loss_list[-1])

    #     if it % 2000 == 0:
    #         plt.clf()  # Clear figure
    #
    #         plt.plot(steps, y_np.flatten(), 'r-')
    #         plt.plot(steps, predict.cpu().data.numpy().flatten(), 'b-', label=f'{it}')
    #         plt.legend()
    #         plt.pause(0.05)
    #
    #         dbg(it, loss.cpu().data.numpy(), force=True)
    #
    # predict = m(x, test_something=False)
    # plt.plot(steps, y_np.flatten(), 'r-')
    # plt.plot(steps, predict.cpu().data.numpy().flatten(), 'b-')
    plt.show()

    torch.save(trainer.model.state_dict(), 'model/torch_model_mlp_class_15.pth')

    # plt.pause(1)
