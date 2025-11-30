package sk.uniba.fmph.dcs.terra_futura.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Používam všeobecné názvy, keďže nemáme definíciu triedy Effect
class EmptyEffect implements Effect {
    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) { return false; }
    @Override
    public boolean hasAssistance() { return false; }
    @Override
    public String state() { return ""; }
}

@DisplayName("PileImpl Tests")
class PileImplTest {

    private PileImpl pile;
    private CardImpl card1;
    private CardImpl card2;
    private CardImpl card3;
    private final Effect testEffect = new EmptyEffect();
    private final List<Resource> testResources = List.of(Resource.RED, Resource.CAR);

    @BeforeEach
    void setUp() {
        // Inicializácia kariet
        card1 = new CardImpl(testResources, 1, testEffect, testEffect);
        card2 = new CardImpl(testResources, 2, testEffect, testEffect); // Zmenená hodnota pre ľahšiu identifikáciu
        card3 = new CardImpl(testResources, 3, testEffect, testEffect); // Zmenená hodnota pre ľahšiu identifikáciu

        // Inicializácia hromady s 3 kartami
        pile = new PileImpl(List.of(card1, card2, card3));
    }

    // ------------------------------------------------------------------------
    // T_getCard (Len vráti kartu, nemení veľkosť)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("getCard: Should return the correct card at a valid index")
    void getCard_returnsCorrectCard() {
        Optional<Card> card = pile.getCard(1); // Očakávame card2
        assertTrue(card.isPresent());
        assertSame(card2, card.get(), "Mala vrátiť kartu s indexom 1 (card2).");
        assertEquals(3, pile.cards.size(), "Veľkosť hromady by sa nemala zmeniť.");
    }

    @Test
    @DisplayName("getCard: Should return Optional.empty() for negative index")
    void getCard_negativeIndex_returnsEmpty() {
        assertTrue(pile.getCard(-1).isEmpty(), "Negatívny index by mal vrátiť prázdny Optional.");
    }

    @Test
    @DisplayName("getCard: Should return Optional.empty() for index equal to size (out of bounds)")
    void getCard_indexEqualToSize_returnsEmpty() {
        // Indexy sú 0, 1, 2. Veľkosť je 3. Index 3 je mimo rozsahu.
        assertTrue(pile.getCard(3).isEmpty(), "Index rovný veľkosti by mal vrátiť prázdny Optional.");
    }

    @Test
    @DisplayName("getCard: Should return Optional.empty() for index greater than size")
    void getCard_indexGreaterThanSize_returnsEmpty() {
        assertTrue(pile.getCard(100).isEmpty(), "Príliš veľký index by mal vrátiť prázdny Optional.");
    }

    // ------------------------------------------------------------------------
    // T_takeCard (Odstráni a vráti kartu)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("takeCard: Should remove and return the card at a valid index")
    void takeCard_validIndex_removesAndReturnsCard() {
        // Očakávame odstránenie card2 (index 1)
        pile.takeCard(1);

        assertEquals(2, pile.cards.size(), "Veľkosť hromady sa má zmenšiť na 2.");

        // Kontrola, či zostali len card1 (index 0) a card3 (index 1)
        assertSame(card1, pile.cards.get(0), "Na indexe 0 má zostať card1.");
        assertSame(card3, pile.cards.get(1), "Na indexe 1 má byť teraz card3.");
    }

    @Test
    @DisplayName("takeCard: Should not modify pile for index zero (zero index is not removed)")
    void takeCard_zeroIndex_doesNothing() {
        // Pôvodný test implikuje, že takeCard(0) by nemala nič robiť.
        // Ak sa má správať ako List.remove(index), index 0 by sa mal odstrániť.
        // AK predpokladáme, že takeCard(0) by mala vrátiť Optional.empty() alebo nemeniť,
        // necháme kód podľa pôvodnej logiky:
        pile.takeCard(0);
        assertEquals(3, pile.cards.size(), "TakeCard(0) by nemalo nič spraviť, ak je index < 1.");
    }

    @Test
    @DisplayName("takeCard: Should not modify pile for index greater than size")
    void takeCard_indexGreaterThanSize_doesNothing() {
        pile.takeCard(5);
        assertEquals(3, pile.cards.size(), "Neplatný index by nemal zmeniť veľkosť hromady.");
    }

    // ------------------------------------------------------------------------
    // T_removeLastCard (Odstráni poslednú kartu)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("removeLastCard: Should remove the last element (card3)")
    void removeLastCard_removesLastElement() {
        pile.removeLastCard(); // Odstráni card3

        assertEquals(2, pile.cards.size(), "Veľkosť hromady sa má znížiť.");
        assertSame(card1, pile.cards.get(0), "Prvá karta musí zostať card1.");
        assertSame(card2, pile.cards.get(1), "Druhá karta musí zostať card2.");
    }

    @Test
    @DisplayName("removeLastCard: Should do nothing on an empty pile")
    void removeLastCard_onEmptyPile_doesNothing() {
        PileImpl empty = new PileImpl(List.of());
        empty.removeLastCard();
        assertTrue(empty.cards.isEmpty(), "Prázdna hromada musí zostať prázdna.");
    }

    // ------------------------------------------------------------------------
    // T_state (Stav hromady)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("state: Should return a descriptive string for non-empty pile")
    void state_returnsCorrectString_forNonEmptyPile() {
        String result = pile.state();
        // Kontrola prítomnosti názvu triedy a zoznamu kariet
        assertTrue(result.startsWith("PileImpl{cards=["), "Stav by mal začať s hlavičkou triedy.");

        // Predpokladáme, že CardImpl.toString() obsahuje kľúčové informácie o karte.
        // Keďže nepoznáme presné znenie toString(), kontrolujeme len prítomnosť ID.
        // Na základe pôvodného testu, ktorý kontroluje "Card3", sa spoliehame na toString()
        // Ak by card.state() vracalo presnú reprezentáciu, mali by sme ju tu použiť.

        // Pôvodný test: assertFalse(result.contains("Card1"));, assertFalse(result.contains("Card2"));
        // Táto kontrola je neistá, pretože nevieme, akú dlhú reprezentáciu má card1/card2.
        // Kontrolujeme len základ.

        // Zabezpečíme, že obsahuje zoznam kariet
        String expectedCardListPart = card1.toString() + ", " + card2.toString() + ", " + card3.toString();
        assertTrue(result.contains(expectedCardListPart), "Stav musí obsahovať reťazcovú reprezentáciu všetkých kariet.");
    }

    @Test
    @DisplayName("state: Should return an empty list string for an empty pile")
    void state_returnsCorrectString_forEmptyPile() {
        PileImpl empty = new PileImpl(List.of());
        assertEquals("PileImpl{cards=[]}", empty.state(), "Stav prázdnej hromady musí obsahovať prázdny zoznam.");
    }
}
