import java.util.ArrayList;
import java.util.Collections;

/**
 * Responsible for sorting lists and adding items to sorted lists.
 */
public class ListSorter
{
	/**
	 * Methods for comparing countries to sort them
	 */
	public enum Methods { ALPHABETICAL, SIZE, PEAK_SIZE, VANQUISHES, LARGEST_ATTACK };
	
	//Functional interface for a Lambda expression
	private interface Comparison 
	{
		/**
		 * Compares two countries
		 * 
		 * @param c1 the first country
		 * @param c2 the second country
		 * @return true if the countries are out of order
		 */
		boolean compare(Country c1, Country c2);
	}
	
	/**
	 * Sorts a list of countries using the specified method.
	 * 
	 * @param countries the list of countries to sort
	 * @param method the criteria to compare two countries (alphabetical, size, peak size)
	 */
	public static void sortCountries(ArrayList<Country> countries, Methods method)
	{
		Comparison criteria = getCriteria(method);
		
		boolean swapped = true; //If no swaps are made, the list is in order and sorting can stop.
		for (int i = 0; i < countries.size() - 1 && swapped; i++)
		{	
			swapped = false;
			for (int j = countries.size() - 1; j > i; j--)
			{
				//Compares two countries using a lambda expression
				if (criteria.compare(countries.get(j), countries.get(j-1)))
				{
					Collections.swap(countries, j, j-1);
					swapped = true;
				}
			}
		}
	}
	
	/**
	 * Adds a country to the correct location in a sorted list using the specified comparison criteria.
	 * 
	 * @param list the list to add the country to
	 * @param country the country to add to the list
	 * @param method the method for comparing two countries (alphabetical, size, peak size)
	 */
	public static void addToCorrectLocation(ArrayList<Country> list, Country country, Methods method)
	{
		Comparison criteria = getCriteria(method);
		
		boolean added = false;
		for (int i = 0; !added && i < list.size(); i++)
		{
			if (!criteria.compare(list.get(i), country))
			{
				list.add(i, country);
				added = true;
			}
		}
		
		if (!added)
		{
			list.add(country);
		}
	}
	
	/**
	 * Determines which criteria to use to compare countries
	 * 
	 * @param method the method to compare the countries (alphabetical, size, peak size)
	 * @return A lambda expression to compare two countries
	 */
	private static Comparison getCriteria(Methods method)
	{
		switch (method)
		{
		case ALPHABETICAL: 
			return (c1, c2) -> (c1.getName().compareToIgnoreCase(c2.getName()) < 0);
		case SIZE: 
			return (c1, c2) -> c1.getSize() < c2.getSize();
		case PEAK_SIZE: 
			return (c1, c2) -> c1.getPeakSize() < c2.getPeakSize();
		case VANQUISHES:
			return (c1, c2) -> c1.getVanquishes() < c2.getVanquishes();
		case LARGEST_ATTACK:
			return (c1, c2) -> c1.getLargestAttack() < c2.getLargestAttack();
		default: 
			return (c1, c2) -> (c1.getName().compareToIgnoreCase(c2.getName()) < 0); //Shouldn't be called
		}
	}
}
