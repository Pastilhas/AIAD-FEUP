package agents;

import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Audience extends Person {
    private final boolean selfconfidence;
    private final HashMap<String, Integer> itemPrice;
    private final DFAgentDescription dfd;
    private int guess;

    public Audience(String id, boolean selfconfidence) {
        super(id);
        this.selfconfidence = selfconfidence;
        itemPrice = new HashMap<>();
        dfd = new DFAgentDescription();
    }

    public void addItem(int id, Integer price) {
        if(price != null)
            itemPrice.put(Integer.toString(id), price);
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

        // Setup behaviours
        SequentialBehaviour sb = new SequentialBehaviour();
        //sb.addSubBehaviour();
        addBehaviour(sb);
    }

    public int getGuess() {
        return guess;
    }
}
