package matrixMapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * This abstract class implement the notion of partition of a matrix,
 *  typically a set of part that respect the conditions:
 *  	1) no overlapping 
 *  	2) cover the whole matrix  
 */

public abstract class Partition implements MatrixAssignment, Serializable {

	private static final long serialVersionUID = 1L;
	private static Random randGen = new Random();
	
	protected Matrix matrix_;
	protected int numReducers_;
	protected Part parts_[];
	
	// Constructor
	public Partition(Matrix _matrix, int _numReducers){
		matrix_=_matrix;
		numReducers_=_numReducers;
		parts_=new Part[numReducers_];
		
		generatePartition();
	}
	
	protected abstract void generatePartition();
	
	/*
	 * @return the area of the larger part of the partition.
	 */
	public int getMaxArea(){
		int max = 0;
		for (int i=0; i<numReducers_; ++i){
			if (parts_[i].getArea()>max){
				max=parts_[i].getArea();
			}
		}
		return max;
	}
	
	/*
	 * @return the sum of half perimeters of Part in the Partition
	 */
	public int getSumHalfPerimeter(){
		int sum=0;
		for (int i=0; i<numReducers_; ++i){
			sum+=parts_[i].getHalfPerimeter();
		}
		return sum;
	}
	
	/*
	 * Method to get a list of indexes of Part that are on a row/column chosen at random
	 */
	public ArrayList<Integer> getRegionIDs(Dimension isSRelation) {
		
		ArrayList<Integer> retList = new ArrayList<Integer>();

		if (isSRelation == Dimension.ROW) { // tuple from relation S
			int sIndex=randGen.nextInt(matrix_.getSizeOfS());
			for (int i=0; i<numReducers_; ++i){	
				if (((!matrix_.isSGreaterThanT())&&parts_[i].intersectRow(sIndex))||((matrix_.isSGreaterThanT())&&parts_[i].intersectColumn(sIndex))){
					retList.add(i);
				}
			}
		} else{  // tuple from relation T
			int tIndex=randGen.nextInt(matrix_.getSizeOfT());
			for (int i=0; i<numReducers_; ++i){	
				if (((matrix_.isSGreaterThanT())&&parts_[i].intersectRow(tIndex))||((!matrix_.isSGreaterThanT())&&parts_[i].intersectColumn(tIndex))){
					retList.add(i);
				}
			}
		}
		return retList;
	}
	
	/*
	 * this method check that each portion of the matrix is covered by exactly one part.
	 * @return
	 * 	 		true if all partition conditions are respected
	 * 			false otherwise 
	 */
	public boolean verifyValidity(){
		for (int h=0; h<matrix_.getHeight();++h){
			for (int w=0; w<matrix_.getWidth();++w){
				int index=0;
				for (int i=0; i<numReducers_; ++i){
					
					if (parts_[i].covers(h, w)){
						index++;
					}
				}
				if (index!=1){
					return false;
				}
			}
		}
		return true;
	}
	
	public void print(String _arg){
		System.out.println("Print of a Partition: " + _arg);
		this.matrix_.print("");
		System.out.println("Number of reducers: " + numReducers_);
		for (int i=0; i<numReducers_; ++i){
			System.out.print("Partition " + i + ": ");
			System.out.println("[( " + parts_[i].getWIndex() + ", " + (parts_[i].getWIndex()+ parts_[i].getWidth()) + "), (" + parts_[i].getHIndex()+ ", " + (parts_[i].getHIndex()+ parts_[i].getHeight()) + ")]" );
		}
		
	}

	@Override
	public int getNumberOfWorkerColumns() {
		return matrix_.getSizeOfT();
	}

	@Override
	public int getNumberOfWorkerRows() {
		return matrix_.getSizeOfS();
	}






}
