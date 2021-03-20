package sample.GameElements;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<Field> snake;

    public Snake(){
        snake = new ArrayList<Field>();

        snake.add(new Field(3, 7));
        snake.add(new Field(2, 7));
        snake.add(new Field(1, 7));
    }

    public List<Field> getSnake(){
        return this.snake;
    }

    public void changeDirection(boolean[] arr){
        snake.get(0).changeDirection(arr);
    }

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

    public void refresh(){
        boolean[] tempArr = snake.get(snake.size()-2).getCurrentDirection();
        snake.get(snake.size()-1).changeDirection(tempArr);
    }

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