def dbg(*args, sep=' ', **kwargs):
    if kwargs.__contains__('force'):
        if kwargs['force']:
            del kwargs['force']
            print(*args, sep=sep, **kwargs)
        return

    if True:
        # if False:
        print(*args, sep=sep, **kwargs)
