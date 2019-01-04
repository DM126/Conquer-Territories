import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//TODO add a back button to go back to the main menu (display dialog to tell the user they will lose the teams)
public class TeamSelectPanel extends JPanel
{
	private static String INVALID_NAME = "";
	private static Color INVALID_COLOR = Color.WHITE;
	
	//This stuff gets sent to the simulation panel
	private ConquerFrame parent;
	private ArrayList<Country> countries;
	private Settings settings;
	
	private JLabel teamName;
	private JButton changeName;
	private JList<Country> countryChoices; //displays all countries not part of a team
	private JList<Country> countriesOnTeam; //displays countries on the currently selected team
	private Team currentTeam;	//The team currently being displayed (null if no teams exist)
	private JComboBox<Team> teamComboBox; //lists all the teams
	private JButton selectTeam;	//Sets the current team to be the one selected in teamComboBox
	private ArrayList<Team> teams;
	private JButton addTeam;	//Creates a new team
	private JButton addCountry, removeCountry;	//Adds and removes countries from the current team
	private JButton chooseColor;
	private JButton deleteTeam;
	private JButton start;
	
	public TeamSelectPanel(ConquerFrame parent, ArrayList<Country> countries, Settings settings)
	{
		this.parent = parent;
		this.countries = countries;
		this.settings = settings;
		
		teams = new ArrayList<Team>();
		
		ButtonListener listener = new ButtonListener();
		
		countryChoices = new JList<Country>();
		JScrollPane countryScroll = new JScrollPane();
		countryScroll.setViewportView(countryChoices);
		countryChoices.addListSelectionListener(listener);
		setCountriesJList();
		
		countriesOnTeam = new JList<Country>();
		JScrollPane currentCountriesScroll = new JScrollPane();
		currentCountriesScroll.setViewportView(countriesOnTeam);
		countriesOnTeam.addListSelectionListener(listener);
		
		teamComboBox = new JComboBox<Team>();
		teamComboBox.addActionListener(listener);
		teamComboBox.setEnabled(false);
		selectTeam = new JButton("Select team");
		selectTeam.setEnabled(false);
		selectTeam.setToolTipText("Open the selected team for viewing");
		selectTeam.addActionListener(listener);
		
		teamName = new JLabel();
		setNameLabel();
		changeName = new JButton("Change name");
		changeName.setEnabled(false);
		changeName.setToolTipText("Change the name of this team");
		changeName.addActionListener(listener);
		
		//Set up the buttons
		addTeam = new JButton("New team");
		addTeam.setToolTipText("Create another team.");
		addTeam.addActionListener(listener);
		chooseColor = new JButton("Choose a color");
		chooseColor.setEnabled(false);
		chooseColor.setToolTipText("Set the color of the currently selected team.");
		chooseColor.addActionListener(listener);
		addCountry = new JButton("Add country");
		addCountry.setEnabled(false);
		addCountry.setToolTipText("Add this country to the currently selected team");
		addCountry.addActionListener(listener);
		removeCountry = new JButton("Remove country");
		removeCountry.setEnabled(false);
		removeCountry.setToolTipText("Remove the selected country from this team.");
		removeCountry.addActionListener(listener);
		deleteTeam = new JButton("Delete team");
		deleteTeam.setEnabled(false);
		deleteTeam.setToolTipText("Deletes this team");
		deleteTeam.addActionListener(listener);
		start = new JButton("Start game!");
		start.setToolTipText("Begin the game");
		start.addActionListener(listener);
		
		JPanel namePanel = new JPanel();
		namePanel.add(teamName);
		namePanel.add(changeName);
		namePanel.setPreferredSize(new Dimension(teamName.getPreferredSize().width, 
												teamName.getPreferredSize().height + 
												changeName.getPreferredSize().height + 10));
		
		JPanel teamPanel = new JPanel();
		teamPanel.add(namePanel);
		teamPanel.add(currentCountriesScroll);
		teamPanel.add(removeCountry);
		teamPanel.add(chooseColor);
		teamPanel.add(deleteTeam);
		teamPanel.setPreferredSize(new Dimension(currentCountriesScroll.getPreferredSize().width + 20, 
												namePanel.getPreferredSize().height + 
												currentCountriesScroll.getPreferredSize().height + 
												chooseColor.getPreferredSize().height + 
												deleteTeam.getPreferredSize().height + 
												removeCountry.getPreferredSize().height + 10));
		
		JPanel countryPanel = new JPanel();
		countryPanel.add(countryScroll);
		countryPanel.add(addCountry);
		countryPanel.setPreferredSize(new Dimension(countryScroll.getPreferredSize().width + 10,
													countryScroll.getPreferredSize().height + 
													addCountry.getPreferredSize().height + 10));
		
		JPanel selectionPanel = new JPanel();
		selectionPanel.add(teamComboBox);
		selectionPanel.add(selectTeam);
		selectionPanel.setPreferredSize(new Dimension(selectTeam.getPreferredSize().width + 10, 
														teamComboBox.getPreferredSize().height +
														selectTeam.getPreferredSize().height + 10));
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(selectionPanel);
		optionsPanel.add(countryPanel);
		optionsPanel.add(teamPanel);
		optionsPanel.setPreferredSize(new Dimension(selectionPanel.getPreferredSize().width + 
													countryPanel.getPreferredSize().width + 
													teamPanel.getPreferredSize().width + 100,
													teamPanel.getPreferredSize().height + 50));
		
		add(optionsPanel);
		add(addTeam);
		add(start);
		
		setPreferredSize(new Dimension(optionsPanel.getPreferredSize().width, 
										optionsPanel.getPreferredSize().height + 
										//keepNeutrals.getPreferredSize().height +
										addTeam.getPreferredSize().height +
										start.getPreferredSize().height + 10));
	}
	
