package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.actions.ProcessAction;
import sk.uniba.fmph.dcs.terra_futura.actions.ProcessActionAssistance;
import sk.uniba.fmph.dcs.terra_futura.actions.SelectReward;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.deck.Pile;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.GameState;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Game class manages game state and player turns.
 */
public final class Game implements TerraFuturaInterface {
    private static final int LAST_REGULAR_TURN = 9;

    private GameState state;
    private final Map<Integer, Grid> grids;
    private final Pile pile;
    private final List<Integer> playerOrder;
    private final Map<Integer, Integer> playerIndex;
    private final int initialStartingPlayer;
    private final int numberOfPlayers;

    private int currentPlayerOnTurn;
    private int turnNumber;

    private final SelectReward selectReward;

    private final Set<Integer> pendingActivationPatternPlayers;
    private final Set<Integer> pendingScoringPlayers;
    private boolean finalActivationPhaseStarted;
    private boolean scoringPhaseStarted;

    /**
     * Creates a new game with given players and starting player.
     *
     * @param players array of player IDs
     * @param grids map of player IDs to their grids
     * @param pile the card pile
     * @param startingPlayer ID of starting player
     */
    public Game(final int[] players, final Map<Integer, Grid> grids,
                final Pile pile, final int startingPlayer) {
        if (players == null || players.length == 0) {
            throw new IllegalArgumentException("At least one player is required");
        }
        boolean startingPlayerFound = false;
        for (int playerId : players) {
            if (playerId == startingPlayer) {
                startingPlayerFound = true;
                break;
            }
        }
        if (!startingPlayerFound) {
            throw new IllegalArgumentException("Starting player must be part of the game");
        }
        this.grids = new HashMap<>(grids);
        this.pile = pile;
        this.initialStartingPlayer = startingPlayer;

        this.playerOrder = new ArrayList<>();
        for (int playerId : players) {
            this.playerOrder.add(playerId);
        }

        Collections.sort(this.playerOrder);

        this.playerIndex = new HashMap<>();
        for (int i = 0; i < playerOrder.size(); i++) {
            playerIndex.put(playerOrder.get(i), i);
        }

        this.numberOfPlayers = playerOrder.size();
        this.currentPlayerOnTurn = startingPlayer;
        this.turnNumber = 1;
        this.state = GameState.TAKE_CARD_NO_CARD_DISCARDED;

        this.selectReward = new SelectReward();
        this.pendingActivationPatternPlayers = new LinkedHashSet<>(playerOrder);
        this.pendingScoringPlayers = new LinkedHashSet<>(playerOrder);
        this.finalActivationPhaseStarted = false;
        this.scoringPhaseStarted = false;
    }

    private boolean isPlayerNotOnTurn(final int playerId) {
        return playerId != currentPlayerOnTurn;
    }

    private void ensureGridExists(final int playerId) {
        if (!grids.containsKey(playerId)) {
            throw new IllegalArgumentException("Player " + playerId + " does not exist");
        }
    }


    private Integer firstPendingPlayer(final Set<Integer> pendingPlayers) {
        for (Integer id : playerOrder) {
            if (pendingPlayers.contains(id)) {
                return id;
            }
        }
        return null;
    }

    private void advanceToNextPlayer() {
        int currentIndex = playerIndex.get(currentPlayerOnTurn);
        int nextIndex = (currentIndex + 1) % numberOfPlayers;
        currentPlayerOnTurn = playerOrder.get(nextIndex);
        if (currentPlayerOnTurn == initialStartingPlayer) {
            turnNumber++;
        }
    }

    private void startFinalActivationPhase() {
        if (finalActivationPhaseStarted) {
            return;
        }
        finalActivationPhaseStarted = true;
        state = GameState.SELECT_ACTIVATION_PATTERN;

        Integer next = firstPendingPlayer(pendingActivationPatternPlayers);
        if (next != null) {
            currentPlayerOnTurn = next;
        } else {
            startScoringPhase();
        }
    }

    private void startScoringPhase() {
        if (scoringPhaseStarted) {
            return;
        }
        scoringPhaseStarted = true;
        state = GameState.SELECT_SCORING_METHOD;

        Integer next = firstPendingPlayer(pendingScoringPlayers);
        if (next != null) {
            currentPlayerOnTurn = next;
        } else {
            state = GameState.FINISH;
        }
    }

