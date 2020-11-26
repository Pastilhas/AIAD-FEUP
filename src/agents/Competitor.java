package agents;

import behaviours.CompetitorReceiveGuess;
import behaviours.CompetitorSendRequest;
import sajas.core.behaviours.SequentialBehaviour;

public class Competitor extends Person {
    public Competitor(String id, long time) {
        super(id, time);
    }

    @Override
    protected void setup() {
        setupAgent("competitor");
    }

    @Override
    void behaviours() {
        SequentialBehaviour sb = new SequentialBehaviour();
        sb.addSubBehaviour(new CompetitorSendRequest(this));
        sb.addSubBehaviour(new CompetitorReceiveGuess(this));
        addBehaviour(sb);
    }

    @Override
    public void finalGuess() {
        float[] a = finalGuessCalc();
        float currentGuess = a[0];
        float maxConfidence = a[1];

        guess = Math.round(currentGuess / maxConfidence);
        ready = true;
    }
}