# evolutionary-neural-network
A neural network with an evolutionary training.

# Motivación
Modificar (red neuronal implementada)[https://github.com/Vichoko/neural-network], incorporando aprendizaje mediante un *algoritmo genetico*. 
Utilizando un esquema parecido al utilizado en el proyecto del ("Adivinador de palabras evolutivo")[https://github.com/Vichoko/evolutionary-guesser-of-words], implementado en el pasado.	

# Detalle implementación
Se tomó como base la implementación de la red neuronal, quitando el codigo no relevante para este proyecto. 

Luego se comenzó a extender la implementación para adecuarse al esquema evolutivo: 
Teniendo redes neuronales como individuos de una población, estas deben poder "aparearse" para producir descendencia. 
Para ello se utilizó 'Test drive developement' acompañado de diseño esquematico, para incorporar estas funcionalidades.

Los tests para esta fase se encuentran en network.EvolutionNeuralNetworkTest.java.
En ellos se comprueba que la creacion de redes neuronales esta correcto. Además, funciona correctamente el crossing-over de 2 redes neuronales y las mutaciones que pueden ocurrir en los pesos y bias.

También se comprueba que las poblaciones estan cambiando de una generación a otra. 
Todo indica que hay un problema con la función de fitness.

PRobar usando como fitness MaxError - ActualError

