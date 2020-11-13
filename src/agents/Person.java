package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Person extends Agent {
    public final Logger logger;

    final String id;
    final HashMap<String, Integer> teamAffinity;
    final HashMap<String, Integer> guesses;
    final HashMap<String, Float> confidence;
    final DFAgentDescription dfd;
    public boolean ready;
    Integer guess;

    Person(String id) {
        this.id = id;
        teamAffinity = new HashMap<>();
        guesses = new HashMap<>();
        confidence = new HashMap<>();
        dfd = new DFAgentDescription();
        ready = false;
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

    protected void startConfidence() {
        Random rnd = new Random();
        DFAgentDescription[] aud = getAudience();
        DFAgentDescription[] com = getCompetitor();

        for (DFAgentDescription a : aud) {
            String id = a.getName().getLocalName();
            if (id.equals(getLocalName())) continue;
            confidence.put(id, rnd.nextFloat() + 0.2f);
        }

        for (DFAgentDescription c : com) {
            String id = c.getName().getLocalName();
            if (id.equals(getLocalName())) continue;
            confidence.put(id, rnd.nextFloat() + 0.2f);
        }
    }

    public void addTeam(String id, Integer value) {
        if (value != null) teamAffinity.put(id, value);
    }

    public HashMap<String, Integer> getGuesses() {
        return guesses;
    }

    public Integer getGuess() {
        return guess;
    }

    public HashMap<String, Integer> getTeam() {
        return teamAffinity;
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

    public void receiveGuess(String id, Integer value) {
        guesses.put(id, value);
    }

    public void startRound() {
        startConfidence();
        behaviours();
    }

    public void endRound(int price) {
        updateConfidence(price);
        guess = null;
        guesses.clear();
        ready = false;
    }

    private void updateConfidence(int price) {
        guesses.values().removeIf(Objects::isNull);
        if (guesses.isEmpty()) return;

        Integer min = Collections.min(guesses.values());
        Integer max = Collections.max(guesses.values());
        int maxDiff = Math.max(Math.abs(price - min), Math.abs(price - max));

        for (Map.Entry<String, Float> entry : confidence.entrySet()) {
            if (guesses.get(entry.getKey()) == null) continue;
            int diff = Math.abs(guesses.get(entry.getKey()) - price);
            confidence.replace(entry.getKey(), entry.getValue() * (1.5f - diff / maxDiff));
        }
    }

    abstract void behaviours();

    public abstract void finalGuess();

    float[] finalGuessCalc() {
        guesses.values().removeIf(Objects::isNull);

        float maxConfidence = 0.0f;
        float currentGuess = 0.0f;

        for (Map.Entry<String, Integer> entry : guesses.entrySet()) {
            currentGuess += entry.getValue() * confidence.get(entry.getKey());
            maxConfidence += confidence.get(entry.getKey());
        }
        float[] a = new float[2];
        a[0] = currentGuess;
        a[1] = maxConfidence;

        return a;
    }
}
