using System;
using System.IO;
using System.Linq;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public static class Parser
    {
        public static string FILE_LOCATION = "wine.csv";

        public static Dictionary<int, Vector> ParseToVectors()
        {
            var vectors = new Dictionary<int, Vector>();
            int columnCounter = 0;

            // Console.WriteLine(FILE_LOCATION.)
            var lines = File.ReadAllLines(FILE_LOCATION);
            var valuesMatrix = new double[lines.Count()][];

            // Filling the lines matrix. 
            lines = lines.ToArray();
            for(int i = 0; i < lines.Count(); i++)
            {
                // The current line in the file is converted to an array of double values in the matrix
                valuesMatrix[i] = lines[i].Split(',').Select(chr => double.Parse(chr.ToString()))
                                          .ToArray();
            }
                

            for(int i = 0; i < valuesMatrix.Length; i++)
            {
                var valuesForCurrentVector = new List<double>();

                for(int j = 0; j < valuesMatrix[i].Length; j++)
                {
                    valuesForCurrentVector.Add(valuesMatrix[i][j]);
                }
                vectors.Add(i, new Vector(valuesForCurrentVector));
            }
            return vectors;
        }
    }
    
}