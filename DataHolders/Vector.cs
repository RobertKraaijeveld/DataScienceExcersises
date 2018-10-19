using System;
using System.Collections.Generic;

namespace DataScienceFinalRetake
{
    public class Vector
    {
        public static readonly IDistance Distance = new EuclideanDistance();
        public int CentroidId;
        public static readonly int DIMENSION = 32;
        public List<double> values = new List<double>();

        public Vector() 
        { 
            this.values = new List<double>();
        }
        public Vector(List<double> values)
        {
            this.values = values;
        }


    	public override bool Equals(Object obj)
        {
            var anotherVector = obj as Vector;
            if(anotherVector == null) return false;

            for(int i = 0; i < this.values.Count; i++)
            {
                if(anotherVector.values[i] != this.values[i]) return false;
            }
            return true;
        }

        public double GetDistance(Vector anotherVector)
        {
            return Distance.GetDistance(this, anotherVector);
        }

        public Vector Sum(Vector anotherVector)
        {
            CheckVectorLengths(anotherVector);

            Vector summedVector = new Vector(this.values);

            for(int i = 0; i < anotherVector.values.Count; i++)
            {
                summedVector.values[i] += anotherVector.values[i];
            }    
            return summedVector;
        }

        public static Vector GetRandomVector(int rowCount)
        {
            var randomVector = new Vector();
            var randomGen = new Random();

            for(int i = 0; i < rowCount; i++)
            {
                randomVector.values.Add(randomGen.Next(0, 1));
            }
            return randomVector;
        }

        public static Vector Mean(List<Vector> vectors)
        {
            var mean = new Vector();
            List<double> totalValues = new List<double>();

            // Going through each dimension of each vector, gathering the totals per dimension
            for(int dimension = 0; dimension < Vector.DIMENSION; dimension++)
            {
                double totalForCurrentDimension = 0;

                foreach(var v in vectors)
                    totalForCurrentDimension += v.values[dimension];
                
                totalValues.Add(totalForCurrentDimension);
            }

            // Taking the average of each dimension, creating the mean vector
            foreach(var totalForDimension in totalValues)
            {
                mean.values.Add(totalForDimension / vectors.Count);
            }
            return mean;
        }


        private double GetEuclidDistance(Vector anotherVector)
        {
            CheckVectorLengths(anotherVector);

            double sumOfSquaredSubtractions = 0; 
            for(int i = 0; i < values.Count; i++)
            {
                sumOfSquaredSubtractions += Math.Pow(values[i] - anotherVector.values[i], 2);
            }
            return Math.Sqrt(sumOfSquaredSubtractions);
        }

        private void CheckVectorLengths(Vector anotherVector)
        {
            if(anotherVector.values.Count != this.values.Count) 
                throw new Exception("Vectors aren't equal");

        }
    }      
}