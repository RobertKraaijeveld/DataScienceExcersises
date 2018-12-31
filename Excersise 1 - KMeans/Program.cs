using System;
using System.Linq;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    class Program
    {
        static void Main(string[] args)
        {
            Dictionary<int, Vector> vectors = Parser.ParseToVectors();
            RunKmeansAndPrintBest(K: 4, amountOfIterations: 150, distanceMeasure: new EuclideanDistance(), vectors: vectors);
        }

        private static void RunKmeansAndPrintBest(int K, int amountOfIterations, IDistance distanceMeasure, Dictionary<int, Vector> vectors)
        {
            Console.WriteLine($"Running {amountOfIterations} iterations of Kmeans with K = {K} and distance {distanceMeasure.GetName()}");
            var iterations = new List<KMeans>();

            for(int i = 0; i < amountOfIterations; i++)
            {
                if(i % 10 == 0) Console.WriteLine($"{i} iterations done");

                var kmeansIteration = new KMeans(K, vectors, distanceMeasure);
                kmeansIteration.IterateUntilConvergence();
                iterations.Add(kmeansIteration);
            }   

            double averageSse = iterations.Average(x => x.FinalSSE);
            double averageSilhouette = iterations.Average(x => x.FinalSilhouette);
            var bestIteration = iterations.OrderByDescending(x => x.FinalSilhouette)
                                          .First(); 
                                          
            Console.WriteLine("-------------------------------------------------------");
            Console.WriteLine($"DONE! Avg SSE (of all iterations): {averageSse}, Avg silhouette (of all iterations): {averageSilhouette}");
            Console.WriteLine("BEST ITERATION:");
            Console.WriteLine("-------------------------------------------------------");

            bestIteration.PrintResults();
        }
    }
}
