package sample.Utilities;

import java.io.Serializable;

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
