package texasholdem;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * This is the main frame for the user interface. It is the only part of the 
 * UI that interacts with the underlying game mechanics.
 * @author John Wardell
 *
 */
@SuppressWarnings("serial")
public class TexasHoldemFrame extends JFrame implements HoldemUI {
	
	private HoldemManager hm;
	private JPanel gamePanel;
	private UserPanel userPanel;
	private PlayerPanel[] playerPanels;
	private JLabel[] communityCards;
	private ImageIcon noCard = new ImageIcon("res/images/no_card.jpg");
	private String lostGame = "You Lost! Would You Like To Play Again?";
	private String wonGame = "You Won! Would You Like To Play Again?";
	
	/**
	 * This is the constructor used in the game.
	 */
	public TexasHoldemFrame() {
		
		setLocation(0, 0);
		
		setTitle("No-Limit Texas Holdem!");
		
		ImageIcon diamond = new ImageIcon("diamond.jpg");
		setIconImage(diamond.getImage());
		
		StartDialog startDialog = new StartDialog(TexasHoldemFrame.this);
		startDialog.setVisible(true);
		
		int numPlayers = startDialog.getNumBots() + 1;
		String userName = startDialog.getName();
		
		hm = new HoldemManager(numPlayers, userName, this);
		
		Player[] players = hm.getPlayers();
		gamePanel = new JPanel();
		gamePanel.setLayout(new GridBagLayout());
		
		JPanel allPlayersPanel = new JPanel();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		//set up the player panels
		playerPanels = new PlayerPanel[players.length];
		
		for (int i = 0; i < players.length; i++) {
			String name = players[i].getName();
			int chips = players[i].getChips();
			playerPanels[i] = new PlayerPanel(name, chips);
			allPlayersPanel.add(playerPanels[i]);
		}
		gamePanel.add(allPlayersPanel, PokerTools.setGBC(gbc, 0, 0, 7, 1));
		//put community cards in their panel
		JPanel commCardsPanel = new JPanel();
		communityCards = new JLabel[5];
		for (int i = 0; i < 5; i++) {
			communityCards[i] = new JLabel(noCard);
			commCardsPanel.add(communityCards[i]);
		}
		
		JLabel commCardsLabel = new JLabel("Community Cards");
		gamePanel.add(commCardsLabel, PokerTools.setGBC(gbc, 0, 1, 7, 1));
		gamePanel.add(commCardsPanel, PokerTools.setGBC(gbc, 0, 2, 7, 1));
		
		userPanel = new UserPanel();
		gamePanel.add(userPanel, PokerTools.setGBC(gbc, 0, 3, 7, 1));
		
		
		add(gamePanel);
		pack();
		//set the frame size
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dimension = tk.getScreenSize();
		setSize(dimension.width, ((dimension.height * 2) / 3));
	}
	/**
	 * Sets the turn indicator to the specified value.
	 * @param index the index of the player selected.
	 * @param image the image to put in the indicator.
	 */
	public void setIndicator(int index, ImageIcon image) {
		playerPanels[index].setTurnLabel(image);
	}
	/**
	 * Activates the user panel to accept a bet.
	 */
	public void takeUserTurn() {
		userPanel.activateUserBetting();
	}
	/**
	 * Tells the frame that the phase is over.
	 */
	public void phaseOver() {
		userPanel.activateNextTurnButton();
	}
	/**
	 * Sets the player's cards to the selected images.
	 * @param cardImages the images to use.
	 * @param index the player's index.
	 */
	public void setPlayerCards(ImageIcon[] cardImages, int index) {
		playerPanels[index].setCards(cardImages);
	}
	/**
	 * 
	 * @param commCards the cards to put in as the community cards.
	 */
	public void updateCommCards(Card[] commCards) {
		int i = 0;
		while (i < commCards.length) {
			communityCards[i].setIcon(PokerTools.getCardImage(commCards[i]));
			i++;
		}
		while (i < communityCards.length) {
			communityCards[i].setIcon(noCard);
			i++;
		}
			
	}
	/**
	 * 
	 * @param minBet the new minimum bet value.
	 */
	public void updateMinBet(int minBet) {
		userPanel.setMinBetLabel(minBet);
	}
	/**
	 * Tells the frame that the hand is over.
	 * 
	 */
	public void handOver() {
		userPanel.startNewHand();
	}
	/**
	 * Updates the pot, chips, and bet. Should be called on 
	 * each turn taken.
	 * @param index the index of the player to work on.
	 * @param pot the current pot.
	 * @param chips the chips of the current player.
	 * @param lastBet 
	 */
	public void updateChipDisplays(int index, int pot, int chips, int lastBet) {
		playerPanels[index].setChips(chips);
		
		playerPanels[index].setBetLabel(lastBet);
		
		userPanel.setPotLabel(pot);
		
		
		repaint();
	}
	/**
	 * Sets all the bets in the player panels to 0.
	 */
	private void resetLastBets() {
		for (PlayerPanel pp: playerPanels) {
			pp.setBetLabel(0);
		}
	}
	/**
	 * 
	 * @param b true if player is active, false if not.
	 * @param index the index of the current player.
	 */
	public void updateActiveDisplay(boolean b, int index) {
		playerPanels[index].setActiveLabel(b);
		if (!b) {
			
			playerPanels[index].setTurnLabel(PokerTools.RED_X);
		}
	}
	/**
	 * 
	 * @param toSee the amount to bet to stay in the game.
	 */
	public void updateAmountToSee(int toSee) {
		userPanel.setToSee(toSee);
	}
	/**
	 * Called for the winner, or each winner in case of a tie.
	 * @param winner the name of the winner.
	 * @param winnings the amount the winner won.
	 * @param hand the hand used to win.
	 */
	public void showWinnerDialog(String winner, int winnings, Hand hand) {
		String handString = hand.toString().toLowerCase();
		handString = handString.replaceAll("_", " ");
		
		JOptionPane.showMessageDialog(TexasHoldemFrame.this,
				winner + " won " + winnings + " with a " + handString);
	}
	/**
	 *Called to notify a tie occurred. 
	 */
	public void showTieDialog() {
		String message = "There was a tie!";
		JOptionPane.showMessageDialog(TexasHoldemFrame.this, message);
	}
	/**
	 * 
	 * @param b true if playing, false if not.
	 * @param index the index of the player to update.
	 */
	public void updatePlayingDisplay(boolean b, int index) {
		playerPanels[index].setPlayingLabel(b);
		
	}
	/**
	 * This method manages restarting or ending the game
	 * when the human player wins or loses.
	 * @param b true if the game is over, false if not.
	 */
	public void gameOver(boolean b) {
		String message = lostGame;
		if (b) {message = wonGame;}
		
		int selection = JOptionPane.showConfirmDialog(TexasHoldemFrame.this,
				message, "Game Over", JOptionPane.YES_NO_OPTION);
		if (selection == JOptionPane.NO_OPTION) {
			dispose();
		}
		
		else if (selection == JOptionPane.YES_OPTION) {
			hm.startNewGame();
		}
				
	}
	/**
	 * Kills this frame, ends the program.
	 */
	public void terminateFrame() {
		dispose();
	}
	/**
	 * This panel contains info and buttons needed by the human player.
	 * @author John Wardell
	 *
	 */
	public class UserPanel extends JPanel {
		
