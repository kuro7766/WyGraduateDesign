import matplotlib.pyplot as plt
import numpy as np

from tool import ml

import re
# print(pd.read_csv('data/intensity10/GPYR0029_group_1/#0/10/#0-10-1637245713190.txt'))
#
# exit(0)
# print(ml.read_string('data/intensity10/GPYR0029_group_1/#0/10/#0-10-1637245713190.txt').strip().split('\n'))
plt.figure(figsize=(6, 2))#6，8分别对应宽和高

f, *subs9 = plt.subplots(2, 5, sharey=True)
subs9=subs9[0]

for row in range(2):
    for col in range(5):
        d = ml.read_string(f'tmp/std_{row * 5 + col}')
        # d2 = ml.read_string('tmp/std_5')
        d2 = ml.read_string(f'tmp/std_{0}')

        ax = subs9[row][col]

        all = np.array([float(i) for i in re.findall(r'[\.\d]+', d)])
        all2 = np.array([float(i) for i in re.findall(r'[\.\d]+', d2)])
        # print(np.argmax(all[200:400]))
        # print(all[200+np.argmax(all[200:400])])
        # ax.plot([x for x in range(len(all))],all-all2,'r-')
        ax.plot([x for x in range(len(all))],all,'r-')
        ax.set(aspect=1.0/ax.get_data_ratio()*1.1)

# w,h

plt.show()
# print(len(all))
# import numpy as np
#
# pre = np.zeros(0, dtype=np.int)
# tru = np.zeros(0, dtype=np.int)
# print(pre)
# for i in range(0, len(all), 2):
#     pred_line = all[i]
#     true_line = all[i + 1]
#     # print(pred_line)
#     pre = np.append(pre, np.array(re.findall('\d+', pred_line), dtype=np.int))
#     tru = np.append(tru, np.array(re.findall('\d+', true_line), dtype=np.int))
# pre = pre.reshape(-1, 16)
# tru = tru.reshape(-1, 16)
# import matplotlib.pyplot as plt
#
# f, *subs16 = plt.subplots(4, 4, sharey=True)
# print(subs16)
# subs16 = subs16[0]
# show_what = 3
# for show_what in range(16):
#     row = show_what // 4
#     col = show_what % 4
#     subs16[row][col].plot([i for i in range(len(tru[:, show_what]))], pre[:, show_what], 'b,')
#     # subs16[row][col].plot([i for i in range(len(tru[:, show_what]))], np.average(pre,axis=1), 'g,')
#     subs16[row][col].plot([i for i in range(len(tru[:, show_what]))], tru[:, show_what], 'r,')
# plt.legend(loc="upper right")
# plt.show()
