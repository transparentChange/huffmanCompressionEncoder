package encoder;

/*
 * CustomPQ
 * This class is a custom implementation of an ascending Priority Queue
 */
public class CustomPQ<E extends Comparable<E>> { 
    private Node<E> head;
    private Node<E> tail;
    private int size;
    
    public CustomPQ() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void enqueue(E item) {
    	Node<E> lastNode = this.tail;
    	Node<E> newNode = new Node<E>(item, null);
    	this.tail = newNode;
    	if (lastNode == null) {
    		this.head = newNode;
    	} else {
    		lastNode.setNext(newNode);
    	}
    	size++;
    }
    
    public E dequeue() {
    	if (this.head == null) {
    		return null;
    	} else {
    		size--;
    		Node<E> firstNode = this.head;
    		if (firstNode.getNext() == null) {
    			this.head = null;
    			this.tail = null;
    		} else {
    			this.head = firstNode.getNext();
    			this.size -= 1;
    		}
    		return firstNode.getItem();
    	}
    }
    
    @Override
    public String toString(){
        if (this.head == null){return "";}
        Node<E> currentNode = this.head;
        String s = currentNode.getItem().toString();
        while(currentNode.getNext()!=null){ 
            currentNode = currentNode.getNext();
            s = s +", "+ currentNode.getItem().toString();
        }
        return "["+ s +"]";
    }
    
    public E peek() {
    	return this.head.getItem();
    }

    public int size() {
    	if (this.head == null) {
    		return 0;
    	}
    	
    	int size = 1;
    	Node<E> currNode = this.head;
    	while (currNode.getNext() != null) {
    		size++;
    		currNode = currNode.getNext();
    	}
    	
        return size;
    }
    
    public boolean isEmpty() {
    	if (this.head == null) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void add(E item) {
    	size++;
    	if (this.head == null) {
    		Node<E> newNode = new Node<E>(item, null);
    		this.head = newNode;
    		this.tail = newNode; 
    		return;
    	}
    	
    	Node<E> currNode = this.head;
    	while (currNode.getItem().compareTo(item) < 0) {
    		if (currNode.getNext() == null) {
    			Node<E> newNode = new Node<E>(item, null);
    			currNode.setNext(newNode);
    			this.tail = newNode;
    			return;
    		} else if (currNode.getNext().getItem().compareTo(item) >= 0) {
    			currNode.setNext(new Node<E>(item, currNode.getNext()));
    			return;
    		}
    		currNode = currNode.getNext();
    	}
    	
    	this.head = new Node<E>(item, this.head);
    }
    
    private class Node<T>{ 
        private T item;
        private Node<T> next;

        public Node(T item, Node<T> next){
            this.item = item;
            this.next = next;
        }
    
        public Node<T> getNext(){
            return this.next;
        }
        public void setNext(Node<T> next){
            this.next = next;
        }
        public T getItem(){
            return this.item;
        }
        public void setItem(T item){
            this.item = item;
        }
    }    
}

