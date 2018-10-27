using System;
using System.Linq;

namespace DataScienceFinalRetake
{
    public class ManhattanDistance : IDistance
    {
        public double GetDistance(Vector a, Vector b)
        {
            double dist = 0; 
            for(int i = 0; i < a.values.Count; i++)
            {
                dist += Math.Abs(a.values[i] - b.values[i]);
            }
            return dist;
        }
    }
}