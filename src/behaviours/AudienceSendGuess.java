package behaviours;

import java.io.IOException;

import agents.Audience;
import agents.MyAgent.Phase;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class AudienceSendGuess extends SendMsgBehaviour {
    private static final long serialVersionUID = 7989267590951846547L;

    public AudienceSendGuess(Audience a) {
        super(a);
    }

    @Override
    public void action() {
        if (agent.phase != Phase.SEND)
            return;
        super.action();
        agent.phase = Phase.READY;
    }

    @Override
    protected ACLMessage getMessage(AID rcv) throws IOException {
        Audience p = (Audience) agent;
        int performative;
        if (p.getGuess(rcv.getLocalName()) == null) {
            p.addEdge(rcv.getLocalName(), "null");
            performative = ACLMessage.REFUSE;
        } else {
            p.addEdge(rcv.getLocalName(), p.getGuess(rcv.getLocalName()).toString());
            performative = ACLMessage.AGREE;
        }
        ACLMessage msg = new ACLMessage(performative);
        msg.setContentObject(p.getGuess(rcv.getLocalName()));
        msg.addReceiver(rcv);
        agent.logger.info(String.format("SENT GUESS %11d TO %14s", p.getGuess(rcv.getLocalName()), rcv.getLocalName()));
        return msg;
    }

    @Override
    protected DFAgentDescription[] chooseReceivers() {
        return agent.getCompetitor();
    }
}