# evolutionary-neural-network

A neural network with an evolutionary training.
.
# Requerimientos
* JUnit 4
* JavaSE-1.7


# Motivación

Modificar (red neuronal implementada)[https://github.com/Vichoko/neural-network], incorporando aprendizaje mediante un *algoritmo genetico*. 
Utilizando un esquema parecido al utilizado en el proyecto del ("Adivinador de palabras evolutivo")[https://github.com/Vichoko/evolutionary-guesser-of-words], implementado en el pasado.	

El objetivo es comparar el desempeño con el entrenamiento evolutivo versus el entrenamiento por *Back Propagation", implementado en la Tarea 1.
# Uso
## Configurar algoritmo evolutivo

Se hace desde el archivo darwin.Global.java.
Permite variar los parametros:

	public static int populationSize = 500; // Tamaño de la población
	public static double mutationRate = 0.05; // Probabilidad de mutación por gen (peso o bias)
	public static double survivorsRate = 0.15; // Fracción de individuos que se puede reproducir (sin mecanismo de ruleta)
	public static double predictionThreshold = 0.5; // al usar una salida singular, se necesita umbral para clasificar
	
## Crear red
### Constructor

```Java

	public NeuralNetwork(double learningRate) {...}
	public NeuralNetwork() {...} // learning rate de 0.1
```

### Crear capas

```Java

	public void newInputLayer(int inputSize, int numberOfNeurons) throws Exception {...} // Crear capa de entrada
	public void newHiddenLayer(int numberOfNeurons) throws Exception {...} // crear capa oculta o de salida
	public void closeNetwork() throws Exception {...} // se transforma ultima capa en capa de salida
```

### Entrenar

```Java

	public void train(double[][] input, double[][] expectedOutput, int nEpochs) throws Exception {...} /** input y expectedOutput deben tener la misma cantidad de elementos, 
	nEpochs es la cantidad de veces que se entrenara con el dataSet.*/

```

### Predecir

Metodo para hacer una prediccion individual, sobre un input cualquiera.

```Java

	public double[] predict(double[] input) throws Exception {...} /** retorna vector de predicciones (con valores entre 0 y 1) con tamaño igual a la cantidad de neuronas de la capa de salida */
	public int[] binaryPredict(double[] input, double threshold) throws Exception {...} /** Metodo para forzar predicciones binarias, se evalua cada elemento de la predicción mediante el threshold y se deja un valor 0 o 1 en el vector */
	
```

# Detalle implementación

Se tomó como base la implementación de la red neuronal, quitando el codigo no relevante para este proyecto. 

Luego se comenzó a extender la implementación para adecuarse al esquema evolutivo: 
Teniendo redes neuronales como individuos de una población, estas deben poder "aparearse" para producir descendencia. 
Para ello se utilizó 'Test drive developement' acompañado de diseño esquematico, para incorporar estas funcionalidades.

Los tests para esta fase se encuentran en network.EvolutionNeuralNetworkTest.java.
En ellos se comprueba que la creacion de redes neuronales esta correcto. Además, funciona correctamente el crossing-over de 2 redes neuronales y las mutaciones que pueden ocurrir en los pesos y bias.

También se comprueba que las poblaciones estan cambiando de una generación a otra. 
## Fitness

Para evaluar el desempeño de un individuo (Red Neuronal) frente al resto de su población se define la siguiente función de fitness:

Fitness = Puntaje_F1 * (ErrorMaximoPosible - Error)
Puntaje_F1 = precision*recall*2/(precision+recall) o media armónica entre precision y recall.

ErrorMaximoPosible: Como outputs toman valor entre 0 y 1. Es la cantidad de outputs por la dimensión del output.
Error cuadratico: Diferencia entre el valor actual obtenido de la red y el valor esperado para ese output, al cuadrado y agregado para todas los datos de entrenamiento.

Esto con el fin de que la red se acercara lo más posible a los valores esperados, mediante el calculo del error; y ponderandolo por el puntaje F1, que promedia las metricas de Precisión y Recall, en un valor entre 0 y 1.

El utilizar el error cuadratico como fitness, fue encontrado como una técnica recurrente en publicaciones que tratan el tema. Sine mbargo, ponderarlo por el F1 es una incorporación propia, con el fin de maximizar el recall y precisión.

Finalmente, se utilizó sólo el error cuadraitico para definir la función de fitness, teniendo un mayor valor si el error es menor, y un menor valor si el error es mayor.
## Reproducción

Se produce un individuo Hijo (Red Neuronal) de 2 individuos Progenitores. El cual se concibe a partir de 2 procesos:
### Crossing-Over

Se producen los pesos y bias del Hijo a partir de una permutación de los genes de los progenitores, a través de una función aleatoria que asigna cada gen con la misma probabilidad.
### Mutación

Se trabajaron con dos modelos de mutación en un gen (peso o bias),:

	1. Generar un nuevo valor aleatorio (uniforme entre 0 y 1)
	2. Generar una variación aditiva o disminutiva, en el 10% del valor actual del gen.
	
El que obtuvo mejores resultados (Individuo con fitness máxima) fue el segundo.

# Resultados

Se hicieron tres tipos de pruebas:

	1. Batería de pruebas
	2. Clasificación de toxicidad de hongos
	3. Clasificación de SPAM

Los data-set y problemas de clasificacion son los mismos que los entregados en la Tarea 1. Esto para cumplir el objetivo de comparar el desempeño de las redes neuronales entrenadas con Algoritmos Evolutivos versus con Back Propagation.
	

## Batería de pruebas

Se entrenan las redes neuronales con los valores de verdad de las compuertas logicas: OR, AND y XOR; y además con los puntos sobre y bajo una funcion linear cualquiera.
Esta prueba se corre ejecutando el archivo network.LayerNetworkTest con JUnit.


Luego de ejecutar muchas veces la batería de tests, y modificando los parametros del problema se convergió que los mejores desempeños se encuentran con una población de 500 individuos y un ratio de mutación de 0.05.

Se pueder la curva de aprendizaje en los siguientes gráficos, para cada una de las pruebas:
### Un solo output; clase determinada por umbral
Se entrenaron 100 generaciones; para todas las pruebas. 

A continuación se muestra como aumenta el fitness del mejor individuo de cada población, a medida que avanzan las generaciones.

#### OR
![](https://i.imgur.com/Ee460I4.png)
#### XOR
![](https://i.imgur.com/SW6A2lZ.png)
#### AND
![](https://i.imgur.com/jfq9U7k.png)
### Funcion Lineal
![](https://i.imgur.com/JsTXqwq.png)

Se puede evidenciar una rapida convergencia a un maximo fitness.
Sin embargo, al momento de evaluar el clasificador obtenido con esos pesos y biases; el desempeño es peor que el logrado con aprendizaje con Back-Propagation. 
Es más, independiente del valor de threshold que se elija; el clasificador final es equivalente a uno que dice todas las clases son verdaderas. Lo cual logra una tasa de aciertos del 50% (Esto dado que se balanceó las clases en el data-set de entrenamiento y test).


## Clasificación de hongos

Se entrenan las redes neuronales dadas las caracterisitcas de un hongo, poder predecir si es toxico o no.
Esta prueba se corre ejecutando el archivo fungi.MainClass.java; para ello es necesario descargar el data-set.
Más información en  [../src/fungi/README.md](https://github.com/Vichoko/neural-network/blob/master/src/fungi/README.md).

Este clasificador utiliza una doble salida (De dimensión 2). Salida que se interpreta como: El indice del mayor valor, es la clase predicha.
Esto para independizar el valor predicho de la elección de un correcto "Threshold".

Lo primero que se observa es que tarda muchisimo más por generación, versus el método de aprendizaje por Back-Propagation.



## Clasificación de SPAM

Se entrenan las redes neuronales dados mensajes de texto (SMS), poder predecir si es SPAM o no (HAM).
Esta prueba se corre ejecutando el archivo spam.MainClass.java; para ello es necesario descargar el data-set.

La clase ejecuta todo el procesamiento de texto necesario para formatear los datos, entrenar las redes y mostrar los resultados; por lo que puede tardar varios minutos.
Más información en  [../src/spam/README.md](https://github.com/Vichoko/neural-network/blob/master/src/spam/README.md).










