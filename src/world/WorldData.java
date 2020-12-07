package world;

import java.util.HashMap;

import agents.Competitor;

public class WorldData {
    private final HashMap<String, Integer> winners;
    private final HashMap<Integer, Integer[]> guesses;
    private World world;

    public WorldData(World world) {
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
        int price = world.getPrice();
        for (Competitor c : world.getCompetitors()) {
            Integer g = c.getGuess();
            if (g == null)
                continue;
            g = g - price;
            if (g < min)
                min = g;
            if (g > max)
                max = g;
        }
        guesses.put(world.getRound(), new Integer[] { min, max });
    }

    private void putWinner() {
        String winner = null;
        Integer min = Integer.MAX_VALUE;
        int price = world.getPrice();
        for (Competitor c : world.getCompetitors()) {
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
