package agents;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import draw.AgentNode;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import world.WorldModel;

public abstract class Person extends MyAgent {
    protected final HashMap<String, Integer> teamAffinity;
    protected final HashMap<String, Integer> guesses;
    protected final HashMap<String, Float> confidence;
    protected final AgentNode node;
    private final WorldModel world;
    Integer guess;

    Person(String id, long time, WorldModel world, Color color, int x, int y) {
        super(id, time);

        teamAffinity = new HashMap<>();
        guesses = new HashMap<>();
        confidence = new HashMap<>();
        node = AgentNode.getNewNode(id, color, x, y);
        guess = null;
        this.world = world;
    }

    public void startConfidence() {
        Random rnd = new Random();
        DFAgentDescription[] aud = getAudience();

        for (DFAgentDescription a : aud) {
            String id = a.getName().getLocalName();
            if (id.equals(getLocalName()))
                continue;
            confidence.putIfAbsent(id, rnd.nextFloat() + 0.2f);
        }
    }

    public void addTeam(String id, Integer value) {
        if (value != null)
            teamAffinity.put(id, value);
    }

    public HashMap<String, Integer> getGuesses() {
        return guesses;
    }

    public Integer getGuess() {
        return guess;
    }

    public AgentNode getNode() {
        return node;
    }

    public HashMap<String, Integer> getTeam() {
        return teamAffinity;
    }

    public void receiveGuess(String id, Integer value) {
        guesses.put(id, value);
    }

    void updateConfidence(int price) {
        guesses.values().removeIf(Objects::isNull);
        if (guesses.isEmpty())
            return;

        Integer min = Collections.min(guesses.values());
        Integer max = Collections.max(guesses.values());
        int maxDiff = Math.max(Math.abs(price - min), Math.abs(price - max));

        for (Map.Entry<String, Float> entry : confidence.entrySet()) {
            if (guesses.get(entry.getKey()) == null)
                continue;
            int diff = Math.abs(guesses.get(entry.getKey()) - price);
            confidence.replace(entry.getKey(), entry.getValue() * (1.5f - diff / maxDiff));
        }
    }

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

    public void parseWorldMsg(ACLMessage msg) {
        Scanner sc = new Scanner(msg.getContent()); // start <string item_id> OR end <int item_price>

        String m = sc.next();
        if (m.equals("start")) {
            String item_id = sc.next();
            startRound(item_id);
        } else if (m.equals("end")) {
            Integer item_price = sc.nextInt();
            endRound(item_price);
        }
    }

    public void addEdge(AgentNode to) {
        node.addEdge(to);
    }

    public void removeEdges() {
        node.removeEdges();
    }

    public void sendMsg(String name) {
        node.addEdge(world.getNode(name));
    }

    public abstract void finalGuess();

    protected abstract void startRound(String item_id);

    protected abstract void endRound(Integer item_price);
}
