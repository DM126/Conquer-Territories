import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

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
	private JButton redo;
	private JCheckBox displayNeighborsOnly; // if checked, Only display neighboring countries in the defender combobox
	private Move lastMove; //TODO: maybe add multiple moves to a stack?
	private Random rand;
	private JLabel attackDescription;
	private Leaderboard leaderboard;
	private JButton quit;
	private JButton saveGame;
	private Settings settings;
	private JList<Province> defenderProvinceList;
	private JButton takeProvince;
	private ArrayList<Province> highlightedProvinces;
	
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
			parent.closeWithError("Error: " + settings.getGame().getMapImageName() + " could not be found.");
		}
		catch (ColorNotFoundException e)
		{
			parent.closeWithError(e.getMessage());
		}
		catch (NoSuchElementException e)
		{
			parent.closeWithError("Error: " + settings.getGame().getProvincesFileName() + 
									" contains invalid or missing data on a province and could not be read.");
		}
		
		highlightedProvinces = new ArrayList<Province>();
		
		rand = new Random();
		
		ButtonListener buttonListener = new ButtonListener();
		
		attack = new JButton("Attack!");
		attack.addActionListener(buttonListener);
		attack.setMnemonic(KeyEvent.VK_A);
		
		vanquishDefender = ComponentFactory.createButton("Vanquish defender!", "take all provinces from the defender", buttonListener, true);
		undo = ComponentFactory.createButton("Undo", "Undo the last attack", buttonListener, false);
		redo = ComponentFactory.createButton("Redo", "Redo the last undone attack", buttonListener, false);
		
		displayNeighborsOnly = new JCheckBox("Only display neighboring countries? ", false);
		attackerSelect = new JComboBox<Country>();
		attackerSelect.setPreferredSize(ComponentFactory.getComboBoxDimensions());
		defenderSelect = new JComboBox<Country>();
		defenderSelect.setPreferredSize(ComponentFactory.getComboBoxDimensions());
		setComboBoxes();
		SelectionListener selectionListener = new SelectionListener();
		attackerSelect.addActionListener(selectionListener);
		defenderSelect.addActionListener(selectionListener);
		displayNeighborsOnly.addActionListener(selectionListener);
		
		//Panel to choose which countries to attack and defend
		JPanel attackInterface = new JPanel(); //left side of interface
		attackInterface.add(attackerSelect);
		attackInterface.add(defenderSelect);
		attackInterface.add(attack);
		attackInterface.add(undo);
		attackInterface.add(redo);
		attackInterface.add(vanquishDefender);
		attackInterface.add(displayNeighborsOnly);
		attackInterface.setPreferredSize(new Dimension(attackerSelect.getPreferredSize().width * 2 + 20, 
														attackerSelect.getPreferredSize().height + 
														attack.getPreferredSize().height * 2 +
														displayNeighborsOnly.getPreferredSize().height + 20));
		
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
		provinceChooserInterface.setPreferredSize(new Dimension(provinceScroll.getPreferredSize().width + 20,
																provinceScroll.getPreferredSize().height + takeProvince.getPreferredSize().height + 10));
		
		//jpanel that holds the comboboxes, buttons, and province list
		JPanel interfacePanel = new JPanel();
		interfacePanel.add(attackInterface);
		interfacePanel.add(provinceChooserInterface);
		
		//jpanel that holds the description label
		JPanel descriptionPanel = new JPanel();
		attackDescription = new JLabel("Click 'attack' to begin.");
		descriptionPanel.setPreferredSize(new Dimension(500, 20));
		descriptionPanel.add(attackDescription);
		
		//JPanel that holds the interface, map, and description
		JPanel gamePanel = new JPanel();
		gamePanel.add(interfacePanel);
		JScrollPane mapScroll = new JScrollPane();
		mapScroll.getVerticalScrollBar().setUnitIncrement(16);
		mapScroll.getHorizontalScrollBar().setUnitIncrement(16);
		mapScroll.setViewportView(mapPanel);
		mapScroll.setPreferredSize(mapPanel.getViewportSize());
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
		Country attacker = (Country)attackerSelect.getSelectedItem();
		Country defender = (Country)defenderSelect.getSelectedItem();
		
		attackerSelect.removeAllItems();

		for (Country c : mapPanel.getCountries())
		{
			attackerSelect.addItem(c);
		}

		//Reselect the country that was selected if it's still on the map.
		if (mapPanel.getCountries().contains(attacker))
		{
			attackerSelect.setSelectedItem(attacker);
			setDefenderComboBox(attacker, defender);
		}
		else
		{
			attackerSelect.setSelectedIndex(0);
			attacker = (Country)attackerSelect.getSelectedItem();
		}
		
		setDefenderComboBox(attacker, defender);
	}
	
	/**
	 * Sets the defender combobox. will display only neighbors if the checkbox
	 * is selected, otherwise it will display every country.
	 * 
	 * @param attacker the currently selected country in the other combo box
	 */
	private void setDefenderComboBox(Country attacker, Country defender)
	{
		defenderSelect.removeAllItems();
		
		//Determine which countries should be displayed: all or just neighbors.
		ArrayList<Country> countriesToDisplay;
		if (displayNeighborsOnly.isSelected())
		{
			countriesToDisplay = attacker.getNeighbors();
		}
		else
		{
			countriesToDisplay = mapPanel.getCountries();
		}
		
		//Display the countries
		if (!countriesToDisplay.isEmpty())
		{
			for (Country c : countriesToDisplay)
			{
				defenderSelect.addItem(c);
			}
			attack.setEnabled(true);
			vanquishDefender.setEnabled(true);
		}
		else
		{
			attack.setEnabled(false);
			vanquishDefender.setEnabled(false);
		}
		
		//Reselect the original defender if it is still being displayed
		if (countriesToDisplay.contains(defender))
		{
			defenderSelect.setSelectedItem(defender);
		}
		else
		{
			if (defenderSelect.getItemCount() > 0)
			{
				defenderSelect.setSelectedIndex(0);
			}
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
		Country c1 = (Country)attackerSelect.getSelectedItem();
		Country c2 = (Country)defenderSelect.getSelectedItem();
		
		if (canAttack(c1, c2))
		{
			int strength = getAttackStrength();
			
			if (strength > 0)
			{
				attackSequence(c1, c2, strength);
			}
			else if (strength < 0)
			{
				attackSequence(c2, c1, -strength);
			}
			else //strength == 0 (draw)
			{
				attackDescription.setText(c1 + " and " + c2 + " draw!");
				lastMove = null;
				undo.setEnabled(false); //TODO: Remove this if an undo stack is added.
			}
		}
	}
	
	/**
	 * Gets the strength of the attack.
	 * 
	 * @return a value from negative max defender strength to max attacker strength
	 */
	private int getAttackStrength()
	{
		int strength;
		
		do
		{
			//get a random number from -defenderMax to attackerMax
			strength = rand.nextInt(settings.getAttackerMax() + settings.getDefenderMax() + 1) - settings.getDefenderMax();
		}
		while (!settings.drawsAllowed() && strength == 0); //loop will only run once if allowDraws is false
		
		return strength;
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
	 * Sets the UI after an attack, vanquishing, or province exchange.
	 * 
	 * @param defender the country that lost provinces in the attack
	 */
	private void endAttack(Country defender)
	{
		mapPanel.repaint();
		
		//if the defender has no provinces left, remove it from the list of countries and update the combo boxes.
		if (!defender.hasProvinces())
		{
			//TODO: fix comboboxes resetting after vanquishing?
			mapPanel.getCountries().remove(defender);
			leaderboard.removeCountry(defender);
			showMessage(defender + " has been vanquished!");
		}
		else
		{
			setDefenderJList((Country)defenderSelect.getSelectedItem());
		}

		setComboBoxes();
		leaderboard.sortList();
		leaderboard.setLeaderboardText();
		undo.setEnabled(true);
		redo.setEnabled(false);
	}
	
	/**
	 * Undo the last attack and return the provinces to the original owners.
	 */
	private void undo()
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
		
		undo.setEnabled(false);
		redo.setEnabled(true);
	}
	
	/**
	 * Redo the last undo
	 */
	private void redo()
	{
		lastMove.redo();
		endAttack(lastMove.getOriginalOwner());
		mapPanel.repaint();
	}
	
	/**
	 * Adds all the defender's province to the attacker. (including non-adjacent provinces)
	 */
	private void vanquishDefender()
	{
		Country c1 = (Country)attackerSelect.getSelectedItem();
		Country c2 = (Country)defenderSelect.getSelectedItem();
		
		if (canAttack(c1, c2))
		{
			lastMove = c1.vanquish(c2);
			attackDescription.setText(c1 + " vanquishes " + c2 + "!");
			endAttack(c2);
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
	 * 
	 * @param province the province to exchange
	 */
	private void takeProvinces(ArrayList<Province> provinces)
	{
		Country c1 = (Country)attackerSelect.getSelectedItem();
		Country c2 = (Country)defenderSelect.getSelectedItem();
		
		if (canAttack(c1, c2)) //TODO: Allow non-adjacent countries to take provinces or no???
		{
			lastMove = c1.takeProvinces(provinces);
			
			if (provinces.size() == 1)
			{
				attackDescription.setText(c1 + " took " + provinces.get(0).getName() + " from " + c2 + "!");
			}
			else
			{
				attackDescription.setText(c1 + " took multiple provinces from " + c2 + "!");
			}
			
			endAttack(c2);
			
			takeProvince.setEnabled(false);
		}
	}
	
	/**
	 * Clears the list of highlighted provinces and unhighlights all provinces in the list.
	 */
	private void clearHighlightedProvinces()
	{
		for (int i = highlightedProvinces.size() - 1; i >= 0; i--)
		{
			highlightedProvinces.get(i).setHighlighted(false);
			highlightedProvinces.remove(i);
		}
		
		takeProvince.setEnabled(false);
	}
	
	/**
	 * Update the list of selected provinces.
	 * Allows the user to select multiple provinces when using ctrl+click.
	 */
	private void updateSelectedProvinces()
	{
		//TODO: only unhighlight if the selection changed, not if selections were added.
		
		//unhighlight the old highlighted provinces
		clearHighlightedProvinces();
		
		//get the new selections (list will be empty if the JList is updated for a new country)
		List<Province> selections = defenderProvinceList.getSelectedValuesList();
		for (int i = 0; i < selections.size(); i++)
		{
			Province selectedProvince = selections.get(i);
			highlightedProvinces.add(selectedProvince);
			selectedProvince.setHighlighted(true);
			takeProvince.setEnabled(true);
		}
		
		mapPanel.repaint();
	}
	
	//Event Listeners----------------------------------------------------------
	
	private class SelectionListener implements ListSelectionListener, ActionListener
	{	
		//Combobox/checkbox selection is changed
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == defenderSelect)
			{
				setDefenderJList((Country)defenderSelect.getSelectedItem());
				clearHighlightedProvinces();
			}
			else if (event.getSource() == attackerSelect)
			{
				if (displayNeighborsOnly.isSelected())
				{
					Country attacker = (Country)attackerSelect.getSelectedItem();
					if (attacker != null)
					{
						Country defender = (Country)defenderSelect.getSelectedItem();
						setDefenderComboBox(attacker, defender);
					}
				}
			}
			else if (event.getSource() == displayNeighborsOnly)
			{
				Country attacker = (Country)attackerSelect.getSelectedItem();
				Country defender = (Country)defenderSelect.getSelectedItem();
				setDefenderComboBox(attacker, defender);
			}
		}
		
		//JList selection is changed
		public void valueChanged(ListSelectionEvent event)
		{	
			if (!event.getValueIsAdjusting())
			{
				updateSelectedProvinces();
			}
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
			else if (event.getSource() == redo)
			{
				redo();
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
				takeProvinces(highlightedProvinces);
			}
		}
	}
}
