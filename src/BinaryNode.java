

public class BinaryNode<T> {
	private T data;
	
	private BinaryNode leftChild;
	private BinaryNode rightChild;
	
	public BinaryNode(T data) {
		this(data, null, null);
	}
	
	public BinaryNode(T data, BinaryNode leftChild, BinaryNode rightChild) {
		this.data = data;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public T getData() {
		return data;
	}
	
	public void setLeftChild(BinaryNode leftChild) {
		this.leftChild = leftChild;
	}
	
	public void setRightChild(BinaryNode rightChild) {
		this.rightChild = rightChild;
	}
	
	public BinaryNode getLeftChild() {
		return leftChild;
	}
	
	public BinaryNode getRightChild() {
		return rightChild;
	}
	
	public boolean hasLeftChild() {
		return leftChild != null;
	}
	
	public boolean hasRightChild() {
		return rightChild != null;
	}
	
	public BinaryNode copy() {
		BinaryNode newRoot = new BinaryNode(data);
		if (leftChild != null)
			newRoot.setLeftChild(leftChild.copy());
		if (rightChild != null)
			newRoot.setLeftChild(rightChild.copy());
		
		return newRoot;
	}
	
	public int getNumberOfNodes() {
		int leftNumber = 0;
		int rightNumber = 0;
		
		if (leftChild != null)
			leftNumber = leftChild.getNumberOfNodes();
		if (rightChild != null)
			rightNumber = rightChild.getNumberOfNodes();
		
		return 1 + leftNumber + rightNumber;
	}
	
	
}
