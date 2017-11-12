# evolutionary-neural-network

A neural network with an evolutionary training.
.
# Requerimientos
* JUnit 4
* JavaSE-1.7


# Motivaci�n

Modificar (red neuronal implementada)[https://github.com/Vichoko/neural-network], incorporando aprendizaje mediante un *algoritmo gen�tico*. 
Utilizando un esquema parecido al utilizado en el proyecto del ("Adivinador de palabras evolutivo")[https://github.com/Vichoko/evolutionary-guesser-of-words], implementado en el pasado.	

El objetivo es comparar el desempe�o con el entrenamiento evolutivo versus el entrenamiento por *Back Propagation", implementado en la Tarea 1.
# Uso
## Configurar algoritmo evolutivo

Se hace desde el archivo darwin.Global.java.
Permite variar los par�metros:

	public static int populationSize = 500; // Tama�o de la poblaci�n
	public static double mutationRate = 0.05; // Probabilidad de mutaci�n por gen (peso o bias)
	public static double survivorsRate = 0.15; // Fracci�n de individuos que se puede reproducir (sin mecanismo de ruleta)
	public static double predictionThreshold = 0.5; // al usar una salida singular, se necesita umbral para clasificar
	
## Crear red
### Constructor

```Java

	public NeuralNetwork() {...} // crea red neuronal vac�a
```

### Crear capas

```Java

	public void newInputLayer(int inputSize, int numberOfNeurons) throws Exception {...} // Crear capa de entrada
	public void newHiddenLayer(int numberOfNeurons) throws Exception {...} // crear capa oculta o de salida
	public void closeNetwork() throws Exception {...} // se transforma ultima capa en capa de salida
```

### Entrenar

```Java

	public void train(double[][] input, double[][] expectedOutput, int nGens, bool isDualOutput, String plotName) throws Exception {...} /** input y expectedOutput deben tener la misma cantidad de elementos, 
	nGen es la cantidad de generaciones que evolucionar� en algoritmo.*/

```

### Predecir

M�todo para hacer una predicci�n individual, sobre un input cualquiera.

```Java

	public double[] predict(double[] input) throws Exception {...} /** retorna vector de predicciones (con valores entre 0 y 1) con tama�o igual a la cantidad de neuronas de la capa de salida */
	public int[] binaryPredict(double[] input, double threshold) throws Exception {...} /** M�todo para forzar predicciones binarias, se eval�a cada elemento de la predicci�n mediante el threshold y se deja un valor 0 o 1 en el vector */
	
```

# Detalle implementaci�n

Se tom� como base la implementaci�n de la red neuronal, quitando el c�digo no relevante para este proyecto. 

Luego se comenz� a extender la implementaci�n para adecuarse al esquema evolutivo: 
Teniendo redes neuronales como individuos de una poblaci�n, estas deben poder "aparearse" para producir descendencia. 
Para ello se utiliz� 'Test drive developement' acompa�ado de dise�o esquem�tico, para incorporar estas funcionalidades.

Los test para esta fase se encuentran en network.EvolutionNeuralNetworkTest.java.
En ellos se comprueba que la creaci�n de redes neuronales est� correcta. Adem�s, funciona correctamente el crossing-over de 2 redes neuronales y las mutaciones que pueden ocurrir en los pesos y bias.

Tambi�n se comprueba que las poblaciones est�n cambiando de una generaci�n a otra. 
## Fitness

Para evaluar el desempe�o de un individuo (Red Neuronal) frente al resto de su poblaci�n se define la siguiente funci�n de fitness:

	Fitness = Puntaje_F1 * (ErrorMaximoPosible - Error)
	Puntaje_F1 = precision*recall*2/(precision+recall) o media arm�nica entre precision y recall.

	* ErrorMaximoPosible: Como outputs toman valor entre 0 y 1. Es la cantidad de outputs por la dimensi�n del output.
	* Error cuadr�tico: Diferencia entre el valor actual obtenido de la red y el valor esperado para ese output, al cuadrado y agregado para todos los datos de entrenamiento.

Esto con el fin de que la red se acercara lo m�s posible a los valores esperados, mediante el c�lculo del error; y ponder�ndolo por el puntaje F1, que promedia las m�tricas de Precisi�n y Recall, en un valor entre 0 y 1.

El utilizar el error cuadr�tico como fitness, fue encontrado como una t�cnica recurrente en publicaciones que tratan el tema. Sine embargo, ponderarlo por el F1 es una incorporaci�n propia, con el fin de maximizar el recall y precisi�n.

Finalmente, se utiliz� s�lo el error cuadr�tico para definir la funci�n de fitness, teniendo un mayor valor si el error es menor, y un menor valor si el error es mayor.
## Reproducci�n

Se produce un individuo Hijo (Red Neuronal) de 2 individuos Progenitores. El cual se concibe a partir de 2 procesos:
### Crossing-Over

Se producen los pesos y bias del Hijo a partir de una permutaci�n de los genes de los progenitores, a trav�s de una funci�n aleatoria que asigna cada gen con la misma probabilidad.
### Mutaci�n

Se trabajaron con dos modelos de mutaci�n en un gen (peso o bias),

	1. Generar un nuevo valor aleatorio (uniforme entre 0 y 1)
	2. Generar una variaci�n aditiva o diminutiva, en el 10% del valor actual del gen.
	
El que obtuvo mejores resultados (Individuo con fitness m�xima) fue el segundo.

# Resultados

Se hicieron tres tipos de pruebas:

	1. Bater�a de pruebas
	2. Clasificaci�n de toxicidad de hongos
	3. Clasificaci�n de SPAM

Los data-set y problemas de clasificaci�n son los mismos que los entregados en la Tarea 1. Esto para cumplir el objetivo de comparar el desempe�o de las redes neuronales entrenadas con Algoritmos Evolutivos versus con Back Propagation.
	

## Bater�a de pruebas

Se entrenan las redes neuronales con los valores de verdad de las compuertas l�gicas: OR, AND y XOR; y adem�s con los puntos sobre y bajo una funci�n linear cualquiera.
Esta prueba se corre ejecutando el archivo network.LayerNetworkTest con JUnit.


Luego de ejecutar muchas veces la bater�a de test, y modificando los par�metros del problema se convergi� que los mejores desempe�os se encuentran con una poblaci�n de 500 individuos y una ratio de mutaci�n de 0.05.

Se evidencia la curva de aprendizaje en los siguientes gr�ficos, para cada una de las pruebas:
### Un solo output; clase determinada por umbral
Se entrenaron 100 generaciones; para todas las pruebas. 

A continuaci�n, se muestra como aumenta el fitness del mejor individuo de cada poblaci�n, a medida que avanzan las generaciones.

#### OR
![](images/OR.png?raw=true)
#### XOR
![](images/XOR.png?raw=true)
#### AND
![](images/AND.png?raw=true)
### Funci�n Lineal
![](images/DIAG.png?raw=true)

Se puede evidenciar una r�pida convergencia a un m�ximo fitness.
Sin embargo, al momento de evaluar el clasificador obtenido con esos pesos y biases; el desempe�o es peor que el logrado con aprendizaje con Back-Propagation. 
Es m�s, independiente del valor de threshold que se elija; el clasificador final es equivalente a uno que dice todas las clases son verdaderas. Lo cual logra una tasa de aciertos del 50% (Esto dado que se balance� las clases en el data-set de entrenamiento y test).


## Clasificaci�n de hongos

Se entrenan las redes neuronales dadas las caracter�sticas de un hongo, poder predecir si es toxico o no.
Esta prueba se corre ejecutando el archivo fungi.MainClass.java; para ello es necesario descargar el data-set.
M�s informaci�n en  [../src/fungi/README.md](src/fungi/README.md).

Este clasificador utiliza una doble salida (De dimensi�n 2). Salida que se interpreta como: El �ndice del mayor valor, es la clase predicha.
Esto para independizar el valor predicho de la elecci�n de un correcto "Threshold".

Lo primero que se observa es que tarda much�simo m�s por generaci�n, versus el m�todo de aprendizaje por Back-Propagation.
El entrenamiento con 50 generaciones tarda aproximadamente 1 hora y muestra la siguiente curva de aprendizaje:

![](images/FUNGI.png?raw=true)
Se puede evidenciar como disminuye sus errores cuadraticos, convergiendo en torno a la generaci�n 25.

A pesar de esta convergencia, el clasificador entrega las siguientes m�tricas de desempe�o:

	Metricas de desempe�o de la red neuronal: 
	Numero de experimentos: 8124
	Verdaderos Positivos: 0
	Verdaderos Negativos: 4208
	Falsos Positivos: 0
	Falsos Negativos: 3916
	Tasa aciertos: 0.517971442639094; tasa desaciertos: 0.48202855736090594
	Precision: NaN
	Recall: 0.0
	F1 Score: NaN
	
Resultados que muestran que realmente el clasificador est� diciendo que todas las clases son 0. Lo cual es un clasificador malo.

## Clasificaci�n de SPAM

Se entrenan las redes neuronales dados mensajes de texto (SMS), poder predecir si es SPAM o no (HAM).
Esta prueba se corre ejecutando el archivo spam.MainClass.java; para ello es necesario descargar el data-set.

La clase ejecuta todo el procesamiento de texto necesario para formatear los datos, entrenar las redes y mostrar los resultados; por lo que puede tardar varios minutos.
M�s informaci�n en  [../src/spam/README.md](src/spam/README.md).











