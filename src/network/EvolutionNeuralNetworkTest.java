package network;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import darwin.Evolution;
import darwin.Global;
import darwin.NaturalSelection;

public class EvolutionNeuralNetworkTest {
	NeuralNetwork dad;
	NeuralNetwork mom;
	NeuralNetwork son;
	int momGenesCounter;
	int dadGenesCounter;
	double sonPreviousHashid;

	@Before
	public void setUp() throws Exception {
		dad = new NeuralNetwork();
		dad.newInputLayer(2, 1);
		dad.newHiddenLayer(1);
		dad.closeNetwork();

		mom = new NeuralNetwork(dad);
		son = new NeuralNetwork(dad, mom);

		momGenesCounter = 0;
		dadGenesCounter = 0;
	}

	@Test
	public void layoutNetCreationTest() throws Exception {
		assertFalse(dad.hashid == mom.hashid);
		// has same scheme test
		assertTrue(mom.layers.size() == dad.layers.size());
		for (int layerIndex = 0; layerIndex < mom.layers.size(); layerIndex++) {
			assertTrue(mom.layers.get(layerIndex).neurons.length == dad.layers.get(layerIndex).neurons.length);
			for (int neuronIndex = 0; neuronIndex < mom.layers.get(layerIndex).neurons.length; neuronIndex++) {
				assertTrue(mom.layers.get(layerIndex).neurons[neuronIndex].weights.length == dad.layers
						.get(layerIndex).neurons[neuronIndex].weights.length);
				assertFalse(mom.layers.get(layerIndex).neurons[neuronIndex].weights[0] == dad.layers
						.get(layerIndex).neurons[neuronIndex].weights[0]);
			}
		}
	}

	@Test
	public void crossOverTest() {
		assertFalse(son.hashid == mom.hashid);
		assertFalse(dad.hashid == son.hashid);
		sonPreviousHashid = son.hashid;

		assertTrue(son.layers.size() == dad.layers.size());
		for (int layerIndex = 0; layerIndex < mom.layers.size(); layerIndex++) {
			assertTrue(son.layers.get(layerIndex).neurons.length == dad.layers.get(layerIndex).neurons.length);
			for (int neuronIndex = 0; neuronIndex < mom.layers.get(layerIndex).neurons.length; neuronIndex++) {
				Neuron dadN = dad.layers.get(layerIndex).neurons[neuronIndex];
				Neuron momN = mom.layers.get(layerIndex).neurons[neuronIndex];
				Neuron sonN = son.layers.get(layerIndex).neurons[neuronIndex];

				if (sonN.bias == dadN.bias) {
					dadGenesCounter++;
				} else if (sonN.bias == momN.bias) {
					momGenesCounter++;
				} else {
					assertTrue(false); // should error
				}

				assertTrue(sonN.weights.length == dadN.weights.length);
				for (int wIndex = 0; wIndex < sonN.weights.length; wIndex++) {
					if (sonN.weights[wIndex] == dadN.weights[wIndex]) {
						dadGenesCounter++;
					} else if (sonN.weights[wIndex] == momN.weights[wIndex]) {
						momGenesCounter++;
					} else {
						assertTrue(false); // should error
					}
				}
			}
		}
		assertTrue(dadGenesCounter > 0);
		assertTrue(momGenesCounter > 0);
	}
	
	@Test
	public void mutationTest(){
		int newDadGenesCounter = 0;
		int newMomGenesCounter = 0;
		int mutationCounter = 0;
		
		son.mutate(0.5);
		assertFalse(son.hashid == sonPreviousHashid);

		assertTrue(son.layers.size() == dad.layers.size());
		for (int layerIndex = 0; layerIndex < mom.layers.size(); layerIndex++) {
			assertTrue(son.layers.get(layerIndex).neurons.length == dad.layers.get(layerIndex).neurons.length);
			for (int neuronIndex = 0; neuronIndex < mom.layers.get(layerIndex).neurons.length; neuronIndex++) {
				Neuron dadN = dad.layers.get(layerIndex).neurons[neuronIndex];
				Neuron momN = mom.layers.get(layerIndex).neurons[neuronIndex];
				Neuron sonN = son.layers.get(layerIndex).neurons[neuronIndex];

				if (sonN.bias == dadN.bias) {
					newDadGenesCounter++;
				} else if (sonN.bias == momN.bias) {
					newMomGenesCounter++;
				} else {
					mutationCounter++;
				}

				assertTrue(sonN.weights.length == dadN.weights.length);
				for (int wIndex = 0; wIndex < sonN.weights.length; wIndex++) {
					if (sonN.weights[wIndex] == dadN.weights[wIndex]) {
						newDadGenesCounter++;
					} else if (sonN.weights[wIndex] == momN.weights[wIndex]) {
						newMomGenesCounter++;
					} else {
						mutationCounter++;
					}
				}
			}
		}
		
		assertTrue(newDadGenesCounter != dadGenesCounter);
		assertTrue(newMomGenesCounter != momGenesCounter);
		assertTrue(mutationCounter > 0);
	}
	
