package texasholdem;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class is the brains of the computer players in a texas holdem poker
 * game. It will base it's decision on the player's cards, the community cards,
 * and how many chips the players have.
 * @author John Wardell
 *
 */
public class HandAnalyzer {
	
	private boolean hangInThere = false;
	private FinalHandChecker fhc;
	private Random random = new Random();
	private Card[] handCards;
	private Card[] commCards;
	private EnumMap<PartialHand, Boolean> partialMap = new EnumMap<>(PartialHand.class);
	private HoldemManager hm;
	private CardNumComparator<Card> cnc = new CardNumComparator<>();
	
	{
		resetPartialMap();
	}
	/**
	 * This is not the constructor used in the game.
	 */
	public HandAnalyzer() {
		this.fhc = new FinalHandChecker();
	}
	/**
	 * Constructs a HandAnalyzer object
	 * @param hm the HoldemManager instance running the poker game
	 * @param fhc a FinalHandChecker
	 */
	public HandAnalyzer(HoldemManager hm, FinalHandChecker fhc) {
		this.hm = hm;
		this.fhc = fhc;
		
	}
	/**
	 * Used to facilitate testing.
	 * @return String representation of the highest PartialHand in PartialMap
	 */
	public String getPresentPartial() {
		String stringHand = "none";
		PartialHand hand = null;
		for (PartialHand h: PartialHand.values()) {
			if (partialMap.get(h)) {
				hand = h;
			}
		}
		if (!(hand == null)) stringHand = hand.toString();
		return stringHand;
	}
	/**
	 * This method generates a bet for computer players.
	 * @return the amount of the bet. Must be validated by caller.
	 */
	public int generateBotBet() {
		updateInfo();
		
		int bet = 0;
		int score = scoreHand();
		//change a score of 1 to 0 about 10% of the time
		if (score == 1) {
			score = score * (100 / (51 + random.nextInt(60)));
		}
		//add some randomness to the score, usually just multiply by 1
		score = score * (100 / (29 + random.nextInt(72)));
		//incorporate the player's "personality"
		if (hm.getPlayerPersonality() > 7) {
			score *= 2;
		}
		else if (hm.getPlayerPersonality() < 2) {
			score = (score / 3) * 2;
		}
		
		int value = score * hm.getMinBet();
		//fold when hand isn't worth playing
		if (hm.getAmtToSee() > value && (score < 6)) {
			bet = PokerTools.FOLD;
		}
		//if hand is worth playing
		else {
			//for preflop bets
			if (hm.getPhase() == Phase.PREFLOP){ 
				bet = (value - hm.getPlayerBet());
			}
			//prevent infinite raises in later stages
			else {
				if (hm.haveAllPlayersBet()) {
					bet = hm.getAmtToSee();
				}
				else {
					bet = value;
				}
			}
			
			//make the bet a factor of the minimum bet
			bet = ((bet + (hm.getMinBet()/2) + 2) / hm.getMinBet()) * hm.getMinBet();
			//put anyone close all in
			if (bet > (hm.getLowChips() / 3)) {
				bet = hm.getLowChips();
			}
			//make sure bet is high enough
			if (bet < hm.getAmtToSee()) {
				bet = hm.getAmtToSee();
			}
			// hangInThere suggests betting more than required to stay in is not worth it
			if (hangInThere) {
				bet = hm.getAmtToSee();
			}
		}
		
		return bet;
	}
	/**
	 * Sets the booleans in partialMap to false.
	 * Called whenever a new set of cards needs to be analyzed.
	 */
	private void resetPartialMap() {
		for (PartialHand partial: PartialHand.values()) {
			partialMap.put(partial, false);
		}
	}
	/**
	 * Copies and sorts the player's hand and the community cards for analysis.
	 */
	private void updateInfo() {
		handCards = copyCardArray(hm.getPlayerCards());
		commCards = copyCardArray(hm.getCommCards());
		CardNumComparator<Card> cnc = new CardNumComparator<>();
		Arrays.sort(handCards, cnc);
		Arrays.sort(commCards, cnc);
	}
	/**
	 * Gauges the value of player's hand.
	 * @return integer representing the value of player's hand.
	 */
	private int scoreHand() {
		if (hm.getPhase() == Phase.PREFLOP) {
			return scorePreflop();
		}
		else {
			return scoreAfterFlop();
		}
	}
	/**
	 * Rates the 2 card hand before the flop cards are turned over.
	 * @return score of the 2 cards in player's hand.
	 */
	private int scorePreflop() {
		
		int score = 0;
		//rate highest card
		if (handCards[1].getNumber() == Number.ACE)
			score = 20;
		
		else if (handCards[1].getNumber() == Number.KING)
			score = 16;
		
		else if (handCards[1].getNumber() == Number.QUEEN)
			score = 14;
		
		else if (handCards[1].getNumber() == Number.JACK)
			score = 12;
		
		else {
			score = handCards[1].getNumber().compareTo(Number.TWO) + 2;
			hangInThere = true;
		}
		
		//rate if pair
		if (handCards[1].isSameNum(handCards[0]))
			score *= 2;
		
		else {
		    //rate if suited
		    if (handCards[1].isSameSuit(handCards[0]))
		    	score += 4;
		
		    //rate for possible straight
		    int diff = handCards[1].compareNum(handCards[0]);
		    if (diff > 4)
		    	score -= 10;
		    else if (diff > 3)
		    	score -= 8;
		    else if (diff > 2)
		    	score -= 4;
		    else if (diff > 1)
		    	score -= 2;
		    	
		}
		//the number 5 is used here to calibrate the score
		return score / 5;
	}
	/**
	 * Copies an array of Cards.
	 * @param cards a card array.
	 * @return a copy of the Card array parameter.
	 */
	private Card[] copyCardArray(Card[] cards) {
		Card[] newCards = new Card[cards.length];
		for (int i = 0; i < cards.length; i++) {
			newCards[i] = cards[i];
		}
		return newCards;
	}
	/**
	 * Scores the hand + community cards for players for all rounds
	 * with face up community cards.
	 * @return the score assigned to the hand.
	 */
	private int scoreAfterFlop() {
		hangInThere = false;
		int score = 0;
		Card[][] hands = new Card[1][2];
		hands[0] = handCards;
		fhc.checkAllHands(commCards, hands);
		
		Hand hand = fhc.getWinningHand();
		
		if (hand == Hand.HIGH_CARD) {
			if (isHoleCardIn2HighCards(fhc.getWinningCards())) {
				score = 5;
				hangInThere = true;
			}
			if (hm.getPhase() == Phase.FLOP){
				score += checkPartialHands();
			}
			
		}
		else if (hand == Hand.PAIR){
			if (isHoleCardIn2HighCards(fhc.getWinningCards())) {
				score = 10;
				hangInThere = true;
			}
			if (hm.getPhase() == Phase.FLOP){
				score += checkPartialHands();
			}
		}
		else if (hand == Hand.TWO_PAIR) { 
			if (isHoleCardIn2HighCards(fhc.getWinningCards())) {
				score = 15;
			}
			else if (isHoleCardIn4HighCards(fhc.getWinningCards())) {
				score = 5;
				hangInThere = true;
			}
			if (hm.getPhase() == Phase.FLOP){
				score += checkPartialHands();
			}
			
		}
		else if (hand == Hand.THREE_OF_A_KIND) {
			if (isHoleCardIn3HighCards(fhc.getWinningCards())) {
				score = 30;
			}
		}
		else if (hand == Hand.STRAIGHT) {
			if (isHoleCardInHand(fhc.getWinningCards())) {
				score = 35;
			}
		}
		else if (hand == Hand.FLUSH) {
			if (isHoleCardInHand(fhc.getWinningCards())) {
				score = 40;
			}
		}
		else if (hand == Hand.FULL_HOUSE) {
			if (isHoleCardInHand(fhc.getWinningCards())) {
				score = 40;
			}
		}
		else if (hand == Hand.FOUR_OF_A_KIND) {
			if (isHoleCardInHand(fhc.getWinningCards())) {
				score = 500;
			}
		}
		else if (hand == Hand.STRAIGHT_FLUSH) {
			if (isHoleCardInHand(fhc.getWinningCards())) {
				score = 40;
			}
		}
		
		
		return score/5;
	}
	/**
	 * Checks for partial hands and sets existing hands to true in partialMap.
	 * @param cards the cards to be checked.
	 */
	private int checkPartialHands() {
		int partScore = 0;
		Card[] cards = new Card[handCards.length + commCards.length];
		cards[0] = handCards[0];
		cards[1] = handCards[1];
		for(int i = 2; i < commCards.length + 2; i++) {
			cards[i] = commCards[i - 2];
		}
		resetPartialMap();
		Arrays.sort(cards, cnc);
		partStraightCheck(cards);
		partFlushCheck(cards);
		if (partialMap.get(PartialHand.FLUSH_DRAW)) {
			partScore += 10;
		}
		if (partialMap.get(PartialHand.OPEN_STRAIGHT)) {
			partScore += 10;
		}
		else if (partialMap.get(PartialHand.INSIDE_STRAIGHT)) {
			partScore += 5;
		}
		return partScore;
	}
	/**
	 * This method is part of a series of methods that follow that check
	 * for the presence of the player's 2 cards in the best hand formed
	 * from the player's cards combined with the community cards.
	 * @param totalHand should come from FinalHandAnalyzer.getWinningCards.
	 * @return true if player's cards are in the hand's 2 highest cards.
	 */
	private boolean isHoleCardIn2HighCards(Card[] totalHand) {
		if (handCards[0].equals(totalHand[totalHand.length - 1])) {
			return true;
		}
		if (handCards[0].equals(totalHand[totalHand.length - 2])) {
			return true;
		}
		if (handCards[1].equals(totalHand[totalHand.length - 1])) {
			return true;
		}
		if (handCards[1].equals(totalHand[totalHand.length - 2])) {
			return true;
		}
		return false;
	}
	
