package world;

import java.util.HashMap;

import agents.Competitor;

public class WorldData {
    private final HashMap<String, Integer> winners;
    private final HashMap<Integer, Integer[]> guesses;
    private WorldModel world;

    public WorldData(WorldModel world) {
        this.world = world;
        winners = new HashMap<>();
        guesses = new HashMap<>();
    }

    public void newRound() {
        /*
         * putGuesses(); putWinner();
         */
    }

    private void putGuesses() {
        Integer min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int price = world.worldAgent.getPrice();
        for (Competitor c : world.competitors) {
            Integer g = c.getGuess();
            if (g == null)
                continue;
            g = g - price;
            if (g < min)
                min = g;
            if (g > max)
                max = g;
        }
        guesses.put(world.round, new Integer[] { min, max });
    }

    private void putWinner() {
        String winner = null;
        Integer min = Integer.MAX_VALUE;
        int price = world.worldAgent.getPrice();
        for (Competitor c : world.competitors) {
            Integer g = c.getGuess();
            if (g == null)
                continue;
            g = g - price;
            if (g < min) {
                min = g;
                winner = c.getLocalName();
            }
        }

        winners.merge(winner, 1, Integer::sum);
    }
}
