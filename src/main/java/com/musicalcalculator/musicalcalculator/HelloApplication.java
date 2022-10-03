package com.musicalcalculator.musicalcalculator;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Pattern;

public class HelloApplication extends Application {
    //Global Variables
    final Label outputText = new Label();
    double num1 = 0;
    double num2 = 0;
    char operator = '\0';
    boolean resetOutput = false;
    String newValueStr = "";
    String oldValueStr = outputText.getText();
    boolean containsOperator = oldValueStr.contains("+") || oldValueStr.contains("-")
            || oldValueStr.contains("/") || oldValueStr.contains("*") || oldValueStr.contains("%");

    //Sound effects Setup (Global)
    Media sound;
    MediaPlayer player;
    String soundType = "Cats";

    public HelloApplication() throws URISyntaxException {}

    @Override
    public void start(Stage primaryStage)  {
        //Sound effect default settings
        try {
            sound = getCatSound();
            player = new MediaPlayer(sound);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        //Labels
        Font calcFont = Font.font("Lucida Sans Unicode", 35);
        outputText.setText("0");
        outputText.setFont(calcFont);
        outputText.setTextFill(Color.SNOW);
        outputText.setPadding(new Insets(10, 10, 10,10));
        outputText.setMinHeight(100);
        outputText.setMaxWidth(primaryStage.getMaxWidth());
        outputText.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        outputText.setAlignment(Pos.CENTER_RIGHT);

        //Number Buttons
        Button[] buttons= new Button[] {
                new Button("1"), new Button("2"), new Button("3"),
                new Button("+"), new Button("4"), new Button("5"),
                new Button("6"), new Button("-"), new Button("7"),
                new Button("8"), new Button("9"), new Button("*"),
                new Button("()"), new Button("0"), new Button("."),
                new Button("/"), new Button("%"), new Button("DEL"),
                new Button("C"), new Button("=")
        };


        //Setup MenuBar
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Sound Options");


        MenuItem catSoundsItem = new MenuItem("Cats");
        catSoundsItem.setOnAction(eventHandler -> {
            soundType = "Cats";
        });

        MenuItem dogSoundsItem = new MenuItem("Dogs");
        dogSoundsItem.setOnAction(eventHandler -> {
            soundType = "Dogs";
        });

        MenuItem pianoSoundsItem = new MenuItem("Piano");
        pianoSoundsItem.setOnAction(eventHandler -> {
            soundType = "Piano";
        });

        menu.getItems().addAll(catSoundsItem,dogSoundsItem,pianoSoundsItem);
        menuBar.getMenus().add(menu);

        //Setup GridPane for numbers and operators
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        //grid.setGridLinesVisible(true);
        grid.setPadding(new Insets(5, 5, 5, 5));

        //populate grid layout with buttons
        grid.add(menuBar, 0, 0, 4, 1);
        grid.add(outputText, 1,1,4,1);
        int row = 2, column = 0;
        for(int i = 0; i < buttons.length; i++){
            //Style the buttons
            buttons[i].setMinSize(100,100);
            buttons[i].setFont(new Font(30));
            buttons[i].setFocusTraversable(false);

            //assign action events to program buttons
            if(!buttons[i].getText().equals("=")) {
                int tempFinalI = i; //to use event in lambda
                buttons[i].setOnAction(eventHandler -> setOutput(buttons[tempFinalI].getText()));
            }

            else if(buttons[i].getText().equals("=")) {
                buttons[i].setOnAction(eventHandler -> getSolution());
            }

            //add buttons to the grid
            grid.add(buttons[i], column, row);

            if(column == 3) {
                column = 0;
                row++;
            }
            else
                column++;
        }


        //Setup observable list of tile pane
        ObservableList list = grid.getChildren();

        //Setup primaryScene
        Scene primaryScene = new Scene(grid, 400, 600);
        primaryScene.getStylesheets().add("/style.css");
        //Setup primaryStage
        primaryStage.setTitle("MusicalCalculator");
        primaryStage.getIcons().add(new Image("/Logo.png"));
        primaryStage.setScene(primaryScene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(660);
        primaryStage.setResizable(false);

        //Keyboard Event Handler
        primaryScene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.EQUALS) {
                if (keyEvent.isShiftDown())
                    setOutput("+");
                else
                    getSolution();
            }

            else if(keyEvent.getCode() == KeyCode.DIGIT5) {
                if(keyEvent.isShiftDown())
                    setOutput("%");
                else
                    setOutput("5");
            }

            else if(keyEvent.getCode() == KeyCode.DIGIT8) {
                if(keyEvent.isShiftDown())
                    setOutput("*");
                else
                    setOutput("8");
            }
            else if(keyEvent.getCode() == KeyCode.SLASH)
                setOutput("/");
            else if(keyEvent.getCode() == KeyCode.C)
                setOutput("C");
            else if(keyEvent.getCode().equals(KeyCode.ENTER))
                getSolution();
            else if(keyEvent.getCode().equals(KeyCode.BACK_SPACE) || keyEvent.getCode() == KeyCode.DELETE)
                setOutput("DEL");
            else if(keyEvent.getCode() == KeyCode.MINUS)
                setOutput("-");
            else if(keyEvent.getCode().isKeypadKey() || keyEvent.getCode().isDigitKey())
                setOutput(keyEvent.getText());
        });

