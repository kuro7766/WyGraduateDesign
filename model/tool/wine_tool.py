import re



def col2data_to_float_list(file_path):
    """
    :param file_path:
    :return:
    """
    # c = execjs.compile(r"""
    #  function find(x) {
    #      return x.match(/(\d+\.)?\d+/g);
    #  }
    # """)
    with open(file_path, 'r',encoding='utf8') as f:
        data = f.readlines()
        data = [float(re.findall('\d+',i)[1]) for i in data]
        # print(data)
        return data


if __name__ == '__main__':
    print(col2data_to_float_list('../data2/data#102/10/#102-10-1643446820238.txt'))
    # print(re.findall('(\d+\.)?\d+', '104	4757'))
