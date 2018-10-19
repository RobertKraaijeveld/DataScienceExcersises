using System;
using System.IO;
using System.Linq;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public static class Parser
    {
        public static string FILE_LOCATION = "wine.csv";

        public static List<Vector> ParseToVectors()
        {
            var vectors = new List<Vector>();
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

            Console.WriteLine("Matrix 1D length = " + valuesMatrix[0][0]);

            while(columnCounter < Vector.DIMENSION)
            {
                var valuesForCurrentVector = new List<double>();

                // Getting the values at this column in the matrix
                for(int i = 0; i < valuesMatrix.Length; i++)
                {
                    valuesForCurrentVector.Add(valuesMatrix[i][columnCounter]);
                }

                vectors.Add(new Vector(valuesForCurrentVector));
                columnCounter++;
            }
            return vectors;
        }
    }
    
}