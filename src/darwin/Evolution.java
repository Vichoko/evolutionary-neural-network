package darwin;

import java.util.ArrayList;

/**
 * 
 * @author Vicente Oyanedel M.
 *
 */
public class Evolution {
																	// gene
																	// perfection
	static int populationSize = 1000;
	static double mutationRate = 0.02;

	/**
	 * Instance variables.
	 */
	int genCounter;
	ArrayList<Individual> population;

	/**
	 * Instantiate new evolution system.
	 */
	public Evolution() {
		genCounter = 0;
		initPopulation(populationSize);
	}

	/**
	 * Generate initial population with random genetics.
	 * 
	 * @param poblation
	 *            size of population
	 */
	private void initPopulation(int poblation) {
		population = new ArrayList<Individual>();
		for (int i = 0; i < poblation; i++) {
			population.add(new Individual(targetWord.length()));
		}
	}

	/**
	 * Executes the evolution algorith to learn the 'targetWord' string.
	 * 
	 * @param args
	 *            Nothing
	 */
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

}
