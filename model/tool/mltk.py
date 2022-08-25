import functools
import threading
import time

_default_lock = threading.Lock()
_lock_list = {'default': _default_lock}

def _dbg(*args,**kwargs):
    # print(*args,**kwargs)
    pass

def synchronized(lock_name='default'):
    if _lock_list.get(lock_name) is None:
        _lock_list[lock_name] = threading.Lock()
        print('create lock:', lock_name)
    lock=_lock_list.get(lock_name, _default_lock)
    assert lock is not None, 'lock_name %s not found' % lock_name
    def sync(wrapped):
        # lock = threading.Lock()
        lk = lock if lock else _lock_list['default']
        _dbg('sync created', lk, id(lk))

        @functools.wraps(wrapped)
        def _wrap(*args, **kwargs):
            with lk:
                _dbg("Calling '%s' with Lock %s@%s from thread %s [%s]"
                      % (wrapped.__name__, lock_name,id(lk),
                         threading.current_thread().name, time.time()))
                result = wrapped(*args, **kwargs)
                _dbg("Done '%s' with Lock %s@%s from thread %s [%s]"
                      % (wrapped.__name__, lock_name,id(lk),
                         threading.current_thread().name, time.time()))
                return result

        return _wrap

    return sync


def monkeypatch_method_to_class(cls):
    def decorator(func):
        setattr(cls, func.__name__, func)
        return func
    return decorator

class Restorer:

    def __init__(self, varName):
        self.varName = varName

    def __enter__(self):
        self.oldValue = globals()[self.varName]

    def __exit__(self, exc_type, exc_val, exc_tb):
        globals()[self.varName] = self.oldValue

if __name__ == '__main__':
    x=1