        //Display the primaryStage
        primaryStage.show();
    }


    //Mathematical Operation Methods--------------------------------
    private void getSolution() {
        String output = outputText.getText();
        String expectedOutput = output.substring(output.indexOf(operator) + 1);

        try{
            num2 = Double.parseDouble(output.substring(output.indexOf(operator) + 1));

            if(operator == '+')
                getSum(num1, num2);
            else if(operator == '-')
                getRemainder(num1,num2);
            else if(operator == '/')
                getQuotient(num1, num2);
            else if(operator == '*')
                getProduct(num1, num2);
            else if(operator == '%')
                getModulus(num1, num2);
        }catch(Exception e) {
            outputText.setText("Invalid Input");
            num1 = 0;
            num2 = 0;
        }

        resetOutput = true;
        containsOperator = false;
    }

    private void getSum(double num1, double num2) {
        double sum = num1 + num2;
        setOutput(sum);
    }
    private void getProduct(double num1, double num2) {
        double product = num1 * num2;
        setOutput(product);
    }
    private void getQuotient(double num1, double num2) {
        double quotient = num1 / num2;
        setOutput(quotient);
    }
    private void getRemainder(double num1, double num2) {
        double remainder = num1 - num2;
        setOutput(remainder);
    }
    private void getModulus(double num1, double num2) {
        double mod = num1 % num2;
        setOutput(mod);
    }
    //--------------------------------------------------------------
    //Sound Control Helper Methods
    private void getSound() throws URISyntaxException {
        if(soundType.equals("Cats"))
            sound = getCatSound();

        else if(soundType.equals("Dogs"))
            sound = getDogSound();

        else
            sound = getPianoSound();

        player = new MediaPlayer(sound);
    }

    private Media getCatSound() throws URISyntaxException {
        Media[] catSoundsArr = new Media[] {
                new Media(getClass().getResource("/catSounds/catSound1.mp3").toURI().toString()),
                new Media(getClass().getResource("/catSounds/catSound2.wav").toURI().toString()),
                new Media(getClass().getResource("/catSounds/catSound3.wav").toURI().toString()),
                new Media(getClass().getResource("/catSounds/catSound4.wav").toURI().toString()),
                new Media(getClass().getResource("/catSounds/catSound5.wav").toURI().toString())
        };
        //get random cat sounds
        Random r = new Random();
        Media catMedia = catSoundsArr[r.nextInt(catSoundsArr.length)];

        return catMedia;
    }

    private Media getDogSound() throws URISyntaxException {
        Media[] dogSoundsArr = new Media[] {
                new Media(getClass().getResource("/dogSounds/dogSound1.mp3").toURI().toString()),
                new Media(getClass().getResource("/dogSounds/dogSound2.mp3").toURI().toString()),
                new Media(getClass().getResource("/dogSounds/dogSound3.mp3").toURI().toString()),
                new Media(getClass().getResource("/dogSounds/dogSound4.mp3").toURI().toString()),
                new Media(getClass().getResource("/dogSounds/dogSound5.mp3").toURI().toString())
        };

        //get random dog sounds
        Random r = new Random();
        Media dogMedia = dogSoundsArr[r.nextInt(5)];

        return dogMedia;
    }

    private Media getPianoSound() throws URISyntaxException {
        Media[] pianoSoundsArr = new Media[24];
        for(int i = 1; i < pianoSoundsArr.length; i++) {
            pianoSoundsArr[i] = new Media(getClass().getResource("/pianoSounds/key" + i + ".mp3").toURI().toString());
        }

        //get random piano sounds
        Random r = new Random();
        Media pianoMedia = pianoSoundsArr[r.nextInt(24)];

        return pianoMedia;
    }

    //--------------------------------------------------------------
    //UI Control Helper Methods
    private void setOutput(double solution) {
        int wholeNumSolution = 0;
        String output;

        //Determine if output needs decimal
        if(solution % 1 == 0) {//if solution doesn't need a decimal
            wholeNumSolution = (int)solution;
            output = "" + wholeNumSolution;
        }
        else
            output = "" + solution;

        //Print output
        outputText.setText(output);
    }

    private void setOutput(String output) {
        oldValueStr = outputText.getText();
        newValueStr = "";
        //Check to see if button is a number or operator
        Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        boolean isNumeric = numericPattern.matcher(output).matches();

        //get new sound effect
        try {
            getSound();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //Play sound effect and reset to start of player
        player.play();
        player.seek(new Duration(0));
        //Prepare output
        if(isNumeric) { //if entering numbers
            int btnValue = Integer.parseInt(output);

            if(oldValueStr .equals("0") || resetOutput) {
                newValueStr = "" + btnValue;
                resetOutput = false;
                num1 = 0;
                num2 = 0;
            }
            else
                newValueStr = "" + oldValueStr + "" + btnValue;
        }
        else if(output.equals(".")) { //if entering decimal
            String btnValue = "" + output;
            newValueStr = "" + oldValueStr + btnValue;
        }

        else if(output.equals("DEL")) {
            if(oldValueStr.length() > 1)
                newValueStr = oldValueStr.substring(0, oldValueStr.length() - 1);
            else
                newValueStr = "0";
        }

        else if(output.equals("C")) {
            newValueStr = "0";
            num1 = 0;
            num2 = 0;
            resetOutput = true;
        }
        else if(!output.matches("[a-zA-Z]+")){ //if entering operator
            String btnValue = "" + output;
            if(!containsOperator) {
                if(num1 == 0)
                    num1 = Double.parseDouble(oldValueStr);
                newValueStr = "" + oldValueStr + " " + btnValue + " ";
                operator = output.charAt(0);
                containsOperator = true;
            }
            else {
                newValueStr = oldValueStr;
            }
        }
        else
            newValueStr = oldValueStr;

        //Update calculator display
        outputText.setText(newValueStr);
    }
    //--------------------------------------------------------------

    public static void main(String[] args) {
        launch();
    }
}