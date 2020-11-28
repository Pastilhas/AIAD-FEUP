package behaviours;

import java.io.IOException;

import agents.Audience;
import agents.Person.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class AudienceShareGuess extends SendMsgBehaviour {
    public AudienceShareGuess(Audience a) {
        super(a);
    }

    @Override
    public void action() {
        if (agent.phase != Phase.SHARE)
            return;
        super.action();
        agent.phase = Phase.WAIT;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Audience p = (Audience) agent;
        int performative = ACLMessage.INFORM;
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getGuess(null));
        msg.addReceiver(rcv);
        agent.logger.info(String.format("AUDIENCE   %10s SENT GUESS     %7d TO   %10s", agent.getLocalName(),
                p.getGuess(null), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return agent.getAudience();
    }
}