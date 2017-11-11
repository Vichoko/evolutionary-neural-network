package darwin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import darwin.Global;
import network.NeuralNetwork;
import util.utils;

/**
 * 
 * @author Vicente Oyanedel M.
 *
 */
public class NaturalSelection {

	public boolean existPerfectIndividual;
	public NeuralNetwork perfectIndividual;

	List<NeuralNetwork> population;
	public List<NeuralNetwork> getPopulation() {
		return this.population;
	}
	
	double[][] input;
	double[][] expectedOutput;

	public int populationSize;
	public int totalFitness;
	//String perfectGenes;

	/**
	 * Instance with an initial 'population' and notion of perfect individual
	 * ('perfectGenes').
	 * 
	 * @param population
	 *            Individuals in the system.
	 * @param perfectGenes
	 *            Genes of the perfect individual (max fitness).
	 */
	public NaturalSelection(List<NeuralNetwork> population, double[][] input, double[][] expectedOutput) {
		this.population = population;
		this.populationSize = population.size();
		this.existPerfectIndividual = false;
		this.input = input;
		this.expectedOutput = expectedOutput;
	}

	/**
	 * Grabs pairs of survivors by roulette and mate them to create a new child.
	 * Until the population is recovered; i.e. populationSize childs are
	 * created.
	 * 
	 * @param mutationRate
	 *            Number between 0 and 1, probability of have a random mutation
	 *            in a gene.
	 */
	public void matingPhaseByRoulette(double mutationRate) {
		Random rand = new Random();
		List<NeuralNetwork> children = new ArrayList<NeuralNetwork>();

		for (int childIndex = 0; childIndex < populationSize;) {
			children.add(runRoulette().sex(runRoulette(), mutationRate));
			childIndex++;
		}
		this.updateGeneration(children);
	}
	
	


	/**
	 * updates fitness of every individual, then get max fitness individual.
	 * 
	 * @return Max fitness individual.
	 * @throws Exception 
	 */
	public NeuralNetwork getMaxFitness() throws Exception {
		updateFitness();
		double max_fitness = Double.MIN_VALUE;
		NeuralNetwork max_fitness_individual = null;
		for (NeuralNetwork i : population) {
			if (i.getFitness() > max_fitness) {
				max_fitness = i.getFitness();
				max_fitness_individual = i;
			}
		}
		if (max_fitness_individual.getFitness() == Global.MAX_FITNESS) { // maxFitness is 1.0
			existPerfectIndividual = true;
			perfectIndividual = max_fitness_individual;
		}
		System.out.println("Best fitness is " + max_fitness_individual.getFitness());
		return max_fitness_individual;
	}


	
	// private methods
	/**
	 * replaces the actual population with the new generation of childs.
	 * 
	 * @param newGeneration
	 *            List of child individuals.
	 */
	private void updateGeneration(List<NeuralNetwork> newGeneration) {
		this.population = newGeneration;
	}
	
	private void updateFitness() throws Exception {
		totalFitness = 0;
		for (NeuralNetwork i : population) {
			calculateFitness(i);
			System.out.println("updating fitness of net: " + i.hashid);
			totalFitness += i.getFitness();
		}
	}
	
	/**
	 * calculates the fitness of the individual, saving the result inside the
	 * individual fitness field.
	 * 
	 * @param i
	 *            Individual which fitness will be calculated
	 * @return fitness calculated.
	 * @throws Exception 
	 */
	private void calculateFitness(NeuralNetwork i) throws Exception {
		boolean verbose = true;
		HashMap<String, Double> metricsData = 
				utils.binaryMetrics(i, input, expectedOutput, Global.predictionThreshold, verbose);
		i.setFitness(metricsData.get("f1"));
	}
	
	/**
	 * Pick an individual by roulette.
	 * Fitness should be calculated before.
	 * @return Individual picked.
	 */
	private NeuralNetwork runRoulette() {
	    float runningScore = 0;
	    float rnd = (float) (Math.random() * this.totalFitness);
	    for (NeuralNetwork i : population)
	    {   
	        if (    rnd>=runningScore &&
	                rnd<=runningScore+i.getFitness())
	        {
	            return i;
	        }
	        runningScore+=i.getFitness();
	    }
	    return null;
	}
	
}
