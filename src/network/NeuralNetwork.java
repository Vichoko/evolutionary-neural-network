package network;

import java.util.ArrayList;
import java.util.Arrays;

import org.plot.Plot;

import darwin.Individual;
import util.utils;
/**
 * Red neuronal implementada mediante neuronas con funcion de activacion sigmoidea.
 * Las neuronas se inicializan con pesos y bias aleatorios entre 0 y 1.
 * 
 * @author vichoko
 *
 */
public class NeuralNetwork {
	ArrayList<NeuralLayer> layers = new ArrayList<>();
	boolean isComplete = false;
	/** CONSTRUCCION */
	/**
	 * Inicia red vacia.
	 */
	public NeuralNetwork() {	
		// Red vacia, learning rate	fijo
	}
	
	public NeuralNetwork(NeuralNetwork copy) {	
		// Red neuronal desde copia
		this();
		this.layers = copy.layers; // TODO: Hay que reconstruir las referencias
	}

	/** make a son net from daddy net and mommy net */
	public NeuralNetwork(NeuralNetwork daddy, NeuralNetwork mommy) {
		this(daddy); // son uses daddy's references. AS dad will be no longer necesary
		
		assert(daddy.layers.size() == mommy.layers.size());
		assert(daddy.layers.size() == this.layers.size());
		for (int layerIndex = 0; layerIndex < this.layers.size(); layerIndex++) {
			NeuralLayer dadLayer = daddy.layers.get(layerIndex);
			NeuralLayer momLayer = mommy.layers.get(layerIndex);
			NeuralLayer thisLayer = this.layers.get(layerIndex);
			
			assert(dadLayer.neurons.length == momLayer.neurons.length);
			assert(dadLayer.neurons.length == thisLayer.neurons.length);
			for (int neuronIndex = 0; neuronIndex < dadLayer.neurons.length; neuronIndex++) {
				Neuron dadNeuron = dadLayer.neurons[neuronIndex];
				Neuron momNeuron = momLayer.neurons[neuronIndex];
				Neuron thisNeuron = thisLayer.neurons[neuronIndex];
				
				assert(dadNeuron.weights.length == momNeuron.weights.length);
				assert(dadNeuron.weights.length == thisNeuron.weights.length);
				for (int weightIndex = 0; weightIndex < dadNeuron.weights.length; weightIndex++) {
					// weights cross over
					if (Math.random() < 0.5) {
						thisNeuron.weights[weightIndex] = dadNeuron.weights[weightIndex];
					} else {
						thisNeuron.weights[weightIndex] = momNeuron.weights[weightIndex];
					}
				}
				// bias cross over
				if (Math.random() < 0.5) {
					thisNeuron.bias = dadNeuron.bias;
				} else {
					thisNeuron.bias = momNeuron.bias;
				}
			}
		}
		
	}

