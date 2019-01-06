import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

//TODO: fix comboboxes resetting after victory/vanquishing?
//then: maybe make defenderList only display countries that share a border with combo1 country? <- might cause issues with rapid gameplay
//TODO: give comboboxes a minimum size so they don't mess up the panel organization, or reorganize the panel into two panels? buttons/comboboxes?
/**
 * Represents the main panel for the simulation.
 */
public class SimulationPanel extends JPanel
{
	private ConquerFrame parent;
	private MapPanel mapPanel;
	private JComboBox<Country> attackerSelect;
	private JComboBox<Country> defenderSelect;
	private JButton attack;
	private JButton vanquishDefender;
	private JButton undo;
	private Move lastMove; //TODO: maybe add multiple moves to a stack? //TODO: add a redo button?
	private Random rand;
	private JLabel attackDescription;
	private Leaderboard leaderboard;
	private JButton quit;
	private JButton saveGame;
	private Settings settings;
	
	private JList<Province> defenderProvinceList;
	private JButton takeProvince;
	private Province highlightedProvince;
	
	public SimulationPanel(ConquerFrame parent, ArrayList<Country> countries, Settings settings)
	{	
		this.parent = parent;
		this.settings = settings;
		
		try
		{
			mapPanel = new MapPanel(countries, settings.getGame());
		} 
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, "Error: " + settings.getGame().getMapImageName() + " could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (ColorNotFoundException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		highlightedProvince = null;
		
		rand = new Random();
		
		attackerSelect = new JComboBox<Country>();
		defenderSelect = new JComboBox<Country>();
		setComboBoxes();
		SelectionListener selectionListener = new SelectionListener();
		defenderSelect.addActionListener(selectionListener);
		
		ButtonListener buttonListener = new ButtonListener();
		
		attack = new JButton("Attack!");
		attack.addActionListener(buttonListener);
		attack.setMnemonic(KeyEvent.VK_A);
		
		vanquishDefender = ComponentFactory.createButton("Vanquish defender!", "take all provinces from the defender", buttonListener, true);
		undo = ComponentFactory.createButton("Undo", "Undo the last attack", buttonListener, false);
		
		attackDescription = new JLabel("Click 'attack' to begin.");
		
		//Panel to choose which countries to attack and defend
		JPanel attackInterface = new JPanel(); //left side of interface
		attackInterface.setPreferredSize(new Dimension(attackerSelect.getPreferredSize().width * 2 + 20, 100));
		attackInterface.add(attackerSelect);
		attackInterface.add(defenderSelect);
		attackInterface.add(attack);
		attackInterface.add(undo);
		attackInterface.add(vanquishDefender);
		
		//Panel to select and take single provinces
		JPanel provinceChooserInterface = new JPanel(); //right side of interface
		defenderProvinceList = new JList<Province>();
		JScrollPane provinceScroll = new JScrollPane();
		provinceScroll.setViewportView(defenderProvinceList);
		provinceScroll.setPreferredSize(new Dimension(200, 100));
		defenderProvinceList.addListSelectionListener(selectionListener);
		setDefenderJList((Country)defenderSelect.getSelectedItem());
		takeProvince = ComponentFactory.createButton("Take province", "Take the highlighted province", buttonListener, false);
		provinceChooserInterface.add(provinceScroll);
		provinceChooserInterface.add(takeProvince);
		
		//jpanel that holds the comboboxes, buttons, and province list
		JPanel interfacePanel = new JPanel();
		interfacePanel.add(attackInterface);
		interfacePanel.add(provinceChooserInterface);
		
		//jpanel that holds the description label
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setPreferredSize(new Dimension(500, 20));
		descriptionPanel.add(attackDescription);
		
		//JPanel that holds the interface, map and description
		JPanel gamePanel = new JPanel();
		gamePanel.add(interfacePanel);
		JScrollPane mapScroll = new JScrollPane();
		mapScroll.setViewportView(mapPanel);
		Dimension mapDimension;
		int mapWidth = mapPanel.getPreferredSize().width;
		int mapHeight = mapPanel.getPreferredSize().height;
		if (mapWidth > 1200 || mapHeight > 800)
		{
			mapDimension = new Dimension(1200, 800);
		}
		else if (mapWidth < 600 || mapHeight < 600)
		{
			mapDimension = new Dimension(600, 600);
		}
		else
		{
			mapDimension = new Dimension(mapPanel.getPreferredSize().width, mapPanel.getPreferredSize().height);
		}
		
		mapScroll.setPreferredSize(mapDimension);
		gamePanel.add(mapScroll);
		gamePanel.add(descriptionPanel);
		gamePanel.setPreferredSize(new Dimension(mapScroll.getPreferredSize().width + 20, 
												interfacePanel.getPreferredSize().height + mapScroll.getPreferredSize().height + descriptionPanel.getPreferredSize().height + 40));
		
		//Set up the side panel with the leaderboard and the save and quit buttons
		leaderboard = new Leaderboard(mapPanel.getCountries());
		saveGame = ComponentFactory.createButton("Save game", "Save the game", buttonListener, true);
		quit = ComponentFactory.createButton("Quit", "End the game and display the results", buttonListener, true);
		JPanel sidePanel = new JPanel();
		sidePanel.add(leaderboard);
		sidePanel.add(saveGame);
		sidePanel.add(quit);
		sidePanel.setPreferredSize(new Dimension(leaderboard.getPreferredSize().width + 10, 
													leaderboard.getPreferredSize().height + quit.getPreferredSize().height + 20));
		
		add(gamePanel);
		add(sidePanel);
		setPreferredSize(new Dimension(gamePanel.getPreferredSize().width + sidePanel.getPreferredSize().width + 20, 
										gamePanel.getPreferredSize().height + 20));
		
		removeEmptyCountries(countries);
	}
	