	@Test
	public void mutation2Test(){
		int newDadGenesCounter = 0;
		int newMomGenesCounter = 0;
		int mutationCounter = 0;
		
		son = dad.sex(mom, 0.5);
		assertFalse(son.hashid == sonPreviousHashid);

		assertTrue(son.layers.size() == dad.layers.size());
		for (int layerIndex = 0; layerIndex < mom.layers.size(); layerIndex++) {
			assertTrue(son.layers.get(layerIndex).neurons.length == dad.layers.get(layerIndex).neurons.length);
			for (int neuronIndex = 0; neuronIndex < mom.layers.get(layerIndex).neurons.length; neuronIndex++) {
				Neuron dadN = dad.layers.get(layerIndex).neurons[neuronIndex];
				Neuron momN = mom.layers.get(layerIndex).neurons[neuronIndex];
				Neuron sonN = son.layers.get(layerIndex).neurons[neuronIndex];

				if (sonN.bias == dadN.bias) {
					newDadGenesCounter++;
				} else if (sonN.bias == momN.bias) {
					newMomGenesCounter++;
				} else {
					mutationCounter++;
				}

				assertTrue(sonN.weights.length == dadN.weights.length);
				for (int wIndex = 0; wIndex < sonN.weights.length; wIndex++) {
					if (sonN.weights[wIndex] == dadN.weights[wIndex]) {
						newDadGenesCounter++;
					} else if (sonN.weights[wIndex] == momN.weights[wIndex]) {
						newMomGenesCounter++;
					} else {
						mutationCounter++;
					}
				}
			}
		}

		assertTrue(mutationCounter > 0);
		assertTrue(newDadGenesCounter > 0);
		assertTrue(newMomGenesCounter > 0);
	}
	
	@Test
	public void evolutionTest() throws Exception{
		Evolution evo = new Evolution(dad); // Inherit dad scheme: layer count and neuron count per layer
		// genera poblacion alteatoria
		ArrayList<NeuralNetwork> firstPopulation = evo.getPopulation();
		
		// check every individual is different
		for (int nIndex = 0; nIndex < firstPopulation.size(); nIndex++){
			for (int nIndex2 = nIndex + 1; nIndex2 < firstPopulation.size(); nIndex2++){
				assertFalse(firstPopulation.get(nIndex).hashid == firstPopulation.get(nIndex2).hashid);
			}
		}
		
		NaturalSelection ns = new NaturalSelection(
				evo.getPopulation(), 
				new double[][]{{0,0},{1,0},{0,1},{1,1}}, 
				new double[][]{{0},{1},{1},{1}}); // OR Input/Output
		
		ns.getMaxFitness();
		NeuralNetwork firstBestIndividual = ns.getMaxFitness();
		
		
		ns.matingPhaseByRoulette(Global.mutationRate);
		ArrayList<NeuralNetwork> secondPopulation = evo.getPopulation();
		
		// check that secondPopulation is different that firstPopulation at least from a gene
		for (int nIndex = 0; nIndex < firstPopulation.size(); nIndex++){
			for (int nIndex2 = nIndex + 1; nIndex2 < secondPopulation.size(); nIndex2++){
				assertFalse(firstPopulation.get(nIndex).hashid == secondPopulation.get(nIndex2).hashid);

			}
		}
		ns.getMaxFitness();
		NeuralNetwork secondBestIndividual = ns.getMaxFitness();
		assertTrue(firstBestIndividual.fitness < secondBestIndividual.fitness);
		
		/** creo que el error esta en como se esta decidiendo el fitness; 
		 * probar definir el fitness como lo cerca que esta el output del valor esperado **/
		
	}		
}
