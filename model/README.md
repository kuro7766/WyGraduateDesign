pytorch学习

- tflite少部分功能是无法使用的，详情见参考文章

- 可以通过ONXX转换任意model（理论上）

# data
最原始的数据集

---------------

以下几个文件夹是不同任务所需要的不同数据,但是他们的#编码是统一的,也就是说可以读取同一个配置文件Wine.json 

因此对于不同的数据集，需要编写一个不同的数据及加载器
## 0029光谱仪数据文件夹

### data2备份文件夹


是存档数据不要使用，为了避免丢失，请重新拷贝一个文件夹

### data3分类任务。


0~12的标签是原先给的酒的标签，#13 14是101 102是我测的标签。

由于编码必须是连续的，所以这里需要**格外关注一下**

normalize这个就不需要再设置了，直接端到端

其中 #0标签丢失，改为非酒（未知）类别

### data4回归任务文件夹

### data5香型分类


## 0005光谱仪

速度很快，适合于项目演示使用

# 对模型的相关说明

输入数据可以直接丢入,模型会自动做归一化操作，可以放装成一个神经网络层

# 项目文件的相关说明

参考文章:

https://www.cnblogs.com/xiaowa/p/15397774.html

