package MySnake.Utilities;

public class KeyBuffer<T> {
    private Node<T> root;

    public KeyBuffer() {
        this.root = null;
    }

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

    public boolean isEmpty() {
        boolean isEmpty = false;
        if (this.root == null) isEmpty = true;
        return isEmpty;
    }

    public void print() {
        Node<T> curr = this.root;
        while (curr != null) {
            System.out.println(curr.getValue());
            curr = curr.getNext();
        }
    }

}

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