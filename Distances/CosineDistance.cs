using System;
using System.Linq;

namespace DataScienceFinalRetake
{
    public class CosineDistance : IDistance
    {
        public double GetDistance(Vector a, Vector b)
        {
            double sum = 0;
            double p = 0;
            double q = 0;

            for (int i = 0; i < a.values.Count; i++)
            {
                sum += a.values[i] * b.values[i];
                p += a.values[i] * a.values[i];
                q += b.values[i] * b.values[i];
            }

            double den = Math.Sqrt(p) * Math.Sqrt(q);
            return sum == 0 ? 1.0 : 1.0 - (sum / den);
        }
    }
}