import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class MainMenu extends JPanel
{
	//max strength value the user can input
	private final int MAX_STRENGTH = 10;
	
	//TODO: add options for random bonus attacks, strength based on number of provinces, strength based on province value, etc...
	//TODO: maybe add a seperate settings screen?
	
	private JComboBox<Game> gameSelect; //Play with eu3 or victoria 2 map
	private JTextField attackerMax, defenderMax;
	private JButton play;
	private JButton selectTeams;
	private JCheckBox allowDraws;
	private ConquerFrame parent;
	
	//TODO: maybe make a class that combines all the data (countries list, booleans, strengths, etc.) to simplify constructors?
	
	public MainMenu(ConquerFrame parent)
	{
		this.parent = parent;
		
		JLabel gameSelectLabel = new JLabel("Select a map: ");
		gameSelect = new JComboBox<Game>();
		getGames();
		
		JPanel gameSelectPanel = new JPanel();
		gameSelectPanel.add(gameSelectLabel);
		gameSelectPanel.add(gameSelect);
		gameSelectPanel.setPreferredSize(new Dimension(gameSelectLabel.getPreferredSize().width + gameSelect.getPreferredSize().width + 20,
														gameSelect.getPreferredSize().height + 10));
		
		selectTeams = new JButton("Select Teams");
		selectTeams.setToolTipText("Combine countries into teams (optional)");
		
		play = new JButton("Play!");
		
		attackerMax = new JTextField(3);
		attackerMax.setText("3");
		attackerMax.setToolTipText("The maximum amount of times the attacker can take provinces from the defender");
		defenderMax = new JTextField(3);
		defenderMax.setText("3");
		defenderMax.setToolTipText("The maximum amount of times the defender can take provinces from the attacker");
		
		allowDraws = new JCheckBox("Allow draws?", true);
		//TODO: maybe make checkbox to remove randomness and just always use the maximum
		
		ButtonListener listener = new ButtonListener();
		play.addActionListener(listener);
		selectTeams.addActionListener(listener);
		attackerMax.getDocument().addDocumentListener(listener);
		defenderMax.getDocument().addDocumentListener(listener);
		
		JLabel title = new JLabel("Conquer Territories");
		title.setFont(new Font("Arial", Font.BOLD, 32));
		
		JLabel attackerLabel = new JLabel("Maximum attack strength:");
		JLabel defenderLabel = new JLabel("Maximum defense strength:");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(gameSelectPanel);
		optionsPanel.add(attackerLabel);
		optionsPanel.add(attackerMax);
		optionsPanel.add(defenderLabel);
		optionsPanel.add(defenderMax);
		optionsPanel.add(allowDraws);
		
		optionsPanel.setPreferredSize(new Dimension(gameSelectPanel.getPreferredSize().width + 10, 
				attackerMax.getPreferredSize().height + defenderMax.getPreferredSize().height + allowDraws.getPreferredSize().height + gameSelectPanel.getPreferredSize().height + 20));
		
		JPanel uiPanel = new JPanel();
		uiPanel.add(optionsPanel);
		uiPanel.add(selectTeams);
		uiPanel.add(play);
		
		uiPanel.setPreferredSize(new Dimension(optionsPanel.getPreferredSize().width, 
												optionsPanel.getPreferredSize().height + play.getPreferredSize().height + 10));
		add(title);
		add(uiPanel);
		
		setPreferredSize(new Dimension(title.getPreferredSize().width + 10, 
										title.getPreferredSize().height + uiPanel.getPreferredSize().height + 20));
	}
	
	/**
	 * Shows an error message if the user enters invalid strength values.
	 * TODO: maybe make a different error message if the user enters 0 for attack max?
	 */
	private void showErrorMessage()
	{
		JOptionPane.showMessageDialog(this, "Input must be a whole number from 0 to " + MAX_STRENGTH, "Invalid Input", JOptionPane.OK_OPTION);
	}
	
	/**
	 * Find all the playable maps
	 */
	private void getGames()
	{
		File dataFile = new File("Maps.txt");
		
		try
		{
			Scanner fileScan = new Scanner(dataFile);
			Game game;
			while (fileScan.hasNext())
			{
				game = new Game(fileScan.nextLine());
				gameSelect.addItem(game);
			}
			
			fileScan.close();
		} 
		catch (FileNotFoundException e)
		{
			//TODO: Display an error message and exit the program by throwing an exception here and catching up up the hierarchy.
			e.printStackTrace();
		}
	}
	
	private class ButtonListener implements ActionListener, DocumentListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == play || event.getSource() == selectTeams)
			{
				try
				{
					int attackerMaximum = Integer.parseInt(attackerMax.getText());
					int defenderMaximum = Integer.parseInt(defenderMax.getText());
					
					//Note: attacker maximum must be greater than 0, but defender maximum can equal 0.
					if (attackerMaximum > 0 && attackerMaximum <= MAX_STRENGTH && defenderMaximum >= 0 && defenderMaximum <= MAX_STRENGTH)
					{
						Game game = (Game)gameSelect.getSelectedItem();
						Settings settings = new Settings(attackerMaximum, defenderMaximum, allowDraws.isSelected(), game);
						if (event.getSource() == play)
						{
							parent.startGame(settings);
						}
						else if (event.getSource() == selectTeams)
						{
							parent.selectTeams(settings);
						}
					}
					else
					{
						showErrorMessage();
					}
				}
				catch (NumberFormatException ex)
				{
					showErrorMessage();
				}
			}
		}

		//Enables the play button if the user types something.
		public void insertUpdate(DocumentEvent e)
		{
			play.setEnabled(true);
		}

		//Disables the play button if the user empties a text field.
		public void removeUpdate(DocumentEvent e)
		{
			if (e.getDocument().getLength() < 1)
			{
				play.setEnabled(false);
			}
		}
		
		public void changedUpdate(DocumentEvent e) {}
	}
}
