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
	
	/**
	 * @param mapData a string containing the game data delimited by '/'
	 */
	public Game(String gameData)
	{
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
	
	public String toString()
	{
		return gameName;
	}
}
