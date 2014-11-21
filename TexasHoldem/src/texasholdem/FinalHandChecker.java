package texasholdem;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;

/**
 * 
 *<code>FinalHandChecker</code> has methods for evaluating cards
 *and hands in texas holdem poker. They are not static methods,
 *a <code>FinalHandChecker</code> object must be instantiated.
 *@author John Wardell
 */
public class FinalHandChecker {
	
	private LinkedList<Integer> winnerIndex = new LinkedList<>();
	
	private Card[] winningCards = new Card[5];
	private Card[] currentCards = new Card[5];
	
	private Hand winningHand = null;
	private Hand currentHand = null;
	
	private EnumMap<Hand, Boolean> handPresenceMap = new EnumMap<>(Hand.class);
	{
		for (Hand h: Hand.values()) 
			handPresenceMap.put(h, false);
	}
	
	/**
	 * A no argument constructor.
	 */
	public FinalHandChecker() {}
	
	/**
	 * Gets the best cards of the last set of cards fed into checkAllHands method.
	 * @return the <code>winningCards</code> array
	 */
	public Card[] getWinningCards() {
		return winningCards;
	}
	
	/**
	 * Gets the index of the winner of the last set of cards fed into checkAllHands method.
	 * @return the integer index of the winning player
	 */
	
	public LinkedList<Integer> getWinner() {
		return winnerIndex;
	}
	
	/**
	 * Gets the winning 'hand', (e.g. 'pair' or 'straight')
	 * of the last set of cards fed into checkAllHands method.
	 * @return the enumerated type <code>Hand</code> of the winning hand
	 */
	public Hand getWinningHand() {
		return winningHand;
	}
	
	/**
	 * Checks all players' cards and shared cards to determine the winner.
	 * The winning integer indices are based on the order of the 
	 * array <code>hands</code>.
	 * @param commCards the 0 - 5 community cards shared by all players 
	 * @param hands the array of arrays of cards that represent each player's
	 * individual hand.
	 * @return a list containing the index of the 1 or more winners
	 */
	public LinkedList<Integer> checkAllHands(Card[] commCards, Card[][] hands) {
		
		//if there is only 1 hand and no community cards as parameters
		if ((hands.length == 1) && (commCards.length == 0)) {
			if (hands[0][0].isSameNum(hands[0][1])) {
				winningHand = Hand.PAIR;
			}
			else {
				winningHand = Hand.HIGH_CARD;
			}
			winnerIndex.clear();
			winnerIndex.add(0);
			return winnerIndex;
		}
		//reset values
		Card[] fullHand = new Card[commCards.length + 2];
		currentHand = null;
		winningHand = null;
		
		int playerIndex = 0;
		for (int m = 0; m < hands.length; m++) {
			//combine player's hand with community cards in fullHand
			fullHand[0] = hands[m][0];
			fullHand[1] = hands[m][1];
			
			for(int i = 0; i < commCards.length; i++) 
				fullHand[i+2] = commCards[i];
			
			//reset handPresenceMap
			for (Hand h: Hand.values())
				handPresenceMap.put(h, false);
			
			//check this hand
		    handCheck(fullHand);
		    
		    //put player's best hand into currentHand
		    for (Hand h: Hand.values()) {
		    	if (handPresenceMap.get(h))
		    		currentHand = h;
		    }
		    
		    //if this is the first hand checked
		    if (winningHand == null) {
		    	replaceWinner(playerIndex);
		    }
		    
		    //if currentHand is higher than winningHand
		    else if (currentHand.compareTo(winningHand) > 0) {
		    	replaceWinner(playerIndex);
		    }
		    
		    /*
		     * If currentHand and WinningHand are equal, 
		     * each card must be compared individually.
		     * A tie is possible.
		    */
		    else if (currentHand.compareTo(winningHand) == 0) {
		    		int tieDetector = winningCards.length;
		    		
		    		for (int k = winningCards.length - 1; k >= 0; k--) {
		    			int result = winningCards[k].compareNum(currentCards[k]);
		    			
		    			if (result > 0) {
		    				break;
		    			}
		    			else if (result < 0) {
		    			    replaceWinner(playerIndex);	
		    				
		    				break;
		    			}
		    			else if (result == 0)
		    			    tieDetector--;
		    	}
		    	if (tieDetector == 0)
		    		winnerIndex.add(playerIndex);
		    }
		   
		  //increment index of current hand (player) 	
		  playerIndex++;  
		}
		
		return winnerIndex;
	}
	
	/**
	 * Puts current cards' (player's) information into winningHand,
	 * winningCards, and winnerIndex.
	 * @param playerIndex the index of the current cards (player)
	 */
	private void replaceWinner(int playerIndex) {
		winningHand = currentHand;
		
		for (int n = 0; n < currentCards.length; n++)
        	winningCards[n] = currentCards[n];
		
		winnerIndex.clear();
		winnerIndex.add(playerIndex);
	}
	
