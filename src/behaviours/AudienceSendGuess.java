package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class AudienceSendGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceSendGuess(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        DFAgentDescription[] res = audience.getCompetitor();
        for (DFAgentDescription re : res) {
            AID rcv = re.getName();
            msg.setContent(Integer.toString(audience.getGuess(rcv)));
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
