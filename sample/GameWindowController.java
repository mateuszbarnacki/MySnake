package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class GameWindowController {
    private final int xSize = 17;
    private final int ySize = 15;
    private Snake snake;
    private boolean firstPress = true;
    private boolean isApple = false;
    private Timeline animation;
    private int xAppleCoord = 12;
    private int yAppleCoord = 7;
    private int counter = 0;
    private Random random;
    @FXML
    private GridPane gameWindow;
    @FXML
    private Label currentScore;

    public void initialize(){
        random = new Random();
        snake = new Snake();
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
        drawSnake();
        gameWindow.setOnKeyPressed(this::moveSnake);
        animation = new Timeline(new KeyFrame(Duration.millis(150), e -> {
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
            System.out.println("END OF THE GAME!!!");
            animation.stop();
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
                        /*if(element == snake.getSnake().get(0)){
                             drawHead(x, y);
                        }else if(element == snake.getSnake().get(snake.getSnake().size()-1)){
                            drawTail(x, y);
                        }else{
                            colorField(x, y, Color.VIOLET);
                        }*/
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
/*
    private void drawHead(int xCoordinate, int yCoordinate){
        ObservableList<Node> nodes = gameWindow.getChildren();
        StackPane stackPane = (StackPane) nodes.get(yCoordinate * xSize + xCoordinate);
        Circle circle = new Circle(10);
        circle.setCenterX(20);
        circle.setFill(Color.rgb(97, 30, 133));
        stackPane.getChildren().add(circle);
    }
*/
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
            boolean[] arr = new boolean[]{true, false, false, false};
            snake.changeDirection(arr);
        }else if(keyEvent.getCode() == KeyCode.DOWN){
            boolean[] arr = new boolean[]{false, false, true, false};
            snake.changeDirection(arr);
        }else if(keyEvent.getCode() == KeyCode.LEFT){
            boolean[] arr = new boolean[]{false, false, false, true};
            snake.changeDirection(arr);
        }else if(keyEvent.getCode() == KeyCode.RIGHT){
            boolean[] arr = new boolean[]{false, true, false, false};
            snake.changeDirection(arr);
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
}