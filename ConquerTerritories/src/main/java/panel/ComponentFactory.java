package panel;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

import map.*;

/**
 * Used to simplify creation and modification of Swing components and keep code looking clean.
 */
public class ComponentFactory
{
	/**
	 * Creates a new button.
	 * 
	 * @param text the text written on the button
	 * @param tooltip the tooltip text when hovering over the button
	 * @param listener the actionlistener to add to the button
	 * @param isEnabled true if the button starts off enabled
	 * @return the new button
	 */
	public static JButton createButton(String text, String tooltip, ActionListener listener, boolean isEnabled)
	{
		JButton button = new JButton(text);
		
		button.setToolTipText(tooltip);
		button.addActionListener(listener);
		button.setEnabled(isEnabled);
		
		return button;
	}
	
	/**
	 * @return the optimal combobox dimension for displaying country names
	 */
	public static Dimension getComboBoxDimensions()
	{
		return new Dimension(140, 25);
	}
	
	//Functional Interface for a lambda expression
	public interface Quantity
	{
		/**
		 * Determines which quantity to write to the text area
		 * 
		 * @param country the country whose information to return
		 * @return a string representing information about a country
		 */
		String quantity(Country country);
	}
	
	/**
	 * Writes the list of countries and their size (current or peak) to a JTextArea
	 * 
	 * @param textArea the JTextArea to write to
	 * @param countries the list of countries to display
	 * @param comparison the attribute of the countries to display (size, peak size, etc.)
	 */
	public static void writeToTextArea(JTextArea textArea, ArrayList<Country> countries, ComparisonMethods comparison)
	{
		Quantity quantityMethod = getCriteria(comparison);
		
		for (int i = 0; i < countries.size(); i++)
		{
			Country c = countries.get(i);
			textArea.append((i + 1) + ". " + c.getName() + " - " + quantityMethod.quantity(c));
		}
	}
	
	/**
	 * Determines which criteria to use to compare countries
	 * 
	 * @param method the method to compare the countries (size, peak size, etc.)
	 * @return A lambda expression to compare two countries
	 */
	private static Quantity getCriteria(ComparisonMethods method)
	{
		switch (method)
		{
		case SIZE: 
			return (country) -> country.getSizeAsString();
		case PEAK_SIZE: 
			return (country) -> country.getPeakSizeAsString();
		case VANQUISHES:
			return (country) -> country.getVanquishesAsString();
		case LARGEST_ATTACK:
			return (country) -> country.getLargestAttackAsString();
		default: 
			return (country) -> country.getName();
		}
	}
}
