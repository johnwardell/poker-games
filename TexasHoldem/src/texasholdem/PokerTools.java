package texasholdem;

import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
/**
 * This class contains static values used in a Texas Holdem poker game.
 * There are static convenience methods used in the game for retrieving
 * card image files, and for using the grid bag layout.
 * @author John Wardell
 *
 */
public class PokerTools {
	
	public static final int FOLD = -1;
	public static final ImageIcon NO_CARD = new ImageIcon("res/images/no_card.jpg");
	public static final ImageIcon CARD_BACK = new ImageIcon("res/images/card_back.jpg");
	public static final ImageIcon GREEN_TRI = new ImageIcon("res/images/green_triangle.jpg");
	public static final ImageIcon BLACK_TRI = new ImageIcon("res/images/black_triangle.jpg");
	public static final ImageIcon RED_TRI = new ImageIcon("res/images/red_triangle.jpg");
	public static final ImageIcon BLACK_BOX = new ImageIcon("res/images/black_box.jpg");
	public static final ImageIcon RED_X = new ImageIcon("res/images/red_x.jpg");
	public static final ImageIcon BLACK_ARROW = new ImageIcon("res/images/black_arrow.jpg");
	public static final ImageIcon EMPTY_BOX = new ImageIcon("res/images/empty_box.jpg");
	
	/**
	 * Uses the card to get the file for the image of the card.
	 * @param card the card for which the image is needed.
	 * @return the image of the card.
	 */
	public static ImageIcon getCardImage(Card card) {
		String fileName = ("res/images/" +
	(card.getNumber().toString()).toLowerCase() +
				"_" + (card.getSuit().toString()).toLowerCase() + ".jpg");
		
		return new ImageIcon(fileName);
	}
	/**
	 * This method makes it easier to use grid bag layout.
	 * @param gbc a reusable GridBagConstraints object.
	 * @param gridx the x value for gbc.
	 * @param gridy the y value for gbc.
	 * @param width the width value for gbc.
	 * @param height the height value for gbc.
	 * @return the GridBagConstraints set the the x, y, width, and height given.
	 */
	public static GridBagConstraints setGBC(
    		GridBagConstraints gbc, int gridx, int gridy, int width, int height)
	{
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		return gbc;
	}

}
