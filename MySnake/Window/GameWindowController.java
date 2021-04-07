package MySnake.Window;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import MySnake.GameElements.Field;
import MySnake.GameElements.Snake;
import MySnake.Utilities.KeyBuffer;
import MySnake.Utilities.Record;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * This class draws the board and the snake. It enables user to control the snake body.
 */

public class GameWindowController {
    private final String filename = "./snake_record.tmp";
    private final int xSize = 17;
    private final int ySize = 15;
    private Snake snake;
    private KeyBuffer<String> keyBuffer;
    private boolean firstPress;
    private boolean isApple;
    private Timeline animation;
    private int xAppleCoord;
    private int yAppleCoord;
    private Record record;
    private int counter = 0;
    private final Random random = new Random();
    @FXML
    public GridPane gameWindow;
    @FXML
    public Label currentScore;
    @FXML
    public Label topScore;

    // initialize() function look for the current top score in the filesystem and prepare game board
    public void initialize(){
        if (!Files.exists(Paths.get(filename))) {
            record = new Record();
        } else {
            try (FileInputStream fileInput = new FileInputStream(filename);
                 ObjectInputStream input = new ObjectInputStream(fileInput)) {
                record = (Record) input.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find snake record: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Couldn't load snake record file: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Couldn't find Record class: " + e.getMessage());
                e.printStackTrace();
            }
            topScore.setText(record.toString());
        }
        setParams();
        refreshGame();
    }

    // refreshGame() prepares default game board. It draws fields, adds event listener to the snake
    // and prepare animation for the snake move.
    private void refreshGame() {
        snake = new Snake();
        keyBuffer = new KeyBuffer<>();
        drawBoard();
        drawSnake();
        gameWindow.setOnKeyPressed(this::moveSnake);
        prepareSnakeAnimation();
    }

    // This function prepares the snake animation
    private void prepareSnakeAnimation() {
        animation = new Timeline(new KeyFrame(Duration.millis(170), e -> {
            if (!keyBuffer.isEmpty()) {
                String direction = keyBuffer.get();
                if (direction != null) {
                    if (direction.compareTo("N") == 0) {
                        boolean[] arr = new boolean[]{true, false, false, false};
                        snake.changeDirection(arr);
                    } else if (direction.compareTo("S") == 0) {
                        boolean[] arr = new boolean[]{false, false, true, false};
                        snake.changeDirection(arr);
                    } else if (direction.compareTo("W") == 0) {
                        boolean[] arr = new boolean[]{false, false, false, true};
                        snake.changeDirection(arr);
                    } else if (direction.compareTo("E") == 0) {
                        boolean[] arr = new boolean[]{false, true, false, false};
                        snake.changeDirection(arr);
                    }
                }
            }
            snake.move();
            drawSnake();
        }));
        animation.setCycleCount(Animation.INDEFINITE);
    }

    // This function draw the field of the board
    private void drawBoard() {
        boolean isEven;
        for(int y = 0; y < ySize; ++y){
            for(int x = 0; x < xSize; ++x){
                isEven = (x + y) % 2 == 0;
                StackPane stackPane = new StackPane();
                Rectangle rectangle = createRec(isEven);
                stackPane.getChildren().add(rectangle);
                if(x == xAppleCoord && y == yAppleCoord){
                    Circle circle = new Circle(10);
                    circle.setFill(Color.RED);
                    stackPane.getChildren().add(circle);
                }
                gameWindow.add(stackPane, x, y);
            }
        }
    }

    // This function draw a single field with appropriate color
    private Rectangle createRec(boolean isEven){
        double firstSize = gameWindow.getPrefWidth() / xSize;
        double secondSize = gameWindow.getPrefHeight() / ySize;
        Rectangle rectangle = new Rectangle(firstSize, secondSize);
        if(isEven) rectangle.setFill(Color.rgb(119, 240, 105));
        else rectangle.setFill(Color.rgb(88, 219, 92));
        return rectangle;
    }

    // This function control snake body. If is end it stops the animation and refresh record.
    // Otherwise it checks whether snake eat apple or not and draw snake body in new coordinates.
    private void drawSnake(){
        if(snake.isEnd()){
            animation.stop();
            prepareEndOfTheGameDialog();
            if (counter > record.getValue()) {
                setNewRecord();
            }
            setParams();
            initialize();
            return;
        }
        checkApple();
        drawSnakeBody();
    }

    // This function check if snake head is on the field with an apple or not.
    private void checkApple() {
        if((snake.getSnake().get(0).getXCoord() == xAppleCoord) && (snake.getSnake().get(0).getYCoord() == yAppleCoord)){
            counter++;
            isApple = true;
        }
    }

