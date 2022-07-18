import bagel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Code for SWEN20003 Project 2, Semester 1, 2022
 *
 * Please fill your name below
 * @author Victor Yoshida
 */
public class ShadowPirate extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static int INVENTORY_Y_OFFSET = 40;
    private final static double LEVEL_0_CD = 300;
    private final static double HZ = 60.0;
    private final static double HUNDRED = 100.0;
    private final static String WIN_MESSAGE0 = "LEVEL COMPLETE!";
    private final static String WIN_MESSAGE1 = "CONGRATULATIONS!";
    private final static String END_MESSAGE = "GAME OVER!";
    private final static String GAME_TITLE = "ShadowPirate";
    private final static String WORLD_FILE0 = "res/level0.csv";
    private final static String WORLD_FILE1 = "res/level1.csv";
    private final static int FONT_Y_POS = 402;
    private final static int FONT_SIZE = 55;
    private final static int INSTRUCTION_OFFSET = 70;
    private final static String LEVEL_MSG_1 = "PRESS SPACE TO START";
    private final static String LEVEL_MSG_2 = "PRESS S TO ATTACK";
    private final static String LEVEL0_MSG_3 = "USE ARROW KEYS TO FIND LADDER";
    private final static String LEVEL1_MSG_3 = "FIND THE TREASURE";
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final Image BACKGROUND_IMAGE1 = new Image("res/background1.png");
    private final Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private boolean gameStart0 = false;
    private boolean gameEnd = false;
    private boolean gameWin0 = false;
    private boolean gameOn1 = false;
    private boolean readBool = false;
    private boolean gameStart1 = false;
    private boolean gameWin1 = false;
    private boolean inventory = false;
    private double level1Count = 0;
    private Sailor sailor;

    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private ArrayList<Entity> entities2 = new ArrayList<Entity>();
    private ArrayList<Pirate> pirates = new ArrayList<Pirate>();
    private ArrayList<Pirate> pirates2 = new ArrayList<Pirate>();
    private  ArrayList<Entity> Items = new ArrayList<Entity>();

    public ShadowPirate() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSV(WORLD_FILE0, entities, pirates);
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowPirate game = new ShadowPirate();
        game.run();

    }

    /**
     * Method used to read file and create objects
     */
    private void readCSV(String fileName, ArrayList<Entity> entities, ArrayList<Pirate> pirates){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            String line;
            if ((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Sailor")){
                    sailor = new Sailor(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                }
            }

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                if (sections[0].equals("Block")){
                    if (!readBool) {
                        entities.add(new Block(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                    }
                    if (readBool) {
                        entities.add(new Bomb(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                    }
                }
                if (sections[0].equals("TopLeft") || sections[0].equals("BottomRight") ){
                    entities.add(new Boundary(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Treasure")) {
                    entities.add(new Treasure(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Elixir")) {
                    entities.add(new Elixir(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Potion")) {
                    entities.add(new Potion(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Sword")) {
                    entities.add(new Sword(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Pirate")) {
                    pirates.add(new Pirate(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
                if (sections[0].equals("Blackbeard")) {
                    pirates.add(new Blackbeard(Integer.parseInt(sections[1]), Integer.parseInt(sections[2])));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        // Display game over message if Sailor is dead
        if (gameEnd){
            drawEndScreen(END_MESSAGE);
            gameStart1 = false;
            gameStart0 = false;
            readBool = false;
        }

        // Check if level 1 is ready to be played
        if (readBool){
            if (!gameStart1){
                drawStartScreen(input);
            }
        }

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // Display level 0 instruction message
        if (!gameStart0 && !gameOn1 && !gameEnd){
            drawStartScreen(input);
        }

        // Displays level 0 completion message, countdowns towards level 1 instruction message
        if (gameWin0){
            drawEndScreen(WIN_MESSAGE0);
            gameStart0 = false;
            gameOn1 = true;
            level1Count++;
            if (level1Count/(HZ/HUNDRED) == LEVEL_0_CD) {
                readBool = true;
                gameWin0 = false;
            }
        }

        // Level 1 completion message
        if (gameWin1){
            drawEndScreen(WIN_MESSAGE1);
        }

        // Start of level 0
        if (gameStart0){
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
            if (!gameEnd && !gameWin0) {

                // print out level entities, update any necessary state changes
                for (Entity current : entities) {
                    current.update();
                }
                for (Pirate current : pirates) {
                    if (current.isDead()){
                        current = null;
                    }
                    if (current instanceof Pirate) {
                        current.update(entities, sailor);
                    }
                }
                sailor.update(input, entities, pirates);

                if (sailor.isDead()) {
                    gameEnd = true;
                }

                if (sailor.hasWon(entities,gameStart0)) {
                    gameWin0 = true;
                }

            }
        }

        // start of level 1
        if (gameStart1){
            BACKGROUND_IMAGE1.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

            // draws inventory
            if (inventory){
                int itemNumY = 40;
                for (Entity current : Items){
                    current.updateIcon(itemNumY);
                    itemNumY = itemNumY + INVENTORY_Y_OFFSET;
                }
            }

            if (!gameEnd && !gameWin1) {

                // check if sailor picks up any items
                for (Entity current : entities2) {
                        current.update();
                        if (current instanceof Elixir || current instanceof Potion || current instanceof Sword){
                            if (current.itemCollision(sailor)){
                                inventory = true;
                                Items.add(current);
                            }
                        }
                }

                // update pirate states
                for (Pirate current : pirates2) {
                    if (current.isDead()){
                        current = null;
                    }
                    if (current instanceof Pirate) {
                        current.update(entities2, sailor);
                    }
                }

                sailor.update(input, entities2, pirates2);

                if (sailor.isDead()) {
                    gameEnd = true;
                }

                if (sailor.hasWon(entities2, gameStart0)) {
                    gameStart1 = false;
                    gameWin1 = true;
                }

            }
        }

    }

    /**
     * Draws start instruction message before each level
     */
    private void drawStartScreen(Input input){

        // level 0 start message
        if (!gameStart0 && !gameOn1) {
            FONT.drawString(LEVEL_MSG_1, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL_MSG_1) / 2.0)),
                    FONT_Y_POS);
            FONT.drawString(LEVEL_MSG_2, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL_MSG_2) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET));
            FONT.drawString(LEVEL0_MSG_3, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL0_MSG_3) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET * 2));
        }

        // level 1 start message
        if (readBool) {
            FONT.drawString(LEVEL_MSG_1, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL_MSG_1) / 2.0)),
                    FONT_Y_POS);
            FONT.drawString(LEVEL_MSG_2, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL_MSG_2) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET));
            FONT.drawString(LEVEL1_MSG_3, (Window.getWidth() / 2.0 - (FONT.getWidth(LEVEL1_MSG_3) / 2.0)),
                    (FONT_Y_POS + INSTRUCTION_OFFSET * 2));
        }

        // start level condition
        if (input.wasPressed(Keys.SPACE) && !gameOn1){
            gameStart0 = true;
        }
        if (input.wasPressed(Keys.SPACE) && readBool){
            gameStart1 = true;
            readCSV(WORLD_FILE1, entities2, pirates2);
            readBool = false;
        }
    }

    /**
     * Method used to draw end screen messages
     */
    private void drawEndScreen(String message){
        FONT.drawString(message, (Window.getWidth()/2.0 - (FONT.getWidth(message)/2.0)), FONT_Y_POS);
    }

}
