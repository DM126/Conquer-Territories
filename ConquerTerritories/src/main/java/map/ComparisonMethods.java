package map;

/**
 * Methods for comparing countries to sort them and display their information
 * using lambda expressions.
 */
public enum ComparisonMethods
{
	ALPHABETICAL, 	//Alphabetical order
	SIZE, 			//Number of provinces
	PEAK_SIZE, 		//Most number of provinces at one time
	VANQUISHES, 	//Number of countries vanquished
	LARGEST_ATTACK	//Most provinces taken in one attack
}
