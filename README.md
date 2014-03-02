##NonlinearGradientsUI

This repository holds the source code for GradientOptimizer, a lightweight graphical user interface for calculating nonlinear gradients in Reversed-Phase Liquid Chromatography experiments. The software allows to calculate three types of nonlinear gradients: *in silico*-optimized, MS1-optimized, and gradients based on a custom retention time distribution. 

####System Requirements 
The only requirement is to have java 1.7 (or higher) installed. You can download java [here](https://www.java.com/en/) 


####Installation instructions
1. Please download the file GradientOptimizer.jar [here](https://github.com/statisticalbiotechnology/NonlinearGradientsUI/releases/download/v1.1/GradientOptimizer.jar) 

2. If you calculate an *in silico*-optimized gradient or a gradient based on a custom retention time distribution, and you don't have tremendously large input files, you can just double-click the file GradientOptimizer.jar. The same is valid if you calculate an MS1-optimized gradient and you use a relattively small .mzML file as input (max a few hundred Mb).

If you calculate an MS1-optimized gradient and you have a large .mzML file as input, please run GradientOptimizer by opening a terminal window, navigating to the folder where you saved GradientOptimizer.jar, then typing: 

```java -Xmx2g -jar GradientOptimizer.jar```

Note that 2g corresponds to allocating to GradientOptimizer a maximum of 2Gb RAM memory. Depending on the amount of memory available on your computer, and the size of your .mzML file, replace 2 with a reasonable value.

To open a terminal window:
- Ubuntu - press Ctrl+Alt+T
- OSX -  navigate to /Applications/Utilities and choose Terminal
- Windows - follow the instructions given [here](http://windows.microsoft.com/en-us/windows-vista/open-a-command-prompt-window)

#### Documentation
A series of tutorial videos illustrating how to calculate each type of nonlinear gradient is available at the links indicated below:
- [*in silico*-optimized gradient](https://www.youtube.com/watch?v=6CaZuHQFUoU)
- [MS1-optimized gradient](https://www.youtube.com/watch?v=tLQM_10-b2g)
- [Nonlinear gradient based on a custom retention time distribution](https://www.youtube.com/watch?v=bFINl9nhAz4)

##### MS1-optimized gradients

When calculating the MS1-optimized gradient, please pay special attention to the following aspects:
 - Make sure to allocate sufficient memory when starting up the software (see Installation Instructions above)
 - If possible, use centroided data. This will speed up the calculations.
 - Use a reasonable intensity threshold. Note that a too low intensity threshold will probably lead to many noise peaks. Usually, the minimum intensity of a peak that is selected for fragmentation is a good choice.  


#### Publications 
For more information about nonlinear gradients, please check:

 - Optimized nonlinear gradients for reversed-phase liquid chromatography in shotgun proteomics.
   Moruz L, Pichler P, Stranzl T, Mechtler K, Käll L.
   In Analytical Chemistry. 2013 Aug;85(16):7777-85. [Pubmed](http://www.ncbi.nlm.nih.gov/pubmed/23841592)

For information about the retention time prediction method Elude, please check:

 - Chromatographic retention time prediction for posttranslationally modified peptides.
   Moruz L, Staes A, Foster JM, Hatzou M, Timmerman E, Martens L, Käll L.
   In Proteomics. 2012 Apr;12(8):1151-9. [PubMed](http://www.ncbi.nlm.nih.gov/pubmed/22577017)

 - Training, selection, and robust calibration of retention time models for targeted proteomics.
   Moruz L, Tomazela D, Käll L.
   In J Proteome Res. 2010 Oct 1;9(10):5209-16. [PubMed](http://www.ncbi.nlm.nih.gov/pubmed/20735070)

#### Contact 
If you experience any problems with running GradientOptimizer, or you have any questions related the use of the software, please contact Lukas Käll, lukas.kall@scilifelab.se


Luminita Moruz, 1.03.2014








