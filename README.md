NonlinearGradientsUI
====================

This repository holds the source code for GradientOptimizer, a lightweight graphical user interface for calculating nonlinear gradients in reversed-phase liquid chromatography experiments. The software allows to calculate three types of nonlinear gradients: in silico-optimized, MS1-optimized, and gradients based on a custom retention time distribution. 

##System Requirements 
The only requirement is to have java 1.7 (or higher) installed. You can install java [here](https://www.java.com/en/) 


##Installation instructions
1. Please download the file GradientOptimizer.jar [here](https://github.com/statisticalbiotechnology/NonlinearGradientsUI/releases/download/v1.0/GradientOptimizer.jar) 

2. If you calculate an insilico-optimized gradient or a gradient based on a custom retention time distribution, you can just double-click the file GradientOptimizer.jar. The same is valid if you calculate an MS1-optimized gradient and you use a small .mzML file as input (max a few hundred Mb).

If you calculate an MS1-optimized gradient and you have a large .mzML file as input, please run GradientOptimizer by opening a terminal window and typing: 
java -Xmx2g -jar GradientOptimizer.jar

Note that 2g corresponds to allocating to GradientOptimizer a maximum of 2Gb RAM memory. Depending on the amount of memory available on your computer, and the size of your .mzML file, replace 2 with a reasonable value.

To open a terminal window:
* Ubuntu - press Ctrl+Alt+T
* OSX -  /Applications/Utilities and doible-click on Terminal
* [Windows](http://windows.microsoft.com/en-us/windows-vista/open-a-command-prompt-window))

MS1:
https://www.youtube.com/watch?v=tLQM_10-b2g
