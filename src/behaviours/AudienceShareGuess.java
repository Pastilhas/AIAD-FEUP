package behaviours;

import agents.Audience;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
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
        msg.setContent(Integer.toString(audience.getGuess()));

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("audience");
        dfd.addServices(sd);

        try {
            DFAgentDescription[] res = DFService.search(audience, dfd);
            for(int i = 0; i < res.length; i++) {
                AID rcv = res[i].getName();
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
