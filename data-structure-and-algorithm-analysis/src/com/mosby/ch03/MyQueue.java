package com.mosby.ch03;

/**
 * ʹ��ѭ������ʵ�� Queue
 * @param <E>
 */
public class MyQueue <E>{
	private Object[] elements;
	//��¼��ǰ������ӵ�е�Ԫ��
	private int size;
	//��¼��ǰ���еĳ���
	private int length;
	//��¼��ͷԪ�ص�λ��
	private int front;
	//��¼��βԪ�ص�λ��
	private int back;
	public MyQueue(int length){
		this.size = 0;
		this.front = 0;
		this.back = 0;
		this.length = length;
		elements = new Object[length];
	}
	
	//���������һ��Ԫ��
	public void enqueue(E element){
		//�ڽ������֮ǰ����������Ҫ�鿴�����Ƿ�����
		if(size == length){
			Object[] newElements = new Object[length * 2];
			for(int i = 0; i < elements.length; i++){
				newElements[i] = elements[i];
			}
			elements = newElements;
			length *= 2;
		}
		//�����βԪ���ڶ��г��ȵ�ĩβ�����ǽ����ŵ��������ǰ��ȥ
		//����ǰ��Ĵ����ڶ���Ϊ��ʱ�Զ��н��������ݣ�������� back == length - 1 ʱ˵�����еĶ�����һ��Ϊ�յ�
		//���� front һ����Ϊ 0
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



























