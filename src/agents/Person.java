package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Person extends Agent {
    public final Logger logger;
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
        logger = Logger.getLogger(id);
        setupLogger();
    }

    private void setupLogger() {
        try {
            FileHandler handler = new FileHandler("logs/" + id + ".log");
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
        }
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
            System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
        }

        return res;
    }

    public void receiveGuess(String audience, Integer guess) {
        guesses.put(audience, guess);
        if(!confidence.containsKey(audience)){
            Random rnd = new Random();
            confidence.put(audience, rnd.nextFloat()+0.2f);
        }
    }

    public void endRound(int price) {
        updateConfidence(price);
        guess = null;
        guesses.clear();
    }

    private void updateConfidence(int price) {
        while(guesses.values().remove(null));
        if(guesses.isEmpty()) return;

        Integer min = Collections.min(guesses.values());
        Integer max = Collections.max(guesses.values());
        int maxDiff = Math.max(Math.abs(price - min), Math.abs(price - max));

        for (Map.Entry<String, Float> entry : confidence.entrySet()) {
            if(guesses.get(entry.getKey()) == null) continue;
            int diff = Math.abs(guesses.get(entry.getKey()) - price);
            confidence.replace(entry.getKey(), entry.getValue() * (1.5f - diff / maxDiff));
        }

    }

    public abstract void finalGuess();
}
