package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Person extends Agent {
    final String id;
    final HashMap<String, Integer> teamAffinity;
    final HashMap<String, Integer> guesses;
    final HashMap<String, Float> confidence;
    Integer guess;

    public Person(String id) {
        this.id = id;
        teamAffinity = new HashMap<>();
        guesses = new HashMap<>();
        confidence = new HashMap<>();
        guess = null;
    }

    public abstract void behaviours();

    public void addTeam(String id, Integer value) {
        if (value != null)
            teamAffinity.put(id, value);
    }

    public HashMap<String, Integer> getGuesses() {
        return guesses;
    }

    public DFAgentDescription[] getCompetitor() {
        return getService("competitor");
    }

    public DFAgentDescription[] getAudience() {
        return getService("audience");
    }

    public DFAgentDescription[] getService(String type) {
        DFAgentDescription[] res = null;

        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            res = DFService.search(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void receiveGuess(String audience, Integer guess) {
        guesses.put(audience, guess);
        confidence.putIfAbsent(audience, 1.0f);
    }

    public void endRound(int price) {
        updateConfidence(price);
        guess = null;
        guesses.clear();
    }

    private void updateConfidence(int price) {
        Integer min = Collections.min(guesses.values());
        Integer max = Collections.max(guesses.values());
        int maxDiff = Math.max(Math.abs(price - min), Math.abs(price - max));

        for (Map.Entry<String, Float> entry : confidence.entrySet()) {
            int diff = Math.abs(guesses.get(entry.getKey()) - price);
            confidence.replace(entry.getKey(), entry.getValue() * (1.5f - diff / maxDiff));
        }

    }
}
