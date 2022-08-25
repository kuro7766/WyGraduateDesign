import matplotlib.pyplot as plt

from tool import ml

# print(pd.read_csv('data/intensity10/GPYR0029_group_1/#0/10/#0-10-1637245713190.txt'))
#
# exit(0)
offset=0
r_c=4
c_c=4

# fs=ml.getAllFiles('data/intensity10/GPYR0029_group_1/#1/10/')
for idx in range(1,2):
    # fs=ml.getAllFiles(rf'data2\data#101_#{idx}\10')
    fs= ml.getAllFiles(rf'../data3/#14/10')

    # data = [int(item.split('\t')[1]) for item in
    #        ml.read_string('data/intensity10/GPYR0029_group_1/#0/10/#0-10-1637245713190.txt').strip().split('\n')]
    # print(len(data))
    # plt.figure(figsize=(6, 2))#6，8分别对应宽和高

    f, *subs9 = plt.subplots(r_c, c_c, sharey=True)
    subs9=subs9[0]

    for row in range(r_c):
        for col in range(c_c):
            # d = ml.read_string(f'tmp/std_{row*5+col}')
            # d2 = ml.read_string('tmp/std_5')
            print(fs[row*5+col])
            r= ml.random_pick(fs, 1)[0]
            print(r)
            data = [int(item.split('\t')[1]) for item in
                    ml.read_string(fs[row * c_c + col + offset]).strip().split('\n')]
                    # ml.read_string(r).strip().split('\n')]
            ax = subs9[row][col]

            # all = np.array([float(i) for i in re.findall(r'[\.\d]+', d)])
            # all2 = np.array([float(i) for i in re.findall(r'[\.\d]+', d2)])
            # print(np.argmax(all[200:400]))
            # print(all[200+np.argmax(all[200:400])])
            # ax.plot([x for x in range(len(all))],all-all2,'r-')
            ax.plot([x for x in range(len(data))],data,'r-')
            # ax.set(aspect=1.0/ax.get_data_ratio()*1.1)

    # w,h
    # plt.savefig(f'alcohol_{idx}.png')
    plt.savefig(f'wine_{idx}.png')

    plt.show()
