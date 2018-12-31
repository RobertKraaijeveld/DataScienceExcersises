# Excersise 1: K-Means
This C# program runs a K-Means clustering on the 'wine.csv' dataset (included in the 'Data Smart' book). Each row represents a customer, with each cell value indicating whether that customer bought a certain offer of wine or not.

The default K is 4, using the Euclidean Distance. The algorithm is ran 150 times, after which the iteration with the best SSE and silhouette is picked and it's contents are printed to the console.

You can change the value of K, the distance used and the amount of iterations in the Program.cs file.

Differences with the reported SSE / Silhouette values in the 'Data Smart' textbook can be explained by the fact that this K-Means implementation is ran multiple times, and that this implementation uses the 'Forgy Method' for picking random starting centers for each cluster, rather than finding the optimal starting centers as it is done in the textbook.

Run using 
`dotnet restore`
`dotnet run`