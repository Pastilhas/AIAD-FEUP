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
        if (audience.getGuess(null) == null) return;

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        int guess = audience.getGuess(null);
        msg.setContent(Integer.toString(guess));

        DFAgentDescription[] res = audience.getAudience();
        for (DFAgentDescription re : res) {
            AID rcv = re.getName();
            if(audience.getLocalName().equals(rcv.getLocalName())) continue;
            msg.addReceiver(rcv);
            System.out.println("Audience " + audience.getLocalName() + " SENT guess: " + guess + " TO agent: " + rcv.getLocalName());
            audience.send(msg);
        }

        finished = true;
    }

    @Override
    public boolean done() {
        return finished;
    }
}
