

import threading
import functools
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
