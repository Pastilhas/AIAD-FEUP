package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
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

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("competitor");
        dfd.addServices(sd);

        try {
            DFAgentDescription[] res = DFService.search(audience, dfd);
            for (DFAgentDescription re : res) {
                AID rcv = re.getName();
                msg.setContent(Integer.toString(audience.getGuess(rcv)));
                msg.addReceiver(rcv);
                audience.send(msg);
            }
        } catch (FIPAException e) {
            System.out.println("!!Exception:" + e.getMessage() + "\n!!" + e.getCause());
        }

        finished = true;
    }

    @Override
    public boolean done() { return finished; }
}