	/**
	 * Delete any countries that start with no provinces
	 * 
	 * @param countries the list of countries
	 */
	private void removeEmptyCountries(ArrayList<Country> countries)
	{
		//Find the empty countries
		ArrayList<Country> emptyCountries = new ArrayList<Country>();
		for (Country country : countries)
		{
			if (!country.hasProvinces())
			{
				emptyCountries.add(country);
			}
		}
		
		//Remove any empty countries found
		for (Country emptyCountry : emptyCountries)
		{
			mapPanel.getCountries().remove(emptyCountry);
			leaderboard.removeCountry(emptyCountry);
		}
		
		setComboBoxes();
		leaderboard.setLeaderboardText();
	}
	
	/**
	 * Sets the combo boxes to display all the countries in the list of countries
	 */
	private void setComboBoxes()
	{
		//Store the currently selected item to keep it selected after updating the combo boxes.
		Country currentlySelected = (Country)attackerSelect.getSelectedItem();
		
		attackerSelect.removeAllItems();
		defenderSelect.removeAllItems();
		
		for (Country c : mapPanel.getCountries())
		{
			attackerSelect.addItem(c);
			defenderSelect.addItem(c);
		}
		
		//Reselect the country that was selected if it's still on the map.
		if (mapPanel.getCountries().contains(currentlySelected))
		{
			attackerSelect.setSelectedItem(currentlySelected);
		}
	}
	
	/**
	 * Displays a dialog box with a message to the user.
	 * 
	 * @param message the message to display
	 */
	private void showMessage(String message)
	{
		JOptionPane.showMessageDialog(this, message);
	}
	
	/**
	 * Initiates the attack sequence between two countries.
	 * 
	 * @param attacker the country taking provinces from the other
	 * @param defender the country losing provinces
	 * @param times the number of times to execute the attack
	 */
	private void attackSequence(Country attacker, Country defender, int times)
	{
		lastMove = attacker.attack(defender, times);
		
		if (times > 1)
		{
			attackDescription.setText(attacker + " attacks " + defender + " " + times + " times!");
		}
		else
		{
			attackDescription.setText(attacker + " attacks " + defender + " " + times + " time!");
		}
		
		endAttack(defender);
	}
	
	/**
	 * Determines if an attack can take place.
	 * (i.e. the two countries are not the same and share a border)
	 * Also determines the strength of attack.
	 */
	private void beginAttack()
	{
		Country c1 = attackerSelect.getItemAt(attackerSelect.getSelectedIndex());
		Country c2 = defenderSelect.getItemAt(defenderSelect.getSelectedIndex());
		
		if (canAttack(c1, c2))
		{
			int strength;
			do
			{
				strength = rand.nextInt(settings.getAttackerMax() + settings.getDefenderMax() + 1) - settings.getDefenderMax();
			}
			while (!settings.drawsAllowed() && strength == 0); //loop will only run once if allowDraws is false
			
			if (strength > 0)
			{
				attackSequence(c1, c2, strength);
				undo.setEnabled(true);
			}
			else if (strength < 0)
			{
				attackSequence(c2, c1, -strength);
				undo.setEnabled(true);
			}
			else
			{
				attackDescription.setText(c1 + " and " + c2 + " draw!");
				lastMove = null;
				undo.setEnabled(false); //Remove this if an undo stack is added.
			}
		}
	}
	
