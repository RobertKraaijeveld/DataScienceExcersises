using System;
using System.Linq;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public class KMeans
    {
        private Dictionary<int, Vector> PreviousIterationCentroidPositions = new Dictionary<int, Vector>();
        public Dictionary<int, Centroid> Centroids { get; set; }
        public List<Vector> Vectors { get; set; }

        public KMeans(int K, List<Vector> inputVectors)
        {
            // Centroids = Centroid.GetRandomCentroids(K)
            //                     .ToDictionary(k => k.CentroidId, v => v);
            Centroids = new Dictionary<int, Centroid>();
            var randomVectors = new List<Vector>();
            var random = new Random();

            for(int i = 0; i < K; i++)
            {
                var v = inputVectors[random.Next(0, inputVectors.Count - 1)];
                Centroids.Add(i+1, new Centroid(i+1, v));
            }

            Vectors = inputVectors;
        }


        public void IterateUntilConvergence()
        {
            while(HaveCentroidPositionsChanged())
            {
                AssignCentroidsToVectors();
                RecomputeCentroidPositions();

                PreviousIterationCentroidPositions = Centroids.ToDictionary(key => key.Key, val => val.Value.Position);
            }

            PrintResults();
        }

        public void PrintResults()
        {
            var offersCountsForCentroid = GetOffersCountPerCentroid();

            foreach(var centroidNr in Centroids.Keys)
            {
                Console.WriteLine("Amount of purchases per offer for Cluster nr. " + centroidNr + ":\n");

                var offerCountsForCentroid = offersCountsForCentroid[centroidNr];
                offerCountsForCentroid.ToList().ForEach(f => 
                {
                     Console.WriteLine("Offer nr. " + f.Key + ": " + f.Value);
                });
            }
            
        }


        /**
        *   Algorithm stuff
        */

        private void AssignCentroidsToVectors()
        {
            // Each vector gets assigned to the nearest centroid
            foreach(var vector in Vectors)
            {
                var centroidsByDistanceToThisVector = Centroids.Select(c => 
                    new 
                    { 
                        CentroidId = c.Key, 
                        Distance = c.Value.Position.GetDistance(vector) 
                    })
                    .ToList(); 

                var nearestCentroid = centroidsByDistanceToThisVector.OrderByDescending(cd => cd.Distance)
                                                                     .First();
                
                vector.CentroidId = nearestCentroid.CentroidId;
            }
        }

        private void RecomputeCentroidPositions()
        {
            // Each centroid's position becomes that of the median position of all that centroids vectors
            foreach(var centroid in Centroids)
            {
                var vectorsOfCentroid = Vectors.Where(v => v.CentroidId == centroid.Key)
                                               .ToList();

                var medianOfVectors = Vector.Mean(vectorsOfCentroid);
                centroid.Value.Position = medianOfVectors;
            }
        }

        private bool HaveCentroidPositionsChanged()
        {
            // If no keys were at first iteration, so positions have to have changed :)
            if(!PreviousIterationCentroidPositions.Keys.Any()) return true;

            bool haveCentroidPositionsChanged = false;

            Centroids.Values.ToList().ForEach(c => {
                var previousPositionOfThisCentroid = PreviousIterationCentroidPositions[c.CentroidId];
                var distanceBetweenPositions = previousPositionOfThisCentroid.GetDistance(c.Position);

                if(distanceBetweenPositions > 0) haveCentroidPositionsChanged = true;
            });

            return haveCentroidPositionsChanged;
        }



        /**
        *  Printing stuff
        */

        private Dictionary<int, Dictionary<int, int>> GetOffersCountPerCentroid()
        {
            var offersCountPerCentroid = new Dictionary<int, Dictionary<int, int>>();

            foreach(var centroidKv in Centroids)
            {
                var vectorsOfCentroid = Vectors.Where(v => v.CentroidId == centroidKv.Key)
                                              .ToList();

                var offerCountsOfVectorsOfThisCentroid = new Dictionary<int, int>();

                vectorsOfCentroid.ForEach(v => 
                {
                    for(int offerNr = 0; offerNr < v.values.Count; offerNr++)
                    {
                        if(v.values[offerNr] == 1) 
                        {
                            if(offerCountsOfVectorsOfThisCentroid.ContainsKey(offerNr))
                                offerCountsOfVectorsOfThisCentroid[offerNr] += 1;
                            else
                                offerCountsOfVectorsOfThisCentroid.Add(offerNr, 1);
                        }
                    }
                });
                
                offersCountPerCentroid.Add(centroidKv.Key, offerCountsOfVectorsOfThisCentroid);
            }
            return offersCountPerCentroid;
        }

    }
}