		private JLabel minBetLabel;
		private JLabel toSeeLabel;
		private JLabel potLabel;
		private JTextField betField;
		private JButton betButton;
		private JButton foldButton;
		private JButton startButton;
		private JButton nextTurnButton;
		private JButton plusOneButton;
		private JButton plusFiveButton;
		private JButton plusTenButton;
		private JButton resetButton;
		private JButton allInButton;
		private int bet = 0;
		
		/**
		 * This is the constructor used in the game.
		 */
		public UserPanel() {
			super();
			
			JLabel potNameLabel = new JLabel("Current Pot:");
			JLabel betNameLabel = new JLabel("Your Bet:");
			JLabel toSeeNameLabel = new JLabel("Amount To See/Call:");
			JLabel minBetNameLabel = new JLabel("Blind Bet:");
			
			betField = new JTextField("0", 6);
			betField.setEditable(false);
			potLabel = new JLabel("0");
			toSeeLabel = new JLabel("0");
			minBetLabel = new JLabel("10");
			
			nextTurnButton = new JButton("See Card(s)");
			nextTurnButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					nextTurnButton.setEnabled(false);
					hm.startNextPhase();
					
				}
			});
			
			startButton = new JButton("Start Hand!");
			startButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					resetLastBets();
					hm.startHand();
					startButton.setEnabled(false);
				}
			});
			
			foldButton = new JButton("Fold");
			foldButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					deactivateUserBetting();
					hm.acceptUserBet(PokerTools.FOLD);
					
				}
			});
			
			betButton = new JButton("Place Bet!");
			betButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					deactivateUserBetting();
					int temp = bet;
					bet = 0;
					hm.acceptUserBet(temp);
				}
			});
			
			plusOneButton = new JButton("+1");
			plusOneButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if ((bet + 1) <= hm.getMaxBet()) {
						bet++;
						betField.setText("" + bet);
						betField.repaint();
					}
				}
			});
			plusFiveButton = new JButton("+5");
			plusFiveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if ((bet + 5) <= hm.getMaxBet()) {
						bet += 5;
						betField.setText("" + bet);
						betField.repaint();
					}
				}
			});
			plusTenButton = new JButton("+10");
			plusTenButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if ((bet + 10) <= hm.getMaxBet()) {
						bet += 10;
						betField.setText("" + bet);
						betField.repaint();
					}
				}
			});
			resetButton = new JButton("Reset Bet");
			resetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					bet = hm.getAmtToSee();
					betField.setText("" + bet);
					betField.repaint();
				}
			});
			allInButton = new JButton("All In!");
			allInButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					bet = hm.getMaxBet();
					betField.setText("" + bet);
					betField.repaint();
				}
			});
			
			deactivateUserBetting();
			nextTurnButton.setEnabled(false);
			
			JPanel startPanel = new JPanel();
			JPanel betFoldPanel = new JPanel();
			JPanel numBetPanel = new JPanel();
			JPanel resetAllInPanel = new JPanel();
			
			JPanel westPanel = new JPanel();
			JPanel eastPanel = new JPanel();
			westPanel.setLayout(new GridBagLayout());
			eastPanel.setLayout(new GridBagLayout());
			
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5, 5, 5, 5);
			
			
			betFoldPanel.add(betButton);
			betFoldPanel.add(foldButton);
			startPanel.add(startButton);
			startPanel.add(nextTurnButton);
			numBetPanel.add(plusOneButton);
			numBetPanel.add(plusFiveButton);
			numBetPanel.add(plusTenButton);
			resetAllInPanel.add(resetButton);
			resetAllInPanel.add(allInButton);
			
			westPanel.add(potNameLabel, PokerTools.setGBC(gbc, 0, 0, 1, 1));
			westPanel.add(potLabel, PokerTools.setGBC(gbc, 1, 0, 1, 1));
			westPanel.add(toSeeNameLabel, PokerTools.setGBC(gbc, 0, 1, 1, 1));
			westPanel.add(toSeeLabel, PokerTools.setGBC(gbc, 1, 1, 1, 1));
			westPanel.add(betNameLabel, PokerTools.setGBC(gbc, 0, 2, 1, 1));
			westPanel.add(betField, PokerTools.setGBC(gbc, 1, 2, 1, 1));
			westPanel.add(minBetNameLabel, PokerTools.setGBC(gbc, 0, 3, 1, 1));
			westPanel.add(minBetLabel, PokerTools.setGBC(gbc, 1, 3, 1, 1));
			westPanel.add(startPanel, PokerTools.setGBC(gbc, 0, 4, 2, 1));
			eastPanel.add(numBetPanel, PokerTools.setGBC(gbc, 0, 0, 2, 1));
			eastPanel.add(resetAllInPanel, PokerTools.setGBC(gbc, 0, 1, 2, 1));
			eastPanel.add(betFoldPanel, PokerTools.setGBC(gbc, 0, 2, 2, 1));
			add(westPanel, PokerTools.setGBC(gbc, 0, 0, 1, 1));
			add(eastPanel, PokerTools.setGBC(gbc, 1, 0, 1, 1));
			
			
		}
		/**
		 * Lets the user choose to start another hand.
		 */
		public void startNewHand() {
			startButton.setEnabled(true);
			nextTurnButton.setEnabled(false);
			deactivateUserBetting();
		}
		/**
		 * Shows the user the amount to see or call.
		 * @param toSee amount needed to bet to stay in the game.
		 */
		public void setToSee(int toSee) {
			bet = toSee;
			toSeeLabel.setText("" + toSee);
			betField.setText("" + toSee);
			toSeeLabel.repaint();
			betField.repaint();
		}
		/**
		 * 
		 * @param minBet the amount to put in the minBet label.
		 */
		public void setMinBetLabel(int minBet) {
			minBetLabel.setText("" + minBet);
			minBetLabel.repaint();
		}
		/**
		 * 
		 * @param pot the amount to put in the pot label
		 */
		public void setPotLabel(int pot) {
			potLabel.setText("" + pot);
			potLabel.repaint();
		}
		/**
		 * Turns on the buttons the user will need to take a turn.
		 */
		public void activateUserBetting() {
			nextTurnButton.setEnabled(false);
			betButton.setEnabled(true);
			foldButton.setEnabled(true);
			plusOneButton.setEnabled(true);
			plusFiveButton.setEnabled(true);
			plusTenButton.setEnabled(true);
			resetButton.setEnabled(true);
			allInButton.setEnabled(true);
			betField.repaint();
		}
		/**
		 * Turns on the button that starts the next phase.
		 */
		public void activateNextTurnButton() {
			nextTurnButton.setEnabled(true);
		}
		/**
		 * Turns off the buttons that the user uses to take a turn.
		 */
		public void deactivateUserBetting() {
			
			betButton.setEnabled(false);
		    foldButton.setEnabled(false);
		    plusOneButton.setEnabled(false);
			plusFiveButton.setEnabled(false);
			plusTenButton.setEnabled(false);
			resetButton.setEnabled(false);
			allInButton.setEnabled(false);
			betField.setText("0");
		    repaint();
		}
	}
	
}
