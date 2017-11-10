package network;

import java.util.Arrays;
import java.util.Random;

import util.utils;
/**
 * Neurona con funcion de activacion sigmoidea.
 * @author vichoko
 *
 */
public class Neuron {
	public double getBias() {
		return bias;
	}

	public double[] getWeights() {
		return this.weights;
	}

	
	double bias;
	double[] weights;
	double threshold = 0.5;
	double lastOutput;
	double delta;
	
	public Neuron(double bias, double[] weights){
		this.bias = bias;
		this.weights = weights;
	}
	
	public Neuron(double bias, double[] weights, double threshold){
		this(bias, weights);
		this.threshold = threshold;
	}

	
	/**
	 * Pondera las entradas mediante la funcion de activación y se ajusta con 'bias'.
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public double synapsis(double[] inputs) throws Exception{
		lastOutput = utils.sigmoid(utils.dotProduct(this.weights, inputs) + bias); 
		return lastOutput;
	}
	
/** CLASIFICADOR MONO NEURONA */
	public int binarySynapsis(double[] inputs) throws Exception{
		double val = synapsis(inputs);
		if (val > threshold)
			return 1;
		return 0;
	}
	
	
	enum E_feedType {POSITIVE, NEGATIVE};
	/**
	 * Ajusta los pesos en un contexto de clasificador mono neurona.
	 * @param learnRate
	 * @param inputs
	 * @param type
	 * @throws Exception
	 */
	@Deprecated
	void adjustWeights(double learnRate, double[] inputs, E_feedType type) throws Exception {
		if (this.weights.length != inputs.length) {
			throw new Exception("weight and inputs vectors have different lengths.");
		}
		
		if (type == E_feedType.POSITIVE) {
			for (int i = 0; i < this.weights.length; i++) {
				this.weights[i] += learnRate*inputs[i];
			}	
		} else {
			for (int i = 0; i < this.weights.length; i++) {
				this.weights[i] -= learnRate*inputs[i];
			}
		}
	}
	/** 
	 * Utilizado al instanciar una sola neurona y utilizarla como clasificador.
	 * @param learnRate
	 * @param inputs
	 * @param desiredOutput
	 * @throws Exception
	 */
	@Deprecated
	public void singleTrain(double learnRate, double[] inputs, int desiredOutput) throws Exception {
		if (desiredOutput<0 || desiredOutput>1) {
			throw new Exception("Output must be binary");
		}
		int output = this.binarySynapsis(inputs);
		if (output != desiredOutput) {
			if (desiredOutput == 0) {
				// Disminuir pesos
				adjustWeights(learnRate, inputs, E_feedType.NEGATIVE);
				} else {
				// Aumentar pesos
				adjustWeights(learnRate, inputs, E_feedType.POSITIVE);
			}
		}
		
		
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Neuron other = (Neuron) obj;
		if (Double.doubleToLongBits(bias) != Double.doubleToLongBits(other.bias))
			return false;
		if (!Arrays.equals(weights, other.weights))
			return false;
		return true;
	}

	public void mutate(double mutationRate) {
		for (int weightIndex = 0; weightIndex < this.weights.length; weightIndex++) {
			if (Math.random() < mutationRate) {
				this.weights[weightIndex] = Math.random();
			}
		}
		if (Math.random() < mutationRate) {
			this.bias = Math.random();
		}
	}

}
