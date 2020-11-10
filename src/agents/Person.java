package agents;

import jade.core.Agent;

import java.util.HashMap;

public class Person extends Agent {
    final String id;
    final HashMap<String, Integer> teamAffinity;

    public Person(String id) {
        this.id = id;
        teamAffinity = new HashMap<>();
    }

    public void addTeam(String id, Integer value) {
        if(value != null)
            teamAffinity.put(id, value);
    }
}
