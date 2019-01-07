import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;

/**
 * Displays the settings on the main menu
 */
public class SettingsPanel extends JPanel
{
	//min/max strength value the user can input
	private final int MAX_STRENGTH = 10;
	private final int MIN_STRENGTH = 0;
	
	private JComboBox<Game> gameSelect; //Choose a map from Maps.txt
	private JSpinner attackerMax, defenderMax;
	private JCheckBox allowDraws;
	private MainMenu menu;
	
	public SettingsPanel(MainMenu menu)
	{
		this.menu = menu;
		
		JLabel gameSelectLabel = new JLabel("Select a map: ");
		gameSelect = new JComboBox<Game>();
		getGames();
		
		JPanel gameSelectPanel = new JPanel();
		gameSelectPanel.add(gameSelectLabel);
		gameSelectPanel.add(gameSelect);
		gameSelectPanel.setPreferredSize(new Dimension(gameSelectLabel.getPreferredSize().width + gameSelect.getPreferredSize().width + 20,
														gameSelect.getPreferredSize().height + 10));
		
		//Set up the JSpinners for getting the maximum strength values
		attackerMax = new JSpinner();
		setSpinner(attackerMax);
		attackerMax.setToolTipText("The maximum amount of times the attacker can take provinces from the defender");
		defenderMax = new JSpinner();
		setSpinner(defenderMax);
		defenderMax.setToolTipText("The maximum amount of times the defender can take provinces from the attacker");
		
		allowDraws = new JCheckBox("Allow draws?", true);
		allowDraws.setToolTipText("If checked, attacks can have 0 strength");
		//TODO: maybe make a checkbox to remove randomness and just always use the maximum
		
		//ChangeListener listener = new ValueListener();
		//attackerMax.addChangeListener(listener);
		//defenderMax.addChangeListener(listener);
		
		JLabel attackerLabel = new JLabel("Maximum attack strength:");
		JLabel defenderLabel = new JLabel("Maximum defense strength:");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(gameSelectPanel);
		optionsPanel.add(attackerLabel);
		optionsPanel.add(attackerMax);
		optionsPanel.add(defenderLabel);
		optionsPanel.add(defenderMax);
		optionsPanel.add(allowDraws);
		
		add(optionsPanel);
		
		optionsPanel.setPreferredSize(new Dimension(gameSelectPanel.getPreferredSize().width + 10, 
				attackerMax.getPreferredSize().height + defenderMax.getPreferredSize().height + allowDraws.getPreferredSize().height + gameSelectPanel.getPreferredSize().height + 20));
	}
	
	/**
	 * Initilizes a JSpinner to have a valid range for the attack/defense strengths
	 * and to be uneditable through keyboard input.
	 * 
	 * @param spinner the spinner to initialize
	 */
	private void setSpinner(JSpinner spinner)
	{
		//Set the valid range for the spinner
		SpinnerModel spinnerModel = new SpinnerNumberModel(3, MIN_STRENGTH, MAX_STRENGTH, 1);
		spinner.setModel(spinnerModel);
		
		//Disable keyboard input
		JSpinner.DefaultEditor editor = new JSpinner.DefaultEditor(spinner);
		spinner.setEditor(editor);
		JFormattedTextField textField = editor.getTextField();
		textField.setEditable(false);
		textField.setColumns(2);
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
			JOptionPane.showMessageDialog(null, "Could not find Maps.txt.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	/**
	 * @return the currently selected settings
	 */
	public Settings getSettings()
	{
		int attackerMaximum = (int)attackerMax.getValue();
		int defenderMaximum = (int)defenderMax.getValue();
		Game game = (Game)gameSelect.getSelectedItem();
		
		return new Settings(attackerMaximum, defenderMaximum, allowDraws.isSelected(), game);
	}
	
//	private class ValueListener implements ChangeListener
//	{
//		public void stateChanged(ChangeEvent arg0)
//		{
//			int attackValue = (int)attackerMax.getValue();
//			int defenseValue = (int)defenderMax.getValue();
//
//			menu.setButtonsEnabled(attackValue >= defenseValue && attackValue != 0);
//		}
//	}
}
