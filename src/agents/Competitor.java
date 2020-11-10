package agents;

import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Competitor extends Person {
    private final DFAgentDescription dfd;

    public Competitor(String id) {
        super(id);
        dfd = new DFAgentDescription();
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

        // Setup behaviours
        SequentialBehaviour sb = new SequentialBehaviour();
        //sb.addSubBehaviour();
        addBehaviour(sb);
    }
}
