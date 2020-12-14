package sample;

public class Field {
    private int xCoord;
    private int yCoord;
    private boolean[] currentDirection; // [top, right, bottom, left]

    public Field(int first, int second){
        this.xCoord = first;
        this.yCoord = second;
        currentDirection = new boolean[4];
        currentDirection[1] = true;
    }

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

    public boolean[] getCurrentDirection(){
        return this.currentDirection;
    }

    public void setDirection(boolean[] arr){
        this.currentDirection = arr;
    }

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

    public int getXCoord(){
        return this.xCoord;
    }

    public int getYCoord(){
        return this.yCoord;
    }

}
