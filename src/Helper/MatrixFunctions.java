package Helper;


public class MatrixFunctions{
	public static double[][] addMatrix(double[][] r1 , double[][]r2) {
		double[][] res = new double[r1.length][r1[0].length];
		
		for(int i=0 ; i<r1.length ; i++) {
			for(int j=0 ; j<r1[i].length ; j++) {
				res[i][j] = r1[i][j] + r2[i][j] ;
			}
		}
		return res;
	}
	
	
	public static double[][] addMatrix(double[][] r1 , double[][]r2 , double[][]r3) {
		double[][] res = new double[r1.length][r1[0].length];
		
		for(int i=0 ; i<r1.length ; i++) {
			for(int j=0 ; j<r1[i].length ; j++) {
				res[i][j] = r1[i][j] + r2[i][j] + r3[i][j];
			}
		}
		return res;
	}
	
	
	public static double[][] subtractMatrix(double[][] r1 , double[][]r2) {
		double[][] res = new double[r1.length][r1[0].length];
		
		for(int i=0 ; i<r1.length ; i++) {
			for(int j=0 ; j<r1[i].length ; j++) {
				res[i][j] = r1[i][j] - r2[i][j];
			}
		}
		return res;
	}
	
	public static double[][] scalarMultipicationMatrix(double[][] r1 , double k) {
		double[][] res = new double[r1.length][r1[0].length];
		
		for(int i=0 ; i<r1.length ; i++) {
			for(int j=0 ; j<r1[i].length ; j++) {
				res[i][j] = k* r1[i][j];
			}
		}
		return res;
	}
	
	
	public static double distanceBetween(double[][]r1 , double[][] r2){
		double temp =0;
		for(int i=0 ; i<r1.length ; i++) {
			for(int j=0 ; j<r1[i].length ; j++) {
				temp+= Math.pow((r1[i][j] - r2[i][j]), 2);
			}
		}
		double dist = Math.sqrt(temp);
		return dist;
	}
	
	
};