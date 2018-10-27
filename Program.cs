using System;

namespace DataScienceFinalRetake
{
    class Program
    {
        static void Main(string[] args)
        {
            var vectors = Parser.ParseToVectors();
            var kmeansAlgo = new KMeans(K: 4, inputVectors: vectors);
            
            kmeansAlgo.IterateUntilConvergence();
            kmeansAlgo.PrintResults();
        }
    }
}
