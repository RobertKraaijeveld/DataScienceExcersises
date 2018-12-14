package com.hro.cmi.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import com.hro.cmi.Tuple;
import com.hro.cmi.Utils;

public class GeneticAlgorithm
{
        Double crossoverRate;
        Double mutationRate;
        boolean elitism; 
        int populationSize;
        int numIterations;
        private static Random r = new Random();

        // TODO: CONVERT FITNESS FUNCTION TO BE: 
        public GeneticAlgorithm(Double crossoverRate, Double mutationRate, boolean elitism, int populationSize, int numIterations)
        {
            this.crossoverRate = crossoverRate;
            this.mutationRate = mutationRate;
            this.elitism = elitism;
            this.populationSize = populationSize;
            this.numIterations = numIterations;
        }

        public BinaryVal Run()
        {
            // initialize the first population
            ArrayList<BinaryVal> initialPopulation = CreateInitialIndividuals();
            ArrayList<BinaryVal> currentPopulation = initialPopulation;

            for (int generation = 0; generation < numIterations; generation++)
            {
                // compute fitness of each Individual in the population
                double[] fitnesses = ComputeFitnesses(currentPopulation);
                ArrayList<BinaryVal> nextPopulation = new ArrayList<BinaryVal>(Collections.nCopies(populationSize, null)); // TODO: CHECk

                // applying elitism (daddy Marx does not approve)
                int startIndex;
                if (elitism)
                {
                    startIndex = 1;
                    ArrayList<Tuple<BinaryVal, Double>> populationWithFitness = GetPopulationWithFitness(currentPopulation, fitnesses);

                    Tuple<BinaryVal, Double> bestIndividual = Collections.max(populationWithFitness, Comparator.comparing(i -> i.Item2));
                   nextPopulation.set(0, bestIndividual.Item1);
                }
                else
                {
                    startIndex = 0;
                }

                // create the Individuals of the next generation
                for (int newDouble = startIndex; newDouble < populationSize; newDouble++)
                {
                    Tuple<BinaryVal, BinaryVal> parents = selectTwoParents(currentPopulation, fitnesses);

                    // do a crossover between the selected parents to generate two children (with a certain probability, crossover does not happen and the two parents are kept unchanged)
                    Tuple<BinaryVal, BinaryVal> offspring;

                    double randomVal = r.nextDouble();
                    if (randomVal  < crossoverRate) 
                    {
                        offspring = crossover(parents);
                    }
                    else 
                    {
                        offspring = parents;
                    }

                    // save the two children in the next population (after mutation)
                    nextPopulation.set(newDouble++, mutation(offspring.Item1, mutationRate));

                    if (newDouble < populationSize) //there is still space for the second children inside the population
                    {
                        nextPopulation.set(newDouble,  mutation(offspring.Item2, mutationRate));
                    }
                }

                // the new population becomes the current one
                currentPopulation = nextPopulation;
            }

            // // recompute the fitnesses on the final population and return the best Individual
            // var finalFitnesses = Enumerable.Range(0, populationSize).Select(i => computeFitness(currentPopulation[i])).ToArray();
            ArrayList<Double> finalFitnesses = new ArrayList<>();
            for(BinaryVal value : currentPopulation)
            {
                finalFitnesses.add(ComputeFitnessOfIndividual(value));
            }

            System.out.println("___________________________________________________");
            System.out.println(" ");
            System.out.println("Genetic algorithm finished");
            System.out.println("___________________________________________________");
            System.out.println("Best fitness = " + Collections.max(finalFitnesses));
            System.out.println("Average fitness = " + (Utils.sum(finalFitnesses) / finalFitnesses.size()));

            // Returning individual with highest fitness
            double bestFitnessYet = Double.MIN_VALUE;
            BinaryVal bestIndividualYet = null;
            for(int i = 0; i < currentPopulation.size(); i++)
            {
                BinaryVal individual = currentPopulation.get(i);
                double fitnessOfIndividual = finalFitnesses.get(i);

                if(fitnessOfIndividual > bestFitnessYet) 
                {
                    bestFitnessYet = fitnessOfIndividual;
                    bestIndividualYet = individual;
                }
            }

            return bestIndividualYet;
        }


        private ArrayList<Tuple<BinaryVal, Double>> GetPopulationWithFitness(ArrayList<BinaryVal> currentPopulation, double[] fitnesses)
        {
            ArrayList<Tuple<BinaryVal, Double>> populationWithFitnesses = new ArrayList<>();
            for(BinaryVal individual : currentPopulation)
            {
                populationWithFitnesses.add(new Tuple<BinaryVal, Double>(individual, ComputeFitnessOfIndividual(individual))); 
            }
            return populationWithFitnesses;
        }

