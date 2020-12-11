# AIAD-FEUP

This repository is dedicated to the project of Agents and Distributed Artifitial Intelligence (AIAD) at Faculty of Engineering of the University of Porto (FEUP).
Made by [João Campos](https://github.com/Pastilhas). *11/2020* 

1. [Objective](#objective)
2. [Description](#description)
3. [First Delivery](#first-delivery)
4. [Second Delivery](#second-delivery)
5. [To run](#to-run)

## Objective
The objective of the first part of this work is to implement communication between agents, and for this communication to affect the decisions these agents take. The second part consists in adapting the previous part to use Repast3.

## Description
The agents in this project play a version of *The Price Is Right* where there are two types of agents: audience and competitors.

In the beginning, each element of the audience is told the price of a few items. At the start of a round, an item is chosen and the audience guesses a first time.
Then, the audience shares their guess with each other and each agent may change their initial guess. After everyone in the audience has a guess, 
the competitors ask the audience their guess. If an elemnt of the audience likes a competitor, they will send their guess, if they do not like him, they ignore the request.
The competitors then have their chance to take a guess. The competitor with the closest guess wins.

After each round, each agent (audience and competitors) will change their confidence on other agents based on their accuracy on the previous round.
The can play with any number of rounds, audience members, competitors and items.

## First Delivery
For the first delivery, made on 15/11/2020, the contents of the project were like on commit [3118ff6](https://github.com/Pastilhas/AIAD-FEUP/commit/3118ff611fef95e0babf14fea256b1fc7a4ceeb4). The code and report may contain errors and should not be used without previous inspection. 
After this point starts the second delivery which consists of adapting the project using Simple API for JADE-based Simulations ([SAJaS](https://web.fe.up.pt/~hlc/doku.php?id=sajas)).

## Second Delivery
For the second delivery, 13/12/2020, the contents were like on commit [here](https://www.youtube.com/watch?v=oHg5SJYRHA0). As with the previous delivery, code should be used with care. This part of the project uses JADE, SAJaS and Repast3

## To run
To run this project you need to download the JADE framework.

Then, compile the project with:
    
>javac -d "./out/" -sourcepath "./src/" -cp "./lib/jade/lib/jade.jar;./lib/repast/repast.jar;./lib/sajas/lib/sajas.jar" ./src/world/WorldModel.java

With the class files generated, now run:
    
>java -cp "./out;./lib/jade/lib/jade.jar;./lib/repast/repast.jar;./lib/sajas/lib/sajas.jar" world.WorldModel [ boolean batch [ int nAudience [ int nCompetitors [ int nItems [ float highConfidenceRate ]]]]] 
