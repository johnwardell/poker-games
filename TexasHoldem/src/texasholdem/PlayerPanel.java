package texasholdem;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
/**
 * Each PlayerPanel represents 1 player in the poker GUI.
 * Computer players and people get a PlayerPanel.
 * @author John Wardell
 *
 */
@SuppressWarnings("serial")
public class PlayerPanel extends JPanel {
    
	
    private JLabel chipsLabel;
    private JLabel activeLabel;
    private JLabel playingLabel;
    private JLabel lastBet;
    private JLabel card1;
    private JLabel card2;
    private JLabel turnLabel = new JLabel(PokerTools.EMPTY_BOX);
    private String active = "ACTIVE";
    private String inactive = "INACTIVE";
    private String playing = "PLAYING";
    private String folded = "FOLDED";
    private Border border = BorderFactory.createEtchedBorder();
    /**
     * The primary constructor.
     * @param name the player's name.
     * @param chips the player's chip amount.
     */
    public PlayerPanel(String name, int chips) {
    	super();
    	
    	setLayout(new GridBagLayout());
    	
    	JLabel nameLabel = new JLabel(name);
    	JLabel lastBetLabel = new JLabel("BET:");
 
    	card1 = new JLabel(PokerTools.NO_CARD);
    	card2 = new JLabel(PokerTools.NO_CARD);
    	lastBet = new JLabel("" + 0);
    	activeLabel = new JLabel(active);
    	playingLabel = new JLabel(playing);
    	chipsLabel = new JLabel(Integer.toString(chips));
    	setBorder(border);
    	
    	//set the layout
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.insets = new Insets(1, 1, 1, 1);
    	gbc.anchor = GridBagConstraints.CENTER;
    	
    	add(turnLabel, PokerTools.setGBC(gbc, 0, 0, 2, 1));
    	add(nameLabel, PokerTools.setGBC(gbc, 0, 1, 1, 1));
    	add(chipsLabel, PokerTools.setGBC(gbc, 1, 1, 1, 1));
    	add(activeLabel, PokerTools.setGBC(gbc, 0, 2, 1, 1));
    	add(playingLabel, PokerTools.setGBC(gbc, 1, 2, 1, 1));
    	
    	add(card1, PokerTools.setGBC(gbc, 0, 3, 1, 1));
    	add(card2, PokerTools.setGBC(gbc, 1, 3, 1, 1));
    	add(lastBetLabel, PokerTools.setGBC(gbc, 0, 4, 1, 1));
    	add(lastBet, PokerTools.setGBC(gbc, 1, 4, 1, 1));
    }
    /**
     * Not the constructor used in the game.
     */
    public PlayerPanel() { }
    /**
     * Sets the playing label to playing or folded.
     * @param b true if playing, false if not.
     */
    public void setPlayingLabel(boolean b) {
    	if (b) playingLabel.setText(playing);
    	else playingLabel.setText(folded);
    	repaint();
    }
    /**
     * Called to indicate whose turn it is currently.
     * @param arrow the ImageIcon to indicate turn taking.
     */
    public void setTurnLabel(ImageIcon arrow) {
    	turnLabel.setIcon(arrow);
    }
    /**
     * Sets the bet amount indicator.
     * @param amount the total bet of the Player associated with this panel.
     */
    public void setBetLabel(int amount) {
    	lastBet.setText("" + amount);
    	repaint();
    }
    /**
     * Indicates if the player associated with this panel is active.
     * @param b true if active, false if not.
     */
    public void setActiveLabel(boolean b) {
    	if (b) activeLabel.setText(active);
    	else activeLabel.setText(inactive);
    	repaint();
    }
    /**
     * Sets the chips to the chips of the player associated with this panel.
     * @param chips the amount of player's chips
     */
    public void setChips(int chips) {
    	chipsLabel.setText(Integer.toString(chips));
    	repaint();
    }
    /**
     * Sets the cards images. Can be an image for no card, a card back, or
     * a card itself.
     * @param cardImages the 2 ImageIcons to use for the player's cards.
     */
    public void setCards(ImageIcon[] cardImages) {
    	
    	card1.setIcon(cardImages[0]);
    	card2.setIcon(cardImages[1]);
    	repaint();
    }
    
}
