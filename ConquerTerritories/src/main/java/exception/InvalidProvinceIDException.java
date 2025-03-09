package exception;

/**
 * Thrown when a country's list of provinces is missing a delimiter and the 
 * list of provinces is checked for too large a value. i.e. 53,54 becomes 5354,
 * which would be out of bounds.
 */
public class InvalidProvinceIDException extends IndexOutOfBoundsException
{
	private final String countryName;
	private final int provinceID;
	
	/**
	 * @param countryName the name of the country whose data is invalid
	 * @param provinceList the country's list of provinces found in the country text file
	 */
	public InvalidProvinceIDException(String countryName, int provinceID)
	{
		super();
		
		this.countryName = countryName;
		this.provinceID = provinceID;
	}
	
	/**
	 * Returns a description of the exception including the country name and its province data
	 */
	@Override
	public String getMessage()
	{
		return "ERROR: province for " + countryName + " Does not exist: " + provinceID
				+ "\nA comma was likely omitted between two province ids in the country text file.";
	}
}
