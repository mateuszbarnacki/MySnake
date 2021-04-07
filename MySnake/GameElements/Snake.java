package MySnake.GameElements;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the whole snake object. Each element of the snake is stored in the List.
 * change direction() function enables to change the direction of the head. Then move function
 * update coordinates and direction for each element of snake body. refresh() function is used after
 * enlarge snake - it set the appropriate direction of the penultimate element.
 * isEnd() function check whether is the end of the game.
 */

public class Snake {
    private List<Field> snake;

    // Initialize List and create a default snake body.
    public Snake(){
        snake = new ArrayList<Field>();

        snake.add(new Field(3, 7));
        snake.add(new Field(2, 7));
        snake.add(new Field(1, 7));
    }

    // Returns the snake body
    public List<Field> getSnake(){
        return this.snake;
    }

    // Change direction of the head element
    public void changeDirection(boolean[] arr){
        snake.get(0).changeDirection(arr);
    }

    /*
        At the beginning function refresh the coordinates of the head. Then it gets the the direction
        of the head and stores it in tempArr. Function refresh the value of the next element of the body,
        gets it's direction and set a new direction, which was a direction of the previous element.
     */
    public void move(){
        snake.get(0).refreshValue();
        boolean[] temp;
        boolean[] tempArr = snake.get(0).getCurrentDirection();
        for(int i = 1; i < snake.size(); ++i){
            snake.get(i).refreshValue();
            temp = snake.get(i).getCurrentDirection();
            snake.get(i).changeDirection(tempArr);
            tempArr = temp;
        }
    }

    // This function is used after enlarge of the snake.
    public void refresh(){
        boolean[] tempArr = snake.get(snake.size()-2).getCurrentDirection();
        snake.get(snake.size()-1).changeDirection(tempArr);
    }

    // If snake hits the wall or itself function returns true. Otherwise returns false.
    public boolean isEnd(){
        boolean isEnd = false;
        if((snake.get(0).getXCoord() < 0) || (snake.get(0).getXCoord() == 17)){
            isEnd = true;
        }
        if((snake.get(0).getYCoord() < 0) || (snake.get(0).getYCoord() == 17)){
            isEnd = true;
        }
        if(snake.size() > 3) {
            for (int i = 1; i < snake.size(); ++i) {
                if((snake.get(i).getXCoord() == snake.get(0).getXCoord()) && (snake.get(i).getYCoord() == snake.get(0).getYCoord())){
                    isEnd = true;
                }
            }
        }
        return isEnd;
    }
}