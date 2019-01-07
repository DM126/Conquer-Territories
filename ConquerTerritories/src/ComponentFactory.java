import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

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
	
	//Functional Interface for a lambda expression
	public interface Size
	{
		/**
		 * Determines which size to write to a text area (current size or peak size)
		 * 
		 * @param country the country whose size to return
		 * @return the current or peak size of the country
		 */
		int size(Country country);
	}
	
	/**
	 * Writes the list of countries and their size (current or peak) to a JTextArea
	 * 
	 * @param textArea the JTextArea to write to
	 * @param countries the list of countries to display
	 * @param sizeMethod the method that returns a country's size (Country.getSize() or Country.getPeakSize())
	 */
	public static void writeToTextArea(JTextArea textArea, ArrayList<Country> countries, Size sizeMethod)
	{
		for (int i = countries.size() - 1; i >= 0; i--)
		{
			Country c = countries.get(i);
			textArea.append((countries.size() - i) + ". " + c.getName() + " - ");
			int size = sizeMethod.size(c);
			if (size == 1)
			{
				textArea.append(size + " province\n");
			}
			else if (size > 1)
			{
				textArea.append(size + " provinces\n");
			}
			else //size == 0
			{
				textArea.append("Vanquished!\n");
			}
		}
	}
}