	/**
	 * Checks the 1 hand passed to it for the presence of a poker hand.
	 * Updates the <code>handPresenceMap</code> for current hand.
	 * Puts the best 5 cards from <code>cards</code> into 
	 * <code>currentCards</code>.
	 * @param cards the 5 - 7 <code>Card</code> array that the
	 * hand can be made out of
	 */
	private void handCheck(Card[] cards) {
		//sort cards in ascending number order
		CardNumComparator<Card> cnc = new CardNumComparator<>();
		Arrays.sort(cards, cnc);
		
		//call the hand check methods in ascending hand order
		highCardCheck(cards);
		pairCheck(cards);
		threeKindCheck(cards);
		straightCheck(cards);
		flushCheck(cards);
		fullHouseCheck(cards);
		fourKindCheck(cards);
		straightFlushCheck(cards);
		
		
	}
	
	/**
	 * Must be called first among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void highCardCheck(Card[] cards) {
		int j = cards.length;
		for (int i = currentCards.length - 1; i >= 0; i--) { 
			j--;
			if (j >= 0) {
				currentCards[i] = cards[j];
			}
		}
		handPresenceMap.put(Hand.HIGH_CARD, true);
	}
	
	/**
	 * Must be called second among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void pairCheck(Card[] cards) {
		LinkedList<Card> pairs = new LinkedList<>();
		LinkedList<Card> tempHand = new LinkedList<>();
		
		int pairCount = 0;
		int i = cards.length - 1;
		while (i >= 0) {
			if ((i > 0) && (cards[i].isSameNum(cards[i - 1]))) {
				pairCount++;
				if (pairCount < 3) { 
				    pairs.addFirst(cards[i]);
				    pairs.addFirst(cards[i - 1]);
				    i -= 2;
				}
				else {
					tempHand.addFirst(cards[i]);
					tempHand.addFirst(cards[i-1]);
					i -= 2;
				}
			}
			else {
				tempHand.addFirst(cards[i]);
				i--;
			}
		}
		//combine lists 
		tempHand.addAll(pairs);
		//trim to 5 elements
		while (tempHand.size() > 5) tempHand.remove();
		
		if (pairCount > 0)
		    currentCards = tempHand.toArray(currentCards);
		
		if (pairCount == 1) {
			handPresenceMap.put(Hand.PAIR, true);
		}
		else if (pairCount > 1) {
			handPresenceMap.put(Hand.TWO_PAIR, true);
		}
		
	}
	
	/**
	 * Must be called third among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void threeKindCheck(Card[] cards) {
		LinkedList<Card> threeKind = new LinkedList<>();
		LinkedList<Card> tempHand = new LinkedList<>();
		
		int i = cards.length - 1;
		while(i >= 0) {
			if ((i > 1) && (cards[i].isSameNum(cards[i - 2]))) {
				threeKind.addFirst(cards[i]);
				threeKind.addFirst(cards[i - 1]);
				threeKind.addFirst(cards[i - 2]);
				i -= 3;
				handPresenceMap.put(Hand.THREE_OF_A_KIND, true);
			}
			else {
				tempHand.addFirst(cards[i]);
				i--;
			}
		}
		tempHand.addAll(threeKind);
		while (tempHand.size() > 5) tempHand.remove();
			
		if (handPresenceMap.get(Hand.THREE_OF_A_KIND)) 
		    currentCards = tempHand.toArray(currentCards);
	}
	
	/**
	 * Must be called fourth among the 'hand' methods.
	 * Used in straightFlush method.
	 * @param cards the current cards being analyzed
	 * @return true if straight present, otherwise false
	 */
	private boolean straightCheck(Card[] cards) {
		LinkedList<Card> tempHand = new LinkedList<>();
		
		for (int i = cards.length-1; i > cards.length-5; i--) {
			tempHand.clear();
			tempHand.add(cards[i]);
			
			int j = i;
			while (j > 0) {
				if (cards[j - 1].isNumNext(cards[j])) {
					j--;
					tempHand.addFirst(cards[j]);
				}
				else if (cards[j].isSameNum(cards[j-1])) {
					j--;
				}
				else break;
			}
			if (tempHand.size() > 4) {
				while (tempHand.size() > 5) tempHand.remove();
				currentCards = tempHand.toArray(currentCards);
				handPresenceMap.put(Hand.STRAIGHT, true);
				return true;
			}
			//in case there is an A,2,3,4,5 straight
			if ((tempHand.size() == 4) && (cards[cards.length-1].getNumber() ==
					Number.ACE) && (tempHand.getFirst().getNumber() ==
					Number.TWO)) {
				tempHand.addFirst(cards[cards.length-1]);
				while (tempHand.size() > 5) tempHand.remove();
				currentCards = tempHand.toArray(currentCards);
				handPresenceMap.put(Hand.STRAIGHT, true);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Must be called fifth among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void flushCheck(Card[] cards) {
		LinkedList<Card> tempHand = new LinkedList<>();
		
		for (int i = cards.length - 1; i > 3; i--) {
			tempHand.clear();
			tempHand.add(cards[i]);
			for (int j = i - 1; j >= 0; j--) {
				if (cards[i].isSameSuit(cards[j])) 
					tempHand.addFirst(cards[j]);
			}
			if (tempHand.size() >= 5) {
				handPresenceMap.put(Hand.FLUSH, true);
				break;
			}
		}
		if (tempHand.size() < 5) return;
		while (tempHand.size() > 5) tempHand.remove();
		
		if (handPresenceMap.get(Hand.FLUSH))
			currentCards = tempHand.toArray(currentCards);
	}
	
	/**
	 * Must be called sixth among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void fullHouseCheck(Card[] cards) {
		//if there is no three of kind, there is no full house
		if (!handPresenceMap.get(Hand.THREE_OF_A_KIND)) return;
		
		LinkedList<Card> tempHand = new LinkedList<>();
		
		for (int i = cards.length - 1; i > 1; i--) {
			if (cards[i].isSameNum(cards[i-2])) {
				tempHand.addFirst(cards[i]);
				tempHand.addFirst(cards[i-1]);
				tempHand.addFirst(cards[i-2]);
				break;
			}
		}
		
		for (int i = cards.length-1; i > 0; i--) {
			if ((i > 1) && cards[i].isSameNum(cards[i-2]) &&
					cards[i].equals(tempHand.peekLast()))
				i -= 3;
			
			if ((i > 0) && cards[i].isSameNum(cards[i-1])) {
				tempHand.addFirst(cards[i]);
				tempHand.addFirst(cards[i-1]);
				handPresenceMap.put(Hand.FULL_HOUSE, true);
				break;
			}
		}
		
		if (handPresenceMap.get(Hand.FULL_HOUSE))
			currentCards = tempHand.toArray(currentCards);
		
	}
	
	/**
	 * Must be called seventh among the 'hand' methods.
	 * @param cards the current cards being analyzed
	 */
	private void fourKindCheck(Card[] cards) {
		int i = cards.length - 1;
		while (i > 2) {
			if (cards[i].isSameNum(cards[i - 3])) {
				for (int j = currentCards.length - 1; j > 0; j--) {
					currentCards[j] = cards[i - (j - 1)];
				}
				//add next highest card to bestHand
				if (i == cards.length - 1) 
					currentCards[0] = cards[i - 4];
				else currentCards[0] = cards[cards.length - 1];
				
				handPresenceMap.put(Hand.FOUR_OF_A_KIND, true);
				break;
			}
			else i--;
		}
	}
	
	/**
	 * Must be called eighth among the 'hand' methods.
	 * Relies on the straightCheck method called inside.
	 * @param cards the current cards being analyzed
	 */
	private void straightFlushCheck(Card[] cards) {
		if (!handPresenceMap.get(Hand.FLUSH) ||
				!handPresenceMap.get(Hand.STRAIGHT)) return;
		
		LinkedList<Card> tempHand = new LinkedList<>();
		
		for (int i = cards.length-1; i > 3; i--) {
			tempHand.clear();
			tempHand.add(cards[i]);
			for (int j = i-1; j >= 0; j--) {
				if (cards[i].isSameSuit(cards[j]))
					tempHand.addFirst(cards[j]);
			}
			if (tempHand.size() >= 5) break;
		}
		if (tempHand.size() < 5) return;
		
		Card[] tempArray = new Card[tempHand.size()];
		tempArray = tempHand.toArray(tempArray);
		//the following call to straightCheck will fill bestHand appropriately
		if (!this.straightCheck(tempArray)) return;
		
		handPresenceMap.put(Hand.STRAIGHT_FLUSH, true);
		
	}
	
	/**
	 * The main method is for testing purposes only
	 * @param args
	 */
	public static void main(String[] args) {
		//Deck deck = new Deck();
		Card[] commCards = new Card[5];
		//for (int i = 0; i < commCards.length; i++)
			//commCards[i] = deck.drawCard();
		
		commCards[0] = new Card(Suit.DIAMONDS, Number.TEN);
		commCards[1] = new Card(Suit.SPADES, Number.TEN);
		commCards[2] = new Card(Suit.CLUBS, Number.ACE);
		commCards[3] = new Card(Suit.DIAMONDS, Number.ACE);
		commCards[4] = new Card(Suit.SPADES, Number.KING);
		
		System.out.println("Community Cards: "+Arrays.toString(commCards));
		
		Card[][] playerCards = new Card[2][2];
		playerCards[0][0] = new Card(Suit.CLUBS, Number.TWO);
		playerCards[0][1] = new Card(Suit.HEARTS, Number.TWO);
		playerCards[1][0] = new Card(Suit.HEARTS, Number.KING);
		playerCards[1][1] = new Card(Suit.CLUBS, Number.KING);
		
		for (int i = 0; i < playerCards.length; i++) {
			//playerCards[i][0] = deck.drawCard();
			//playerCards[i][1] = deck.drawCard();
			
			System.out.println("P"+i+" cards:"+playerCards[i][0].toString()+"/"+
			    playerCards[i][1].toString());
		}
		
		FinalHandChecker fhc = new FinalHandChecker();
		
		fhc.checkAllHands(commCards, playerCards);
		System.out.println(fhc.getWinner());
		System.out.println(fhc.getWinningHand());
		System.out.println(Arrays.toString(fhc.getWinningCards()));
	}
}
