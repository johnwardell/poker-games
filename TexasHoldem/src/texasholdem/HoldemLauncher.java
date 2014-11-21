package texasholdem;

import java.awt.EventQueue;

import javax.swing.JFrame;
/**
 * Contains the main method for launching the game.
 * Brings up a TexasHoldemFrame.
 * @author John Wardell
 *
 */
public class HoldemLauncher {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				TexasHoldemFrame frame = new TexasHoldemFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
