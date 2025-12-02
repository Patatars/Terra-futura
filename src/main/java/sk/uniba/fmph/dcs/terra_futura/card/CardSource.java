package sk.uniba.fmph.dcs.terra_futura.card;

import sk.uniba.fmph.dcs.terra_futura.enums.Deck;

/**
 * Source of a card (deck and index).
 *
 * @param deck deck type
 * @param index card index in deck
 */
public record CardSource(Deck deck, int index) {
}