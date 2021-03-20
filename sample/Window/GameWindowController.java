package sample.Window;

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
import sample.GameElements.Field;
import sample.GameElements.Snake;
import sample.Utilities.KeyBuffer;
import sample.Utilities.Record;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    private void refreshGame() {
        boolean isEven;
        snake = new Snake();
        keyBuffer = new KeyBuffer<>();
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
        drawSnake();
        gameWindow.setOnKeyPressed(this::moveSnake);
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

    private Rectangle createRec(boolean isEven){
        double firstSize = gameWindow.getPrefWidth() / xSize;
        double secondSize = gameWindow.getPrefHeight() / ySize;
        Rectangle rectangle = new Rectangle(firstSize, secondSize);
        if(isEven) rectangle.setFill(Color.rgb(119, 240, 105));
        else rectangle.setFill(Color.rgb(88, 219, 92));
        return rectangle;
    }

    private void drawSnake(){
        if(snake.isEnd()){
            animation.stop();
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
            if (counter > record.getValue()) {
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
            setParams();
            initialize();
            return;
        }
        if((snake.getSnake().get(0).getXCoord() == xAppleCoord) && (snake.getSnake().get(0).getYCoord() == yAppleCoord)){
            counter++;
            isApple = true;
        }
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
                    drawApple();
                }
            }
        }
    }

    private void colorField(int xCoordinate, int yCoordinate, Color color){
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yCoordinate * xSize + xCoordinate);
        Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
        rectangle.setFill(color);
    }

    private void drawApple(){
        animation.stop();
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yAppleCoord * xSize + xAppleCoord);
        Rectangle rectangle = (Rectangle) stackPane.getChildren().get(0);
        rectangle.setFill(Color.VIOLET);
        stackPane.getChildren().remove(1);
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
        currentScore.setText(Integer.toString(counter));
        isApple = false;
        boolean isOriginal = false;
        while(!isOriginal) {
            xAppleCoord = random.nextInt(17);
            yAppleCoord = random.nextInt(15);
            isOriginal = checkAppleCoords();
        }
        stackPane = (StackPane) nodes.get(yAppleCoord * xSize + xAppleCoord);
        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        stackPane.getChildren().add(circle);
        animation.play();
    }

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

    private boolean checkAppleCoords(){
        for(Field element : snake.getSnake()){
            if((yAppleCoord == element.getYCoord()) && (xAppleCoord == element.getXCoord())){
                return false;
            }
        }
        return true;
    }

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