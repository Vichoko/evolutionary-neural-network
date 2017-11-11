package network;

import java.util.Arrays;

public class NeuralLayer {
	Neuron[] neurons;
	int inputSize;
	double hashid = 0;
	
	
	/** CONSTRUCTORES*/
	NeuralLayer(int n, double[] weights, double bias) {
		inputSize = weights.length;
		neurons = new Neuron[n];// explicit declaration
		for (int i = 0; i < n; i++) {
			neurons[i] = new Neuron(bias, weights);
		}
	}
	/**
	 * Constructor de capa que inicializa los pesos y bias aleatoriamente entre 0 y 1.
	 * 
	 * @param neuronQuantity Cantidad de neuronas de la capa
	 * @param inputSize Cantidad de input que recibira cada neurona i.e. la capa en su totalidad.
	 */
	NeuralLayer(int neuronQuantity, int inputSize) {
		neurons = new Neuron[neuronQuantity];
		this.inputSize = inputSize;
		// todas las neuronas de una layer tienen la misma cantidad de pesos
		// bias recomendado entre 0 y 1. Pesos recomendados entre 0 y 1.
		for (int i = 0; i < neuronQuantity; i++) {
			double[] weights = new double[inputSize];
			for (int j = 0; j < inputSize; j++) {
				weights[j] = Math.random();
				hashid += weights[j];
			}
			double bias = Math.random();
			neurons[i] = new Neuron(bias, weights);
			hashid += bias;
		}
	}

	
	/** METODOS */
	/**
	 * Obtiene tamano de la salida.
	 * @return
	 */
	int getOutputSize() {
		return neurons.length;
	}
	/**
	 * Pasa una serie de input por la capa y obtiene su salida.
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	double[] synapsis(double[] inputs) throws Exception {
		// must have same size as weights
		if (inputs.length != neurons[0].getWeights().length) {
			throw new Exception("inputs and weights have different sizes.");
		}
		
		double[] pastOutputs = new double[neurons.length];
		for (int i = 0; i < pastOutputs.length; i++) {
			pastOutputs[i] = neurons[i].synapsis(inputs);
		}
		return pastOutputs;
		
	}

	
	public void mutate(double mutationRate) {
		for (Neuron n : this.neurons) {
			n.mutate(mutationRate);
		}
		
	}

}