	/**
	 * Sets the countryChoices JList to display all countries not currently on a team.
	 */
	private void setCountriesJList()
	{
		int selectedIndex = countryChoices.getSelectedIndex();
		DefaultListModel<Country> model = new DefaultListModel<Country>();
		for (Country c : countries)
		{
			model.addElement(c);
		}
		
		countryChoices.setModel(model);
		if (selectedIndex != -1)
		{
			countryChoices.setSelectedIndex(selectedIndex);
		}
	}
	
	/**
	 * Sets the team JList to display the countries on the currently selected teams
	 * 
	 * @param team the team to display
	 */
	private void setTeamJList(Team team)
	{
		DefaultListModel<Country> model = new DefaultListModel<Country>();
		if (team != null)
		{
			for (Country c : team.getCountries())
			{
				model.addElement(c);
			}
		}
		
		countriesOnTeam.setModel(model);
	}
	
	/**
	 * Sets the team select combobox to contain all the teams.
	 */
	private void setTeamComboBox()
	{
		teamComboBox.removeAllItems();
		for (Team t : teams)
		{
			teamComboBox.addItem(t);
		}
	}
	
	/**
	 * Sets the label to display the name of the currently selected team.
	 */
	private void setNameLabel()
	{
		if (currentTeam != null)
		{
			teamName.setText(currentTeam.getName());
			teamName.setForeground(currentTeam.getColor());
		}
		else
		{
			teamName.setText("Click 'new team' to begin");
			teamName.setForeground(Color.BLACK);
		}
	}
	
	/**
	 * Displays a color chooser to pick a new color for the currently selected team.
	 */
	private void setTeamColor()
	{		
		Color color;
		do
		{
			color = inputColor();
		}
		while (color != null && color.equals(INVALID_COLOR));
		
		if (color != null)
		{
			currentTeam.setColor(color);
			teamName.setForeground(currentTeam.getColor());
		}
	}
	
