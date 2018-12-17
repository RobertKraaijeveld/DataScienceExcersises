package com.hro.cmi.GeneticAlgorithm;

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

    public GeneticAlgorithm(ArrayList<Vector> vectors, Double crossoverRate, Double mutationRate, boolean elitism, int populationSize, int numIterations) 
    {
        this.vectors = vectors;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.populationSize = populationSize;
        this.numIterations = numIterations;
    }

    public GeneticIndividual Run() 
    {
        // initialize the first population
        ArrayList<GeneticIndividual> initialPopulation = CreateInitialIndividuals();
        ArrayList<GeneticIndividual> currentPopulation = initialPopulation;

        for (int generation = 0; generation < numIterations; generation++) 
        {
            // compute fitness of each Individual in the population
            double[] fitnesses = ComputeFitnesses(currentPopulation);
            ArrayList<GeneticIndividual> nextPopulation = new ArrayList<GeneticIndividual>(Collections.nCopies(populationSize, null)); 

            // applying elitism (daddy Marx does not approve)
            int startIndex;
            if (elitism) 
            {
                startIndex = 1;
                ArrayList<Tuple<GeneticIndividual, Double>> populationWithFitness = GetPopulationWithFitness(currentPopulation, fitnesses);

                Tuple<GeneticIndividual, Double> bestIndividual = Collections.max(populationWithFitness,Comparator.comparing(i -> i.Item2));
                nextPopulation.set(0, bestIndividual.Item1);
            } 
            else 
            {
                startIndex = 0;
            }

            // create the Individuals of the next generation
            for (int newDouble = startIndex; newDouble < populationSize; newDouble++) 
            {
                Tuple<GeneticIndividual, GeneticIndividual> parents = selectTwoParents(currentPopulation, fitnesses);

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
        }

        // recompute the fitnesses on the final population and return the best individual
        ArrayList<Double> finalFitnesses = new ArrayList<>();
        for (GeneticIndividual value : currentPopulation) {
            finalFitnesses.add(ComputeFitnessOfIndividual(value));
        }

        System.out.println("___________________________________________________");
        System.out.println(" ");
        System.out.println("Genetic algorithm finished");
        System.out.println("___________________________________________________");
        System.out.println("Best fitness = " + Collections.max(finalFitnesses));
        System.out.println("Average fitness = " + (Utils.sum(finalFitnesses) / finalFitnesses.size()));

        // Returning individual with highest fitness
        double bestFitnessYet = Double.NEGATIVE_INFINITY;
        GeneticIndividual bestIndividualYet = null;

        for (int i = 0; i < currentPopulation.size(); i++) {
            GeneticIndividual individual = currentPopulation.get(i);
            double fitnessOfIndividual = finalFitnesses.get(i);

            if (fitnessOfIndividual > bestFitnessYet) {
                bestFitnessYet = fitnessOfIndividual;
                bestIndividualYet = individual;
            }
        }

        return bestIndividualYet;
    }

    private ArrayList<Tuple<GeneticIndividual, Double>> GetPopulationWithFitness(ArrayList<GeneticIndividual> currentPopulation, double[] fitnesses) 
    {
        ArrayList<Tuple<GeneticIndividual, Double>> populationWithFitnesses = new ArrayList<>();

        for (GeneticIndividual individual : currentPopulation) 
        {
            populationWithFitnesses.add(new Tuple<GeneticIndividual, Double>(individual, ComputeFitnessOfIndividual(individual)));
        }
        return populationWithFitnesses;
    }

    private double[] ComputeFitnesses(ArrayList<GeneticIndividual> individuals) 
    {
        double[] fitnesses = new double[individuals.size()];
        for (int i = 0; i < individuals.size(); i++) 
        {
            fitnesses[i] = ComputeFitnessOfIndividual(individuals.get(i));
        }
        return fitnesses;
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
        double[] valuesOfIndividual = GeneticIndividual.ToDoubles(individual);
        MultipleLinearRegression multipleLinearRegression = new MultipleLinearRegression();

        double totalSSE = 0.0;
        for (Vector v : this.vectors) 
        {
            totalSSE += multipleLinearRegression.RunAndReturnSSE(Utils.To2DArrayWithSingle2ndElement(valuesOfIndividual), v); 
        }

        // Higher SSE is worse, so we multiply by -1 to make higher SSE's return a lower fitness value.
        return Double.MAX_VALUE - totalSSE;
    }

    /*
     * ROULETTE WHEEL SELECTION
     */

    private Tuple<GeneticIndividual, GeneticIndividual> selectTwoParents(ArrayList<GeneticIndividual> population, double[] fitnesses) 
    {
        ArrayList<GeneticIndividual> chosenParents = new ArrayList<GeneticIndividual>();
        ArrayList<Tuple<Double, Double>> wheelSections = getRouletteWheelSections(population, fitnesses);

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

    private static ArrayList<Tuple<Double, Double>> getRouletteWheelSections(ArrayList<GeneticIndividual> population, double[] fitnesses) 
    {
        ArrayList<Tuple<Double, Double>> wheelSections = new ArrayList<>();

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
        int crossoverIndex = Utils.getExistingRandomIndex(parents.Item1.bytes, parents.Item2.bytes);

        GeneticIndividual firstChild = new GeneticIndividual(new double[GeneticIndividual.DIMENSION]);
        GeneticIndividual secondChild = new GeneticIndividual(new double[GeneticIndividual.DIMENSION]);

        Tuple<byte[], byte[]> fatherSplit = Utils.splitArrayOnIndex(parents.Item1.bytes, crossoverIndex);
        Tuple<byte[], byte[]> motherSplit = Utils.splitArrayOnIndex(parents.Item2.bytes, crossoverIndex);

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
            if (randomPercent < mutationRate) populationMember.flipByte(i);
        }
        return populationMember;
    }
}