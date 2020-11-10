package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashMap;

public class Person extends Agent {
    final String id;
    final HashMap<String, Integer> teamAffinity;

    public Person(String id) {
        this.id = id;
        teamAffinity = new HashMap<>();
    }

    public void addTeam(String id, Integer value) {
        if(value != null)
            teamAffinity.put(id, value);
    }

    public int getCompetitorLength(){ return getTypeLength("competitor"); }

    public int getAudienceLength(){ return getTypeLength("audience"); }

    public int getTypeLength(String type) {
        DFAgentDescription[] res = null;

        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);
            res = DFService.search(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return res.length;
    }
}
