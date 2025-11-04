package linkedlists.lockbased;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import contention.abstractions.AbstractCompositionalIntSet;

public class HandOverHandListIntSetWaitFreeCont extends AbstractCompositionalIntSet {

    // sentinel nodes
    private Node head;
    private Node tail;

    public HandOverHandListIntSetWaitFreeCont(){
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
    }

    /*
     * Insert (hand-over-hand). Cleans up marked nodes it encounters.
     */
    @Override
    public boolean addInt(int item) {
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                // traverse with lock-coupling
                while (curr.key < item) {
                    // help unlink logically deleted nodes
                    if (curr.marked) {
                        pred.next = curr.next;
                        curr.unlock();
                        curr = pred.next;
                        curr.lock();
                        continue;
                    }
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                // help unlink if the target is logically deleted
                if (curr.marked) {
                    pred.next = curr.next;
                    curr.unlock();
                    curr = pred.next;
                    curr.lock();
                }
                // if key already present and not marked, fail
                if (curr.key == item && !curr.marked) {
                    return false;
                }
                // insert new node between pred and curr
                Node newNode = new Node(item);
                newNode.next = curr;
                pred.next = newNode; // linearization point for add
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    /*
     * Remove (hand-over-hand). Logical delete (mark) then physical unlink.
     */
    @Override
    public boolean removeInt(int item){
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                // traverse with lock-coupling
                while (curr.key < item) {
                    // help unlink logically deleted nodes
                    if (curr.marked) {
                        pred.next = curr.next;
                        curr.unlock();
                        curr = pred.next;
                        curr.lock();
                        continue;
                    }
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                // at position: curr.key >= item; clean if marked
                if (curr.marked) {
                    pred.next = curr.next;
                    return false;
                }
                if (curr.key == item) {
                    // logical deletion
                    curr.marked = true;          // removal linearizes here
                    // physical removal
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    /*
     * Contains — wait-free and linearizable.
     * Traverses without locks and ignores logically deleted nodes.
     */
    @Override
    public boolean containsInt(int item){
        Node curr = head;
        // single forward pass, no locking
        while (curr.key < item) {
            curr = curr.next;
        }
        return (curr.key == item) && !curr.marked;
    }

    /* Node class - representing the list's nodes */
    private static class Node {
        final int key;
        volatile Node next;
        volatile boolean marked = false; // logical deletion flag
        private final Lock lock = new ReentrantLock();

        Node(int item) {
            this.key = item;
            this.next = null;
        }
        void lock()   { lock.lock(); }
        void unlock() { lock.unlock(); }
    }

    @Override
    public void clear() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
    }

    /**
     * Non atomic and thread-unsafe
     */
    @Override
    public int size() {
        int count = 0;
        Node curr = head.next;
        while (curr.key != Integer.MAX_VALUE) {
            if (!curr.marked) count++;
            curr = curr.next;
        }
        return count;
    }
}
