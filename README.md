# evolutionary-neural-network

A neural network with an evolutionary training.
.
# Requerimientos
* JUnit 4
* JavaSE-1.7


# Motivación

Modificar [red neuronal implementada](https://github.com/Vichoko/neural-network), incorporando aprendizaje mediante un *algoritmo genético*. 
Utilizando un esquema parecido al utilizado en el proyecto del ["Adivinador de palabras evolutivo"](https://github.com/Vichoko/evolutionary-guesser-of-words), implementado en el pasado.	

El objetivo es comparar el desempeño con el entrenamiento evolutivo versus el entrenamiento por **Back Propagation**, implementado en la Tarea 1.
# Uso
## Configurar algoritmo evolutivo

Se hace desde el archivo darwin.Global.java.
Permite variar los parámetros:

	public static int populationSize = 500; // Tamaño de la población
	public static double mutationRate = 0.05; // Probabilidad de mutación por gen (peso o bias)
	public static double survivorsRate = 0.15; // Fracción de individuos que se puede reproducir (sin mecanismo de ruleta)
	public static double predictionThreshold = 0.5; // al usar una salida singular, se necesita umbral para clasificar
	
## Ejecutar pruebas
	Referir a sección de Resultados, para detalle de ejecución de pruebas.
## Crear red
### Constructor

```Java

	public NeuralNetwork() {...} // crea red neuronal vacía
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
	nGen es la cantidad de generaciones que evolucionará en algoritmo.*/

```

### Predecir

Método para hacer una predicción individual, sobre un input cualquiera.

```Java

	public double[] predict(double[] input) throws Exception {...} /** retorna vector de predicciones (con valores entre 0 y 1) con tamaño igual a la cantidad de neuronas de la capa de salida */
	public int[] binaryPredict(double[] input, double threshold) throws Exception {...} /** Método para forzar predicciones binarias, se evalúa cada elemento de la predicción mediante el threshold y se deja un valor 0 o 1 en el vector */
	
```

# Detalle implementación

Se tomó como base la implementación de la red neuronal, quitando el código no relevante para este proyecto. 

Luego se comenzó a extender la implementación para adecuarse al esquema evolutivo: 
Teniendo redes neuronales como individuos de una población, estas deben poder "aparearse" para producir descendencia. 
Para ello se utilizó 'Test drive developement' acompañado de diseño esquemático, para incorporar estas funcionalidades.

Los test para esta fase se encuentran en network.EvolutionNeuralNetworkTest.java.
En ellos se comprueba que la creación de redes neuronales está correcta. Además, funciona correctamente el crossing-over de 2 redes neuronales y las mutaciones que pueden ocurrir en los pesos y bias.

También se comprueba que las poblaciones están cambiando de una generación a otra. 
## Fitness

Para evaluar el desempeño de un individuo (Red Neuronal) frente al resto de su población se define la siguiente función de fitness:

	Fitness = Puntaje_F1 * (ErrorMaximoPosible - Error)
	Puntaje_F1 = precision*recall*2/(precision+recall) o media armónica entre precision y recall.

	* ErrorMaximoPosible: Como outputs toman valor entre 0 y 1. Es la cantidad de outputs por la dimensión del output.
	* Error cuadrático: Diferencia entre el valor actual obtenido de la red y el valor esperado para ese output, al cuadrado y agregado para todos los datos de entrenamiento.

Esto con el fin de que la red se acercara lo más posible a los valores esperados, mediante el cálculo del error; y ponderándolo por el puntaje F1, que promedia las métricas de Precisión y Recall, en un valor entre 0 y 1.

El utilizar el error cuadrático como fitness, fue encontrado como una técnica recurrente en publicaciones que tratan el tema. Sine embargo, ponderarlo por el F1 es una incorporación propia, con el fin de maximizar el recall y precisión.

**Finalmente, se utilizó sólo el error cuadrático para definir la función de fitness, teniendo un mayor valor si el error es menor, y un menor valor si el error es mayor.**
## Reproducción

Se produce un individuo Hijo (Red Neuronal) de 2 individuos Progenitores. El cual se concibe a partir de 2 procesos:
### Crossing-Over

Se producen los pesos y bias del Hijo a partir de una permutación de los genes de los progenitores, a través de una función aleatoria que asigna cada gen con la misma probabilidad.
### Mutación

Se trabajaron con dos modelos de mutación en un gen (peso o bias),

	1. Generar un nuevo valor aleatorio (uniforme entre 0 y 1)
	2. Generar una variación aditiva o diminutiva, en el 10% del valor actual del gen.
	
El que obtuvo mejores resultados (Individuo con fitness máxima) fue el primero.

# Resultados

Se hicieron tres tipos de pruebas:

	1. Batería de pruebas
	2. Clasificación de toxicidad de hongos
	3. Clasificación de SPAM

Los data-set y problemas de clasificación son los mismos que los entregados en la Tarea 1. Esto para cumplir el objetivo de comparar el desempeño de las redes neuronales entrenadas con Algoritmos Evolutivos versus con Back Propagation.
	

## Batería de pruebas

Se entrenan las redes neuronales con los valores de verdad de las compuertas lógicas: OR, AND y XOR; y además con los puntos sobre y bajo una función linear cualquiera.
Esta prueba se corre ejecutando el archivo network.LayerNetworkTest con JUnit.


Luego de ejecutar muchas veces la batería de test, y modificando los parámetros del problema se convergió que los mejores desempeños se encuentran con una población de 500 individuos y una ratio de mutación de 0.05.

Se evidencia la curva de aprendizaje en los siguientes gráficos, para cada una de las pruebas:
### Un solo output; clase determinada por umbral
Se entrenaron 100 generaciones; para todas las pruebas. 

A continuación, se muestra como aumenta el fitness del mejor individuo de cada población, a medida que avanzan las generaciones.

#### OR
![](img/OR.png?raw=true)
#### XOR
![](img/XOR.png?raw=true)
#### AND
![](img/AND.png?raw=true)
### Función Lineal
![](img/DIAG.png?raw=true)

Se puede evidenciar una rápida convergencia a un máximo fitness.
Sin embargo, al momento de evaluar el clasificador obtenido con esos pesos y biases; el desempeño es peor que el logrado con aprendizaje con Back-Propagation. 
Es más, independiente del valor de threshold que se elija; el clasificador final es equivalente a uno que dice todas las clases son verdaderas. Lo cual logra una tasa de aciertos del 50% (Esto dado que se balanceó las clases en el data-set de entrenamiento y test).


## Clasificación de hongos

Se entrenan las redes neuronales dadas las características de un hongo, poder predecir si es toxico o no.
Esta prueba se corre ejecutando el archivo fungi.MainClass.java; para ello es necesario descargar el data-set.
Más información en  [../src/fungi/README.md](src/fungi/README.md).

Este clasificador utiliza una doble salida (De dimensión 2). Salida que se interpreta como: El índice del mayor valor, es la clase predicha.
Esto para independizar el valor predicho de la elección de un correcto "Threshold".

Lo primero que se observa es que tarda muchísimo más por generación, versus el método de aprendizaje por Back-Propagation.
El entrenamiento con 50 generaciones tarda aproximadamente 1 hora y muestra la siguiente curva de aprendizaje:

![](img/FUNGI.png?raw=true)
Se puede evidenciar como disminuye sus errores cuadraticos, convergiendo en torno a la generación 25.

A pesar de esta convergencia, el clasificador entrega las siguientes métricas de desempeño:

	Metricas de desempeño de la red neuronal: 
	Numero de experimentos: 8124
	Verdaderos Positivos: 0
	Verdaderos Negativos: 4208
	Falsos Positivos: 0
	Falsos Negativos: 3916
	Tasa aciertos: 0.517971442639094; tasa desaciertos: 0.48202855736090594
	Precision: NaN
	Recall: 0.0
	F1 Score: NaN
	
Resultados que muestran que realmente el clasificador está diciendo que todas las clases son 0. Lo cual es un clasificador malo.

## Clasificación de SPAM

Se entrenan las redes neuronales dados mensajes de texto (SMS), poder predecir si es SPAM o no (HAM).
Esta prueba se corre ejecutando el archivo spam.MainClass.java; para ello es necesario descargar el data-set.

La clase ejecuta todo el procesamiento de texto necesario para formatear los datos, entrenar las redes y mostrar los resultados; por lo que puede tardar varios minutos.
Más información en  [../src/spam/README.md](src/spam/README.md).

Para este experimento ocurre algo interesante.
La red neuronal utilizada para clasificar texto contiene una cantidad exorbitante de neuronas, dado que la literatura recomienda tener una capa de entrada con una cantidad de neuronas equivalente a la cantidad de *features* del problema; el cual en este caso es la cantidad de terminos en el diccionario (después de filtrar stop-words y hacer stemming).

Por otro lado, el algoritmo genetico requiere instanciar una población masiva de redes neuronales; las cuales en este caso son gigantescas.
El resultado final, es que el programa se cae dado que no logra instanciar la población inicial con una cantidad suficiente de redes neuronales; por que se acaba la memoria del equipo.

Este problema, a pesar de dejarnos míopes al resultado real. Nos habla de un problema intrínseco del entrenamiento de redes neuronales con algoritmos geneticos, que tiene que ver con su alto requerimiento de recursos.

# Conclusión
Luego de ejecutar todas las pruebas explicadas anteriormente se concluye:
* El aprendizaje por este Algoritmo Genetico (en adelante A.G.) es más lento que por Back Propagation.
* El aprendizaje por este A.G. consume más recursos que por Back Propagation, dado que requiere instanciar multiples redes neuronales (las cuales pueden tener una estructura masiva).
* El resultado final de aprendizaje por este A.G. es un clasificador con un desempeño igual a un clasificador que etiqueta todos los datos como la misma clase (baseline).

Esta ultima conclusión da para pensar las razones por las cuales puede ocurrir. Al analizar los graficos de la curva de aprendizaje, se evidencia como el fitness parece converger a un valor, en todos los ejemplos. 
Si bien esto parece bueno, las metricas finales muestran un clasificador sin poder de clasificación real. 

Esto puede ser por como se decidió que se haría el Crossing-Over entre 2 individuos. Además, de como ocurren las mutaciones dentro de los apareamientos. Y no menos importante, como se definió la función de Fitness.

Se intentó con multiples modelos de apareamiento, mutación y fitness. De todos los probados, el que esta actualmente implementado fue el que alcanzó el mejor desempeño en términos de su curva de aprendizaje. Sin embargo, no se logró hacer un clasificador con un desempéño minimamente aceptable. 

Referenciando a la literatura, existen varias publicaciones que tratan este tipo de aprendizaje, lo cual demuestra que es un tópico con profundidad teorica suficiente como para hacer una investigación en torno a ella. 

Me parece sumamente interesante, dado que en un primer momento me pareció que podría lograr un mejor desempeño, dada la simplicidad conceptual que define los A.G.. Pero, en la práctica, resultó que en las sutilezas en la definición de la funcion de fitness, los procesos de crossing-over y mutación recae la mayoría de la responsabilidad de que el resultado final sea aceptable.

Los Algoritmos Geneticos toman sentido viendolos como una versión mejorada de la fuerza bruta, por lo cual hereda parte de sus defectos (exigencia de tiempo y recursos). Aunque logra ser una clara mejora de esta ultima, para su mejor aplicación se requiere un diseño detallado y un ambiente operacional optimo (Por ejemplo, utilizar paralelismo).










