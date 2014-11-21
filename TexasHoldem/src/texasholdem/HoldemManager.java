package texasholdem;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
/**
 * This class controls the entire poker game. It is the only part
 * of the underlying game that interacts with the user interface.
 * 
 * @author John Wardell
 *
 */
public class HoldemManager {
	
	
	
	private Player[] players;
	private Deck deck = new Deck();
	private Card[] commCards = new Card[5];
	
	private Random random = new Random();
	private Name[] names = Name.values();
	private int maxName = names.length - 1;
	private int minBet = 10;
	private Timer blindTimer;
	private Timer turnTimer;
	/**
	 * 5 minutes in milliseconds
	 */
	public static int FIVE_MINUTES = 5 * 60 * 1000;
	private int pendingMinBet = 10;
	private int takeTurnsCount = -1;
	private boolean allIn = false;
	private int allInAmt = 0;
	private ImageIcon[] cardBacks = {PokerTools.CARD_BACK, PokerTools.CARD_BACK};
	private ImageIcon[] emptyCards = {PokerTools.NO_CARD, PokerTools.NO_CARD};
	private ImageIcon[] cardImages = new ImageIcon[2];
	private int userIndex = 0;
	private Phase phase = Phase.PREFLOP;
	private FinalHandChecker fhc;
	private HandAnalyzer ha;
	private HoldemUI frame;
	private LinkedList<Integer> winner = new LinkedList<>();
	private int turnIndex = 0;
	private int smallBlindIndex = 0;
	private int bigBlindIndex = 1;
	
	private int chips = 500; //set to 500
	/**
	 * 
	 * @param numPlayers the total number of players.
	 * @param name the name of the user player.
	 * @param frame the game's user interface.
	 */
	public HoldemManager(int numPlayers, String name, HoldemUI frame) {
		players = new Player[numPlayers];
		this.frame = frame;
		fhc = new FinalHandChecker();
		ha = new HandAnalyzer(this, fhc);
		
		//set bot players
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player(chips, generateName());
			players[i].setBot(true);
			players[i].setPersonality(random.nextInt(10));
		}
		
