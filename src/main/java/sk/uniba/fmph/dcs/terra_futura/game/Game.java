package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.deck.Pile;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.GameState;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.*;

/**
 * Game class manages game state and player turns.
 */
public class Game implements TerraFuturaInterface {
    private GameState state;
    private final Map<Integer, Grid> grids;
    private final Pile pile;
    private final List<Integer> playerOrder;
    private final Map<Integer, Integer> playerIndex;
    private final int initialStartingPlayer;
    private final int numberOfPlayers;
    private int currentPlayerOnTurn;
    private int turnNumber;
    private Integer playerAwaitingReward;
    private final Set<Integer> pendingActivationPatternPlayers;
    private final Set<Integer> finalActivationsCompleted;
    private final Set<Integer> pendingScoringPlayers;
    private boolean finalActivationPhaseStarted;
    private boolean scoringPhaseStarted;

    /**
     * Creates a new game with given players and starting player.
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
        this.playerAwaitingReward = null;
        this.pendingActivationPatternPlayers = new LinkedHashSet<>(playerOrder);
        this.finalActivationsCompleted = new HashSet<>();
        this.pendingScoringPlayers = new LinkedHashSet<>(playerOrder);
        this.finalActivationPhaseStarted = false;
        this.scoringPhaseStarted = false;
    }

    private boolean isPlayerNotOnTurn(final int playerId) {
        return playerId != currentPlayerOnTurn;
    }

    private void advanceToNextPlayer() {
        int currentIndex = playerIndex.get(currentPlayerOnTurn);
        int nextIndex = (currentIndex + 1) % playerOrder.size();
        currentPlayerOnTurn = playerOrder.get(nextIndex);
        if (currentPlayerOnTurn == initialStartingPlayer) {
            turnNumber++;
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

    private void startFinalActivationPhase() {
        if (finalActivationPhaseStarted) {
            return;
        }
        finalActivationPhaseStarted = true;
        turnNumber = Math.max(turnNumber, 10);
        state = GameState.SELECT_ACTIVATION_PATTERN;
        if (!pendingActivationPatternPlayers.contains(currentPlayerOnTurn)) {
            Integer fallback = firstPendingPlayer(pendingActivationPatternPlayers);
            if (fallback != null) {
                currentPlayerOnTurn = fallback;
            } else {
                startScoringPhase();
            }
        }
    }

    private void startScoringPhase() {
        if (scoringPhaseStarted) {
            return;
        }
        scoringPhaseStarted = true;
        Integer next = firstPendingPlayer(pendingScoringPlayers);
        if (next == null) {
            state = GameState.FINISH;
            return;
        }
        currentPlayerOnTurn = next;
        state = GameState.SELECT_SCORING_METHOD;
    }

    private void ensureGridExists(final int playerId) {
        if (!grids.containsKey(playerId)) {
            throw new IllegalStateException("Grid not found for player " + playerId);
        }
    }

    private void markRewardPending(final Integer playerId) {
        playerAwaitingReward = playerId;
    }

    private void endPlayerGridTurn(final int playerId) {
        Grid grid = grids.get(playerId);
        if (grid != null) {
            grid.endTurn();
        }
    }

    private void handleFinalPhaseTurnFinished(final int playerId) {
        finalActivationsCompleted.add(playerId);

        if (!pendingActivationPatternPlayers.isEmpty()) {
            Integer next = firstPendingPlayer(pendingActivationPatternPlayers);
            if (next != null) {
                currentPlayerOnTurn = next;
                state = GameState.SELECT_ACTIVATION_PATTERN;
                return;
            }
        }

        if (finalActivationsCompleted.size() >= numberOfPlayers) {
            startScoringPhase();
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
        Grid grid = grids.get(playerId);
        if (grid == null || !grid.canPutCard(destination)) {
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
            final List<GridPosition> pollution) {
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
        grid.setActivated(cardPosition);
        if (card.hasAssistance() && turnNumber <= 9) {
            markRewardPending(playerId);
            state = GameState.SELECT_REWARD;
        } else {
            markRewardPending(null);
            state = GameState.ACTIVATE_CARD;
        }
    }

    @Override
    public void selectReward(final int playerId, final Resource resource) {
        if (state != GameState.SELECT_REWARD) {
            throw new IllegalStateException("selectReward allowed only in SELECT_REWARD state");
        }
        if (playerAwaitingReward == null || playerAwaitingReward != playerId) {
            throw new IllegalStateException("Player " + playerId
                    + " is not allowed to select a reward now");
        }
        markRewardPending(null);
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
        endPlayerGridTurn(playerId);
        if (turnNumber <= 9) {
            advanceToNextPlayer();
            if (turnNumber <= 9) {
                state = GameState.TAKE_CARD_NO_CARD_DISCARDED;
            } else {
                startFinalActivationPhase();
            }
            return true;
        }
        handleFinalPhaseTurnFinished(playerId);
        return true;
    }

    @Override
    public boolean selectActivationPatter(final int playerId, final int card) {
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