	/**
	 * Displays a dialog box to change the name of the currently selected country
	 */
	private void changeName()
	{
		String name;
		do
		{
			name = inputName();
		}
		while (name != null && name.equals(INVALID_NAME));
		
		if (name != null)
		{
			currentTeam.setName(name);
			setNameLabel();
			setTeamComboBox();
		}
	}
	
	//TODO: duplicate code in Team class
	/**
	 * Add a country or team to a list in the correct alphabetical location.
	 * 
	 * @param list the list of countries or teams
	 * @param x the playable country/team to add to the list
	 */
	private void addToCorrectLocation(ArrayList<Country> list, Country c)
	{
		boolean added = false;
		for (int i = 0; !added && i < list.size(); i++)
		{
			if (list.get(i).getName().compareToIgnoreCase(c.getName()) > 0)
			{
				list.add(i, c);
				added = true;
			}
		}
		
		if (!added)
		{
			list.add(c);
		}
	}
	
	/**
	 * Used for determining if a name chosen for a team by the user is already in use.
	 * Checks the lists of countries and teams for the name.
	 * 
	 * @param name the name chosen
	 * @return true if the name is used by another country/team already
	 */
	private boolean isDuplicateName(String name)
	{
		for (Country c : countries)
		{
			if (name.equals(c.getName()))
			{
				return true;
			}
		}
		
		for (Team team : teams)
		{
			if (name.equals(team.getName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Used for determining if a color chosen for a team by the user is already in use.
	 * Checks the lists of countries and teams for the color.
	 * 
	 * @param color the name chosen
	 * @return true if the color is used by another country/team already
	 */
	private boolean isDuplicateColor(Color color)
	{
		for (Country country : countries)
		{
			if (color.equals(country.getColor()))
			{
				return true;
			}
		}
		
		for (Team team : teams)
		{
			if (color.equals(team.getColor()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Displays a dialog box for the user to enter a name.
	 * Displays error messages for invalid input.
	 * 
	 * @returns the name of a team, INVALID_NAME if input is invalid, or null if cancel is chosen
	 */
	private String inputName()
	{
		String name = JOptionPane.showInputDialog(this, "Enter team name");
		
		if (name != null)
		{
			if (name.length() == 0)
			{
				JOptionPane.showMessageDialog(this, "Must enter a name.", "Error", JOptionPane.ERROR_MESSAGE);
				name = INVALID_NAME;
			}
			else if (name.length() > 16)
			{
				JOptionPane.showMessageDialog(this, "Name is too long.", "Error", JOptionPane.ERROR_MESSAGE);
				name = INVALID_NAME;
			}
			else if (isDuplicateName(name))
			{
				JOptionPane.showMessageDialog(this, "The name \"" + name + "\" is already in use.", "Error", JOptionPane.ERROR_MESSAGE);
				name = INVALID_NAME;
			}
		}
		
		return name;
	}
	
	/**
	 * Displays a JColorChooser for the user to choose a team color
	 * Displays error messages for invalid input.
	 * 
	 * @returns the color of a team, INVALID_COLOR if an invalid color is entered, or null if cancel is chosen
	 */
	private Color inputColor()
	{
		Color color = JColorChooser.showDialog(this, "Choose a color", Color.RED);
		
		if (color != null)
		{
			if (color.equals(Color.WHITE))
			{
				JOptionPane.showMessageDialog(this, "Cannot use the chosen color.", "Error", JOptionPane.ERROR_MESSAGE);
				color = INVALID_COLOR;
			}
			else if (isDuplicateColor(color))
			{
				JOptionPane.showMessageDialog(this, "The chosen color is already in use.", "Error", JOptionPane.ERROR_MESSAGE);
				color = INVALID_COLOR;
			}
			//TODO: Any other invalid inputs?
		}
		
		return color;
	}
	
	/**
	 * Allows the user to create a new team with a name and color
	 */
	private void createNewTeam()
	{
		String name;
		do
		{
			name = inputName();
		}
		while (name != null && name.equals(INVALID_NAME));
		
		if (name != null) //If the user did not click cancel
		{
			Color color;
			do
			{
				color = inputColor();
			}
			while (color != null && color.equals(INVALID_COLOR));
			
			if (color != null) //If the user did not click cancel
			{
				Team newTeam = new Team(name, color);
				currentTeam = newTeam;
				teams.add(newTeam);
				setTeamComboBox();
				teamComboBox.setSelectedItem(newTeam);
				setNameLabel();
				setTeamJList(newTeam);
				if (!countryChoices.isSelectionEmpty())
				{
					addCountry.setEnabled(true);
				}
				else
				{
					addCountry.setEnabled(false);
				}
				selectTeam.setEnabled(true);
				deleteTeam.setEnabled(true);
				chooseColor.setEnabled(true);
				changeName.setEnabled(true);
				teamComboBox.setEnabled(true);
			}
		}
	}
	
	/**
	 * Deletes the currently selected team.
	 */
	private void deleteTeam()
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this team?", "Delete team", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			teams.remove(currentTeam);
			for (Country c : currentTeam.getCountries())
			{
				addToCorrectLocation(countries, c);
			}
			
			if (!teams.isEmpty())
			{	
				currentTeam = teams.get(0);
			}
			else
			{
				currentTeam = null;
				selectTeam.setEnabled(false);
				deleteTeam.setEnabled(false);
				chooseColor.setEnabled(false);
				changeName.setEnabled(false);
				teamComboBox.setEnabled(false);
			}
			
			setTeamJList(currentTeam);
			setCountriesJList();
			setTeamComboBox();
			teamComboBox.setSelectedItem(currentTeam);
			setNameLabel();
		}
	}
	
	/**
	 * Adds the currently selected country to the current team.
	 */
	private void addSelectedCountry()
	{
		Country c = countryChoices.getSelectedValue();
		countries.remove(c);
		currentTeam.addCountry(c);
		int selectedIndex = countryChoices.getSelectedIndex();
		if (selectedIndex == countryChoices.getModel().getSize() - 1)
		{
			countryChoices.setSelectedIndex(selectedIndex - 1);
		}
		setCountriesJList();
		setTeamJList(currentTeam);
	}
	
	private class ButtonListener implements ActionListener, ListSelectionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == addTeam)
			{
				createNewTeam();
			}
			else if (event.getSource() == addCountry) //TODO: allow the user to double-click a country to add it
			{
				addSelectedCountry();
			}
			else if (event.getSource() == selectTeam)
			{
				if (currentTeam == null || !currentTeam.equals((Team)teamComboBox.getSelectedItem()))
				{
					currentTeam = (Team)teamComboBox.getSelectedItem();
					setTeamJList(currentTeam);
					setNameLabel();
				}
			}
			else if (event.getSource() == changeName)
			{
				changeName();
			}
			else if (event.getSource() == chooseColor)
			{
				setTeamColor();
			}
			else if (event.getSource() == removeCountry)
			{
				Country c = countriesOnTeam.getSelectedValue();
				currentTeam.removeCountry(c);
				
				addToCorrectLocation(countries, c);
				
				setCountriesJList();
				setTeamJList(currentTeam);
			}
			else if (event.getSource() == deleteTeam)
			{
				deleteTeam();
			}
			else if (event.getSource() == start)
			{
				for (Team team : teams)
				{
					if (!team.getCountries().isEmpty()) //TODO: display a confirmation box before starting if there are any empty teams
					{
						addToCorrectLocation(countries, new Country(team));
					}
				}
				
				parent.startGame(countries, settings);
			}
		}

		public void valueChanged(ListSelectionEvent event)
		{
			if (event.getSource() == countryChoices)
			{
				if (currentTeam != null)
				{
					addCountry.setEnabled(!countryChoices.isSelectionEmpty());
				}
			}
			else if (event.getSource() == countriesOnTeam)
			{
				removeCountry.setEnabled(!countriesOnTeam.isSelectionEmpty());
			}
		}
	}
}
