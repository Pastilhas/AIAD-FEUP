package agents;

import behaviours.*;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;
import java.util.Map;

public class Competitor extends Person {
    private final DFAgentDescription dfd;
    public boolean ready;

    public Competitor(String id) {
        super(id);
        dfd = new DFAgentDescription();
    }

    @Override
    public void behaviours() {
        // Setup behaviours
        SequentialBehaviour sb = new SequentialBehaviour();
        sb.addSubBehaviour(new CompetitorSendRequest(this));
        sb.addSubBehaviour(new CompetitorReceiveGuess(this));
        addBehaviour(sb);
    }

    public void startRound() {
        ready = false;
        behaviours();
    }

    @Override
    public void endRound(int price) {
        super.endRound(price);
        ready = false;
    }

    public void setup() {
        // Register agent in yellow pages
        try {
            ServiceDescription sd = new ServiceDescription();
            sd.setType("competitor");
            sd.setName(getLocalName());
            dfd.setName(getAID());
            dfd.addServices(sd);
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
        }
    }

    public HashMap<String, Integer> getRequest() {
        return teamAffinity;
    }

    public Integer getGuess() { return guess; }

    public void finalGuess() {
        float maxConfidence = 0.0f;
        float currentGuess = 0.0f;

        for (Map.Entry<String, Integer> entry : guesses.entrySet()) {
            currentGuess += entry.getValue() * confidence.get(entry.getKey());
            maxConfidence += confidence.get(entry.getKey());
        }

        guess = Math.round(currentGuess / maxConfidence);
        ready = true;
    }
}
