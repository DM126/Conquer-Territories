/**
 * TODO: DOCUMENT EVERYTHING IN THIS FILE
 */
public class Settings
{
	private int attackerMax;
	private int defenderMax;
	private boolean allowDraws;
	private Game game;
	
	/**
	 * TODO: DOCUMENT
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
	 * @return
	 */
	public int getAttackerMax()
	{
		return attackerMax;
	}
	
	/**
	 * @return
	 */
	public int getDefenderMax()
	{
		return defenderMax;
	}
	
	/**
	 * @return
	 */
	public boolean drawsAllowed()
	{
		return allowDraws;
	}
	
	/**
	 * @return
	 */
	public Game getGame()
	{
		return game;
	}
}
