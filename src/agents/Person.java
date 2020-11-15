package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
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

    Person(String id, long time) {
        this.id = id;
        teamAffinity = new HashMap<>();
        guesses = new HashMap<>();
        confidence = new HashMap<>();
        dfd = new DFAgentDescription();
        ready = false;
        guess = null;
        logger = Logger.getLogger(id);
        setupLogger(time);
    }

    private void setupLogger(long time) {
        try {
            FileHandler handler = new FileHandler("logs/" + time + "/" + id + ".log");
            handler.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s%n";
                private final Date dat = new Date();

                @Override
                public String format(LogRecord record) {
                    dat.setTime(record.getMillis());
                    String message = formatMessage(record);
                    return String.format(format, dat, record.getLevel().getLocalizedName(), message);
                }
            });
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Exception thrown while setting up " + id + " logger.");
            e.printStackTrace();
            System.exit(3);
        }
    }

    public void startConfidence() {
        Random rnd = new Random();
        DFAgentDescription[] aud = getAudience();

        for (DFAgentDescription a : aud) {
            String id = a.getName().getLocalName();
            if (id.equals(getLocalName())) continue;
            confidence.putIfAbsent(id, rnd.nextFloat() + 0.2f);
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

    DFAgentDescription[] getService(String type) {
        DFAgentDescription[] res = null;
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            res = DFService.search(this, dfd);
        } catch (FIPAException e) {
            logger.severe("Exception thrown while getting service " + type + ".");
            e.printStackTrace();
            System.exit(3);
        }
        return res;
    }

    public void receiveGuess(String id, Integer value) {
        guesses.put(id, value);
    }

    public void startRound() {
        behaviours();
    }

    public void endRound(int price) {
        updateConfidence(price);
        guess = null;
        guesses.clear();
        ready = false;
    }

    void updateConfidence(int price) {
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
        Random rnd = new Random();

        for (Map.Entry<String, Integer> entry : guesses.entrySet()) {
            confidence.putIfAbsent(entry.getKey(), rnd.nextFloat() + 0.2f);
            currentGuess += entry.getValue() * confidence.get(entry.getKey());
            maxConfidence += confidence.get(entry.getKey());
        }
        float[] a = new float[2];
        a[0] = currentGuess;
        a[1] = maxConfidence;

        return a;
    }

    protected void setupAgent(String type) {
        try {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            logger.severe("Exception thrown while setting up " + id + ".");
            e.printStackTrace();
            System.exit(3);
        }
    }
}
