using System;
using System.Linq;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public class KMeans
    {
        private Dictionary<int, Vector> PreviousIterationCentroidPositions = new Dictionary<int, Vector>();
        public Dictionary<int, Centroid> AllCentroids { get; set; } // KV of { Id, Centroid }
        public Dictionary<int, Vector> Vectors { get; set; } // KV of { Id, Vector }
        private Dictionary<int, int> VectorIdPerCentroidId = new Dictionary<int, int>(); // KV of { VectorId, CentroidId }
        private Dictionary<int, double> SilhouetteScorePerCentroid = new Dictionary<int, double>();
        public double FinalSSE { get; set; }
        public double FinalSilhouette { get; set; }

        public KMeans(int K, Dictionary<int, Vector> inputVectors, IDistance distanceMeasure)
        {
            // Setting distance
            Vector.Distance = distanceMeasure;

            // Populating VectorIdPerCentroidId dictionary
            VectorIdPerCentroidId = new Dictionary<int, int>();
            foreach (var kv in inputVectors)
            {
                VectorIdPerCentroidId.Add(kv.Key, 0); // centroidId 0 because not known yet.
            }

            // Picking initial centroid positions using the Forgy Method.            
            AllCentroids = new Dictionary<int, Centroid>();
            var random = new Random();

            for (int i = 0; i < K; i++)
            {
                var v = inputVectors[random.Next(0, inputVectors.Count - 1)];
                AllCentroids.Add(i + 1, new Centroid(i + 1, v));
            }

            Vectors = inputVectors;
        }


        public void IterateUntilConvergence()
        {
            while (HaveCentroidPositionsChanged())
            {
                AssignCentroidsToVectors();
                RecomputeCentroidPositions();

                PreviousIterationCentroidPositions = AllCentroids.ToDictionary(key => key.Key, val => val.Value.Position);
            }

            ComputeSse();
            ComputeSilhouettePerCentroid();
        }

        public void PrintResults()
        {
            var offersCountsForCentroid = GetOffersCountPerCentroid();

            Console.WriteLine("Distance used in this iteration: " + Vector.Distance.GetName());
            Console.WriteLine("Final SSE of this iteration: " + this.FinalSSE);
            Console.WriteLine("Final silhouette of this iteration: " + this.FinalSilhouette);

            foreach (var centroidNr in AllCentroids.Keys)
            {
                Console.WriteLine("----------------------------------------------------");
                Console.WriteLine("Centroid nr: " + centroidNr);
                Console.WriteLine("Centroid silhouette: " + SilhouetteScorePerCentroid[centroidNr]);
                Console.WriteLine("Amount of purchases per offer for centroid nr. " + centroidNr + ":\n");

                var offerCounts = offersCountsForCentroid[centroidNr];
                offerCounts.ToList().ForEach(f =>
                {
                    Console.WriteLine("Offer nr. " + f.Key + ": " + f.Value);
                });
                Console.WriteLine("----------------------------------------------------");
            }

        }


        /**
        * SSE and Silhouette-related code
        */


        public void ComputeSse()
        {
            var centroidsWithVectors = GetCentroidsAndTheirVectors();

            foreach (var centroidAndVectorsKv in centroidsWithVectors)
            {
                var centroid = AllCentroids[centroidAndVectorsKv.Key];
                var vectorsOfCentroid = centroidAndVectorsKv.Value;

                foreach (var vector in vectorsOfCentroid)
                {
                    var sqrdDistanceToCentroidPosition = Math.Pow(vector.Item2.GetDistance(centroid.Position), 2);
                    this.FinalSSE += sqrdDistanceToCentroidPosition;
                }
            }
        }

        public void ComputeSilhouettePerCentroid()
        {
            var distanceMatrix = GetDistanceMatrix();
            var centroidsAndTheirVectors = GetCentroidsAndTheirVectors();

            foreach (var centroidAndVectors in centroidsAndTheirVectors)
            {
                var silhouettesPerVector = new Dictionary<int, double>();
                var allOtherClustersWithVectors = centroidsAndTheirVectors.Where(x => x.Key != centroidAndVectors.Key
                                                                                && x.Value.Any())
                                                                          .ToList();

                foreach (var vectorKv in centroidAndVectors.Value)
                {
                    if (centroidAndVectors.Value.Where(x => x.Item1 != vectorKv.Item1)
                                               .Any())
                    {
                        var avgDistToOtherPointsInCluster =
                            centroidAndVectors.Value.Where(x => x.Item1 != vectorKv.Item1)
                                                    .Average(x => x.Item2.GetDistance(vectorKv.Item2));

                        var avgDistancesToOtherClusters =
                            allOtherClustersWithVectors.Select(x => x.Value.Average(y => y.Item2.GetDistance(vectorKv.Item2)))
                                                       .ToList();

                        var smallestAvgDistanceToOtherClusters = avgDistancesToOtherClusters.Min();


                        var divisor = avgDistToOtherPointsInCluster > smallestAvgDistanceToOtherClusters ?
                                    avgDistToOtherPointsInCluster : smallestAvgDistanceToOtherClusters;

                        if (divisor == 0) divisor = 1;

                        var silhouetteForThisVector = (smallestAvgDistanceToOtherClusters - avgDistToOtherPointsInCluster)
                                                    / divisor;


                        silhouettesPerVector.Add(vectorKv.Item1, silhouetteForThisVector);
                    }
                }

                double silhouetteForThisCentroid = 0.0;
                if (silhouettesPerVector.Any())
                {
                    silhouetteForThisCentroid = silhouettesPerVector.Values.Average();
                }

                SilhouetteScorePerCentroid.Add(centroidAndVectors.Key, silhouetteForThisCentroid);
            }

            // Making sure that centroids without any vectors get standard silhouette 0.0
            foreach (var cluster in centroidsAndTheirVectors)
            {
                if (!SilhouetteScorePerCentroid.ContainsKey(cluster.Key))
                    SilhouetteScorePerCentroid.Add(cluster.Key, 0.0);
            }

            // Calculating the final silhouette value for this iteration
            this.FinalSilhouette = SilhouetteScorePerCentroid.Values.Average();
        }

        private Dictionary<int, Dictionary<int, double>> GetDistanceMatrix()
        {
            var distanceMatrix = new Dictionary<int, Dictionary<int, double>>();

            foreach (var v1 in Vectors)
            {
                distanceMatrix.Add(v1.Key, new Dictionary<int, double>());

                foreach (var v2 in Vectors)
                {
                    distanceMatrix[v1.Key].Add(v2.Key, v1.Value.GetDistance(v2.Value));
                }
            }
            return distanceMatrix;
        }

        private Dictionary<int, List<Tuple<int, Vector>>> GetCentroidsAndTheirVectors()
        {
            var centroidsWithVectors = new Dictionary<int, List<Tuple<int, Vector>>>();
            foreach (var vectorKv in Vectors)
            {
                var centroidId = vectorKv.Value.CentroidId;
                var vectorAndIdTuple = new Tuple<int, Vector>(vectorKv.Key, vectorKv.Value);

                if (centroidsWithVectors.ContainsKey(vectorKv.Value.CentroidId))
                    centroidsWithVectors[centroidId].Add(vectorAndIdTuple);
                else
                    centroidsWithVectors.Add(centroidId, new List<Tuple<int, Vector>>() { vectorAndIdTuple });
            }

            // Making sure centroids with no vectors are added as well.
            foreach (var centroid in AllCentroids)
            {
                if (!centroidsWithVectors.ContainsKey(centroid.Key))
                    centroidsWithVectors.Add(centroid.Key, new List<Tuple<int, Vector>>());
            }

            return centroidsWithVectors;
        }

        /** 
        *   Algorithm-related code
        */
        private void AssignCentroidsToVectors()
        {
            // Each vector gets assigned to the nearest centroid
            foreach (var vectorKv in Vectors)
            {
                var centroidsByDistanceToThisVector = AllCentroids.Select(c =>
                    new
                    {
                        CentroidId = c.Key,
                        Distance = c.Value.Position.GetDistance(vectorKv.Value)
                    })
                    .ToList();

                var nearestCentroid = centroidsByDistanceToThisVector.OrderByDescending(cd => cd.Distance)
                                                                     .First();

                vectorKv.Value.CentroidId = nearestCentroid.CentroidId;
                VectorIdPerCentroidId[vectorKv.Key] = nearestCentroid.CentroidId;
            }
        }

        private void RecomputeCentroidPositions()
        {
            // Each centroid's position becomes that of the median position of all that centroids vectors
            foreach (var centroid in AllCentroids)
            {
                var vectorsOfCentroid = Vectors.Where(kv => kv.Value.CentroidId == centroid.Key)
                                               .Select(kv => kv.Value)
                                               .ToList();

                var medianOfVectors = Vector.Mean(vectorsOfCentroid);
                centroid.Value.Position = medianOfVectors;
            }
        }

        private bool HaveCentroidPositionsChanged()
        {
            // If no keys were at first iteration, so positions have to have changed :)
            if (!PreviousIterationCentroidPositions.Keys.Any()) return true;

            bool haveCentroidPositionsChanged = false;

            AllCentroids.Values.ToList().ForEach(c =>
            {
                var previousPositionOfThisCentroid = PreviousIterationCentroidPositions[c.CentroidId];
                var distanceBetweenPositions = previousPositionOfThisCentroid.GetDistance(c.Position);

                haveCentroidPositionsChanged = Vector.Distance.HasDistanceChanged(distanceBetweenPositions);
            });

            return haveCentroidPositionsChanged;
        }



        /**
        *  Printing stuff
        */

        private Dictionary<int, Dictionary<int, int>> GetOffersCountPerCentroid()
        {
            var offersCountPerCentroid = new Dictionary<int, Dictionary<int, int>>();

            foreach (var centroidKv in AllCentroids)
            {
                var vectorsOfCentroid = Vectors.Where(kv => kv.Value.CentroidId == centroidKv.Key)
                                              .ToList();

                var offerCountsOfVectorsOfThisCentroid = new Dictionary<int, int>();

                vectorsOfCentroid.ForEach(kv =>
                {
                    for (int offerNr = 0; offerNr < kv.Value.values.Count; offerNr++)
                    {
                        if (kv.Value.values[offerNr] == 1)
                        {
                            if (offerCountsOfVectorsOfThisCentroid.ContainsKey(offerNr))
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
