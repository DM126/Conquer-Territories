import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.*;
import java.util.*;

public class LoadGamePanel extends JPanel
{
	private ConquerFrame parent;
	private JButton returnToMenu; //TODO: common fields, use inheritance?
	private JButton loadSave;
	private JButton deleteSave;
	//private JButton renameSave;
	private JList<File> saveFilesList; //JList of save files
	private JTextArea fileInfo; //displays info of the selected save file //TODO: make a separate panel with JLabels?
	
	private static final String SAVE_FOLDER_NAME = "ConquerTerritories/Saved Games"; //name of save file folder
	private static final String FILE_EXTENSION = ".save"; //file extension for save files
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	
	public LoadGamePanel(ConquerFrame parent)
	{
		this.parent = parent;
		
		EventListener listener = new EventListener();
		returnToMenu = ComponentFactory.createButton("Main Menu", "Return to the main menu", listener, true);
		loadSave = ComponentFactory.createButton("Load Game", "Load the selected file", listener, false);
		deleteSave = ComponentFactory.createButton("Delete Save", "Delete the selected file", listener, false);
		
		saveFilesList = new JList<File>();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(saveFilesList);
		scrollPane.setPreferredSize(new Dimension(300, 100));
		saveFilesList.addListSelectionListener(listener);
		saveFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		saveFilesList.setModel(new DefaultListModel<File>());
		
		fileInfo = new JTextArea(8, 20);
		fileInfo.setEditable(false);
		displayFileInfo(null);
		
		getSaveFiles();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loadSave);
		buttonPanel.add(deleteSave);
		
		add(fileInfo);
		add(buttonPanel);
		add(scrollPane);
		add(returnToMenu);
		
		setPreferredSize(new Dimension(400, 320));
	}
	
	/**
	 * Lists all the save files in the JList
	 */
	private void getSaveFiles()
	{
		File folder = new File(SAVE_FOLDER_NAME);
		File[] saveFiles = folder.listFiles();
		DefaultListModel<File> model = (DefaultListModel<File>)saveFilesList.getModel();
		model.removeAllElements();
		
		for (int i = 0; i < saveFiles.length; i++) 
		{
			File file = saveFiles[i];
			if (file.isFile() && file.getName().endsWith(FILE_EXTENSION)) //TODO: Just use FileFilter instead?
			{
				model.addElement(file); //TODO: Only list file name, don't include the folder
			}
		}
	}
	
	/**
	 * Load the selected saved file and start the game.
	 * 
	 * @param saveFile the save file to load
	 */
	private void loadSave(File saveFile)
	{
		try
		{
			Scanner scan = new Scanner(saveFile);
			Settings settings = new Settings(scan.nextLine());
			
			ArrayList<Country> countries = new ArrayList<Country>();
			while (scan.hasNext())
			{
				String countryData = scan.nextLine();
				int peakSize = Integer.parseInt(scan.nextLine());
				int vanquishes = Integer.parseInt(scan.nextLine());
				int largestAttack = Integer.parseInt(scan.nextLine());
				
				countries.add(new Country(countryData, peakSize, vanquishes, largestAttack));
			}
			scan.close();
			
			ListSorter.sortCountries(countries, ComparisonMethods.ALPHABETICAL);
			
			parent.startGame(countries, settings);
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Error: The save file could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace(); //debug
			JOptionPane.showMessageDialog(null, "Error: The save data could not be read.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Deletes the save file
	 * 
	 * @param saveFile the file to delete
	 */
	private void deleteSave(File saveFile)
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this save file?", "Confirm deletion", JOptionPane.OK_CANCEL_OPTION);
		
		if (choice == JOptionPane.OK_OPTION)
		{
			if (saveFile.delete())
			{
				JOptionPane.showMessageDialog(this, "The save file was deleted succesfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				getSaveFiles();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "The save file could not be deleted.", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	/**
	 * Display information about a save file in the text area
	 * 
	 * @param saveFile the selected file whose info to display
	 */
	private void displayFileInfo(File saveFile)
	{
		//If no file is selected, just display a default message
		if (saveFile == null)
		{
			fileInfo.setText("Select a save file.");
			return;
		}
		
		try
		{
			Path filePath = saveFile.toPath();
			BasicFileAttributes fileData = Files.readAttributes(filePath, BasicFileAttributes.class);
			
			fileInfo.setText("Save File Info:\n\n"
							+ "File Name: " + saveFile.getName() + "\n"
							+ "Date Created: " + DATE_FORMAT.format(fileData.creationTime().toMillis()) + "\n"
							+ "Last Modified: " + DATE_FORMAT.format(fileData.lastModifiedTime().toMillis()) + "\n");
		} 
		catch (IOException e)
		{
			fileInfo.setText("Error: could not read save file.");
		}
	}
	
	private class EventListener implements ActionListener, ListSelectionListener
	{
		//ActionListener
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == returnToMenu)
			{
				parent.returnToMenu();
			}
			else if (event.getSource() == loadSave)
			{
				loadSave(saveFilesList.getSelectedValue());
			}
			else if (event.getSource() == deleteSave)
			{
				deleteSave(saveFilesList.getSelectedValue());
			}
		}

		//ListSelectionListener
		public void valueChanged(ListSelectionEvent event)
		{
			if (event.getSource() == saveFilesList && !event.getValueIsAdjusting())
			{
				//enable or disable buttons depending on if a save is selected
				boolean selectionMade = !saveFilesList.isSelectionEmpty();
				loadSave.setEnabled(selectionMade);
				deleteSave.setEnabled(selectionMade);
				
				displayFileInfo(saveFilesList.getSelectedValue());
			}
		}
	}
}