	/**
	 * Checks if two countries can attack each other. Displays dialog boxes if they cannot.
	 * 
	 * @param c1 the attacking country
	 * @param c2 the defending country
	 * @return true if the two countries are not the same country and share a border.
	 */
	private boolean canAttack(Country c1, Country c2)
	{
		if (c1.equals(c2))
		{
			showMessage("A country cannot attack itself.");
			return false;
		}
		
		if (!c1.borders(c2))
		{
			showMessage(c1.getName() + " and " + c2.getName() + " do not share a border.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Determines if the defending country still exists and removes it if it doesn't. Also sets the leaderboard.
	 * 
	 * @param defender
	 */
	private void endAttack(Country defender)
	{
		mapPanel.repaint();
		
		//if the defender has no provinces left, remove it from the list of countries and update the combo boxes.
		if (!defender.hasProvinces())
		{
			mapPanel.getCountries().remove(defender);
			leaderboard.removeCountry(defender);
			setComboBoxes();
			showMessage(defender + " has been vanquished!");
		}
		else
		{
			setDefenderJList((Country)defenderSelect.getSelectedItem());
		}
		
		leaderboard.sortList();
		leaderboard.setLeaderboardText();
	}
	
	/**
	 * Undo the last attack and return the provinces to the original owners.
	 */
	private void undo()
	{
		if (undo != null)
		{
			lastMove.undo();
			
			//If the original owner was vanquished on the last move, add them back
			//to the list of countries.
			if (lastMove.wasVanquishing())
			{
				Country revived = lastMove.getOriginalOwner();
				mapPanel.reviveCountry(revived);
				leaderboard.reviveCountry(revived);
				
				setComboBoxes();
			}
			
			setDefenderJList((Country)defenderSelect.getSelectedItem());
			
			mapPanel.repaint();
		}
		
		undo.setEnabled(false);
	}
	
	/**
	 * Adds all the defender's province to the attacker. (including non-adjacent provinces)
	 */
	private void vanquishDefender()
	{
		Country c1 = attackerSelect.getItemAt(attackerSelect.getSelectedIndex());
		Country c2 = defenderSelect.getItemAt(defenderSelect.getSelectedIndex());
		
		if (canAttack(c1, c2))
		{
			lastMove = c1.vanquish(c2);
			attackDescription.setText(c1 + " vanquishes " + c2 + "!");
			endAttack(c2);
			undo.setEnabled(true);
		}
	}
	
	/**
	 * Sets the defender provinces JList to display the provinces of the currently selected defending country
	 * 
	 * @param country the country whose provinces to display
	 */
	private void setDefenderJList(Country country) //TODO: Only display provinces that share a border with attacker???
	{
		DefaultListModel<Province> model = new DefaultListModel<Province>();
		if (country != null)
		{
			for (Province p : country.getProvinces())
			{
				model.addElement(p);
			}
		}
		
		defenderProvinceList.setModel(model);
	}
	
	/**
	 * Displays a confirmation message and saves the game data in a text file.
	 */
	private void saveGame()
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to save? \n(saved data will be overwritten)", "Save game", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			try
			{
				leaderboard.saveGame(settings);
				JOptionPane.showMessageDialog(this, "The game was saved.", "Save game", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException e)
			{
				//e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error: The game could not be saved.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Ends the game and display the final scores.
	 */
	private void quit()
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit? \n(All unsaved progress will be lost)", "Quit", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			ArrayList<Country> finalScores = leaderboard.getResults();
			parent.showFinalResults(finalScores);
		}
	}
	
	/**
	 * Takes the currently highlighted province from the defender and gives it to the attacker.
	 */
	private void takeProvince(Province province)
	{
		Country c1 = attackerSelect.getItemAt(attackerSelect.getSelectedIndex());
		Country c2 = defenderSelect.getItemAt(defenderSelect.getSelectedIndex());
		
		if (canAttack(c1, c2)) //TODO: Allow non-adjacent countries to take provinces or no???
		{
			lastMove = c1.takeProvince(province);
			undo.setEnabled(true);
			
			attackDescription.setText(c1 + " took " + province.getName() + " from " + c2 + "!");
			
			endAttack(c2);
			
			takeProvince.setEnabled(false);
		}
	}
	
	//Event Listeners----------------------------------------------------------
	
	private class SelectionListener implements ListSelectionListener, ActionListener
	{	
		//Combobox selection is changed
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == defenderSelect)
			{
				setDefenderJList((Country)defenderSelect.getSelectedItem());
				if (highlightedProvince != null) //TODO: test if this is necessary.
				{
					highlightedProvince.setHighlighted(false);
				}
				else
				{
					highlightedProvince = null;
				}
				
				takeProvince.setEnabled(false);
			}
		}

		//JList selection is changed
		public void valueChanged(ListSelectionEvent event)
		{	
			//unhighlight the old highlighted province
			if (highlightedProvince != null)
			{
				highlightedProvince.setHighlighted(false);
			}
			
			//get the new selection (will be null if the JList is updated)
			highlightedProvince = defenderProvinceList.getSelectedValue();
			if (highlightedProvince != null)
			{
				highlightedProvince.setHighlighted(true);
				takeProvince.setEnabled(true);
			}
			
			mapPanel.repaint();
		}
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == attack)
			{
				beginAttack();
			}
			else if (event.getSource() == undo)
			{
				undo();
			}
			else if (event.getSource() == vanquishDefender)
			{
				vanquishDefender();
			}
			else if (event.getSource() == saveGame)
			{
				saveGame();
			}
			else if (event.getSource() == quit)
			{
				quit();
			}
			else if (event.getSource() == takeProvince)
			{
				takeProvince(highlightedProvince);
			}
		}
	}
}
