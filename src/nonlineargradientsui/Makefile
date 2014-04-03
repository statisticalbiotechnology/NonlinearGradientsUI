#A makefile just to avoid using netbeans when developing (in case you, like me, don't enjoy useing it).
#Requires still however, that netbeans is installed and has the project added.
#Change the MYWD variable to whatever your position of your build.xml-file is (inside your home directory).
NBPATH=/usr/share/netbeans
ANTPATH=${NBPATH}/extide/ant/bin
MYWD=SciLifeLab/github/NonlinearGradientsUI
all: myjarfile

myjarfile: *.java
	${ANTPATH}/ant -f ${HOME}/${MYWD} -Dnb.internal.action.name=rebuild clean jar
	${ANTPATH}/ant -f ${HOME}/${MYWD} package-for-store
