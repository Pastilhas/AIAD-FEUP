package agents;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.core.Agent;
import sajas.domain.DFService;

public class WorldAgent extends Agent {
    WorldAgent() {

    }

    @Override
    protected void setup() {

    }

    public DFAgentDescription[] getCompetitor() {
        return getService("competitor");
    }

    public DFAgentDescription[] getAudience() {
        return getService("audience");
    }

    DFAgentDescription[] getService(String type) {
        DFAgentDescription[] res = null;
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            res = DFService.search(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
            System.exit(3);
        }
        return res;
    }
}
