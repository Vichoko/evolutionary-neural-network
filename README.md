# evolutionary-neural-network
A neural network with an evolutionary training.

# Motivaci�n
Modificar (red neuronal implementada)[https://github.com/Vichoko/neural-network], incorporando aprendizaje mediante un *algoritmo genetico*. 
Utilizando un esquema parecido al utilizado en el proyecto del ("Adivinador de palabras evolutivo")[https://github.com/Vichoko/evolutionary-guesser-of-words], implementado en el pasado.	

# Detalle implementaci�n
Se tom� como base la implementaci�n de la red neuronal, quitando el codigo no relevante para este proyecto. 

Luego se comenz� a extender la implementaci�n para adecuarse al esquema evolutivo: 
Teniendo redes neuronales como individuos de una poblaci�n, estas deben poder "aparearse" para producir descendencia. 
Para ello se utiliz� 'Test drive developement' acompa�ado de dise�o esquematico, para incorporar estas funcionalidades.

Los tests para esta fase se encuentran en network.EvolutionNeuralNetworkTest.java.
En ellos se comprueba que la creacion de redes neuronales esta correcto. Adem�s, funciona correctamente el crossing-over de 2 redes neuronales y las mutaciones que pueden ocurrir en los pesos y bias.

Tambi�n se comprueba que las poblaciones estan cambiando de una generaci�n a otra. 
Todo indica que hay un problema con la funci�n de fitness.

PRobar usando como fitness MaxError - ActualError

