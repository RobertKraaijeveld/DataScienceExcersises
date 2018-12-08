using System;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public class Centroid
    {
        public Centroid(int centroidId, Vector position)
        {
            CentroidId = centroidId;
            Position = position;
        }

        public int CentroidId { get; set; } 
        public Vector Position { get; set; } 

        public static List<Centroid> GetRandomCentroids(int amount)
        {
            var randomCentroids = new List<Centroid>();

            for(int i = 0; i < amount; i++)
            {
                var randomPosition = Vector.GetRandomVector(Vector.DIMENSION);

                var newCentroid = new Centroid(i + 1, randomPosition);
                randomCentroids.Add(newCentroid);
            } 
            return randomCentroids;
        }
    }      
}