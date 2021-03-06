using System;

namespace DataScienceFinalRetake
{
    public class EuclideanDistance : IDistance
    {
        public double GetDistance(Vector a, Vector b)
        {
            double sumOfSquaredSubtractions = 0; 
            for(int i = 0; i < a.values.Count; i++)
            {
                sumOfSquaredSubtractions += Math.Pow(a.values[i] - b.values[i], 2);
            }
            var dist = Math.Sqrt(sumOfSquaredSubtractions); 
            return dist;
        }

        public string GetName()
        {
            return "Euclidean Distance";
        }

        public bool HasDistanceChanged(double distanceBetweenOldAndNewPositions)
        {
            return distanceBetweenOldAndNewPositions > 0.0;
        }
    }
}