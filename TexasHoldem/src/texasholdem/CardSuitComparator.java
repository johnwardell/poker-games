package texasholdem;

import java.util.Comparator;

/**
 * A <code>Comparator</code> that will put <code>Card</code>
 * sets in order by suit.
 * * !!Note: this comparator imposes orderings that are inconsistent with equals!!
 * @author John Wardell
 *
 * @param <T> the type must be <code>Card</code>
 */
public class CardSuitComparator<T> implements Comparator<Card> {
	
	/**
	 * Compares two cards by suit.
	 * @param card1 the first card
	 * @param card2 the second card
	 */
	public int compare(Card card1, Card card2) {
		int i = card1.getSuit().compareTo(card2.getSuit());
		return i;
	}

}