	private boolean isHoleCardIn3HighCards(Card[] totalHand) {
		if (isHoleCardIn2HighCards(totalHand)) {
			return true;
		}
		if (handCards[0].equals(totalHand[totalHand.length - 3])) {
			return true;
		}
		if (handCards[1].equals(totalHand[totalHand.length - 3])) {
			return true;
		}
		return false;
		
	}
	
	private boolean isHoleCardIn4HighCards(Card[] totalHand) {
		if (isHoleCardIn3HighCards(totalHand)) {
			return true;
		}
		if (handCards[0].equals(totalHand[totalHand.length - 4])) {
			return true;
		}
		if (handCards[1].equals(totalHand[totalHand.length - 4])) {
			return true;
		}
		return false;
	}
	
	private boolean isHoleCardInHand(Card[] totalHand) {
		if (isHoleCardIn4HighCards(totalHand)) {
			return true;
		}
		if (handCards[0].equals(totalHand[totalHand.length - 5])) {
			return true;
		}
		if (handCards[1].equals(totalHand[totalHand.length - 5])) {
			return true;
		}
		return false;
		
	}
	/**
	 * Checks for the presence of a partial flush.
	 * @param cards the cards to be checked.
	 */
	private void partFlushCheck(Card[] cards) {
		int flushSize = 0;
		
		for (int i = cards.length - 1; i > 1; i--) {
			flushSize = 1;
			for (int j = i - 1; j >= 0; j--) {
				if (cards[i].isSameSuit(cards[j])) {
					flushSize++;
				}
			}
			if (flushSize == 5) return;
			if (flushSize == 3) {
				partialMap.put(PartialHand.BACKDOOR_FLUSH, true);
				return;
			}
			else if (flushSize == 4) {
				partialMap.put(PartialHand.FLUSH_DRAW, true);
				return;
			}
		}
	}
	/**
	 * Checks for the presence of a partial straight.
	 * @param cards the cards to be checked.
	 */
	private void partStraightCheck(Card[] cards) {
		LinkedList<Card> tempHand = new LinkedList<>();
		boolean gap = false;
		
		for (int i = cards.length - 1; i > 1; i--) {
			tempHand.clear();
			tempHand.add(cards[i]);
			int j = i;
			gap = false;
			while (j > 0) {
				j--;
				if (cards[j].isNumNext(cards[j + 1])) {
					tempHand.addFirst(cards[j]);
				}
				else if (!gap && (cards[j + 1].compareNum(cards[j]) == 2)) {
					tempHand.addFirst(cards[j]);
					gap = true;
				}
				else if (!cards[j].isSameNum(cards[j + 1])) {
					break;
				}
			}
			//a 5-card straight will not count as a partial straight
			if (tempHand.size() == 5) return;
			if (tempHand.size() == 4) {
				//5-card straight starting with an ace doesn't count as a partial.
				if ((tempHand.peekFirst().getNumber() == Number.TWO) &&
						(cards[cards.length - 1].getNumber() == Number.ACE)) {
					return;
				}
				if (gap) {
					partialMap.put(PartialHand.INSIDE_STRAIGHT, true);
					return;
				}
				else {
					partialMap.put(PartialHand.OPEN_STRAIGHT, true);
					return;
				}
			}
			//a 4-card straight starting with an ace counts as an inside straight
			else if ((tempHand.size() == 3) &&
					(cards[cards.length - 1].getNumber() == Number.ACE) &&
					((tempHand.peekFirst().getNumber() == Number.TWO) ||
							(tempHand.peekFirst().getNumber() == Number.THREE))) {
				partialMap.put(PartialHand.INSIDE_STRAIGHT, true);
				return;
			}
			//only a consecutive 3-card straight will be counted
			else if ((tempHand.size() == 3) && !gap) {
				partialMap.put(PartialHand.BACKDOOR_STRAIGHT, true);
				return;
			}
		}
	}
	//main method for testing only
	public static void main(String[] args) {
		for (int j = 0; j < 100; j++) {
			HandAnalyzer ha = new HandAnalyzer();
			Card[] cards = new Card[3];
			cards[0] = new Card(Suit.CLUBS, Number.FOUR);
			cards[1] = new Card(Suit.CLUBS, Number.TWO);
			cards[2] = new Card(Suit.CLUBS, Number.THREE);
			//cards[3] = new Card(Suit.CLUBS, Number.FOUR);
			//cards[4] = new Card(Suit.HEARTS, Number.TEN);
			//cards[5] = new Card(Suit.HEARTS, Number.SEVEN);
			CardNumComparator<Card> cnc = new CardNumComparator<>();
			Arrays.sort(cards, cnc);
			ha.resetPartialMap();
			System.out.println(Arrays.toString(cards));
			ha.partStraightCheck(cards);
			System.out.println(ha.getPresentPartial());
		}
	}
	
	
}
