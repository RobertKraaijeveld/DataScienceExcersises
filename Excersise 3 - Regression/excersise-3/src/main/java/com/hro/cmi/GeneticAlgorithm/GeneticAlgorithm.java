package com.hro.cmi.GeneticAlgorithm;

// import org.apache.commons.lang.time.StopWatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import com.hro.cmi.Tuple;
import com.hro.cmi.Utils;
import com.hro.cmi.Vector;
import com.hro.cmi.Regression.MultipleLinearRegression;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.rules.Stopwatch;

/// <summary>
/// Used to get the best set of beta coefficients for a given vector, by minimizing the SSE using a Genetic Algorithm.
/// This has to be done for each vector: In logistic regression, each vector has it's own set of betas, which can then be 
/// </summary>
public class GeneticAlgorithm 
{
    ArrayList<Vector> vectors;
    Double crossoverRate;
    Double mutationRate;
    boolean elitism;
    int populationSize;
    int numIterations;

    private static Random r = new Random();
    MultipleLinearRegression multipleLinearRegression;


    public GeneticAlgorithm(ArrayList<Vector> vectors, Double crossoverRate, Double mutationRate, boolean elitism, int populationSize, int numIterations) 
    {
        this.vectors = vectors;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.populationSize = populationSize;
        this.numIterations = numIterations;

        this.multipleLinearRegression = new MultipleLinearRegression(this.vectors);
    }

	public GeneticIndividual Run() 
    {
        long generationRunTimeSum = 0;

        // initialize the first population
        ArrayList<GeneticIndividual> initialPopulation = CreateInitialIndividuals();
        ArrayList<GeneticIndividual> currentPopulation = initialPopulation;
        
        for (int generation = 0; generation < numIterations; generation++) 
        {
            StopWatch sw = new StopWatch();
            sw.start();

            currentPopulation = ComputeFitnesses(currentPopulation); 
	        ArrayList<GeneticIndividual> nextPopulation = new ArrayList<GeneticIndividual>(Collections.nCopies(populationSize, null)); 

            // applying elitism (Karl Marx does not approve)
            int startIndex;
            if (elitism) 
            {
                GeneticIndividual bestIndividual = Collections.max(currentPopulation,Comparator.comparing(i -> i.currentFitness));
                nextPopulation.set(0, bestIndividual);

                startIndex = 1;
            } 
            else 
            {
                startIndex = 0;
            }

            // create the Individuals of the next generation
            for (int newDouble = startIndex; newDouble < populationSize; newDouble++) 
            {           	
                Tuple<GeneticIndividual, GeneticIndividual> parents = selectTwoParents(currentPopulation);   
                
                // do a crossover between the selected parents to generate two children (with a
                // certain probability, crossover does not happen and the two parents are kept
                // unchanged)
                Tuple<GeneticIndividual, GeneticIndividual> offspring;
                
            	double randomVal = r.nextDouble();
                if (randomVal < crossoverRate) 
                {
                    offspring = crossover(parents);
                } 
                else 
                {
                    offspring = parents;
                }
                
                // save the two children in the next population (after mutation)
                nextPopulation.set(newDouble++, mutation(offspring.Item1, mutationRate));
                
                if (newDouble < populationSize) // there is still space for the second children inside the population
                {
                	nextPopulation.set(newDouble, mutation(offspring.Item2, mutationRate));    
                }
            }

            // the new population becomes the current one
            currentPopulation = nextPopulation;


            sw.stop();
            generationRunTimeSum += sw.getTime();
        }

        GeneticIndividual bestFinalIndividual = Collections.max(currentPopulation,Comparator.comparing(i -> i.currentFitness));

        System.out.println("___________________________________________________");
        System.out.println(" ");
        System.out.println("Genetic algorithm finished");
        System.out.println("___________________________________________________");
        System.out.println("Best fitness = " + bestFinalIndividual.currentFitness);
        System.out.println("Average time taken per generation = " + generationRunTimeSum / this.numIterations + " ms");

        // Returning individual with highest fitness
        double bestFitnessYet = Double.NEGATIVE_INFINITY;
        GeneticIndividual bestIndividualYet = null;

        for (int i = 0; i < currentPopulation.size(); i++) 
        {
            GeneticIndividual individual = currentPopulation.get(i);
            double fitnessOfIndividual = individual.currentFitness;

            if (fitnessOfIndividual > bestFitnessYet) 
            {
                bestFitnessYet = fitnessOfIndividual;
                bestIndividualYet = individual;
            }
        }
        return bestIndividualYet;
    }


    private ArrayList<GeneticIndividual> ComputeFitnesses(ArrayList<GeneticIndividual> individuals) 
    {
        for (GeneticIndividual currentIndividual : individuals) 
        {
            double newFitnessForCurrentIndividual = ComputeFitnessOfIndividual(currentIndividual); 
            currentIndividual.currentFitness = newFitnessForCurrentIndividual;
        }
        return individuals;
    }

