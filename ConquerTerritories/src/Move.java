import java.util.ArrayList;

/**
 * Represents a record provinces taken in an attack, used to undo mistakes.
 */
public class Move
{
	private ArrayList<Province> provincesTaken;
	private Country originalOwner; //lost provinces
	private Country newOwner; //gained provinces
	private boolean wasVanquishing; //did the original owner lose the last of their provinces?
	private int oldPeakSize; //Peak size of the new owner before the move.
	
	public Move(Country originalOwner, Country newOwner)
	{
		this.originalOwner = originalOwner;
		this.newOwner = newOwner;
		oldPeakSize = newOwner.getPeakSize();
		provincesTaken = new ArrayList<Province>();
		wasVanquishing = false;
	}
	
	public void add(Province p)
	{
		provincesTaken.add(p);
		wasVanquishing = !originalOwner.hasProvinces();
	}
	
	public Country getOriginalOwner()
	{
		return originalOwner;
	}
	
	public Country getNewOwner()
	{
		return newOwner;
	}
	
	public ArrayList<Province> getProvincesTaken()
	{
		return provincesTaken;
	}
	
	/**
	 * Return all provinces taken during the move and reset the countries' data.
	 */
	public void undo()
	{
		//Return the provinces taken
		for (Province p : provincesTaken)
		{
			originalOwner.addProvince(p);
		}
		
		//Reset the peak size if it was changed
		if (newOwner.getPeakSize() > oldPeakSize)
		{
			newOwner.resetPeakSize(this);
		}
		
		if (wasVanquishing)
		{
			newOwner.undoVanquish();
		}
	}
	
	/**
	 * 
	 */
	public void redo()
	{
		for (Province p : provincesTaken)
		{
			newOwner.addProvince(p);
		}
	}
	
	/**
	 * @return true if this move resulted in the vanquishing of the original owner
	 */
	public boolean wasVanquishing()
	{
		return wasVanquishing;
	}
	
	/**
	 * @return the peak size of the attacking country before the move
	 */
	public int getPeakSize()
	{
		return oldPeakSize;
	}
	
	//TODO: add a toString for debugging
}
