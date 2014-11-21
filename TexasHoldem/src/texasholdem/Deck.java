package texasholdem;

import java.util.Arrays;

/**
 * Represents a deck of 52 cards.
 * The deck appears shuffled by drawing random cards.
 * Each card can only be drawn once.
 * @author John Wardell
 *
 */
public class Deck {
	private Card[] cards;
	private int remainingCards = 52;
	
	/**
	 * The <code>Deck</code> constructor.
	 * Fills the <code>Deck</code> with <code>Card</code> objects.
	 */
	public Deck() {
		cards = new Card[52];
		
		fillDeck();
	}
	
	/**
	 * Gets all the cards in order.
	 * @return the array of cards
	 */
	public Card[] getCards() {
		return cards;
	}
	
	/**
	 * Draws 1 <code>Card</code> in random order.
	 * Decrements remainingCards.
	 * @return the <code>Card</code> drawn
	 */
	public Card drawCard() {
		int i = (int) (Math.random() * remainingCards);
		Card card = cards[i];
		remainingCards--;
		cards[i] = cards[remainingCards];
		return card;	
	}
	
	/**
	 * Fills the <code>Deck</code> with 52 <code>Card</code> objects.
	 */
	public void fillDeck() {
		remainingCards = 52;
		
		int i = 0;
		for (Suit s : Suit.values()) {
			for (Number n : Number.values()) {
				cards[i] = new Card(s, n);
				i++;
			}
		}
		
	}
	
	/**
	 * The main method is for testing purposes only.
	 * @param args
	 */
	public static void main(String[] args) {
		Deck deck = new Deck();
		
		System.out.println(Arrays.toString(deck.getCards()));
		
		for (int j = 0; j < 6; j++) {
		    Card[] cards = new Card[7];
		
		    for (int i = 0; i < 7; i++)
			    cards[i] = deck.drawCard();
		
		    System.out.println(Arrays.toString(cards));
		}
		
		System.out.println(Arrays.toString(deck.getCards()));
	}
}
