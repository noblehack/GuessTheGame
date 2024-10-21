import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static JFrame mainFrame;
    public static String totalPath;
    public static ImageIcon jImage;
    public static BufferedImage image;
    public static JLabel  guessesLeftField = new JLabel("6 guesses left");
    public static int guessesLeft = 5;
    static int imageX =26;
    static int imageY = 15;
    static ArrayList<Integer> indexListOfGames = new ArrayList<>();
    static int currentGameCount = 0;
    static String currentGameName;
    static int ammountOfGames = 16326;
    static String[] listOfGameNames = new String[ammountOfGames];
    static JComboBox boxGameList;
    static JLabel platformHintLabel = new JLabel("Platform: ");
    static String userGuess = "";
    static JButton levelSelectButton = new JButton();
    static JLabel levelText = new JLabel();
   static JTextField guessField;
   static JLabel yearHint = new JLabel("Year(s): ");
   static boolean wantRandomized = false;
   static boolean ranOutOfGuesses = false;


    public static void main(String[] args) throws IOException {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        path +="\\src";
        totalPath = path;

    setupArrayList();
    makeArrayOfGameNames();


        currentGameName = getGameNameByIndex(indexListOfGames.get(currentGameCount), path);
         image = readImageIn(currentGameName,path);
        //game image is image
        //game name is currentGameName
        //game index spot is indexSpot
        createFrame(image);
        guessField.requestFocusInWindow();


        setupEnterListener();
        setupArrowKeys();
        setupGuessFieldListener();
        setupRandomizer();
        setupValidation();




    }



    public static BufferedImage readImageIn(String gameName, String imagePath){
BufferedImage image;
try {
    image = ImageIO.read(new File(imagePath + "\\games\\" + gameName + ".jpg"));
    if (image == null)
        image = ImageIO.read(new File(imagePath + "\\games\\" + gameName + ".jpeg"));
    if (image == null)
        image = ImageIO.read(new File(imagePath + "\\games\\" + gameName + ".png"));
    return image;
} catch (IOException e){
    System.out.println("image reading failed");
}
       return null;

    }


    public static String getGameNameByIndex(int indexToFind, String path) throws IOException {
        String gameName = "";


        String gamePath = path + "\\vgsales.csv";


        try (Stream<String> lines = Files.lines(Paths.get(gamePath))) {
            String line2 = lines.skip(indexToFind-1).findFirst().get();
            gameName = line2.substring(line2.indexOf(",")+1);
            gameName = gameName.substring(0, gameName.indexOf(","));

        }

        return gameName;

    }

    public static void createFrame(Image image){
        // Create the main frame
        mainFrame = new JFrame("Guessing Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1920, 1080);

        // Create a menu bar

        guessField = new JTextField();
        boxGameList = new JComboBox<>(listOfGameNames);
        System.out.println("size of box is " + boxGameList.getItemCount());



        boxGameList = new JComboBox(listOfGameNames);

        boxGameList.setPreferredSize(new Dimension(400,30));
        boxGameList.setFont(new Font("Serif", Font.PLAIN, 20));

        guessField.setPreferredSize(new Dimension(200,30));
        guessField.setFont(new Font("Serif", Font.PLAIN, 20));

        //creates a button?
        JButton button = new JButton("Guess");
        button.setFont(new Font("Serif", Font.PLAIN, 20));
        button.setPreferredSize(new Dimension(100,60));
        button.addActionListener(e -> {
            try {
                nextButtonPressed();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        guessesLeftField.setFont(new Font("Serif", Font.PLAIN, 20));
        guessesLeftField.setText(guessesLeft+ " guesses left");
        Panel lowerPanel = new Panel();

        lowerPanel.add(guessField);
        lowerPanel.add(boxGameList);
        lowerPanel.add(button);
        lowerPanel.add(guessesLeftField);

        Panel westPanel = new Panel(new GridLayout(10,1));


        platformHintLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        yearHint.setFont(new Font("Serif", Font.PLAIN, 20));
        westPanel.add(platformHintLabel);
        westPanel.add(yearHint);

        Panel eastPanel = new Panel();
       eastPanel.setLayout(new GridLayout(10,1));
        levelSelectButton.addActionListener(e->{
            levelSelection();
        });
        levelSelectButton.setText("Select Level");
        levelText.setText("Level: " + currentGameCount);
        levelText.setPreferredSize(new Dimension(100,20));
        levelText.setFont(new Font("Serif", Font.PLAIN, 20));

        eastPanel.add(levelSelectButton);
        eastPanel.add(levelText);




        // Set layout for the main frame
        mainFrame.setLayout(new BorderLayout());
        Image newImage = image.getScaledInstance(imageX, imageY, Image.SCALE_AREA_AVERAGING); //for 16x9 image
        Image newImage2 = newImage.getScaledInstance(1066,600,Image.SCALE_SMOOTH);
         jImage = new ImageIcon(newImage2);


        JLabel imageLabel = new JLabel(jImage);

        mainFrame.add(imageLabel,BorderLayout.NORTH);

        mainFrame.add(lowerPanel,BorderLayout.CENTER);
        mainFrame.add(westPanel, BorderLayout.WEST);
        mainFrame.add(eastPanel, BorderLayout.EAST);
        mainFrame.setVisible(true);

    }

    private static void levelSelection() {
        try {
            int tempLevel = currentGameCount;
            currentGameCount = Integer.parseInt(JOptionPane.showInputDialog("Please enter level from 0-" + (indexListOfGames.size() - 1)));

            if (!(currentGameCount >= 0 && currentGameCount < indexListOfGames.size())){
                currentGameCount = tempLevel;
            }
            guessesLeft = 0;
            currentGameCount--;

            nextButtonPressed();
            System.out.println(currentGameName);
        } catch (NumberFormatException | IOException e) {
            System.out.println("stuff exploded but its prolly fine");;
        }
    }



    private static void nextButtonPressed() throws IOException {


        if (guessesLeft > 0){
            if (guessesLeft < 4){
                platformHintLabel.setText("Platform(s): " + getPlatformsByName(currentGameName));
                yearHint.setText("Years(s): " + getYearsByName(currentGameName));
            }

            try{
                userGuess = boxGameList.getSelectedItem().toString();
            } catch (Exception e){
                System.out.println("user guess is null");
            }


           imageX*=2.5;
           imageY*=2.5;
            System.out.println(imageX);
            System.out.println(guessesLeft);

            Image newImage = image.getScaledInstance(imageX,imageY,Image.SCALE_DEFAULT);
            Image newImage2 = newImage.getScaledInstance(1066,600,Image.SCALE_FAST);

            jImage.setImage(newImage2);


            mainFrame.setVisible(false);
            mainFrame.setVisible(true);

            if (userGuess.equals(currentGameName)){ // what happens if user guesses the right answer

                JOptionPane.showMessageDialog( mainFrame, "you won");
                guessesLeft = 0;
                nextButtonPressed();
                return;
            }
            guessesLeft--;
            guessesLeftField.setText(guessesLeft+ " guesses left");
            if (guessesLeft == 0){
                ranOutOfGuesses = true;
                nextButtonPressed();
            }

        } else{
            if (ranOutOfGuesses)
            JOptionPane.showMessageDialog(mainFrame, "The game was \n" + currentGameName);
            ranOutOfGuesses = false;
            currentGameCount++;
            guessField.setText("");
            guessField.requestFocusInWindow();
            levelText.setText("Level: " + currentGameCount);
            currentGameName = getGameNameByIndex(indexListOfGames.get(currentGameCount),totalPath);
            platformHintLabel.setText("Platform(s): ");
            yearHint.setText("Year(s) "




            );
            imageX = 26;
            imageY = 15;
            guessesLeft = 5;
            guessesLeftField.setText(guessesLeft+ " guesses left");
             image = readImageIn(currentGameName, totalPath);
            Image newImage = image.getScaledInstance(imageX,imageY,Image.SCALE_DEFAULT);
            Image newImage2 = newImage.getScaledInstance(1066,600,Image.SCALE_FAST);
            jImage.setImage(newImage2);
            mainFrame.setVisible(false);
            mainFrame.setVisible(true);

        }


    }

    static void setupArrayList(){
        indexListOfGames.add(2);
        indexListOfGames.add(124);
        indexListOfGames.add(14245);
        indexListOfGames.add(177);
        indexListOfGames.add(119);
        indexListOfGames.add(3721);
        indexListOfGames.add(2339);
        indexListOfGames.add(10034);
        indexListOfGames.add(1986); // legend of zelda majoas mask
        indexListOfGames.add(15983); // uncharted 4
        indexListOfGames.add(16079); // far cry primal
        indexListOfGames.add(15987); // overwatch
        indexListOfGames.add(15988); // no mans sky
        indexListOfGames.add(16011); //lego star wars the force awakens
        indexListOfGames.add(16022); // life is strange
        indexListOfGames.add(16029); // deus ex manking divded
        indexListOfGames.add(16033); // mirrors edge catalyst
        indexListOfGames.add(16034); //LEGO Marvel's Avengers
        indexListOfGames.add(16066); // Doom (2016)
        indexListOfGames.add(16094); // DiRT Rally
        indexListOfGames.add(12117); // F1 2010
        indexListOfGames.add(12146); // Sam & Max Beyond Time and Space
        indexListOfGames.add(12223); // Prince of Persia The Forgotten Sands
        indexListOfGames.add(12); //frogger
        indexListOfGames.add(14); //E.T. The Extra Terrestrial
        indexListOfGames.add(18); //Centipede
        indexListOfGames.add(28); //King Kong
        indexListOfGames.add(75); //Popeye
        indexListOfGames.add(97); //Donkey Kong
        indexListOfGames.add(8352); //Saints Row 2
        indexListOfGames.add(8346); //Wii Music
        indexListOfGames.add(8342); //Left 4 Dead
        indexListOfGames.add(8336); // Fallout 3
        indexListOfGames.add(8329); //Call of Duty World at War
        indexListOfGames.add(8330); //Gears of War 2
        indexListOfGames.add(8328); // Pokémon Platinum Version
        indexListOfGames.add(8324); //Mario Kart Wii
        indexListOfGames.add(8325); //Super Smash Bros. Brawl
        indexListOfGames.add(8326); //Grand Theft Auto IV
        indexListOfGames.add(8297); //Fullmetal Alchemist Trading Card Game
        indexListOfGames.add(8263); //Guitar Hero III Legends of Rock
        indexListOfGames.add(8194); //spider man 3
        indexListOfGames.add(8189);//the fast and the furious
        indexListOfGames.add(8142); //Betty Boop's Double Shift
        indexListOfGames.add(8084); //Harry Potter and the Order of the Phoenix
        indexListOfGames.add(7975); // the witcher
        indexListOfGames.add(7970); //Unreal Tournament III
        indexListOfGames.add(7865); //Halo 2
        indexListOfGames.add(7841); //Hot Wheels Ultimate Racing
        indexListOfGames.add(7774); //Need for Speed ProStreet




    }
    static void makeArrayOfGameNames() throws IOException {
        BufferedReader BR = new BufferedReader(new FileReader(totalPath + "\\vgsales.csv"));

        int tempCount = 0;
        String line = "";
        while ((line = BR.readLine()) != null){

            line = line.substring(line.indexOf(",")+1);
            line = line.substring(0, line.indexOf(","));

            listOfGameNames[tempCount] = line;
            tempCount++;

        }

        listOfGameNames = removeDuplicates(listOfGameNames);

        listOfGameNames[0] = "name";

    }

    static String getPlatformsByName(String nameOfGame) throws IOException {
        String platforms = "";
        BufferedReader BR = new BufferedReader(new FileReader(totalPath + "\\vgsales.csv"));
        String tempPlatform = "";
        int tempCount = 0;
        String line = "";
        while ((line = BR.readLine()) != null){

            line = line.substring(line.indexOf(",")+1);
            tempPlatform = line.substring(line.indexOf(",")+1);
            tempPlatform = tempPlatform.substring(0,tempPlatform.indexOf(","));
            line = line.substring(0, line.indexOf(","));

            if (line.equals(nameOfGame)){

                platforms+=tempPlatform + ", ";
            }
            tempCount++;

        }
        return platforms;
    }
    static String getYearsByName(String currentGameName) throws IOException {
        String platforms = "";
        BufferedReader BR = new BufferedReader(new FileReader(totalPath + "\\vgsales.csv"));
        String tempPlatform = "";
        String line = "";
        while ((line = BR.readLine()) != null){

            line = line.substring(line.indexOf(",")+1);
            tempPlatform = line.substring(line.indexOf(",")+1);
            tempPlatform = tempPlatform.substring(tempPlatform.indexOf(",")+1);
            tempPlatform = tempPlatform.substring(0,tempPlatform.indexOf(","));
            line = line.substring(0, line.indexOf(","));

            if (line.equals(currentGameName)){

                platforms+=tempPlatform + ", ";
            }

        }
        return platforms;
    }

    public static String[] removeDuplicates(String[] array) {
        HashSet<String> set = new HashSet<>(Arrays.asList(array));
        return set.toArray(new String[0]); // Convert back to an array
    }

    private static void setupEnterListener() {
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        onEnterPressed(); // Call your method here
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    static void onEnterPressed() throws IOException {
        nextButtonPressed();
    }


    static void setupArrowKeys(){
        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upKeyPressed(); // Call your method here
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                    downKeyPressed();
                }
            }
        });
    }
    static void upKeyPressed(){
        try{
            boxGameList.setSelectedIndex(boxGameList.getSelectedIndex()-1);
        } catch (Exception e) {
            System.out.println("Guess Box Arrow Key Out Of Bounds");
        }

    }
    static void downKeyPressed(){
        try{
            boxGameList.setSelectedIndex(boxGameList.getSelectedIndex()+1);
        } catch (Exception e) {
            System.out.println("Guess Box Arrow Key Out Of Bounds");
        }

    }
    static void setupGuessFieldListener(){

        guessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP ||e.getKeyCode() == KeyEvent.VK_DOWN ||
                        e.getKeyCode() == KeyEvent.VK_ENTER ){
                    return;
                }
                String text = guessField.getText().toLowerCase();
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) boxGameList.getModel();
                model.removeAllElements();


                for (int i = 0; i < listOfGameNames.length-1; i++) {
                    String item = listOfGameNames[i];

                    String fixedItem = item.replaceAll("-", " ");
                    fixedItem = fixedItem.replaceAll("[.]","");
                    fixedItem = fixedItem.replaceAll("'","");
                    fixedItem = fixedItem.replaceAll("[(]", "");
                    fixedItem = fixedItem.replaceAll("[)]", "");
                    fixedItem = fixedItem.replaceAll("é", "e");
                    if (fixedItem.toLowerCase().contains(text) || item.toLowerCase().contains(text)) {

                        model.addElement(item);

                    }
                }
                boxGameList.showPopup(); // Show the dropdown if there are matching options
            }
        });
    }

    static void setupRandomizer() throws IOException {
        int response = JOptionPane.showConfirmDialog(
                mainFrame,
                "Do you want to randomize the levels?",
                "RandomizePrompt",
                JOptionPane.YES_NO_OPTION
        );
        if (response == JOptionPane.YES_OPTION) {
            wantRandomized = true;
        }
       if (wantRandomized){
          Collections.shuffle(indexListOfGames);
          guessesLeft = 0;
          currentGameCount = -1;
          nextButtonPressed();
           }



       }


       static void setupValidation() throws IOException {
           int response = JOptionPane.showConfirmDialog(
                   mainFrame,
                   "Do you want to validate the levels?",
                   "validate",
                   JOptionPane.YES_NO_OPTION
           );
           if (response == JOptionPane.YES_OPTION) {
               boolean failedValidation = false;
               String failedGames = "";
               String gameName = "";
               BufferedImage image;
               for (int i = 0; i< indexListOfGames.size(); i++){
                      gameName = getGameNameByIndex(indexListOfGames.get(i), totalPath);

                   try {
                       image = ImageIO.read(new File(totalPath + "\\games\\" + gameName + ".jpg"));

                   } catch (IOException e){
                       System.out.println("image reading failed");
                       failedValidation = true;
                       failedGames+= gameName + " id: ";
                       failedGames += indexListOfGames.get(i);

                   }
               }
               if (!failedValidation){
                   JOptionPane.showMessageDialog(mainFrame, "Validation Successful!");
               } else{
                   JOptionPane.showMessageDialog(mainFrame, "Validation failed! " + failedGames );
               }
           }
    }
}

