package network;

import java.util.ArrayList;
import java.util.Arrays;

import org.plot.Plot;

import darwin.Evolution;
import darwin.NaturalSelection;
import util.utils;

/**
 * Red neuronal implementada mediante neuronas con funcion de activacion
 * sigmoidea. Las neuronas se inicializan con pesos y bias aleatorios entre 0 y
 * 1.
 * 
 * @author vichoko
 *
 */
public class NeuralNetwork {
	ArrayList<NeuralLayer> layers = new ArrayList<>();
	boolean isComplete = false;
	double fitness;
	public double getFitness() {return fitness;}
	public void setFitness(Double f1) {this.fitness = f1;}

	public double hashid = 0;
	/** CONSTRUCCION */
	/**
	 * Inicia red vacia.
	 */
	public NeuralNetwork() {}

	/**
	 * Inicia red neuronal con la misma cantidad de capas y neuronas por capa que layout.
	 * @param layout modelo de red neuronal que se usara.
	 */
	public NeuralNetwork(NeuralNetwork layout) {
		// Red neuronal desde modelo.
		this();
		try {
			for (int layerIndex = 0; layerIndex < layout.layers.size(); layerIndex++) {
				NeuralLayer modelLayer = layout.layers.get(layerIndex);
				if (layerIndex == 0) {
					// input layer case
					this.newInputLayer(modelLayer.inputSize, modelLayer.neurons.length);

				} else {
					// hidden layer case
					this.newHiddenLayer(modelLayer.neurons.length);
				}
			}
			this.closeNetwork(); // make the last layer, output layer

		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}



	/** make a son net from daddy net and mommy net */
	/**
	 * Inicia red neuronal con valores de pesos y bias heredado de sus padres (50% chance por cada progenitor, por cada gen)
	 * @param daddy
	 * @param mommy
	 */
	public NeuralNetwork(NeuralNetwork daddy, NeuralNetwork mommy) {
		this(daddy); // use daddy as scheme for layers and neuros per layer
		hashid = 0;
		assert (daddy.layers.size() == mommy.layers.size());
		assert (daddy.layers.size() == this.layers.size());
		for (int layerIndex = 0; layerIndex < this.layers.size(); layerIndex++) {
			NeuralLayer dadLayer = daddy.layers.get(layerIndex);
			NeuralLayer momLayer = mommy.layers.get(layerIndex);
			NeuralLayer thisLayer = this.layers.get(layerIndex);

			assert (dadLayer.neurons.length == momLayer.neurons.length);
			assert (dadLayer.neurons.length == thisLayer.neurons.length);
			for (int neuronIndex = 0; neuronIndex < dadLayer.neurons.length; neuronIndex++) {
				Neuron dadNeuron = dadLayer.neurons[neuronIndex];
				Neuron momNeuron = momLayer.neurons[neuronIndex];
				Neuron thisNeuron = thisLayer.neurons[neuronIndex];

				assert (dadNeuron.weights.length == momNeuron.weights.length);
				assert (dadNeuron.weights.length == thisNeuron.weights.length);
				for (int weightIndex = 0; weightIndex < dadNeuron.weights.length; weightIndex++) {
					// weights cross over
					if (Math.random() < 0.5) {
						thisNeuron.weights[weightIndex] = dadNeuron.weights[weightIndex];
					} else {
						thisNeuron.weights[weightIndex] = momNeuron.weights[weightIndex];
					}
					hashid += thisNeuron.weights[weightIndex];
				}
				// bias cross over
				if (Math.random() < 0.5) {
					thisNeuron.bias = dadNeuron.bias;
				} else {
					thisNeuron.bias = momNeuron.bias;
				}
				hashid += thisNeuron.bias;
			}
		}

	}

	

	/**
	 * Crea capa de entrada con pesos y bias aleatorios.
	 * 
	 * @param inputSize
	 *            Cantidad de entradas
	 * @param numberOfNeurons
	 *            Cantidad de neuronas
	 * @throws Exception
	 *             En caso de agregar mas de una capa de entrada
	 */
	public void newInputLayer(int inputSize, int numberOfNeurons) throws Exception {
		if (layers.size() > 0) {
			throw new Exception("Tried adding more than one input layer");
		}
		NeuralLayer newL = new NeuralLayer(numberOfNeurons, inputSize);
		layers.add(newL);
		hashid += newL.hashid;
	}

	/**
	 * Crea capa escondida o de salida  (si se llama closeNetork despues) con pesos y bias aleatorios.
	 * 
	 * @param numberOfNeurons
	 *            Numero de neuronas que tendra la capa
	 * @throws Exception
	 */
	public void newHiddenLayer(int numberOfNeurons) throws Exception {
		if (layers.size() == 0) {
			throw new Exception("Tried adding hidden layer without input layer");
		} else if (isComplete) {
			throw new Exception("Tried adding hidden layer after network is closed (i.e. output layer added)");
		}
		NeuralLayer previousLayer = layers.get(layers.size() - 1);
		NeuralLayer newL = new NeuralLayer(numberOfNeurons, previousLayer.getOutputSize());
		layers.add(newL);
		hashid += newL.hashid;
	}

	/**
	 * Transforma ultima capa oculta/entrada en capa de salida. Cierra la red
	 * neuronal a modificaciones topologicas.
	 * 
	 * @throws Exception
	 *             en caso de cerrar dos veces o si se llama antes de crear capa
	 *             de entrada.
	 */
	public void closeNetwork() throws Exception {
		/**
		 * Transforma la ultima capa en Output Layer y cierra red a
		 * modificaicones
		 */
		if (layers.size() == 0) {
			throw new Exception("Tried adding output layer without input layer");
		} else if (isComplete) {
			throw new Exception("Tried adding more than one output layer, network already closed");
		}
		isComplete = true;
	}

	/** METODOS PUBLICOS */
	/**
	 * Entrena la red neuronal con el conjunto de entrenamiento entregado en
	 * input y expectedOutput. La cantidad de elementos de input y
	 * expectedOutput deben coincidir.
	 * 
	 * @param input
	 *            Entradas a la red neuronal, debe coincidir su cantidad con
	 *            numero de entradas de la capa de entrada.
	 * @param expectedOutput
	 *            Salidas esperadas de la red neuronal. Su cantidad debe
	 *            coincidir con la cantidad de neuronas de la capa de salida.
	 * @param nGen
	 *            Cantidad de veces que se entrenara con el data set entregado.
	 * @param errorPlotName
	 *            if not null, error is plotted in file with this name.
	 * @throws Exception
	 *             En caso de detectar inconsistencias entre input y
	 *             expectedOutput.
	 */
	public void train(double[][] input, double[][] expectedOutput, int nGen, String errorPlotName) throws Exception {
		// recibe dataset de entrenamiento; varios input con sus respectivos
		// output
		System.out.println("train");

		nGen = 3000;
		if (input.length != expectedOutput.length) {
			throw new Exception("train :: dataset input and expectedOutput arrays have different lenghts.");

		}
		double[] fitnesses = new double[nGen];
		// Siguientes variables son para no saturar de impresiones en consola.
		int fraction = nGen / 10;
		int counter = 0;
		int counter2 = 0;


		Evolution evo = new Evolution(this);
		NaturalSelection ns = new NaturalSelection(evo.getPopulation(), input, expectedOutput);

		for (int genIndex = 0; genIndex < nGen; genIndex++) {
			System.out.println("gen" + genIndex);
			fitnesses[genIndex] = ns.getMaxFitness().getFitness();
			ns.matingPhaseByRoulette(evo.getMutationRate());
			evo.incrGenCounter();

			// debug
			if (counter2 == 0 || counter++ >= fraction || nGen < 100) {
				System.out
						.println("Elapsed: " + (counter2++ * 10) + "%, Generation: " + genIndex + ", Fitness: " + fitnesses[genIndex]);
				counter = 0;
			}
		}

		// do plot to outfile
		if (errorPlotName != null) {
			double[] x = new double[fitnesses.length];
			for (int i = 0; i < x.length; i++)
				x[i] = i + 1;
			Plot plot = Plot.plot(Plot.plotOpts().title("Fitness vs Generation")).xAxis("Epochs", null).yAxis("Fitness", null)
					.series(null, Plot.data().xy(x, fitnesses), null);
			plot.save(errorPlotName, "png");
			System.out.println("train :: 'Plot' de errores cuadraticos guardado en ../" + errorPlotName + ".png");
		}
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
	public NeuralNetwork sex(NeuralNetwork peer, double mutationRate) {
		NeuralNetwork fetus = this.crossover(peer);
		fetus.mutate(mutationRate);
		return fetus;
	}


	/**
	 * cross-over genes of the father/mother with equal probabilities per gene.
	 * 
	 * @param mommy
	 *            peer gene container.
	 * @return son genes after cross over between father (this) and mother
	 *         (param).
	 */
	public NeuralNetwork crossover(NeuralNetwork mommy) {
		NeuralNetwork daddy = this;
		NeuralNetwork sonGenes = new NeuralNetwork(daddy, mommy);
		return sonGenes;
	}

	public void mutate(double mutationRate) {
		for (NeuralLayer layer : this.layers) {
			layer.mutate(mutationRate);
		}
	}

	/**
	 * Obtener prediccion de la red neuronal, dado una entrada particular.
	 * 
	 * @param input
	 *            Entrada que se desea hacer una prediccion.
	 * @return Salida predecida por la red neuronal. Valores entre 0 y 1. Su
	 *         dimension coincide con la capa de salida.
	 * @throws Exception
	 */
	public double[] predict(double[] input) throws Exception {
		return forwardFeed(input);
	}

	/**
	 * Obtener prediccion de la red neuronal, obteniendo valores 0 o 1. Al pasar
	 * la prediccion real por umbral explicitado.
	 *
	 * @param input
	 *            Entrada que se desea hacer una prediccion.
	 * @param threshold
	 *            Umbral desde el cual se considerara la clase como 1. De lo
	 *            contrario 0.
	 * @return Salida predecida, obteniendo valores 0 o 1.
	 * @throws Exception
	 */
	public int[] binaryPredict(double[] input, double threshold) throws Exception {
		int[] res = new int[forwardFeed(input).length];
		int index = 0;
		for (double i : forwardFeed(input)) {
			res[index++] = i > threshold ? 1 : 0;
		}
		return res;
	}

	/** METODOS (PRIVADOS) DE APRENDIZAJE MEDIANTE BACK PROPAGATION */

	double[] forwardFeed(double[] food) throws Exception {
		for (NeuralLayer layer : layers) {
			food = layer.synapsis(food);
		}
		return food;
	}


}
