package MySnake.Utilities;

/**
 * This class is a queue which enables to store elements which represent a key arrow typed by user.
 * @param <T>
 */

public class KeyBuffer<T> {
    private Node<T> root;

    public KeyBuffer() {
        this.root = null;
    }

    // Add element to the end of the list
    public void add(T elem) {
        if (this.root == null) {
            this.root = new Node<>(elem);
        } else {
            Node<T> curr = this.root;
            while (curr.getNext() != null) {
                curr = curr.getNext();
            }
            curr.setNext(elem);
        }
    }

    // Get the first element of the list
    public T get() {
        T value = null;
        if (this.root.getNext() != null) {
            Node<T> tmp = this.root;
            value = tmp.getValue();
            this.root = this.root.getNext();
        } else if (this.root != null) {
            Node<T> tmp = this.root;
            value = tmp.getValue();
            this.root = null;
        }
        return value;
    }

    // Check whether the queue is empty
    public boolean isEmpty() {
        boolean isEmpty = false;
        if (this.root == null) isEmpty = true;
        return isEmpty;
    }

    // Print function used to test the class
    public void print() {
        Node<T> curr = this.root;
        while (curr != null) {
            System.out.println(curr.getValue());
            curr = curr.getNext();
        }
    }

}

/**
 * This class represents the single element of the queue.
 * @param <T>
 */

class Node<T> {
    private final T value;
    private Node<T> next;

    public Node(T value) {
        this.value = value;
        this.next = null;
    }

    public T getValue() {
        return this.value;
    }

    public void setNext(T elem) {
        this.next = new Node<>(elem);
    }

    public Node<T> getNext() {
        return this.next;
    }

}