        private double[] ComputeFitnesses(ArrayList<BinaryVal> individuals)
        {
            double[] fitnesses = new double[individuals.size()];
            for(int i = 0; i < individuals.size(); i++)
            {
                fitnesses[i] = ComputeFitnessOfIndividual(individuals.get(i));
            }
            return fitnesses;
        }

        private ArrayList<BinaryVal> CreateInitialIndividuals()
        {
            ArrayList<BinaryVal> initialIndividuals = new ArrayList<>();
            for(int i = 0; i < populationSize; i++)
            {
                initialIndividuals.add(CreateIndividual());
            }
            return initialIndividuals;
        }
        
        private BinaryVal CreateIndividual()
        {
            return BinaryVal.intToBinary(r.nextInt(32));
        }


        private static double ComputeFitnessOfIndividual(BinaryVal binaryVal)
        {
            int intForBinary = BinaryVal.binaryToInt(binaryVal);
            return -1 * (Math.pow(intForBinary, 2.0)) + (7 * intForBinary); 
        }


        /*
        *    ROULETTE WHEEL SELECTION
         */

        private Tuple<BinaryVal, BinaryVal> selectTwoParents (ArrayList<BinaryVal> population, double[] fitnesses)
        {
            ArrayList<BinaryVal> chosenParents = new ArrayList<BinaryVal>();
            ArrayList<Tuple<Double, Double>> wheelSections = getRouletteWheelSections(population, fitnesses); 

            while(chosenParents.size() < 2)
            {
                //doesnt always add up to exactly 100 because of rounding
                Double highestWheelSectionValue =  wheelSections.get(wheelSections.size() - 1).Item2;
                double randomPercentage = Utils.getRandomDoubleWithMinMax(r, 0.0, highestWheelSectionValue); 

                for (int i = 0; i < wheelSections.size(); i++)
                {
                    if(randomPercentage >= wheelSections.get(i).Item1 && randomPercentage <= wheelSections.get(i).Item2
                        && chosenParents.contains(population.get(i)) == false)
                    {
                        chosenParents.add(population.get(i));
                        break;
                    }
                }
            }

            return new Tuple<BinaryVal, BinaryVal>(chosenParents.get(0), chosenParents.get(1));
        }

        private static ArrayList<Tuple<Double, Double>> getRouletteWheelSections(ArrayList<BinaryVal> population, double[] fitnesses)
        {
            ArrayList<Tuple<Double, Double>> wheelSections = new ArrayList<>();

            double totalFitness = Utils.sum(fitnesses);
            ArrayList<Double> fitnessesAsPercentages = Utils.calculatePercentagesList(fitnesses, totalFitness);

            for(int i = 0; i < population.size(); i++)
            {
                double currentFitnessPercentage = fitnessesAsPercentages.get(i);

                if(i > 0)
                {
                    double previousSectionEnd = wheelSections.get(i-1).Item2;
                    double x = previousSectionEnd + currentFitnessPercentage;

                    wheelSections.add(new Tuple<Double, Double>(previousSectionEnd, x));                                       
                }
                else
                    wheelSections.add(new Tuple<Double, Double>(0.0, currentFitnessPercentage));
            }
            return wheelSections;
        }

        private static Tuple<BinaryVal, BinaryVal> crossover (Tuple<BinaryVal, BinaryVal> parents)
        {
            int crossoverIndex = Utils.getExistingRandomIndex(parents.Item1.bits, parents.Item2.bits);

            BinaryVal firstChild = new BinaryVal(new int[5]);
            BinaryVal secondChild = new BinaryVal(new int[5]);            

            Tuple<int[], int[]> fatherSplit = Utils.splitArrayOnIndex(parents.Item1.bits, crossoverIndex);
            Tuple<int[], int[]> motherSplit = Utils.splitArrayOnIndex(parents.Item2.bits, crossoverIndex);

            firstChild.bits = Utils.concatenate(fatherSplit.Item1, motherSplit.Item2);
            secondChild.bits = Utils.concatenate(motherSplit.Item1, fatherSplit.Item2);

            return new Tuple<BinaryVal, BinaryVal>(firstChild, secondChild);
        }

        private static BinaryVal mutation(BinaryVal populationMember, double mutationRate)
        {
            double randomPercent;
            for (int i = 0; i < populationMember.bits.length; i++)
            {
                randomPercent = Utils.getRandomDoubleWithMinMax(r, 0.0, 100.0);
                if(randomPercent < mutationRate) populationMember.flipBit(i);                   
            }
            return populationMember;
        }
    }