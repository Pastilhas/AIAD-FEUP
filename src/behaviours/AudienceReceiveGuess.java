package behaviours;

import agents.Audience;
import jade.core.behaviours.SimpleBehaviour;

public class AudienceReceiveGuess extends SimpleBehaviour {
    private boolean finished = false;
    private final Audience audience;

    public AudienceReceiveGuess(Audience audience) {
        this.audience = audience;
    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() { return finished; }
}
