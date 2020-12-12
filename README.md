# Optimal Binary Search Tree
This project is aimed to calculate cost of a Optimal Binary Search Tree in Parallel using MPI in Java. 

**How to calculate cost of OBST using Dynamic Programming in parallel** </br>
Usage : mpiexec --hostfile myHostfile java -cp mpi.jar ParallelOBST 500 8 Probabilities_500.txt </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; or </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mpiexec --hostfile myHostfile java -cp mpi.jar ParallelOBST 500 8 </br>

**How to calculate cost of OBST in serial** </br>
Usage : java SerialOBST 500 ​Probabilities_500.txt </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; or </br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; java SerialOBST 500

**How to configure myHostFile** </br>
To set the number of processors for parallel execution we have to change configurations in myHostFile. The Contents of the file are “​localhost slots=4
” changing number to 8 will set the number of processors to 8.

**How to generate access probabilities for nodes** </br>
Generate probilities and store in a file. </br>
Usage​:​ java DataCreation 500 ​Probabilities_500.txt
