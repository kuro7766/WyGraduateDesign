import numpy as np


class GrowableNumpyArray:

    def __init__(self, dtype=np.float, grow_speed=4):
        self.data = np.zeros((100,), dtype=dtype)
        self.capacity = 100
        self.size = 0
        self.grow_speed = grow_speed

    def update(self, row):
        for r in row:
            self.add(r)

    def add(self, x):
        if self.size == self.capacity:
            self.capacity *= self.grow_speed
            newdata = np.zeros((self.capacity,))
            newdata[:self.size] = self.data
            self.data = newdata

        self.data[self.size] = x
        self.size += 1

    def finalize(self):
        data = self.data[:self.size]
        return data
