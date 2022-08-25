from sklearn.svm import SVC
from sklearn.datasets import load_iris
from sklite import LazyExport

iris = load_iris()
X_train, y_train = iris.data, iris.target
clf = SVC()
clf.fit(X_train, y_train)
lazy = LazyExport(clf)
lazy.save('model/svciris.json')