    @Override
    public boolean takeCard(final int playerId, final CardSource source,
            final GridPosition destination) {
        if (isPlayerNotOnTurn(playerId)) {
            return false;
        }
        if (state != GameState.TAKE_CARD_NO_CARD_DISCARDED
                && state != GameState.TAKE_CARD_CARD_DISCARDED) {
            return false;
        }
        ensureGridExists(playerId);
        Grid grid = grids.get(playerId);
        if (!grid.canPutCard(destination)) {
            return false;
        }
        Card card = pile.takeCard(source.index());
        if (card == null) {
            return false;
        }
        try {
            grid.putCard(destination, card);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        state = GameState.ACTIVATE_CARD;
        return true;
    }

    @Override
    public boolean discardLastCardInDeck(final int playerId, final Deck deck) {
        if (isPlayerNotOnTurn(playerId)) {
            return false;
        }
        if (state != GameState.TAKE_CARD_NO_CARD_DISCARDED) {
            return false;
        }
        boolean discarded = pile.discardCard();
        if (discarded) {
            state = GameState.TAKE_CARD_CARD_DISCARDED;
        }
        return discarded;
    }

    @Override
    public void activateCard(final int playerId, final GridPosition cardPosition,
            final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs,
            final List<GridPosition> pollution, final Optional<Integer> otherPlayer,
                             final Optional<Card> otherCard) {
        if (state != GameState.ACTIVATE_CARD) {
            throw new IllegalStateException("activateCard allowed only in ACTIVATE_CARD state");
        }
        if (isPlayerNotOnTurn(playerId)) {
            throw new IllegalStateException("Only the active player can activate cards");
        }
        ensureGridExists(playerId);
        Grid grid = grids.get(playerId);
        if (!grid.canBeActivated(cardPosition)) {
            throw new IllegalStateException("Card cannot be activated at position " + cardPosition);
        }
        Optional<Card> maybeCard = grid.getCard(cardPosition);
        Card card = maybeCard.orElseThrow(() ->
                new IllegalStateException("No card present at position " + cardPosition));

        boolean activationSuccess;

        if (otherCard.isPresent() && otherPlayer.isPresent() && card.hasAssistance() && turnNumber <= LAST_REGULAR_TURN) {
            ProcessActionAssistance process = new  ProcessActionAssistance();
            activationSuccess = process.activateCard(card, grid, otherPlayer.get(), otherCard.get(), inputs, outputs, pollution);
            if (activationSuccess) {
                grid.setActivated(cardPosition);
                List<Resource> rewards = inputs.stream().map(Pair::getLeft).distinct().toList();
                if (selectReward.setReward(otherPlayer.get(), otherCard.get(), rewards)) {
                    state = GameState.SELECT_REWARD;
                } else {
                    state = GameState.ACTIVATE_CARD;
                }
            }
        } else {
            ProcessAction process = new  ProcessAction();
            activationSuccess = process.activateCard(card, grid, inputs, outputs, pollution);
            if (activationSuccess) {
                grid.setActivated(cardPosition);
                state = GameState.ACTIVATE_CARD;
            }
        }

        if (!activationSuccess) {
            throw new IllegalStateException("Card cannot be activated at position " + cardPosition);
        }
    }

    @Override
    public void selectReward(final int playerId, final Resource resource) {
        if (state != GameState.SELECT_REWARD) {
            throw new IllegalStateException("selectReward allowed only in SELECT_REWARD state");
        }
        if (!selectReward.canSelectReward(resource)) {
            throw new IllegalStateException("Select reward can only be selected in SELECT_REWARD");
        }
        selectReward.selectReward(resource);
        state = GameState.ACTIVATE_CARD;

    }

    @Override
    public boolean turnFinished(final int playerId) {
        if (isPlayerNotOnTurn(playerId)) {
            return false;
        }
        if (state != GameState.ACTIVATE_CARD) {
            return false;
        }
        ensureGridExists(playerId);
        Grid grid = grids.get(playerId);
        grid.endTurn();

        if (!finalActivationPhaseStarted) {
            advanceToNextPlayer();

            if (turnNumber > LAST_REGULAR_TURN) {
                startFinalActivationPhase();
            } else {
                state = GameState.TAKE_CARD_NO_CARD_DISCARDED;
            }
        }
        return true;
    }

    @Override
    public boolean selectActivationPattern(final int playerId, final int card) {
        if (state != GameState.SELECT_ACTIVATION_PATTERN) {
            return false;
        }
        if (isPlayerNotOnTurn(playerId)) {
            return false;
        }
        if (!pendingActivationPatternPlayers.contains(playerId)) {
            return false;
        }
        pendingActivationPatternPlayers.remove(playerId);
        state = GameState.ACTIVATE_CARD;
        return true;
    }

    @Override
    public boolean selectScoring(final int playerId, final int card) {
        if (state != GameState.SELECT_SCORING_METHOD) {
            return false;
        }
        if (isPlayerNotOnTurn(playerId)) {
            return false;
        }
        if (!pendingScoringPlayers.contains(playerId)) {
            return false;
        }
        pendingScoringPlayers.remove(playerId);
        if (pendingScoringPlayers.isEmpty()) {
            state = GameState.FINISH;
        } else {
            Integer next = firstPendingPlayer(pendingScoringPlayers);
            if (next != null) {
                currentPlayerOnTurn = next;
            }
        }
        return true;
    }

    public GameState getState() {
        return state;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getCurrentPlayerOnTurn() {
        return currentPlayerOnTurn;
    }

    public int getStartingPlayer() {
        return initialStartingPlayer;
    }
}
