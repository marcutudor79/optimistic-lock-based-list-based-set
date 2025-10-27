package linkedlists.lockbased;

import contention.abstractions.CompositionalSortedSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import contention.abstractions.AbstractCompositionalIntSet;

public class HandOverHandListIntSet extends AbstractCompositionalIntSet {

    // sentinel nodes
    private Node head;
    private Node tail;

    public HandOverHandListIntSet(){
	  head = new Node(Integer.MIN_VALUE);
	  tail = new Node(Integer.MAX_VALUE);
          head.next = tail;
    }

    /*
     * Insert
     *
     * @see contention.abstractions.CompositionalIntSet#addInt(int)
     */
    @Override
    public boolean addInt(int item) {
        // try to lock the head
        head.lock();
        Node pred = head;
        // proceed to lock the next node
        try {
            Node curr = pred.next;
            // falls asleep until it can lock curr
            curr.lock();
            try {
                // traverse the list until finding the right position
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                // if the key is already present, return false
                if (curr.key == item) {
                    return false;
                }
                // else, insert the new node
                Node newNode = new Node(item);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            // unlock curr
            } finally {
                curr.unlock();
            }
        // unlock pred
        } finally {
            pred.unlock();
        }
    }

    /*
     * Remove
     *
     * @see contention.abstractions.CompositionalIntSet#removeInt(int)
     */
    @Override
    public boolean removeInt(int item){
	    Node pred = null, curr = null;

        // try to lock the head
        head.lock();
        try {
            pred = head;
            curr = pred.next;

            // try to lock curr
            curr.lock();
            try {
                // traverse the list until finding the right position
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
            // if the key is present, remove it
            if (curr.key == item) {
                pred.next = curr.next;
                return true;
            }
            // if not found, return false
            return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }


    /*
     * Contains
     *
     * @see contention.abstractions.CompositionalIntSet#containsInt(int)
     */
    @Override
    public boolean containsInt(int item){
	    // try to lock the head
        head.lock();
        Node pred = head;
        // proceed to lock the next node
        try {
            Node curr = pred.next;
            // falls asleep until it can lock curr
            curr.lock();
            try {
                // traverse the list until finding the right position
                while (curr.key < item) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                // if the key is already present, return false
                if (curr.key == item) {
                    return true;
                }
                return false;
            // unlock curr
            } finally {
                curr.unlock();
            }
        // unlock pred
        } finally {
            pred.unlock();
        }
    }

    /* Node class - representing the list's nodes */
    private class Node {
	Node(int item) {
	    key = item;
	    next = null;
	}
	public int key;
	public Node next;
    public Lock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
    }

    @Override
    public void clear() {
       head = new Node(Integer.MIN_VALUE);
       head.next = new Node(Integer.MAX_VALUE);
    }

    /**
     * Non atomic and thread-unsafe
     */
    @Override
    public int size() {
        int count = 0;

        Node curr = head.next;
        while (curr.key != Integer.MAX_VALUE) {
            curr = curr.next;
            count++;
        }
        return count;
    }
}
