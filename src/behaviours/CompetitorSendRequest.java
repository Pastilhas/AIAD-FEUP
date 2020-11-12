package behaviours;

import agents.Competitor;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class CompetitorSendRequest extends SimpleBehaviour {
    private final Competitor competitor;
    private boolean finished = false;

    public CompetitorSendRequest(Competitor competitor) {
        this.competitor = competitor;
    }


    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        DFAgentDescription[] res = competitor.getAudience();
        for (DFAgentDescription re : res) {
            try {
                AID rcv = re.getName();
                if (competitor.getLocalName().equals(rcv.getLocalName())) continue;
                msg.setContentObject(competitor.getRequest());
                msg.addReceiver(rcv);
                competitor.logger.info("Competitor " + competitor.getLocalName() + " SENT request TO agent: " + rcv.getLocalName());

                competitor.send(msg);
            } catch (IOException e) {
                System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
            }
        }

        finished = true;
    }

    @Override
    public boolean done() {
        return finished;
    }
}
