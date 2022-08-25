from sklearn.cluster import KMeans
import argparse
# import numpy as np
parser = argparse.ArgumentParser('lib', add_help=False)
parser.add_argument('--a', help='action')
args, unparsed = parser.parse_known_args()
# args.a=0
# print(args)
action = args.a
# a = '0'

def csv2list(file_name):
    with open(file_name, 'r') as f:
        s=f.read()
        return [float(d) for d in s.strip().split(' ')]


x = [csv2list(f'tmp/std_{(i + 0) % 10}') for i in range(10)]
# print(x)
# print(np.array(x).shape)
# x = np.array([[1,2],[2,3],[2,4]])

kmeans = KMeans(n_clusters=2)
a = kmeans.fit(x)

centroids = kmeans.cluster_centers_

labels = kmeans.labels_
print(labels)
# print(centroids)
# print(np.logical_xor(labels, np.ones((len(labels)), )))
# check [:5] and [5:] are different
check = labels[0]
flag = True
for i in range(len(labels)):
    current = labels[i]

    if not current == check:
        flag = False
        break

    if i == len(labels) // 2 - 1:
        check = not check

print(1 if flag else 0)
