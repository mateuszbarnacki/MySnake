package MySnake.GameElements;

/**
 *  This class represents a single element of snake body.
 *  It stores the coordinates of the element and boolean array which specify the current direction.
 *  In boolean array only one element has 'true' value. It determines changes of the coordinates in
 *  refreshValue() function. The current direction array could be changed in changeDirection() function,
 *  but user can't change the direction to the opposite of the current one.
 */

public class Field {
    private int xCoord;
    private int yCoord;
    private boolean[] currentDirection; // [top, right, bottom, left]

    // Set the coordinates of the field and default direction of the fields.
    public Field(int first, int second){
        this.xCoord = first;
        this.yCoord = second;
        this.currentDirection = new boolean[4];
        this.currentDirection[1] = true;
    }

    // Enables to change the direction of the field. It prevents to change the direction to the
    // opposite one e.g from right to left.
    public boolean changeDirection(boolean[] arr){
        if(((this.currentDirection[0]) && (arr[2])) ||
          ((this.currentDirection[1]) && (arr[3])) ||
          ((this.currentDirection[2]) && (arr[0])) ||
          ((this.currentDirection[3]) && (arr[1]))){
            return false;
        }
        this.currentDirection = arr;
        return true;
    }

    // Getter of the current direction array
    public boolean[] getCurrentDirection(){
        return this.currentDirection;
    }

    // Setter of the current direction array
    public void setDirection(boolean[] arr){
        this.currentDirection = arr;
    }

    // Refresh value change the value of appropriate coordinate
    public void refreshValue(){
        if(currentDirection[0]){
            this.yCoord--;
        }else if(currentDirection[1]){
            this.xCoord++;
        }else if(currentDirection[2]){
            this.yCoord++;
        }else if(currentDirection[3]){
            this.xCoord--;
        }
    }

    // Getter of x coordinate
    public int getXCoord(){
        return this.xCoord;
    }

    // Getter of y coordinate
    public int getYCoord(){
        return this.yCoord;
    }

}
