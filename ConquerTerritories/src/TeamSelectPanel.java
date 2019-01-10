import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TeamSelectPanel extends JPanel
{
	private static final String INVALID_NAME = "";
	private static final Color INVALID_COLOR = Color.WHITE;
	
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
	private JButton startGame;
	private JButton returnToMenu;
	
	public TeamSelectPanel(ConquerFrame parent, ArrayList<Country> countries, Settings settings)
	{
		this.parent = parent;
		this.countries = countries;
		this.settings = settings;
		
		JLabel title = new JLabel("Team Creation");
		title.setFont(new Font("Arial", Font.BOLD, 32));
		
		teams = new ArrayList<Team>();
		
		EventListener listener = new EventListener();
		
		countryChoices = new JList<Country>();
		JScrollPane countryScroll = new JScrollPane();
		countryScroll.setViewportView(countryChoices);
		countryChoices.addListSelectionListener(listener);
		setCountriesJList();
		
		countriesOnTeam = new JList<Country>();
		JScrollPane currentCountriesScrollPane = new JScrollPane();
		currentCountriesScrollPane.setViewportView(countriesOnTeam);
		countriesOnTeam.addListSelectionListener(listener);
		
		teamComboBox = new JComboBox<Team>();
		//teamComboBox.addActionListener(listener);
		teamComboBox.setEnabled(false);
		teamComboBox.setPreferredSize(ComponentFactory.getComboBoxDimensions());
		selectTeam = ComponentFactory.createButton("Select team", "Open the selected team for viewing", listener, false);
		
		teamName = new JLabel();
		setNameLabel();
		changeName = ComponentFactory.createButton("Change name", "Change the name of this team", listener, false);
		
		//Set up the buttons
		addTeam = ComponentFactory.createButton("New team", "Create another team.", listener, true);
		chooseColor = ComponentFactory.createButton("Choose a color", "Set the color of the currently selected team.", listener, false);
		addCountry =  ComponentFactory.createButton("Add country", "Add this country to the currently selected team", listener, false);
		removeCountry = ComponentFactory.createButton("Remove country", "Remove the selected country from this team.", listener, false);
		deleteTeam = ComponentFactory.createButton("Delete team", "Deletes this team", listener, false);
		startGame = ComponentFactory.createButton("Start game!", "Begin the game", listener, true);
		returnToMenu = ComponentFactory.createButton("Return to menu", "Return to the main menu", listener, true);
		
		JPanel namePanel = new JPanel();
		namePanel.add(teamName);
		namePanel.add(changeName);
		namePanel.setPreferredSize(new Dimension(teamName.getPreferredSize().width + 30, 
												teamName.getPreferredSize().height + 
												changeName.getPreferredSize().height + 10));
		
		//Panel containing the current team's info
		JPanel teamPanel = new JPanel();
		teamPanel.add(namePanel);
		teamPanel.add(currentCountriesScrollPane);
		teamPanel.add(removeCountry);
		teamPanel.add(chooseColor);
		teamPanel.add(deleteTeam);
		teamPanel.setPreferredSize(new Dimension(currentCountriesScrollPane.getPreferredSize().width + 20, 
												namePanel.getPreferredSize().height + 
												currentCountriesScrollPane.getPreferredSize().height + 
												chooseColor.getPreferredSize().height + 
												deleteTeam.getPreferredSize().height + 
												removeCountry.getPreferredSize().height + 10));
		
		//Panel containing the list of countries
		JPanel countryPanel = new JPanel();
		countryPanel.add(countryScroll);
		countryPanel.add(addCountry);
		countryPanel.setPreferredSize(new Dimension(countryScroll.getPreferredSize().width + 20,
													countryScroll.getPreferredSize().height + 
													addCountry.getPreferredSize().height + 10));
		
		//Panel for selecting which team to edit
		JPanel teamSelectionPanel = new JPanel();
		teamSelectionPanel.add(teamComboBox);
		teamSelectionPanel.add(selectTeam);
		teamSelectionPanel.add(addTeam);
		teamSelectionPanel.setPreferredSize(new Dimension(selectTeam.getPreferredSize().width + 40, 
														teamComboBox.getPreferredSize().height +
														selectTeam.getPreferredSize().height + 
														addTeam.getPreferredSize().height + 15));
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.add(teamSelectionPanel);
		optionsPanel.add(countryPanel);
		optionsPanel.add(teamPanel);
		optionsPanel.setPreferredSize(new Dimension(teamSelectionPanel.getPreferredSize().width + 
													countryPanel.getPreferredSize().width + 
													teamPanel.getPreferredSize().width + 70,
													teamPanel.getPreferredSize().height + 10));
		
		add(title);
		add(optionsPanel);
		add(startGame);
		add(returnToMenu);
		setPreferredSize(new Dimension(optionsPanel.getPreferredSize().width,
										title.getPreferredSize().height +
										optionsPanel.getPreferredSize().height + 
										startGame.getPreferredSize().height + 25));
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
			else if (name.contains("/")) //Characters used as delimiters for the data files
			{
				JOptionPane.showMessageDialog(this, "Name contains invalid characters.", "Error", JOptionPane.ERROR_MESSAGE);
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
				enableComponents(true);
			}
		}
	}
	
	/**
	 * Deletes the currently displayed team.
	 */
	private void deleteTeam()
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this team?", "Delete team", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			teams.remove(currentTeam);
			for (Country c : currentTeam.getCountries()) //Add the team's countries back to the main list, then sort the list
			{
				countries.add(c);
			}
			ListSorter.sortCountries(countries, ListSorter.Methods.ALPHABETICAL);
			
			if (!teams.isEmpty())
			{	
				currentTeam = teams.get(0);
			}
			else
			{
				currentTeam = null;
				enableComponents(false);
			}
			
			setTeamJList(currentTeam);
			setCountriesJList();
			setTeamComboBox();
			teamComboBox.setSelectedItem(currentTeam);
			setNameLabel();
		}
	}
	
	/**
	 * Enables or disables the buttons on the team display panel.
	 * 
	 * @param enabled true if the components should be enabled
	 */
	private void enableComponents(boolean enabled)
	{
		selectTeam.setEnabled(enabled);
		deleteTeam.setEnabled(enabled);
		chooseColor.setEnabled(enabled);
		changeName.setEnabled(enabled);
		teamComboBox.setEnabled(enabled);
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
	
	/**
	 * Remove the selected country from the current team and add it back 
	 * to the list of countries.
	 */
	private void removeSelectedCountry()
	{
		Country c = countriesOnTeam.getSelectedValue();
		currentTeam.removeCountry(c);
		
		ListSorter.addToCorrectLocation(countries, c, ListSorter.Methods.ALPHABETICAL);
		
		setCountriesJList();
		setTeamJList(currentTeam);
	}
	
	/**
	 * Displays a confirmation dialog box and returns to the main menu.
	 */
	private void returnToMenu()
	{
		String message = "Are you sure you want to return to the menu?";
		if (!teams.isEmpty())
		{
			message += "\n(all created teams will be lost)";
		}
		
		int choice = JOptionPane.showConfirmDialog(this, message, "Return to menu", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			parent.returnToMenu();
		}
	}
	
	private class EventListener implements ActionListener, ListSelectionListener
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
				removeSelectedCountry();
			}
			else if (event.getSource() == deleteTeam)
			{
				deleteTeam();
			}
			else if (event.getSource() == startGame)
			{
				for (Team team : teams)
				{
					if (!team.getCountries().isEmpty()) //TODO: display a confirmation box before starting if there are any empty teams
					{
						ListSorter.addToCorrectLocation(countries, new Country(team), ListSorter.Methods.ALPHABETICAL);
					}
				}
				
				parent.startGame(countries, settings);
			}
			else if (event.getSource() == returnToMenu)
			{
				returnToMenu();
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
