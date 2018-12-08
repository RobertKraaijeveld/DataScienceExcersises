using System;

namespace DataScienceFinalRetake
{
    public interface IDistance
    {
        double GetDistance(Vector a, Vector b);
        string GetName();

        bool HasDistanceChanged(double distanceBetweenOldAndNewPositions);
    }
}