	public NeuralNetwork(int inputSize, int inputLayerSize, int[] hiddenLayersSizes, int outputLayerSize) {
		this();
		try {
			this.newInputLayer(inputSize, inputLayerSize);
			for (int numberOfNeurons : hiddenLayersSizes) {
				this.newHiddenLayer(numberOfNeurons);
			}
			this.newHiddenLayer(outputLayerSize);
			this.closeNetwork();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/** Crea capa de entrada
	 * @param inputSize Cantidad de entradas
	 * @param numberOfNeurons Cantidad de neuronas
	 * @throws Exception En caso de agregar mas de una capa de entrada
	 */
	public void newInputLayer(int inputSize, int numberOfNeurons) throws Exception {
		if (layers.size() > 0) {
			throw new Exception("Tried adding more than one input layer");
		}
		layers.add(new NeuralLayer(numberOfNeurons, inputSize));
	}
	/**
	 * Crea capa escondida o de salida (si se llama closeNetork despues)
	 * @param numberOfNeurons Numero de neuronas que tendra la capa
	 * @throws Exception
	 */
	public void newHiddenLayer(int numberOfNeurons) throws Exception {
		if (layers.size() == 0) {
			throw new Exception("Tried adding hidden layer without input layer");
		} else if (isComplete) {
			throw new Exception("Tried adding hidden layer after network is closed (i.e. output layer added)");
		}
		NeuralLayer previousLayer = layers.get(layers.size()-1);
		layers.add(new NeuralLayer(numberOfNeurons, previousLayer.getOutputSize()));
	}
	/**
	 * Transforma ultima capa oculta/entrada en capa de salida.
	 * Cierra la red neuronal a modificaciones topologicas.
	 * @throws Exception en caso de cerrar dos veces o si se llama antes de crear capa de entrada.
	 */
	public void closeNetwork() throws Exception {
		/** Transforma la ultima capa en Output Layer y cierra red a modificaicones*/
		if (layers.size() == 0) {
			throw new Exception("Tried adding output layer without input layer");
		} else if (isComplete) {
			throw new Exception("Tried adding more than one output layer, network already closed");
		}
		isComplete = true;
	}
	/** METODOS PUBLICOS */
	/**
	 * Entrena la red neuronal con el conjunto de entrenamiento entregado en input y expectedOutput.
	 * La cantidad de elementos de input y expectedOutput deben coincidir.
	 *  
	 * @param input Entradas a la red neuronal, debe coincidir su cantidad con numero de entradas de la capa de entrada.
	 * @param expectedOutput Salidas esperadas de la red neuronal. Su cantidad debe coincidir con la cantidad de neuronas de la capa de salida.
	 * @param nGen Cantidad de veces que se entrenara con el data set entregado.
	 * @param errorPlotName if not null, error is plotted in file with this name.
	 * @throws Exception En caso de detectar inconsistencias entre input y expectedOutput.
	 */
	public void train(double[][] input, double[][] expectedOutput, int nGen, String errorPlotName) throws Exception {
		// recibe dataset de entrenamiento; varios input con sus respectivos output
		if (input.length != expectedOutput.length) {
			throw new Exception("train :: dataset input and expectedOutput arrays have different lenghts.");
			
		}
		double[] errors = new double[nGen];
		// Siguientes variables son para no saturar de impresiones en consola.
		int fraction = nGen/10;
		int counter = 0;
		int counter2 = 0;
		// TODO: Generar poblacion inicial
		for (int genIndex = 0; genIndex < nGen; genIndex++) {
			double f1 = 0;
			// TODO: Aparear poblacion (Cross-over; elegir por ruleta)
			// TODO: Obtener genes con maximo fitness (max f1) para imprimir avance
			/**
			 * for (int dataIndex = 0; dataIndex < input.length; dataIndex++) {
			 
				// entrenar sobre cada par de vectores input/output.
				double[] realOutput = this.forwardFeed(input[dataIndex]);
				if (realOutput.length != expectedOutput[dataIndex].length) {
					throw new Exception("train :: one of layers realOutput/expectedOutput have different sizes.");
					
				}

				for (int outputIndex = 0; outputIndex < realOutput.length; outputIndex++) {
					// Para cada input se calcula el error cuadratico medio para visualizar aprendizaje
					sumError += Math.pow((expectedOutput[dataIndex][outputIndex]-realOutput[outputIndex]), 2);
				}
				this.backPropagation(expectedOutput[dataIndex]);
				this.updateWeights(input[dataIndex]);		
			}*/
			
			errors[genIndex] = f1;
			// debug
			if (counter2 == 0 || counter++ >= fraction || nGen < 100) {
				System.out.println("Elapsed: " + (counter2++*10) + "%, Generation: "+genIndex+", F1 Score: "+f1);
				counter = 0;
			}
		}
		
		// do plot to outfile
		if (errorPlotName != null) {
			double[] x = new double[errors.length];
			for (int i = 0; i < x.length; i++)
				x[i] = i+1;
			Plot plot = Plot.plot(Plot.plotOpts().title("Error vs Epochs")).
					xAxis("Epochs", null).
					yAxis("Error", null).
					series(null, Plot.data().xy(x, errors), null);
			plot.save(errorPlotName, "png");
			System.out.println("train :: 'Plot' de errores cuadraticos guardado en ../" + errorPlotName + ".png");
		}
		
		}
	/*public void train(double[][] input, double[][] expectedOutput, int nEpochs, String errorPlotName) throws Exception {
		// recibe dataset de entrenamiento; varios input con sus respectivos output
		if (input.length != expectedOutput.length) {
			throw new Exception("train :: dataset input and expectedOutput arrays have different lenghts.");
			
		}
		System.out.println("learnRate: " + learningRate);
		double[] errors = new double[nEpochs];
		// Siguientes variables son para no saturar de impresiones en consola.
		int epochsPart = nEpochs/10;
		int counter = 0;
		int counter2 = 0;
		for (int epochIndex = 0; epochIndex < nEpochs; epochIndex++) {
			double sumError = 0;
			for (int dataIndex = 0; dataIndex < input.length; dataIndex++) {
				// entrenar sobre cada par de vectores input/output.
				double[] realOutput = this.forwardFeed(input[dataIndex]);
				if (realOutput.length != expectedOutput[dataIndex].length) {
					throw new Exception("train :: one of layers realOutput/expectedOutput have different sizes.");
					
				}

				for (int outputIndex = 0; outputIndex < realOutput.length; outputIndex++) {
					// Para cada input se calcula el error cuadratico medio para visualizar aprendizaje
					sumError += Math.pow((expectedOutput[dataIndex][outputIndex]-realOutput[outputIndex]), 2);
				}
				this.backPropagation(expectedOutput[dataIndex]);
				this.updateWeights(input[dataIndex]);		
			}
			errors[epochIndex] = sumError;
			// debug
			if (counter2 == 0 || counter++ >= epochsPart || nEpochs < 100) {
				System.out.println("Elapsed: " + (counter2++*10) + "%, Epoch: "+epochIndex+", error: "+sumError);
				counter = 0;
			}
			if (epochIndex > 3 && false) {
				if (errors[epochIndex - 1] == errors[epochIndex - 2] && errors[epochIndex - 1] == errors[epochIndex]) {
					// si error no cambia en 3 ultimas epocas, probablemente no cambie mas.
					System.out.println("train :: finishing train because of no-change in error.");
					break;
				}
			}
		}
		
		// do plot to outfile
		if (errorPlotName != null) {
			double[] x = new double[errors.length];
			for (int i = 0; i < x.length; i++)
				x[i] = i+1;
			Plot plot = Plot.plot(Plot.plotOpts().title("Error vs Epochs")).
					xAxis("Epochs", null).
					yAxis("Error", null).
					series(null, Plot.data().xy(x, errors), null);
			plot.save(errorPlotName, "png");
			System.out.println("train :: 'Plot' de errores cuadraticos guardado en ../" + errorPlotName + ".png");
		}
		
		}
		*/
	
	public void mutate(double mutationRate) {
		for (NeuralLayer layer : this.layers) {
			layer.mutate(mutationRate);
		}
	}
	/**
	 * Obtener prediccion de la red neuronal, dado una entrada particular.
	 * @param input Entrada que se desea hacer una prediccion.
	 * @return Salida predecida por la red neuronal. Valores entre 0 y 1. Su dimension coincide con la capa de salida.
	 * @throws Exception
	 */
	public double[] predict(double[] input) throws Exception {
		return forwardFeed(input);
	}
	
	/**
	 * Obtener prediccion de la red neuronal, obteniendo valores 0 o 1. Al pasar la prediccion real por umbral explicitado.
	 *
	 * @param input Entrada que se desea hacer una prediccion.
	 * @param threshold Umbral desde el cual se considerara la clase como 1. De lo contrario 0.
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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NeuralNetwork other = (NeuralNetwork) obj;
		if (isComplete != other.isComplete)
			return false;
		if (layers == null) {
			if (other.layers != null)
				return false;
		} else if (!layers.equals(other.layers))
			return false;
		return true;
	}

}
