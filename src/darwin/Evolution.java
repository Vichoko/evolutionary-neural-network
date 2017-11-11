package darwin;

import java.util.ArrayList;

import network.NeuralNetwork;

/**
 * 
 * @author Vicente Oyanedel M.
 *
 */
public class Evolution {
																	/**
	 * Instance variables.
	 */
	int genCounter;
	public void incrGenCounter(){
		genCounter++;
	}
	ArrayList<NeuralNetwork> population;

	/**
	 * @return the mutationRate
	 */
	public static double getMutationRate() {
		return Global.mutationRate;
	}

	/**
	 * @return the population
	 */
	public ArrayList<NeuralNetwork> getPopulation() {
		return population;
	}

	/**
	 * Instantiate new evolution system.
	 */
	public Evolution(NeuralNetwork model) {
		genCounter = 0;
		initPopulation(Global.populationSize, model);
	}

	/**
	 * Generate initial population with random genetics based in model scheme.
	 * 
	 * @param poblation
	 *            size of population
	 */
	private void initPopulation(int pupulationSize, NeuralNetwork model) {
		population = new ArrayList<NeuralNetwork>();
		for (int i = 0; i < Global.populationSize; i++) {
			NeuralNetwork randomNN = new NeuralNetwork(model);
			population.add(randomNN);
		}
	}

	/**
	 * Executes the evolution algorith to learn the 'targetWord' string.
	 * 
	 * @param args
	 *            Nothing
	 */
	/*
	public static void main(String[] args) {
		Evolution evo = new Evolution();
		NaturalSelection ns = new NaturalSelection(evo.population, targetWord);
		ns.getMaxFitness();

		while (!ns.existPerfectIndividual) {
			// compute new generation
			ns.matingPhaseByRoulette(mutationRate); // sex time
			ns.getMaxFitness(); // calculate fitness of every new individual and get max one
			evo.genCounter++;
		}
		Individual perfectOne = ns.perfectIndividual;
		System.out.println("Secret word is: " + perfectOne.getGenesStr());
		System.out.println("Found secret word my evolving " + evo.genCounter + " generations.");
	}
*/
}
