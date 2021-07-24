package map;

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
	private int oldLargestAttack; //most number of provinces taken by the new owner before the move.
	
	/**
	 * Creates a record of an attack.
	 * 
	 * @param originalOwner the country that lost provinces
	 * @param newOwner the country that gained provinces
	 */
	public Move(Country originalOwner, Country newOwner)
	{
		this.originalOwner = originalOwner;
		this.newOwner = newOwner;
		oldPeakSize = newOwner.getPeakSize();
		oldLargestAttack = newOwner.getLargestAttack();
		provincesTaken = new ArrayList<Province>();
		wasVanquishing = false;
	}
	
	//TODO: consider creating a subclass for colonization moves
	/**
	 * Creates a record of a country colonizing an unowned province.
	 * 
	 * @param newOwner the country that took the province
	 * @param provinceTaken the colonized province
	 */
	public Move(Country newOwner, Province provinceTaken)
	{
		this.originalOwner = null;
		this.newOwner = newOwner;
		oldPeakSize = newOwner.getPeakSize();
		oldLargestAttack = newOwner.getLargestAttack();
		provincesTaken = new ArrayList<Province>();
		provincesTaken.add(provinceTaken);
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
		if (originalOwner != null)
		{
			//Return the provinces taken from an attack
			for (Province p : provincesTaken)
			{
				originalOwner.addProvince(p);
			}
		}
		else
		{
			//Uncolonize the province
			newOwner.removeProvince(provincesTaken.get(0));
			provincesTaken.get(0).setOwner(null);
		}
		newOwner.undoMove(this);
	}
	
	/**
	 * Redo this move after it was undone and retake all provinces.
	 */
	public void redo()
	{
		for (Province p : provincesTaken)
		{
			newOwner.addProvince(p);
		}
		
		newOwner.redoMove(this);
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
	
	/**
	 * @return the previous largest attack of the new owner
	 */
	public int getLargestAttack()
	{
		return oldLargestAttack;
	}
	
	/**
	 * @return the number of provinces taken in this attack
	 */
	public int getNumberOfProvincesTaken()
	{
		return provincesTaken.size();
	}
	
	//TODO: add a toString for debugging
}
