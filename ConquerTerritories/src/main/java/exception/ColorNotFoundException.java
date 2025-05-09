package exception;

import java.awt.Color;

import map.Province;
import settings.Game;

/**
 * Thrown when a province's color in the province text file does not match its
 * color on the map image file, and its color cannot be found on the map.
 * 
 * TODO: change name to reflect a general color error, i.e. duplicate colors?
 */
public class ColorNotFoundException extends Exception
{
	private final Province province;
	private final Color provinceColor;
	private final Game game;
	
	/**
	 * 
	 * @param province the province that could not be found
	 * @param provinceColor the color that could not be found
	 * @param game the game that was attempted to load
	 */
	public ColorNotFoundException(Province province, Color provinceColor, Game game)
	{
		super();
		
		this.province = province;
		this.provinceColor = provinceColor;
		this.game = game;
	}
	
	/**
	 * Returns a description of the exception including the province and its color.
	 */
	@Override
	public String getMessage()
	{
		return "Could not find color: " + provinceColor + " in province " + province + 
				"\nMake sure you enterred the color correctly in " + game.getProvincesFileName();
	}
}
