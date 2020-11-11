package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class AudienceShareGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceShareGuess(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(Integer.toString(audience.getGuess(null)));

        DFAgentDescription[] res = audience.getAudience();
        for (DFAgentDescription re : res) {
            AID rcv = re.getName();
            msg.addReceiver(rcv);
            audience.send(msg);
        }

        finished = true;
    }

    @Override
    public boolean done() {
        return finished;
    }
}
