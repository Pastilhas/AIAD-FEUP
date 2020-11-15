# AIAD-FEUP

This repository is dedicated to the project of Agents and Distributed Artifitial Intelligence (AIAD) at Faculty of Engineering of the University of Porto (FEUP).
Made by [João Campos](https://github.com/Pastilhas). *11/2020*

## Objective
The objective of this work is to implement communication between agents, and for this communication to affect the decisions these agents take.

## Description
The agents in this project play a version of *The Price Is Right* where there are two types of agents: audience and competitors.

In the beginning, each element of the audience is told the price of a few items. At the start of a round, an item is chosen and the audience guesses a first time.
Then, the audience shares their guess with each other and each agent may change their initial guess. After everyone in the audience has a guess, 
the competitors ask the audience their guess. If an elemnt of the audience likes a competitor, they will send their guess, if they do not like him, they ignore the request.
The competitors then have their chance to take a guess. The competitor with the closest guess wins.

After each round, each agent (audience and competitors) will change their confidence on other agents based on their accuracy on the previous round.
The can play with any number of rounds, audience members, competitors and items.

## To run
To run this project you need to download the jade library.

Then, compile the project with:
    
    javac -d "out/" -sourcepath "src/" -cp "path/to/jade.jar" src/world/World.java
With the class files generated, now run:
    
    java -cp "out/" world.World <nAudience> <nCompetitors> <nItems> <highConfidenceRate> <tries> <rounds>
