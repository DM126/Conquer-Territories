import java.awt.*;
import java.awt.image.*;
import java.util.*;

//TODO: Keep track of first pixel found of new color so you don't have to scan
//		the entire image all over again?
/**
 * Responsible for creating the province polygons and determing province adjacencies
 */
public class WorldBuilder
{
	private static final int seaColor = Color.WHITE.getRGB();
	
	private BufferedImage map;
	private int[][] pixels;
	private Game game;
	
	/**
	 * Creates the polygons for each province and determines their adjacencies 
	 * by reading the map image
	 * 
	 * @param provinces the list of all the provinces
	 * @throws ColorNotFoundException if a province's color is entered incorrectly in the text file
	 */
	public WorldBuilder(ArrayList<Province> provinces, BufferedImage mapImage) throws ColorNotFoundException
	{	
		map = mapImage;
		pixels = new int[map.getHeight()][map.getWidth()];
		for (int y = 0; y < map.getHeight(); y++)
		{
			for (int x = 0; x < map.getWidth(); x++)
			{
				pixels[y][x] = map.getRGB(x, y);
			}
		}
		
		//Set up the hashmap of provinces hashed by rgb values
		HashMap<Integer, Province> rgbProvinces = new HashMap<Integer, Province>(provinces.size());
		for (Province p : provinces)
		{
			rgbProvinces.put(p.getBMPColor().getRGB(), p);
		}

		for (int i = 0; i < provinces.size(); i++)
		{
			//debug
			//System.out.println((i + 1) + "/" + provinces.size());
			
			createProvince(rgbProvinces, provinces.get(i));
		}
	}

	/**
	 * Creates a province's shape and finds the provinces adjacent to it.
	 * 
	 * @param rgbProvinces hashmap of provinces hashed by rgb values
	 * @param thisProvince the province to create
	 * @throws ColorNotFoundException if a province's color cannot be found in the image
	 */
	private void createProvince(HashMap<Integer, Province> rgbProvinces, Province thisProvince) throws ColorNotFoundException
	{
		int provinceColor = thisProvince.getBMPColor().getRGB();
		
		//list of points along the border of this province
		ArrayList<MapPoint> border = new ArrayList<MapPoint>();
		
		//rgb values of all colors adjacent to this province
		//HashSet is used so you don't have to repeatedly check contains().
		HashSet<Integer> adjColors = new HashSet<Integer>();
		
		//Read each pixel of the map, if it is the province's color and any 
		//adjacent pixel is not that color, add it to the border.
		for (int y = 0; y < map.getHeight(); y++)
		{
			for (int x = 0; x < map.getWidth(); x++)
			{
				if (pixels[y][x] == provinceColor)
				{
					if (isEdgeOfImage(x, y) || isAdjacentToDifferentColor(x, y, provinceColor))
					{
						border.add(new MapPoint(x, y));
						lookForAdjacentColors(x, y, provinceColor, adjColors);
					}
				}
			}
		}
		
		//Check if no pixels of this color were found:
		if (border.isEmpty())
		{
			throw new ColorNotFoundException(thisProvince, new Color(provinceColor), game);
		}
		
		//debug:
		//System.out.println(adjColors);
		
		thisProvince.setPolygon(border);
		thisProvince.setAdjacencies(rgbProvinces, adjColors);
	}
	
	/**
	 * Determines if a coordinate (either x or y) are in bounds of the map
	 * 
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @return true if the location is inside the map
	 */
	private boolean isInBounds(int x, int y)
	{
		return (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight());
	}
	
	/**
	 * Looks for different colors adjacent to the selected pixel, and if some
	 * are found, adds them to a hashset of colors (represented as integers)
	 * 
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @param provinceColor the color of this pixel
	 * @param adjColors a hashset containing the colors adjacent to this province
	 */
	private void lookForAdjacentColors(int x, int y, int provinceColor, HashSet<Integer> adjColors)
	{
		for (int by = y - 1; by <= y + 1; by++)
		{
			for (int bx = x - 1; bx <= x + 1; bx++)
			{
				if (isInBounds(bx, by))
				{
					int adj = pixels[by][bx];
					if (adj != seaColor && adj != provinceColor)
					{
						adjColors.add(adj);
					}
				}
			}
		}
	}
	
	/**
	 * Checks if a pixel is along the edge of the map
	 * 
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @return true if this pixel is along the edge of the image file
	 */
	private boolean isEdgeOfImage(int x, int y)
	{
		return (x == 0 || x == map.getWidth() - 1 || y == 0 || y == map.getHeight() - 1);
	}
	
	/**
	 * Checks if there is a different color pixel around this one.
	 * 
	 * @param x the x coordinate of the pixel
	 * @param y the y coordinate of the pixel
	 * @param provinceColor the color of the province
	 * @return true if this pixel is adjacent to a pixel of a different color
	 */
	private boolean isAdjacentToDifferentColor(int x, int y, int provinceColor)
	{
		return (pixels[y - 1][x] != provinceColor || pixels[y + 1][x] != provinceColor ||
				pixels[y][x - 1] != provinceColor || pixels[y][x + 1] != provinceColor);
	}
	
	/**
	 * Used for determining which color on the map is not part of any province.
	 * 
	 * @return the sea color on the map
	 */
	public static int getSeaColor()
	{
		return seaColor;
	}
}