		//set user player
		userIndex = random.nextInt(numPlayers);
		players[userIndex].setName(name);
		players[userIndex].setBot(false);
		//set to 5 minutes!!!!!
		blindTimer = new Timer(FIVE_MINUTES, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				increasePendingMinBet();
			}
		});
		blindTimer.start();
		
		turnTimer = new Timer(750, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				startTurn();
			}
		});
		
	}
	/**
	 * This is not the constructor used in the game.
	 */
	public HoldemManager() { }
	/**
	 * Gets the cards of the player whose turn it is.
	 * @return current player's cards.
	 */
	public Card[] getPlayerCards() {
		return players[turnIndex].getCards();
	}
	/**
	 * Gets the chips of the player whose turn it is.
	 * @return current player's chips.
	 */
	public int getPlayerChips() {
		return players[turnIndex].getChips();
	}
	/**
	 * Gets the array of players.
	 * @return the Player array.
	 */
	public Player[] getPlayers() {
		return players;
	}
	/**
	 * Checks if the indicated player is in "playing" state.
	 * @param index of the player in the player array.
	 * @return true if player is still playing, false if not.
	 */
	public boolean isPlaying(int index) {
		return players[index].isPlaying();
	}
	/**
	 * Gets personality value of player whose turn it is.
	 * 
	 * @return current player's personality, 0 for user player.
	 */
	public int getPlayerPersonality() {
		return players[turnIndex].getPersonality();
	}
	/**
	 * Gets the amount of chips that the poorest player who is currently
	 * playing.
	 * @return the poorest player's chip amount.
	 */
	public int getLowChips() {
		int lowChips = 0;
		int totalChips = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i].isPlaying()) {
				totalChips = players[i].getChips() + players[i].getBet();
				if (lowChips == 0) {
					lowChips = totalChips;
				}
				else if (totalChips < lowChips) {
					
					lowChips = totalChips;
				}
			}
		}
		return lowChips;
	}
	/**
	 * Gets the largest amount the player whose turn it is can legally bet.
	 * @return current player's maximum bet.
	 */
	public int getMaxBet() {
		return players[turnIndex].getChips();
	}
	/**
	 * Gets the amount needed to bet to stay in the game.
	 * @return amount needed to stay in the game.
	 */
	public int getAmtToSee() {
		int amount = getHighBet() - getPlayerBet();
		if (amount < 0) return 0;
		
		else if (amount > players[turnIndex].getChips()) {
			return players[turnIndex].getChips();
		}
		
		else return amount;
	}
	/**
	 * Gets the minimum bet or 'big blind' bet for the round.
	 * @return the minimum bet amount.
	 */
	public int getMinBet() {
		return minBet;
	}
	/**
	 * Gets the amount the player whose turn it is has bet so far this hand.
	 * @return the current player's total bet.
	 */
	public int getPlayerBet() {
		return getBet(turnIndex);
	}
	/**
	 * Finds the winner, calls distribute winnings.
	 */
	private void determineWinner() {
		int numPlaying = getPlayingPlayers();
		
		int[] stillPlayingIndex = new int[numPlaying];
		
		Card[][] hands = new Card[numPlaying][2];
		for (int i = 0; i < players.length; i++) {
			if (players[i].isPlaying()) {
				Card[] pCards = players[i].getCards();
				cardImages[0] = PokerTools.getCardImage(pCards[0]);
				cardImages[1] = PokerTools.getCardImage(pCards[1]);
				frame.setPlayerCards(cardImages, i);
				
				numPlaying--;
				stillPlayingIndex[numPlaying] = i;
				hands[numPlaying] = players[i].getCards();
			}
		}
		
		for (int i: fhc.checkAllHands(getCommCards(), hands)) {
			winner.add(stillPlayingIndex[i]);
		}
		distributeWinnings();	
	}
	/**
	 * Gives the pot to the winner. Divides the pot as needed for a tie.
	 */
	private void distributeWinnings() {
		int winnings = getPot();
		winnings /= winner.size();
		if (winner.size() > 1) {
			frame.showTieDialog();
		}
		for (int i: winner) {
			players[i].setChips(players[i].getChips() + winnings);
			frame.updateChipDisplays(i, 0, players[i].getChips(), 0);
			frame.showWinnerDialog(players[i].getName(), winnings, fhc.getWinningHand());
			
		}
		winner.clear();
		if (players[userIndex].getChips() <= 0) {
			frame.gameOver(false);
		}
		else if (players[userIndex].getChips() > 0) {
			int survivors = 0;
			for(int i = 0; i < players.length; i++) {
				if (players[i].getChips() > 0) {
					survivors++;
				}
			}
			if (survivors < 2) {
				frame.gameOver(true);
			}
		}
	}
	/**
	 * This method returns the number of players including players
	 * that have folded or run out of money.
	 * @return the total number of players.
	 */
	public int getNumPlayers() {
		return players.length;
	}
	/**
	 * Calculates and returns the number of currently playing players.
	 * @return the number of playing players.
	 */
	public int getPlayingPlayers() {
		int playingPlayers = 0;
		for (Player p: players) {
			if (p.isPlaying()) {
				playingPlayers++;
			}
		}
		return playingPlayers;
	}
	/**
	 * 
	 * @return the phase of the current game.
	 */
	public Phase getPhase() {
		return phase;
	}
	/**
	 * Gets the correct community cards for each round of play.
	 * @return array of community cards.
	 */
	public Card[] getCommCards() {
		if (phase == Phase.FLOP) 
			return Arrays.copyOfRange(commCards, 0, 3);
		
		if (phase == Phase.TURN)
			return Arrays.copyOfRange(commCards, 0, 4);
		
		if (phase == Phase.RIVER)
			return commCards;
		
		else return new Card[0];
	}
	/**
	 * Restarts the entire program to play a new game.
	 */
	public void startNewGame() {
		frame.terminateFrame();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				TexasHoldemFrame frame = new TexasHoldemFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
	/**
	 * Starts a new hand of cards, deals the cards to players, and makes
	 * the big blind and small blind bets.
	 * Resets allIn, allInAmt, takeTurnsCount, minimum bet, and blind indexes.
	 */
	public void startHand() {
		
		phase = Phase.PREFLOP;
		setBets();
		deck.fillDeck();
		allIn = false;
		allInAmt = 0;
		
		for (int i = 0; i < commCards.length; i++) {
			commCards[i] = deck.drawCard();
		}
		
		frame.updateCommCards(getCommCards());
		frame.updateMinBet(minBet);
		
		for (int m = 0; m < players.length; m++) {
			players[m].setBet(0);
			if (players[m].isActive()) {
				if (players[m].getChips() == 0) {
					players[m].setActive(false);
					players[m].setPlaying(false);
					frame.setIndicator(m, PokerTools.BLACK_BOX);
					frame.updatePlayingDisplay(false, m);
					frame.updateActiveDisplay(false, m);
					frame.setPlayerCards(emptyCards, m);
				}
				else {
					Card[] hand = {deck.drawCard(), deck.drawCard()};
					players[m].setCards(hand);
					players[m].setAllIn(false);
					players[m].setPlaying(true);
					frame.setIndicator(m, PokerTools.EMPTY_BOX);
					frame.updatePlayingDisplay(true, m);
					if (players[m].isBot()) {
						frame.setPlayerCards(cardBacks, m);
					}
					else {
						Card[] pCards = players[m].getCards();
						cardImages[0] = PokerTools.getCardImage(pCards[0]);
						cardImages[1] = PokerTools.getCardImage(pCards[1]);
						frame.setPlayerCards(cardImages, m);
					}
				}
			}
		}
		
		
		
		incrementBlindIndexes();
		//blind bets
		
		setPhaseBet(bigBlindIndex, minBet);
		
		setPhaseBet(smallBlindIndex, (minBet / 2));
		
		//callAmt = minBet;
		takeTurnsCount = -1;
		takeTurns();
	}
	/**
	 * Starts the process of players taking turns.
	 */
	public void takeTurns() {
		startTurn();
	}
	/**
	 * Takes a player's turn. Stops the turn timer for the duration of the turn
	 * for human and computer players.
	 */
	public void startTurn() {
		turnTimer.stop();
		finishLastTurn();
		//the first round of betting.
		if (takeTurnsCount < players.length) {
			playingTurn();
		}
		//responding to any raises
		else if (!isPhaseOver()) {
			playingTurn();
		}
		//wrap up the hand
		else {
			if ((phase == Phase.RIVER) || (getPlayingPlayers() <= 1)){
				determineWinner();
				frame.handOver();
			}
			else {
				frame.phaseOver();
			}
		}
	}
	/**
	 * Checks if player is playing and takes the bet.
	 */
	private void playingTurn() {
		
		if (players[turnIndex].isPlaying()) {
			frame.setIndicator(turnIndex, PokerTools.BLACK_ARROW);
			if (!players[turnIndex].isBot()) {
				frame.updateAmountToSee(getAmtToSee());
				frame.takeUserTurn();
			}
			else {
				setPhaseBet(turnIndex, ha.generateBotBet());
				frame.updateChipDisplays(turnIndex, getPot(),
						players[turnIndex].getChips(), players[turnIndex].getBet());
				turnTimer.start();
				
			}
		}
		else {
			startTurn();
		}
	}
	/**
	 * Wraps up a players turn. Updates relevant UI components.
	 * Increments turnIndex and takeTurnsCount.
	 */
	public void finishLastTurn() {
		
		
		
		if (players[turnIndex].isPlaying()) {
			frame.setIndicator(turnIndex, PokerTools.EMPTY_BOX);
		}
		else {
			frame.setIndicator(turnIndex, PokerTools.RED_X);
		}
		turnIndex = (turnIndex + 1) % players.length;
		takeTurnsCount++;
		
	}
	/**
	 * Called from user interface when user places a bet.
	 * @param userBet the amount the user player would like to bet.
	 */
	public void acceptUserBet(int userBet) {
		setPhaseBet(turnIndex, userBet);
		frame.updateChipDisplays(turnIndex, getPot(), players[turnIndex].getChips(),
				players[turnIndex].getBet());
		startTurn();
	}
	/**
	 * Called from the user interface to see the next cards.
	 * Increments the phase, resets turnIndex, resets takeTurnsCount,
	 * and checks for an all-in state.
	 */
	public void startNextPhase() {
		int phaseIndex = (phase.ordinal() + 1) % Phase.values().length;
		phase = Phase.values()[phaseIndex];
		frame.updateCommCards(getCommCards());
		turnIndex = bigBlindIndex;
		if (allIn) {
			allInAmt = 0;
			frame.updateCommCards(getCommCards());
			if ((phase == Phase.RIVER) || (getPlayingPlayers() <= 1)){
				determineWinner();
				frame.handOver();
			}
			else {
				frame.phaseOver();
			}
		}
		else {
			takeTurnsCount = -1;
			startTurn();
		}
	}
	/**
	 * Gets the current pot total.
	 * @return the pot.
	 */
	public int getPot() {
		int pot = 0;
		for (Player p: players) {
			pot += p.getBet();
		}
		return pot;
	}
	/**
	 * Adjusts the minimum bet up periodically.
	 * Resets allIn to false.
	 */
	private void setBets() {
		minBet = pendingMinBet;
		allIn = false;
		
	}
	/**
	 * Called by the bet timer to set the amount of the next
	 * big blind and minimum bet.
	 */
	private void increasePendingMinBet() {
		if (pendingMinBet < 40) {
		    pendingMinBet *= 2;
		}
		else if (pendingMinBet < 100){
			pendingMinBet += 30;
		}
		else {
			pendingMinBet += 50;
		}
	}
	/**
	 * Checks to see if the phase is over after the initial round of betting
	 * is already complete.
	 * @return true if phase is complete, false if not.
	 */
	private boolean isPhaseOver() {
		
		if (getPlayingPlayers() <= 1) return true; 
		
		for (int i = 0; i < players.length; i++) {
			if(players[i].isPlaying() && (getBet(i) != getHighBet())) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Get the total bet amount for the player whose turn it is.
	 * @param index the players array index of the desired player.
	 * @return the selected player's bet.
	 */
	private int getBet(int index) {
		return players[index].getBet();
	}
	/**
	 * Accepts a bet amount and validates the amount against player's chips,
	 * checks for all-in status, updates player's chips and bet.
	 * Bet may be reduced to a legal amount automatically.
	 * @param index the player to take the bet from.
	 * @param bet the requested amount to bet.
	 */
	private void setPhaseBet(int index, int bet) {
		if (bet == PokerTools.FOLD) {
			players[index].setPlaying(false);
			frame.updatePlayingDisplay(false, index);
		}
		else {
			if (allIn) {
				int total = players[index].getChips() + players[index].getBet();
				if (allInAmt > total) {
					allInAmt = total;
					returnExcessBets();
				}
				bet = allInAmt - players[index].getBet();
			}
			
			else if (players[index].getChips() <= bet) {
				bet = players[index].getChips();
				players[index].setAllIn(true);
				allInAmt = players[index].getChips() + players[index].getBet();
				if (bet < getAmtToSee()) {
					returnExcessBets();
				}
				allIn = true;
				
			}
			players[index].setChips(players[index].getChips() - bet);
			players[index].setBet(players[index].getBet() + bet);
		    frame.updateChipDisplays(index, getPot(), players[index].getChips(),
		    		players[index].getBet());
		    
		}
	}
	/**
	 * Called in case someone goes all in after other players have bet more than
	 * the all in players amount of chips.
	 * Gives back the extra bet amounts immediately. 
	 */
	private void returnExcessBets() {
		for (int i = 0; i < players.length; i++) {
			if (players[i].isPlaying() &&
					(players[i].getBet() > allInAmt)) {
				int excess = players[i].getBet() - allInAmt;
				players[i].setChips(players[i].getChips() + excess);
				
				players[i].setBet(allInAmt);
				frame.updateChipDisplays(i, getPot(), players[i].getChips(), allInAmt);
			}
			else if (((i == bigBlindIndex) || (i == smallBlindIndex)) &&
					(players[i].getBet() > allInAmt)) {
				int excess = players[i].getBet() - allInAmt;
				players[i].setChips(players[i].getChips() + excess);
				
				players[i].setBet(allInAmt);
				frame.updateChipDisplays(i, getPot(), players[i].getChips(), allInAmt);
			}
		}
	}
	/**
	 * Picks a name from the Name enumeration at random.
	 * @return a random name.
	 */
	private String generateName() {
		int index = random.nextInt(maxName);
		String name = names[index].toString();
		names[index] = names[maxName];
		maxName--;
		return name;
	}
	/**
	 * Increments the blind indexes for each hand.
	 */
	private void incrementBlindIndexes() {
		smallBlindIndex = incrementIndex(smallBlindIndex);
		bigBlindIndex = incrementIndex(bigBlindIndex);
		if (smallBlindIndex == bigBlindIndex) {
			bigBlindIndex = incrementIndex(bigBlindIndex);
		}
		turnIndex = bigBlindIndex;
	}
	/**
	 * Increments past inactive players.
	 * @param index the expired index.
	 * @return the updated index.
	 */
	private int incrementIndex(int index) {
        index = (index + 1) % players.length;
		
		while(!(players[index].isActive())) {
			index = (index + 1) % players.length;
		}
		
		return index;
	}
	/**
	 * Gets the highest bet of all the playing players.
	 * @return the current high bet.
	 */
	public int getHighBet() {
		int highBet = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i].isPlaying()) {
				if (highBet == 0) {
					highBet = players[i].getBet();
				}
				else if (players[i].getBet() > highBet) {
					highBet = players[i].getBet();
				}
			}
		}
		return highBet;
	}
	/**
	 * Checks if all players have taken their turn once.
	 * @return true if all players have taken a turn, false if not.
	 */
	public boolean haveAllPlayersBet() {
		if (takeTurnsCount >= (players.length - 1)) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		
		
	}
	
	
}