    private ArrayList<GeneticIndividual> CreateInitialIndividuals() 
    {
        ArrayList<GeneticIndividual> initialIndividuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) 
        {
            initialIndividuals.add(CreateIndividual());
        }
        return initialIndividuals;
    }

    private GeneticIndividual CreateIndividual()
     {
        double[] randomValues = new double[GeneticIndividual.DIMENSION];
        for (int i = 0; i < GeneticIndividual.DIMENSION; i++) 
        {
            randomValues[i] = r.nextDouble() * 2 - 1;
        }
        return new GeneticIndividual(randomValues);
    }

    private double ComputeFitnessOfIndividual(GeneticIndividual individual)
    {
        StopWatch sw = new StopWatch();
        sw.start();

        double totalSSE = 0.0;
        for (Vector v : this.vectors) 
        {
            totalSSE += this.multipleLinearRegression.RunAndReturnSSE(GeneticIndividual.ToDoublesMatrix(individual), v); 
        }

        sw.stop();
        System.out.println("ComputeFitnessOfIndividual time: " + sw.getTime());

        // Higher SSE is worse, so we multiply by -1 to make higher SSE's return a lower fitness value.
        return Double.MAX_VALUE - totalSSE;
    }

    /*
     * ROULETTE WHEEL SELECTION
     */

    private Tuple<GeneticIndividual, GeneticIndividual> selectTwoParents(ArrayList<GeneticIndividual> population) 
    {
        ArrayList<GeneticIndividual> chosenParents = new ArrayList<GeneticIndividual>();
        ArrayList<Tuple<Double, Double>> wheelSections = getRouletteWheelSections(population);

        while (chosenParents.size() < 2) 
        {
            // doesnt always add up to exactly 100 because of rounding
            Double highestWheelSectionValue = wheelSections.get(wheelSections.size() - 1).Item2;
            double randomPercentage = Utils.getRandomDoubleWithMinMax(r, 0.0, highestWheelSectionValue);

            for (int i = 0; i < wheelSections.size(); i++) 
            {
                if (randomPercentage >= wheelSections.get(i).Item1 
                    && randomPercentage <= wheelSections.get(i).Item2
                    && chosenParents.contains(population.get(i)) == false) 
                {
                    chosenParents.add(population.get(i));
                    break;
                }
            }
        }
        
        return new Tuple<GeneticIndividual, GeneticIndividual>(chosenParents.get(0), chosenParents.get(1));
    }

    private static ArrayList<Tuple<Double, Double>> getRouletteWheelSections(ArrayList<GeneticIndividual> population) 
    {
        ArrayList<Tuple<Double, Double>> wheelSections = new ArrayList<>();
        double[] fitnesses = new double[population.size()];

        for(int j = 0; j < population.size(); j++)
        {
            fitnesses[j] = population.get(j).currentFitness;
        }


        double totalFitness = Utils.sum(fitnesses);
        ArrayList<Double> fitnessesAsPercentages = Utils.calculatePercentagesList(fitnesses, totalFitness);

        for (int i = 0; i < population.size(); i++) 
        {
            double currentFitnessPercentage = fitnessesAsPercentages.get(i);

            if (i > 0) 
            {
                double previousSectionEnd = wheelSections.get(i - 1).Item2;
                double x = previousSectionEnd + currentFitnessPercentage;

                wheelSections.add(new Tuple<Double, Double>(previousSectionEnd, x));
            } 
            else wheelSections.add(new Tuple<Double, Double>(0.0, currentFitnessPercentage));
        }
        return wheelSections;
    }

    private static Tuple<GeneticIndividual, GeneticIndividual> crossover(Tuple<GeneticIndividual, GeneticIndividual> parents) 
    {
        byte[] parent1FlattenedBytes = Utils.FlattenBytes2DArray(parents.Item1.bytes);
        byte[] parent2FlattenedBytes = Utils.FlattenBytes2DArray(parents.Item2.bytes);

        int crossoverIndex = Utils.getExistingRandomIndex(parent1FlattenedBytes, parent2FlattenedBytes);

        GeneticIndividual firstChild = new GeneticIndividual(new double[GeneticIndividual.DIMENSION]);
        GeneticIndividual secondChild = new GeneticIndividual(new double[GeneticIndividual.DIMENSION]);

        Tuple<byte[][], byte[][]> fatherSplit = Utils.splitMatrixOnIndex(parents.Item1.bytes, crossoverIndex);
        Tuple<byte[][], byte[][]> motherSplit = Utils.splitMatrixOnIndex(parents.Item2.bytes, crossoverIndex);

        firstChild.bytes = Utils.concatenate(fatherSplit.Item1, motherSplit.Item2);
        secondChild.bytes = Utils.concatenate(motherSplit.Item1, fatherSplit.Item2);

        return new Tuple<GeneticIndividual, GeneticIndividual>(firstChild, secondChild);
    }

    private static GeneticIndividual mutation(GeneticIndividual populationMember, double mutationRate) 
    {
        double randomPercent;
        for (int i = 0; i < populationMember.bytes.length; i++) 
        {
            randomPercent = Utils.getRandomDoubleWithMinMax(r, 0.0, 100.0);
            if (randomPercent < mutationRate) populationMember.flipValue(i);
        }
        return populationMember;
    }
}