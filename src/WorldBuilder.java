import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

//TODO: Keep track of first pixel found of new color so you don't have to scan
//		the entire image all over again?
//
//TODO: add a comment saying what will cause an IOException
/**
 * Responsible for creating the province polygons and determing province adjacencies
 */
public class WorldBuilder
{
	private BufferedImage map;
	private int seaColor;
	private int[][] pixels;
	private Game game;
	
	/**
	 * Creates the polygons for each province and determines their adjacencies 
	 * by reading the map image
	 * 
	 * @param provinces the list of all the provinces
	 * @throws IOException if map.bmp cannot be found
	 * @throws ColorNotFoundException if a province's color is entered incorrectly in the text file
	 */
	public WorldBuilder(ArrayList<Province> provinces, Game game) throws IOException, ColorNotFoundException
	{
		this.game = game;
		
		File mapFile = new File("Map Data/" + game.getMapImageName());
		map = ImageIO.read(mapFile);
		seaColor = map.getRGB(0, 0);
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
					//TODO: consider an isBorder() method?
					//Check if the current pixel is along the edge of the image
					if (x == 0 || x == map.getWidth() - 1 || y == 0 || y == map.getHeight() - 1)
					{
						border.add(new MapPoint(x, y));
					}
					else if (pixels[y - 1][x] != provinceColor || pixels[y + 1][x] != provinceColor ||
							 pixels[y][x - 1] != provinceColor || pixels[y][x + 1] != provinceColor)
					{
						border.add(new MapPoint(x, y));
						
						//check surrounding colors for adjacent provinces
						for (int by = y - 1; by <= y + 1; by++)
						{
							for (int bx = x - 1; bx <= x + 1; bx++)
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
	 * @return the dimensions of the map image
	 */
	public Dimension getDimensions()
	{
		return new Dimension(map.getWidth(), map.getHeight());
	}
}
