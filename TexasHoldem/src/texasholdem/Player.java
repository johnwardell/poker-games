package texasholdem;

/**
 * This class represents a poker player. This class does not contain
 * the betting logic needed to play the game, but it holds the
 * player's cards, chip amount, and other useful values. It represents
 * both human and computer players.
 * @author John Wardell
 *
 */
public class Player {
	private Card[] cards;
	private int chips;
	private String name;
	private boolean active = true;
	private boolean playing = true;
	private boolean bot;
	private boolean allIn = false;
	private int personality = 0;
	
	private int bet = 0;
	
	/**
	 * Not the constructor used in the game.
	 */
	public Player() {}
	/**
	 * The primary constructor.
	 * @param chips the quantity of chips the player starts with.
	 * @param name the player's name.
	 */
	public Player(int chips, String name) {
		this.chips = chips;
		this.name = name;
	}
	/**
	 * 
	 * @param cards the cards this player will have.
	 */
	public void setCards(Card[] cards) {
		this.cards = cards;
	}
	/**
	 * 
	 * @return this player's cards.
	 */
	public Card[] getCards() {
		Card[] returnCards = {cards[0], cards[1]};
		return returnCards;
	}
	/**
	 * The personality value influences how the player will bet.
	 * @param i the 0-9 personality of this player.
	 */
	public void setPersonality(int i) {
		personality = i;
	}
	/**
	 * 
	 * @return the personality value of this player.
	 */
	public int getPersonality() {
		return personality;
	}
	/**
	 * 
	 * @param name the name of this player.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return the name of this player.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the amount of chips the player will have for the game.
	 * This amount must be validated by the caller.
	 * @param chips this player's chips.
	 */
	public void setChips(int chips) {
		this.chips = chips;
	}
	/**
	 * 
	 * @return this player's chips amount.
	 */
	public int getChips() {
		return chips;
	}
	/**
	 * A player is active until they run out of chips.
	 * @param b true if active, false if not.
	 */
	public void setActive(boolean b) {
		active = b;
	}
	/**
	 * 
	 * @return true if active, false if not.
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * A player is playing until they fold or are inactive.
	 * Each active player is set to playing for each hand.
	 * @param b true if playing, false if not.
	 */
	public void setPlaying(boolean b) {
		playing = b;
	}
	/**
	 * 
	 * @return true if playing, false if not.
	 */
	public boolean isPlaying() {
		return playing;
	}
	/**
	 * A bot is a computer controlled player.
	 * @param b true if a bot, false if a person.
	 */
	public void setBot(boolean b) {
		bot = b;
	}
	/**
	 * 
	 * @return true if a bot, false if a person.
	 */
	public boolean isBot() {
		return bot;
	}
	/**
	 * Player is set to all in when they have bet all their chips.
	 * @param b true if all in, false if not.
	 */
	public void setAllIn(boolean b) {
		allIn = b;
	}
	/**
	 * 
	 * @return true if all in, false if not.
	 */
	public boolean isAllIn() {
		return allIn;
	}
	/**
	 * Player's bet is the total amount bet by this player in this hand.
	 * This amount must be validated by the caller.
	 * @param bet the player's total, current bet.
	 */
	public void setBet(int bet) {
		this.bet = bet;
	}
	/**
	 * 
	 * @return the player's total, current bet.
	 */
	public int getBet() {
		return bet;
	}
	
	
}
