using System;
using System.Linq;
using System.Collections.Generic;


namespace DataScienceFinalRetake
{
    public static class KMeansController
    {
        public static void Run(int K, int iterationCount)
        {
            var vectors = Parser.ParseToVectors();
            var iterations = new List<KMeansIteration>();

            for(int i = 0; i < iterationCount; i++)
            {
                var it = new KMeansIteration(K, vectors);
                it.IterateUntilConvergence();
                
                iterations.Add(it);
            }

            var bestIteration = iterations.OrderBy(x => x.FinalSSE)
                                          .First();
            bestIteration.PrintResults();
        }
    }
}