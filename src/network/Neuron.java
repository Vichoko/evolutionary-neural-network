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

	
	public Neuron(double bias, double[] weights){
		this.bias = bias;
		this.weights = weights;
	}
	

	/**
	 * Pondera las entradas mediante la funcion de activación y se ajusta con 'bias'.
	 * @param inputs
	 * @return
	 * @throws Exception
	 */
	public double synapsis(double[] inputs) throws Exception{
		double lastOutput = utils.sigmoid(utils.dotProduct(this.weights, inputs) + bias); 
		return lastOutput;
	}


	public void mutate(double mutationRate) {
		for (int weightIndex = 0; weightIndex < this.weights.length; weightIndex++) {
			if (Math.random() < mutationRate) {
				if (Math.random() > 0.5){
					this.weights[weightIndex] += this.weights[weightIndex]*0.25;
				} else {
					this.weights[weightIndex] -= this.weights[weightIndex]*0.25;
				}
			}
		}
		if (Math.random() < mutationRate) {
			this.bias = Math.random();
		}
	}

}
