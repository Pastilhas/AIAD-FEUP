package agents;

import behaviours.CompetitorReceiveGuess;
import behaviours.CompetitorSendRequest;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class Competitor extends Person {
    public Competitor(String id) {
        super(id);
    }

    @Override
    protected void setup() {
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

    @Override
    void behaviours() {
        SequentialBehaviour sb = new SequentialBehaviour();
        sb.addSubBehaviour(new CompetitorSendRequest(this));
        sb.addSubBehaviour(new CompetitorReceiveGuess(this));
        addBehaviour(sb);
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        float currentGuess = a[0];
        float maxConfidence = a[1];

        guess = Math.round(currentGuess / maxConfidence);
        ready = true;
    }
}