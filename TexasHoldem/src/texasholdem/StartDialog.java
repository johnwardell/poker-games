package texasholdem;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This is the dialog that comes up at the beginning of the game for
 * selection of number of players and entering name.
 * @author John Wardell
 *
 */
@SuppressWarnings("serial")
public class StartDialog extends JDialog {
	
	private JComboBox<Integer> numCombo;
	private JTextField nameField = new JTextField("Player", 11);
	private String name = "Player";
	private int numBots = 1;
	
	/**
	 * This is the constructor used in the game.
	 * @param owner the parent frame of this dialog.
	 */
	public StartDialog(TexasHoldemFrame owner) {
		super(owner, "Make Your Selections!", true);
		
		setLocationByPlatform(true);
		
		JButton startButton = new JButton("Start Game!");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				numBots = (Integer) numCombo.getSelectedItem();
				name = nameField.getText();
				StartDialog.this.setVisible(false);
			}
		});
		
		JLabel numLabel = new JLabel("Computer Players:");
		JLabel nameLabel = new JLabel("Enter Your Name:");
		
		JLabel card1 = new JLabel(new ImageIcon("ace_spades.jpg"));
		JLabel card2 = new JLabel(new ImageIcon("ace_hearts.jpg"));
		JLabel card3 = new JLabel(new ImageIcon("ace_clubs.jpg"));
		JLabel card4 = new JLabel(new ImageIcon("ace_diamonds.jpg"));
		
		Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};
		numCombo = new JComboBox<Integer>(numbers);
		
		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		mainPanel.add(nameLabel, PokerTools.setGBC(gbc, 0, 0, 2, 1));
		mainPanel.add(nameField, PokerTools.setGBC(gbc, 2, 0, 2, 1));
		mainPanel.add(numLabel, PokerTools.setGBC(gbc, 0, 1, 2, 1));
		mainPanel.add(numCombo, PokerTools.setGBC(gbc, 2, 1, 2, 1));
		mainPanel.add(startButton, PokerTools.setGBC(gbc, 0, 2, 4, 1));
		mainPanel.add(card1, PokerTools.setGBC(gbc, 0, 3, 1, 1));
		mainPanel.add(card2, PokerTools.setGBC(gbc, 1, 3, 1, 1));
		mainPanel.add(card3, PokerTools.setGBC(gbc, 2, 3, 1, 1));
		mainPanel.add(card4, PokerTools.setGBC(gbc, 3, 3, 1, 1));
		add(mainPanel);
		pack();
	}
	/**
	 * Gets the name entered in the start dialog.
	 * @return human player's name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @return the number of computer players.
	 */
	public int getNumBots() {
		return numBots;
	}

}
