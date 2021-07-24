package panel;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import map.*;

public class ResultsDisplayPanel extends JPanel
{
	private JLabel titleLabel;
	private JTextArea textArea;
	
	/**
	 * Creates a panel with a JTextbox to display countries sorted by a certain criteria.
	 * 
	 * @param title the title that describes the jtextbox
	 * @param countries the list of countries to display
	 * @param method the criteria to sort the countries and the information to display
	 */
	public ResultsDisplayPanel(String title, ArrayList<Country> countries, ComparisonMethods method)
	{
		titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 22));
		add(titleLabel);
		
		textArea = createTextArea();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		add(scrollPane);
		
		ListSorter.sortCountries(countries, method);
		ComponentFactory.writeToTextArea(textArea, countries, method);
		
		setPreferredSize(new Dimension(scrollPane.getPreferredSize().width + 20, 
				scrollPane.getPreferredSize().height + titleLabel.getPreferredSize().height + 20));
		
		//Display the top of the text area first
		textArea.setCaretPosition(0);
	}
	
	/**
	 * create a new JTextArea to be uneditable with plain size 20 arial font,
	 * 15 rows and 21 columns.
	 * 
	 * @return a new JTextArea
	 */
	private JTextArea createTextArea()
	{
		JTextArea text = new JTextArea(12, 21);
		text.setEditable(false);
		text.setFont(new Font("Arial", Font.PLAIN, 18));
		text.setText("");
		
		return text;
	}
}
