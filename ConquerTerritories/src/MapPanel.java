import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Class used to display the map within the game panel.
 */
public class MapPanel extends JPanel
{
	private ArrayList<Country> countries;
	private ArrayList<Province> provinces;
	
	//TODO: comment this explaning where the exceptions can come from
	public MapPanel(ArrayList<Country> countries, Game game) throws IOException, ColorNotFoundException
	{
		this.countries = countries;
		
		//Create the list of provinces by reading the text file.
		File provinceFile = new File("Map Data/" + game.getProvincesFileName());
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
			country.scanProvinces(provinces);
		}
		
		for (Province province : provinces)
		{
			province.setSeaAdjacencies(provinces);
		}
		
		//Create polygons and set adjacencies by reading the map image file
		WorldBuilder wb = new WorldBuilder(provinces, game);
		
		//DEBUG:
		//for (Country c : countries)
		//	System.out.println(c);
		
		setPreferredSize(wb.getDimensions());
		setBackground(Color.WHITE);
		
		repaint();
	}
	
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		
		//Draw provinces
		for (Province p : provinces)
		{
			//If an exception is thrown here it means you forgot to update map.bmp, or you left a province out of countries.txt
			//try
			//{
				page.setColor(p.getColor());
			//}
			//catch (NullPointerException e)
			//{
			//	System.out.println(p.getID());
			//}
			page.drawPolygon(p.getPolygon());
		}
	}
	
	public ArrayList<Country> getCountries()
	{
		return countries;
	}
	
	/**
	 * Returns a vanquished country to the list of countries.
	 */
	public void reviveCountry(Country c)
	{
		//TODO: DUPLICATE CODE IN 2 OTHER CLASSES
		//TODO: Binary search?
		boolean added = false;
		for (int i = 0; !added && i < countries.size(); i++)
		{
			if (countries.get(i).getName().compareTo(c.getName()) > 0)
			{
				countries.add(i, c);
				added = true;
			}
		}
		
		if (!added)
		{
			countries.add(c);
		}
	}
}
