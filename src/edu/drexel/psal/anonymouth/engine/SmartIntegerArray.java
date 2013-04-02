package edu.drexel.psal.anonymouth.engine;

/**
 * Wrapper for int[]. Allows using integer arrays as keys in HashMap, and does other generally intelligent things. 
 * @author Andrew W.E. McDonald
 *
 */
public class SmartIntegerArray{
	private int[] numbers;
	private int len;
	
	/**
	 * constructor
	 * @param numbers the integer array to wrap
	 */
	public SmartIntegerArray(int[] numbers){
		this.numbers = numbers;
		this.len = numbers.length;
	}
	
	/**
	 * defines two SmartIntegerArray objects to be equal if they are the same length, and contain identical values in identical positions.
	 * @return
	 * 	true if equal
	 */
	public boolean equals(Object obj){
		int i = 0;
		boolean isSame = true;
		int otherLen =((SmartIntegerArray)obj).numbers.length;
		if(len != otherLen)
			isSame = false;
		else{
			for(i=0;i<len;i++){
				if(this.numbers[i] != ((SmartIntegerArray)obj).numbers[i]){
					isSame = false;
					break;
				}
			}
		}
		return isSame;
	}
	
	/**
	 * generates a hashcode for SmartIntegerArray, modulus 987643211 (an arbitrary large prime number) to mitigate risk of integer overflow. Multiplier is 31,
	 * hash value starts at 7, and multiplies itself by the product of 'numbers[i]'.
	 * @return
	 * 	hashcode
	 */
	public int hashCode(){
		final int thePrime = 31;
		final int arbitraryLargePrime = 987643211;
		long longHash = 7;
		int i = 0;
		if(numbers != null){
			for(i=0; i < len; i++){
				longHash = longHash*numbers[i]*thePrime;
				longHash = longHash % arbitraryLargePrime;// to eliminate wrap-around
			}
		}
		int hash = (int)longHash;
		return hash;
	}
	
	
	/**
	 * returns string representation of contained integer array
	 */
	public String toString(){
		String whatIsInside = "[";
		int i=0;
		for(i=0;i<len;i++){
			whatIsInside += numbers[i];
			if(i<len-1) whatIsInside+= ", ";
		}	
		whatIsInside +="]";
		return whatIsInside;
	}
	
	/**
	 * returns original input int[] 
	 * @return
	 */
	public int[] toIntArray(){
		return numbers;
	}
	
	/**
	 * returns the length of the stored int[]
	 * @return
	 */
	public int length(){
		return numbers.length;
	}
//	
//	public int[] getFreq(int ){
//		int i=0;
//		int tempMin;
//		int minVal = 1000000; // arbitrary large initial value
//		for(i=0;i<len;i++){
//			tempMin = numbers[i];
//			if(tempMin < minVal
//		}
//		return minVal;
//		
//	}
	/*
	public static void main(String[] args){
		int[] one = new int[]{1,2,3,4,5};
		int[] two = new int[]{1,2,3,4,5};
		SmartIntegerArray sia = new SmartIntegerArray(one);
		System.out.println(sia);
		SmartIntegerArray  siat = new SmartIntegerArray(two);
		System.out.println(siat.equals(sia));
		System.out.println(one.equals(two));
	}
	*/
	
}