package com.mosby.ch03;

/**
 * 使用循环数组实现 Queue
 * @param <E>
 */
public class MyQueue <E>{
	private Object[] elements;
	//记录当前队列中拥有的元素
	private int size;
	//记录当前队列的长度
	private int length;
	//记录对头元素的位置
	private int front;
	//记录队尾元素的位置
	private int back;
	public MyQueue(int length){
		this.size = 0;
		this.front = 0;
		this.back = 0;
		this.length = length;
		elements = new Object[length];
	}
	
	//给队列添加一个元素
	public void enqueue(E element){
		//在进入队列之前，我们首先要查看队列是否已满
		if(size == length){
			Object[] newElements = new Object[length * 2];
			for(int i = 0; i < elements.length; i++){
				newElements[i] = elements[i];
			}
			elements = newElements;
			length *= 2;
		}
		//如果队尾元素在对列长度的末尾，我们将它放到数组的最前面去
		//由于前面的代码在队列为满时对队列进行了扩容，所以如果 back == length - 1 时说明队列的顶部是一定为空的
		//并且 front 一定不为 0
		if(back == length){
			elements[back] = element;
			back = 0;
		}else{
			elements[back] = element;
			back++;
		}
		size++;
	}
	
	@SuppressWarnings("unchecked")
	public E dequeue(){
		E result;
		if(size == 0){
			throw new RuntimeException("the queue is an empty queue");
		}
		result = (E) elements[front];
		if(front == length - 1){
			front = 0;
		}else{
			front += 1;
		}
		size--;
		return result;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		int countToChangeLine = 0;
		for(int i = front; i < front + size; i++){
			if(i == length - 1){
				i = 0;
			}
			countToChangeLine++;
			sb.append(elements[i].toString() + " ");
			if(countToChangeLine % 10 == 0){
				sb.append("\r\n");
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		MyQueue<Integer> queue = new MyQueue<Integer>(5);
		queue.enqueue(1);
		queue.enqueue(2);
		queue.enqueue(3);
		queue.enqueue(4);
		queue.enqueue(5);
		queue.enqueue(6);
		System.out.println(queue);
		System.out.println(queue.dequeue());
		System.out.println(queue.dequeue());
		System.out.println(queue);
		queue.enqueue(7);
		System.out.println(queue);
	}
}



























