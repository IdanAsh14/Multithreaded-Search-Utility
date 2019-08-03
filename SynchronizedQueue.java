/**
 * A synchronized bounded-size queue for multithreaded producer-consumer applications.
 * 
 * @param <T> Type of data items
 */
public class SynchronizedQueue<T> {

	private T[] buffer;
	private int producers;
	private int size;
	private int start;
	private int end;
	
	/**
	 * Constructor. Allocates a buffer (an array) with the given capacity and
	 * resets pointers and counters.
	 * @param capacity Buffer capacity
	 */
    @SuppressWarnings("unchecked")
	public SynchronizedQueue(int capacity) {
		this.buffer = (T[])(new Object[capacity]);
		this.producers = 0;
		this.size = 0;
		this.start= 0;
		this.end= -1;
	}
	
	/**
	 * Dequeues the first item from the queue and returns it.
	 * If the queue is empty but producers are still registered to this queue, 
	 * this method blocks until some item is available.
	 * If the queue is empty and no more items are planned to be added to this 
	 * queue (because no producers are registered), this method returns null.
	 * 
	 * @return The first item, or null if there are no more items
	 * @see #registerProducer()
	 * @see #unregisterProducer()
	 */
	synchronized public T dequeue() {
		if (producers == 0 && size ==0)
			return null;

		if (this.size == 0){
			try {
				while (producers != 0) {
					this.wait();
				}
			}catch (InterruptedException e){
				System.out.println("Interrupt exception: " + e.getMessage());
			}
		}

		T item = buffer[start];
		buffer[start] = null;
		start++;
		start = start % this.getCapacity();
		size--;
		this.notifyAll();

		return item;
	}

	/**
	 * Enqueues an item to the end of this queue. If the queue is full, this 
	 * method blocks until some space becomes available.
	 * 
	 * @param item Item to enqueue
	 */
	synchronized public void enqueue(T item) {

        try {
            while (size == this.getCapacity()) {
                this.wait();
            }
        }catch (InterruptedException e) {
            System.out.println("Interrupt exception: " + e.getMessage());
        }

        end++;
        end = end % this.getCapacity();
        buffer[end] = item;
        size++;
        this.notifyAll();
	}

	/**
	 * Returns the capacity of this queue
	 * @return queue capacity
	 */
	public int getCapacity() {
		return buffer.length;
	}

	/**
	 * Returns the current size of the queue (number of elements in it)
	 * @return queue size
	 */
	public int getSize() {
        return size;
	}
	
	/**
	 * Registers a producer to this queue. This method actually increases the
	 * internal producers counter of this queue by 1. This counter is used to
	 * determine whether the queue is still active and to avoid blocking of
	 * consumer threads that try to dequeue elements from an empty queue, when
	 * no producer is expected to add any more items.
	 * Every producer of this queue must call this method before starting to 
	 * enqueue items, and must also call <see>{@link #unregisterProducer()}</see> when
	 * finishes to enqueue all items.
	 * 
	 * @see #dequeue()
	 * @see #unregisterProducer()
	 */
	synchronized public void registerProducer() {
		this.producers++;
		this.notify();
	}

	/**
	 * Unregisters a producer from this queue. See <see>{@link #registerProducer()}</see>.
	 * 
	 * @see #dequeue()
	 * @see #registerProducer()
	 */
	synchronized public void unregisterProducer() {
		this.producers--;
		this.notify();
	}
}
