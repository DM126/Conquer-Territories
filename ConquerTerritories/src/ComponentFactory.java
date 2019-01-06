import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Used to simplify creation of Swing components and keep code looking clean.
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
}
