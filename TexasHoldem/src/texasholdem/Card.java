package texasholdem;

/**
 * Each <code>Card</code> object represents a playing card
 * from a standard card deck.
 * Each card has a suit represented by the suit and a value
 * represented by the number.
 * @author John Wardell
 *
 */
public class Card {
	private final Suit suit;
	private final Number number;
	
	/**
	 * Constructs <code>Card</code> with supplied suit and value.
	 * @param suit 
	 * @param number
	 */
	public Card(Suit suit, Number number) {
		this.suit = suit;
		this.number = number;
	}
	
	/**
	 * A no argument constructor
	 */
	public Card() {
		suit = Suit.CLUBS;
		number = Number.ACE;
	}
	
	/**
	 * Gets this card's suit.
	 * @return the card's suit
	 */
	public Suit getSuit() {
		return suit;
	}
	
	/**
	 * Gets this card's number (value)
	 * @return the card's value
	 */
	public Number getNumber() {
		return number;
	}
	
	/**
	 * Checks for equality 
	 * @param otherCard
	 * @return true if cards are the same card
	 */
	public boolean equals(Card otherCard) {
		if ((this.suit == otherCard.suit) && (this.number == otherCard.number))
			return true;
		
		return false;
	}
	 /**
	  * Creates and returns a string description of this card.
	  */
	public String toString() {
		String numSuit = (number.toString() + " " + suit.toString());
		return numSuit;
	}
	
	/**
	 * Compares card values.
	 * @param otherCard the other <code>Card</code>
	 * 
	 */
	public int compareNum(Card otherCard) {
		return number.compareTo(otherCard.number);
	}
	
	/**
	 * Returns true if the cards have the same value,
	 * unless they are the same exact card.
	 * @param otherCard the other <code>Card</code>
	 * 
	 */
	public boolean isSameNum(Card otherCard) {
		if (this.equals(otherCard)) return false;
		if (number.compareTo(otherCard.number) == 0) return true;
		return false;
	}
	
	/**
	 * Returns true if the value of otherCard directly follows
	 * this card.
	 * In case of ace to two, this method returns false.
	 * @param otherCard the other <code>Card</code>
	 * 
	 */
	public boolean isNumNext(Card otherCard) {
		int compareNum = number.compareTo(otherCard.number);
		if (compareNum == -1) return true;
		return false;
	}
	
	/**
	 * Returns true if the suit of otherCard matches this card,
	 * unless they are the exact same card.
	 * @param otherCard
	 * @return true if cards are the same suit
	 */
	public boolean isSameSuit(Card otherCard) {
		if (this.equals(otherCard)) return false; 
		if (suit.compareTo(otherCard.suit) == 0) return true;
		return false;
	}
	
	/**
	 * The main method is for testing purposes only.
	 * @param args
	 */
	public static void main(String[] args) {
		Card card1 = new Card(Suit.CLUBS, Number.FIVE);
		Card card2 = new Card(Suit.HEARTS, Number.THREE);
		Card card3 = new Card(Suit.SPADES, Number.ACE);
		Card card4 = new Card(Suit.DIAMONDS, Number.TWO);
		Card card5 = new Card();
		Card card6 = new Card();
		System.out.println(card5.equals(card6));
		System.out.println(card1.equals(card2));
		System.out.println(card6.isNumNext(card4));
		System.out.println(card1.isSameSuit(card5));
		System.out.println(card3.toString());
	}

}
