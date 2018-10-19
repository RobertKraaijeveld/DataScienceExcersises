using System;

namespace DataScienceFinalRetake
{
    public static class KMeansController
    {
        public static void Run(int K)
        {
            var vectors = Parser.ParseToVectors();

            var kmeans = new KMeans(K, vectors);
            kmeans.IterateUntilConvergence();
        }
    }
}