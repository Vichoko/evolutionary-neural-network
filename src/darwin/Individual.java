package darwin;

import java.util.Arrays;
import network.NeuralNetwork;
/**
 * 
 * @author Vicente Oyanedel M.
 *
 */
public class Individual {
	public NeuralNetwork genes; // in this case a set of weigths and biases (all net)
	int fitness;

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	/**
	 * Instance by existing array of genes
	 * 
	 * @param genes
	 */
	public Individual(NeuralNetwork genes) {
		this.genes = genes;
	}

	/**
	 * Instance by random array of genes
	 * 
	 * @param genesNumber
	 *            Number of genes to be generated
	 */
	// TODO: Iniciar red neuronal vacía
	public Individual(int inputSize, int inputLayerSize, int[] hiddenLayersSizes, int outputLayerSize) {
		this.genes = new NeuralNetwork(inputSize, inputLayerSize, hiddenLayersSizes, int outputLayerSize);
	}

	/**
	 * Pairs two individuals creating a Child. First it does a cross-over of the
	 * genes, then generate random mutations and finally returning the son.
	 * 
	 * @param peer
	 *            individual which will be peared with the actual one.
	 * @param mutationRate
	 *            probability of generate mutation in one gene.
	 * @return son.
	 */
	public Individual sex(Individual peer, double mutationRate) {
		NeuralNetwork fetus = this.crossover(peer);
		fetus.mutate(mutationRate);
		Individual son = new Individual(fetus);
		return son;
	}


	/**
	 * cross-over genes of the father/mother with equal probabilities per gene.
	 * 
	 * @param mommy
	 *            peer gene container.
	 * @return son genes after cross over between father (this) and mother
	 *         (param).
	 */
	public NeuralNetwork crossover(Individual mommy) {
		Individual daddy = this;
		NeuralNetwork sonGenes = new NeuralNetwork(daddy.genes, mommy.genes);
		return sonGenes;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Individual other = (Individual) obj;
		if (fitness != other.fitness)
			return false;
		if (genes == null) {
			if (other.genes != null)
				return false;
		} else if (!genes.equals(other.genes))
			return false;
		return true;
	}

	


}
