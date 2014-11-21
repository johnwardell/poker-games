package texasholdem;

import java.util.Comparator;
/**
 * A <code>Comparator</code> that will put <code>Card</code>
 * objects in order by value, with ace high.
 * !!Note: this comparator imposes orderings that are inconsistent with equals!!
 * @author John Wardell
 *
 * @param <T> must be a <code>Card</code>
 */
public class CardNumComparator<T> implements Comparator<Card>{
	
	/**
	 * compares 2 cards by value with ace high.
	 */
	public int compare(Card card1, Card card2) {
		int i = card1.getNumber().compareTo(card2.getNumber());
		return i;
	}

}
