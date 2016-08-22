import sys
from itertools import islice


def main(filename):
    with open(filename) as fp:
        l = []
        for line in islice(fp.readlines(), 2, None):
            name, money, skill, cheat, awareness = line.split(';')
            a = int(money) / int(skill)
            l.append(a)
        print(sum(l) / len(l))


if __name__ == '__main__':
    filename = sys.argv[1]
    main(filename)
