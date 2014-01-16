import sys 
import matplotlib.pyplot as plt 

lines = open(sys.argv[1]).readlines()[1:]
print lines[0:10]
times = [float(l.split()[0]) for l in lines if len(l.strip())>1]
b = [float(l.split()[1]) for l in lines if len(l.strip())>1]

plt.plot(times, b, "g--", markersize=2.0)
plt.plot([times[0], times[len(times)-1]], [b[0], b[len(b)-1]], "b-")
plt.xlim(0, 300)

plt.show()
