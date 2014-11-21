package texasholdem;

import javax.swing.ImageIcon;

/**
 * Provides the methods needed by the HoldemManager to interact with
 * the user interface.
 * @author John Wardell
 *
 */
public interface HoldemUI {
	/**
	 * Updates the pot, chips, and bet. Should be called on 
	 * each turn taken.
	 * @param index the index of the player to work on.
	 * @param pot the current pot.
	 * @param chips the chips of the current player.
	 * @param lastBet 
	 */
	void updateChipDisplays(int index, int pot, int chips, int lastBet);
	/**
	 * 
	 * @param commCards the cards to put in as the community cards.
	 */
	void updateCommCards(Card[] cards);
	/**
	 * Activates the user panel to accept a bet.
	 */
	void takeUserTurn();
	/**
	 * Sets the player's cards to the selected images.
	 * @param cardImages the images to use.
	 * @param index the player's index.
	 */
	void setPlayerCards(ImageIcon[] cardImages, int index);
	/**
	 * Sets the turn indicator to the specified value.
	 * @param index the index of the player selected.
	 * @param image the image to put in the indicator.
	 */
	void setIndicator(int index, ImageIcon image);
	/**
	 * Called for the winner, or each winner in case of a tie.
	 * @param winner the name of the winner.
	 * @param winnings the amount the winner won.
	 * @param hand the hand used to win.
	 */
	void showWinnerDialog(String winner, int winnings, Hand hand);
	/**
	 * Tells the frame that the phase is over.
	 */
	void phaseOver();
	/**
	 * Tells the frame that the hand is over.
	 * 
	 */
	void handOver();
	/**
	 * 
	 * @param toSee the amount to bet to stay in the game.
	 */
	void updateAmountToSee(int toSee);
	/**
	 * 
	 * @param b true if playing, false if not.
	 * @param index the index of the player to update.
	 */
	void updatePlayingDisplay(boolean b, int index);
	/**
	 * 
	 * @param b true if player is active, false if not.
	 * @param index the index of the current player.
	 */
	void updateActiveDisplay(boolean b, int index);
	/**
	 * 
	 * @param minBet the new minimum bet value.
	 */
	void updateMinBet(int minBet);
	/**
	 * This method manages restarting or ending the game
	 * when the human player wins or loses.
	 * @param b true if the game is over, false if not.
	 */
	void gameOver(boolean b);
	/**
	 * Kills this frame, ends the program.
	 */
	void terminateFrame();
	/**
	 *Called to notify a tie occurred. 
	 */
	void showTieDialog();

}
