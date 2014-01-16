for l in open("train.txt").readlines():
    p = l.split()[0].split(".")[1]
    t = l.split()[1]
    print p, " ", t
