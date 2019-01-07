import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JPanel
{	
	//TODO: add options for random bonus attacks, strength based on number of provinces, strength based on province value, etc...
	
	private JButton play;
	private JButton selectTeams;
	private JButton loadGame;
	private SettingsPanel settingsPanel;
	private ConquerFrame parent;
	
	public MainMenu(ConquerFrame parent)
	{
		this.parent = parent;
		
		JLabel title = new JLabel("Conquer Territories");
		title.setFont(new Font("Arial", Font.BOLD, 32));
		
		ButtonListener listener = new ButtonListener();
		
		selectTeams = ComponentFactory.createButton("Select Teams", "Combine countries into teams (optional)", listener, true);
		
		File saveFile = new File("Saved Games/GameData.save");
		loadGame = ComponentFactory.createButton("Load game", "Load a saved game", listener, saveFile.exists());
		//TODO: add functionality to delete a save
		
		play = ComponentFactory.createButton("Play!", "Start a new game with the chosen settings", listener, true);
		
		settingsPanel = new SettingsPanel(this);
		
		JPanel uiPanel = new JPanel();
		uiPanel.add(settingsPanel);
		uiPanel.add(selectTeams);
		uiPanel.add(loadGame);
		uiPanel.add(play);
		
		uiPanel.setPreferredSize(new Dimension(settingsPanel.getPreferredSize().width, 
												settingsPanel.getPreferredSize().height + play.getPreferredSize().height * 2 + 20));
		
		add(title);
		add(uiPanel);
		
		setPreferredSize(new Dimension(title.getPreferredSize().width + 40, 
										title.getPreferredSize().height + uiPanel.getPreferredSize().height + 20));
	}
	
	/**
	 * Shows an error message if the user enters invalid strength values.
	 * TODO: maybe make a different error message if the user enters 0 for attack max?
	 */
	private void showErrorMessage()
	{
		JOptionPane.showMessageDialog(this, "Attack strength must be greater than 0 and defense strength", "Invalid Input", JOptionPane.OK_OPTION);
	}
	
	/**
	 * Enables or disables the play button. To be used if invalid input is
	 * detected in the settings.
	 * 
	 * @param enabled true if the buttons should be enabled, false otherwise
	 */
	public void setButtonsEnabled(boolean enabled)
	{
		play.setEnabled(enabled);
		selectTeams.setEnabled(enabled);
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == play || event.getSource() == selectTeams)
			{
				Settings settings = settingsPanel.getSettings();
				
				if (settings.getAttackerMax() > 0 && settings.getAttackerMax() >= settings.getDefenderMax())
				{
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
			else if (event.getSource() == loadGame)
			{
				parent.loadGame();
			}
		}
	}
}
