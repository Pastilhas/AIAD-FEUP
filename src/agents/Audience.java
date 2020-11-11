package agents;

import behaviours.AudienceReceiveGuess;
import behaviours.AudienceReceiveRequest;
import behaviours.AudienceSendGuess;
import behaviours.AudienceShareGuess;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Audience extends Person {
    private final float selfconfidence;
    private final HashMap<String, Integer> itemPrice;
    private final DFAgentDescription dfd;
    private final HashMap<String, Integer> compatibility;
    public boolean ready;

    public Audience(String id, float selfconfidence) {
        super(id);
        this.selfconfidence = selfconfidence;
        itemPrice = new HashMap<>();
        dfd = new DFAgentDescription();
        compatibility = new HashMap<>();
    }

    @Override
    public void behaviours() {
        // Setup behaviours
        SequentialBehaviour sb = new SequentialBehaviour();
        sb.addSubBehaviour(new AudienceShareGuess(this));
        sb.addSubBehaviour(new AudienceReceiveGuess(this));
        sb.addSubBehaviour(new AudienceReceiveRequest(this));
        sb.addSubBehaviour(new AudienceSendGuess(this));
        addBehaviour(sb);
    }

    public void startRound(String item) {
        initialGuess(item);
        ready = false;
        behaviours();
    }

    public void setup() {
        // Register agent in yellow pages
        try {
            ServiceDescription sd = new ServiceDescription();
            sd.setType("audience");
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
        }
    }

    public void addItem(int id, Integer price) {
        if (price != null)
            itemPrice.put(Integer.toString(id), price);
    }

    public Integer getGuess(String rcv) {
        // Share with rest of audience
        if (rcv == null || compatibility.get(rcv) > 50) {
            return guess;
        } else {
            return null;
        }
    }

    public HashMap<String, Integer> getCompatibility() {
        return compatibility;
    }

    public void checkCompetitor(String competitor, HashMap<String, Integer> map) {
        int comp = 0;
        for (Map.Entry<String, Integer> entry : teamAffinity.entrySet()) {
            comp += Math.abs(entry.getValue() - map.get(entry.getKey()));
        }
        compatibility.put(competitor, comp);
    }

    public void initialGuess(String item) {
        Random rnd = new Random();
        if (itemPrice.get(item) != null) {
            guess = itemPrice.get(item) + rnd.nextInt((int) (itemPrice.get(item) * 0.2f)) - (int) (itemPrice.get(item) * 0.1f);
        } else {
            guess = rnd.nextInt(15001);
        }
    }

    public void finalGuess() {
        float maxConfidence = 0.0f;
        float currentGuess = 0.0f;

        for (Map.Entry<String, Integer> entry : guesses.entrySet()) {
            if(entry.getValue() == null) continue;
            currentGuess += entry.getValue() * confidence.get(entry.getKey());
            maxConfidence += confidence.get(entry.getKey());
        }

        currentGuess += guess * selfconfidence;
        maxConfidence += selfconfidence;

        guess = Math.round(currentGuess / maxConfidence);
        ready = true;
    }
}