    // This function draw snake body.
    private void drawSnakeBody() {
        List<Field> temp = snake.getSnake();
        for(int y = 0; y < ySize; ++y){
            for(int x = 0; x < xSize; ++x){
                if((y*xSize+x) % 2 == 0){
                    colorField(x, y, Color.rgb(150, 240, 105));
                }else{
                    colorField(x, y, Color.rgb(88, 219, 92));
                }
                for(Field element : temp){
                    if((y == element.getYCoord()) && (x == element.getXCoord())){
                        colorField(x, y, Color.rgb(97, 30, 133));
                    }
                }
                if(isApple){
                    refreshScore();
                }
            }
        }
    }

    // This function prepare end of the game dialog window.
    private void prepareEndOfTheGameDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("End of the game");
        dialog.setContentText("This is the end of the game. Your score: " + counter);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.show();
        dialog.setOnShowing(e -> {

            Optional<ButtonType> response = dialog.showAndWait();
            if (response.isPresent() && (response.get() == ButtonType.OK)) {
                dialog.close();
            }
        });
    }

    // This function update top score if it's needed.
    private void setNewRecord() {
        record.setValue(counter);
        topScore.setText(record.toString());
        try (FileOutputStream fileOutput = new FileOutputStream(filename);
             ObjectOutputStream output = new ObjectOutputStream(fileOutput)) {
            output.writeObject(record);
        } catch (IOException e) {
            System.out.println("Couldn't write Record object: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // This function set an appropriate color of the field.
    private void colorField(int xCoordinate, int yCoordinate, Color color){
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yCoordinate * xSize + xCoordinate);
        Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
        rectangle.setFill(color);
    }

    // This function remove apple eaten by a snake
    private void removeOldApple() {
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yAppleCoord * xSize + xAppleCoord);
        Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
        rectangle.setFill(Color.VIOLET);
        stackPane.getChildren().remove(1);
    }

    // This function enlarge the snake body by adding a new field at the end of the snake
    private void enlargeSnake() {
        int xCoordinate = snake.getSnake().get(snake.getSnake().size()-1).getXCoord();
        int yCoordinate = snake.getSnake().get(snake.getSnake().size()-1).getYCoord();
        boolean[] direction = snake.getSnake().get(snake.getSnake().size()-1).getCurrentDirection();
        if(direction[0]){
            yCoordinate += 1;
        }else if(direction[1]){
            xCoordinate -= 1;
        }else if(direction[2]){
            yCoordinate -= 1;
        }else if(direction[3]){
            xCoordinate += 1;
        }
        Field field = new Field(xCoordinate, yCoordinate);
        field.setDirection(direction);
        snake.getSnake().add(field);
        snake.refresh();
    }

    // It sets the random coordinates of new apple
    private void setCoordinatesOfNewApple() {
        boolean isOriginal = false;
        while(!isOriginal) {
            xAppleCoord = random.nextInt(17);
            yAppleCoord = random.nextInt(15);
            isOriginal = checkAppleCoords();
        }
    }

    // It draws apple (red circle)
    private void drawApple() {
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yAppleCoord * xSize + xAppleCoord);
        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        stackPane.getChildren().add(circle);
    }

    // This function refresh the score and set a new localization of the apple.
    private void refreshScore(){
        animation.stop();
        removeOldApple();
        enlargeSnake();
        currentScore.setText(Integer.toString(counter));
        isApple = false;
        setCoordinatesOfNewApple();
        drawApple();
        animation.play();
    }

    // This function store all the key events in queue.
    private void moveSnake(KeyEvent keyEvent){
        if(firstPress){
            animation.play();
            firstPress = false;
        }
        if(keyEvent.getCode() == KeyCode.UP){
            keyBuffer.add("N");
        }else if(keyEvent.getCode() == KeyCode.DOWN){
            keyBuffer.add("S");
        }else if(keyEvent.getCode() == KeyCode.LEFT){
            keyBuffer.add("W");
        }else if(keyEvent.getCode() == KeyCode.RIGHT){
            keyBuffer.add("E");
        }
        keyEvent.consume();
    }

    // This function check whether the snake eat an apple or not.
    private boolean checkAppleCoords(){
        for(Field element : snake.getSnake()){
            if((yAppleCoord == element.getYCoord()) && (xAppleCoord == element.getXCoord())){
                return false;
            }
        }
        return true;
    }

    // This function set default parameter of the game.
    private void setParams() {
        counter = 0;
        topScore.setText(record.toString());
        currentScore.setText(Integer.toString(counter));
        gameWindow.getChildren().clear();
        gameWindow.setOnKeyPressed(null);
        xAppleCoord = 12;
        yAppleCoord = 7;
        firstPress = true;
        isApple = false;
    }
}