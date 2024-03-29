package settings;

import java.util.Scanner;

/**
 * Represents a game found in Maps.txt.
 * 
 * Format for Maps.txt: name/countriesFile.txt/provincesFile.txt/mapImage.bmp
 * '/' is the delimiter for Maps.txt, so don't use that in the names.
 */
public class Game
{
	private String gameName;
	private String countriesFileName;
	private String provincesFileName;
	private String mapImageName;
	private String serialization; //data stored in the text file for loading/saving
	
	/**
	 * @param mapData a string containing the game data delimited by '/'
	 */
	public Game(String gameData)
	{
		serialization = gameData;
		
		Scanner scan = new Scanner(gameData);
		scan.useDelimiter("/");
		
		gameName = scan.next();
		countriesFileName = scan.next();
		provincesFileName = scan.next();
		mapImageName = scan.next();
		
		scan.close();
	}
	
	public String getName()
	{
		return gameName;
	}
	
	public String getCountriesFileName()
	{
		return countriesFileName;
	}
	
	public String getProvincesFileName()
	{
		return provincesFileName;
	}
	
	public String getMapImageName()
	{
		return mapImageName;
	}
	
	/**
	 * @return a string representation of the game to be scanned from a text file
	 */
	public String serialize()
	{
		return serialization;
	}
	
	public String toString()
	{
		return gameName;
	}
}
