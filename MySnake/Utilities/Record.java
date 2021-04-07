package MySnake.Utilities;

import java.io.Serializable;

/**
 * This class enables to store the best score in snake_record.tmp file.
 */

public class Record implements Serializable {
    private int value;

    public Record() {
        this.value = 0;
    }

    public void setValue(int number) {
        this.value = number;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
