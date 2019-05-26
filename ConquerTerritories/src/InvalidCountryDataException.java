import java.util.InputMismatchException;

/**
 * Thrown when a country's information in the country text file contains
 * an error, i.e. using a comma instead of a forward slash or a period
 * instead of a comma.
 */
public class InvalidCountryDataException extends InputMismatchException
{
	private String countryName;
	
	/**
	 * @param countryName the name of the country whose data is invalid
	 * @param provinceList the country's list of provinces found in the country text file
	 */
	public InvalidCountryDataException(String countryName)
	{
		super();
		
		this.countryName = countryName;
	}
	
	/**
	 * Returns a description of the exception including the country name and its province data
	 */
	public String getMessage()
	{
		return "ERROR: the data for " + countryName + " in the countries text file was entered incorrectly.\n"
				+ "Make sure all delimters are correct and no information is missing.";
	}
}
