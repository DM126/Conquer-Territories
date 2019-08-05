import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Class used to display the map within the game panel.
 */
public class MapPanel extends JPanel
{
	private static final int MIN_WIDTH = 600;
	private static final int MIN_HEIGHT = 600;
	private static final int MAX_WIDTH = 1500;
	private static final int MAX_HEIGHT = 800;
	private static final Color NO_OWNER = Color.LIGHT_GRAY; //Used to draw provinces that don't belong to any country
	private static final String MAP_FOLDER = "ConquerTerritories/Map Data/"; //Folder containing the text/image files for the map data
	
	private ArrayList<Country> countries;
	private ArrayList<Province> provinces;
	private BufferedImage mapImage;
	private SimulationPanel simulationPanel;
	private RightClickMenu rightClickMenu;
	
	/**
	 * Creates the panel to display the map and instantiates the provinces.
	 * 
	 * @param countries the list of every country on the map
	 * @param game the chosen game
	 * @param simPanel the SimulationPanel that contains this MapPanel
	 * @throws IOException If there is an issue reading the province text file or the map image file
	 * @throws ColorNotFoundException if a province's color is entered incorrectly in the text file
	 * @throws InvalidCountryDataException if a country's data is entered incorrectly in the country text file
	 * @throws InvalidProvinceIDException if a province id from the countries text file does not exist
	 */
	public MapPanel(ArrayList<Country> countries, Game game, SimulationPanel simPanel) throws IOException, ColorNotFoundException, InvalidCountryDataException, InvalidProvinceIDException
	{
		this.countries = countries;
		this.simulationPanel = simPanel;
		
		//Create the list of provinces by reading the text file.
		File provinceFile = new File(MAP_FOLDER + game.getProvincesFileName());
		Scanner scan = new Scanner(provinceFile);
		provinces = new ArrayList<Province>();
		while (scan.hasNext())
		{
			provinces.add(new Province(scan.nextLine()));
		}
		scan.close();
		
		//Add the provinces to each country.
		for (Country country : countries)
		{
			country.scanProvinces(provinces); //TODO: check for duplicate provinces
		}
		
		for (Province province : provinces)
		{
			province.setSeaAdjacencies(provinces);
		}
		
		File mapFile = new File(MAP_FOLDER + game.getMapImageName());
		mapImage = ImageIO.read(mapFile);
		
		//Create polygons and set adjacencies by reading the map image file
		WorldBuilder wb = new WorldBuilder(provinces, mapImage);
		
		setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
		setBackground(Color.WHITE);
		
		ClickListener listener = new ClickListener();
		addMouseListener(listener);
		rightClickMenu = new RightClickMenu(listener);
		
		repaint();
	}
	
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		
		//Draw provinces
		for (Province p : provinces)
		{
			//If an exception is thrown here it means you forgot to update the map image file.
			if (p.getOwner() != null)
			{
				page.setColor(p.getColor());
			}
			else if (p.isHighlighted())
			{
				page.setColor(Color.BLACK);
			}
			else
			{
				page.setColor(NO_OWNER);
				//DEBUG:
				//System.out.println(p);
			}
			page.drawPolygon(p.getPolygon());
		}
	}
	
	/**
	 * @return the list of countries currently on the map
	 */
	public ArrayList<Country> getCountries()
	{
		return countries;
	}
	
	/**
	 * Returns a vanquished country to the list of countries.
	 */
	public void reviveCountry(Country c)
	{
		ListSorter.addToCorrectLocation(countries, c, ComparisonMethods.ALPHABETICAL);
	}
	
	/**
	 * @return the preferred size to display this panel within a JScrollPane
	 */
	public Dimension getViewportSize()
	{
		//The +5 offsets are to prevent a scrollbar from showing up on a size within the valid range
		int mapWidth = getValidLength(getPreferredSize().width + 5, MIN_WIDTH, MAX_WIDTH);
		int mapHeight = getValidLength(getPreferredSize().height + 5, MIN_HEIGHT, MAX_HEIGHT);
		
		return new Dimension(mapWidth, mapHeight);
	}
	
	/**
	 * Fits a side length to be within a minimum or maximum value.
	 * If the original value is greater than the maximum, the maximum will be returned.
	 * If the original value is less than the minimum, the minimum will be returned.
	 * otherwise the original value is valid and will be returned
	 * 
	 * @param originalValue the side length to fit within the range
	 * @param maxValue the maximum side length
	 * @param minValue the minimum side length
	 * @return a value within the specified range
	 */
	private int getValidLength(int originalValue, int minValue, int maxValue)
	{
		if (originalValue >= maxValue)
		{
			return maxValue;
		}
		else if (originalValue <= minValue)
		{
			return minValue;
		}
		
		return originalValue;
	}
	
	/**
	 * Returns the province at a specific location on the map.
	 * 
	 * @param x the x coordinate of the location
	 * @param y the y coordinate of the location
	 * @return the province at the location, or null if there is no province there
	 */
	private Province getProvinceAtLocation(int x, int y)
	{
		int provinceColor = mapImage.getRGB(x, y);
		
		if (provinceColor == WorldBuilder.getSeaColor())
		{
			return null;
		}
		
		for (int i = 0; i < provinces.size(); i++)
		{
			if (provinceColor == provinces.get(i).getBMPColor().getRGB())
			{
				return provinces.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Highlights or unhighlights a specific province.
	 * 
	 * @param province the province selected
	 * @param shouldHighlight determines if the province should be highlighted or unhighlighted
	 */
	private void highlightProvince(Province province, boolean shouldHighlight)
	{
		province.setHighlighted(shouldHighlight);
		repaint();
	}
	
	/**
	 * Changes the highlighted state of a clicked province and dispalys an info
	 * dialog box. Checks if the province was taken and resets its highlighted 
	 * state accordingly.
	 * 
	 * @param provinceClicked the province that was clicked
	 */
	private void selectProvince(Province provinceClicked)
	{
		//Unhighlighted provinces should be highlighted and vice versa
		boolean originalHighlightState = provinceClicked.isHighlighted();
		Country originalOwner = provinceClicked.getOwner();
		
		highlightProvince(provinceClicked, !originalHighlightState);
		simulationPanel.displayProvinceInfo(provinceClicked);
		Country currentOwner = provinceClicked.getOwner(); //check if the owner changed
		
		//If the province wasn't taken, reset it's highlighted state
		if (currentOwner == null || (originalOwner != null && originalOwner.equals(currentOwner)))
		{
			highlightProvince(provinceClicked, originalHighlightState);
		}
		else
		{
			//If the province was taken, unhighlight it
			highlightProvince(provinceClicked, false);
		}
	}
	
	//Listens for mouse clicks on the map and right click menu
	private class ClickListener implements MouseListener, ActionListener
	{
		//For clicks on the map
		public void mouseClicked(MouseEvent event)
		{
			int x = event.getX();
			int y = event.getY();
			
			Province provinceClicked = getProvinceAtLocation(x, y);
			if (provinceClicked != null)
			{
				if (event.getButton() == MouseEvent.BUTTON1) //Left click
				{
					selectProvince(provinceClicked);
				}
				else if (event.getButton() == MouseEvent.BUTTON3 && provinceClicked.getOwner() != null) //Right click
				{
					//TODO: If province is null, allow for colonization? Only for bordering country?
					rightClickMenu.show(event.getComponent(), event.getX(), event.getY(), provinceClicked.getOwner());
				}
			}
		}

		//For right click menu selections
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == rightClickMenu.makeAttacker)
			{
				simulationPanel.setAttacker(rightClickMenu.countryClicked);
			}
			else if (event.getSource() == rightClickMenu.makeDefender)
			{
				simulationPanel.setDefender(rightClickMenu.countryClicked);
			}
		}
		
		//Unused MouseListener methods
		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
		public void mousePressed(MouseEvent event) {}
		public void mouseReleased(MouseEvent event) {}
	}
	
	/**
	 * Menu that pops up upon right click to allow setting the attacker and 
	 * defender.
	 */
	private class RightClickMenu extends JPopupMenu
	{
		//TODO: Add options to attack and vanquish straight from right click menu (should this change the comboboxes?)
		//TODO: Disable/grey out options that can't be chosen?
		private Country countryClicked;
		private JLabel countryName;
		private JMenuItem makeAttacker;
		private JMenuItem makeDefender;
		
		public RightClickMenu(ClickListener listener)
		{
			//TODO: center country name in menu
			this.countryClicked = null;
			countryName = new JLabel("No country");
			countryName.setFont(new Font("Arial", Font.BOLD, 16));
			
			makeAttacker = new JMenuItem("Make attacker");
			makeAttacker.addActionListener(listener);
			makeDefender = new JMenuItem("Make defender");
			makeDefender.addActionListener(listener);
			
			this.add(countryName);
			this.addSeparator();
			this.add(makeAttacker);
			this.add(makeDefender);
		}
		
		public void show(Component component, int x, int y, Country countryClicked)
		{
			this.countryClicked = countryClicked;
			countryName.setText(countryClicked.getName());
			super.show(component, x, y);
		}
	}
}
