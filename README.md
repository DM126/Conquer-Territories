# Conquer-Territories
Map simulation game  

(This version is not playable)*

How it works:  The program requires three files to scan: an image file containing all the provinces of the world represented with unique colors, a text file containing information on every province in the game (i.e. its color on the image file), and another text file containing information on every country (name, representative color, and all owned provinces). First, the image file is scanned to create polygons for every province and determine which province they share a border with. Next, the provinces text file is scanned and each province is matched with a polygon using the unique color as a key. Finally, the countries are created and assigned their provinces by scanning the countries text file.  

Once the map has been created and the game has begun, you can select any country on the map and have it attack a country it shares a border with. Over time, countries will grow and shrink as they take provinces from each other. After deciding to end the game, you will be able to see different statistics for each country.  

Adding a custom map is simple: just update Maps.txt with the name of the map, the names of the text files containing information on the countries and provinces, and the name of the map image file (.bmp). Place the three files in the Map Data folder.  

*The map images used were originally created by Paradox Interactive, I have omitted them to avoid infringing on their intellectual property. Unfortunately, the game doesn't work without the image files.

Main menu:  
![MainMenu](https://github.com/DM126/Conquer-Territories/blob/master/sample%20images/mainMenu.png)

<br/>

Team selection is an optional feature to allow the user to create a team made up of multiple countries, giving them a unique name and color.  
Team selection screen:  
![TeamSelect](https://github.com/DM126/Conquer-Territories/blob/master/sample%20images/teamSelect.png)

<br/>

Game screen using a custom team:  
![SampleGame](https://github.com/DM126/Conquer-Territories/blob/master/sample%20images/sampleGame.png)
