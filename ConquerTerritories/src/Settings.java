import java.util.Scanner;

/**
 * Represents the settings for a game
 */
public class Settings
{
	private int attackerMax;
	private int defenderMax;
	private boolean allowDraws;
	private Game game;
	
	/**
	 * Sets the data for the game.
	 * 
	 * @param attackerMax maximum attacker strength
	 * @param defenderMax maximum defender retaliation strength
	 * @param allowDraws can the two countries draw?
	 * @param game the game to play
	 */
	public Settings(int attackerMax, int defenderMax, boolean allowDraws, Game game)
	{
		this.attackerMax = attackerMax;
		this.defenderMax = defenderMax;
		this.allowDraws = allowDraws;
		this.game = game;
	}
	
	/**
	 * Creates a settings object from data scanned from a text file.
	 * Arguments should be seperated by spaces in the format:
	 * int int boolean String
	 * with the final string being '/'-delimited game data
	 * 
	 * @param data the line of data in a text file
	 */
	public Settings(String data)
	{
		Scanner scan = new Scanner(data);
		attackerMax = scan.nextInt();
		defenderMax = scan.nextInt();
		allowDraws = scan.nextBoolean();
		game = new Game(scan.nextLine());
		scan.close();
	}
	
	/**
	 * @return the maximum times an attacker can steal the border provinces from another country
	 */
	public int getAttackerMax()
	{
		return attackerMax;
	}
	
	/**
	 * @return the maximum times a defender can retaliate against an attack
	 */
	public int getDefenderMax()
	{
		return defenderMax;
	}
	
	/**
	 * @return true if countries can score 0 on an attack
	 */
	public boolean drawsAllowed()
	{
		return allowDraws;
	}
	
	/**
	 * @return the game data
	 */
	public Game getGame()
	{
		return game;
	}
	
	/**
	 * Returns a string representation of the settings to be scanned from a text file
	 */
	public String toString()
	{
		return attackerMax + " " + defenderMax + " " + allowDraws + " " + game.serialize();
	}
}
