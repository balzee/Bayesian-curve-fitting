import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.*;
import org.apache.commons.csv.*;

import Jama.Matrix;
//import org.apache.commons.math3.linear.Array2DRowRealMatrix;
//import org.apache.commons.math3.linear.RealMatrix;

public class bayesian {

	public static void main(String[] args) {
	
		double[] x_buffer = new double[1000];
		double[] t_buffer = new double[1000];
		int count = 0;
		Reader in = null;
		try {
			in = new FileReader("testdata.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.EXCEL.parse(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (CSVRecord record : records) {
		    x_buffer[count] = Double.parseDouble(record.get(0));
		    t_buffer[count] = Double.parseDouble(record.get(1));
		    count++;
		}
		double[] x_array = new double[count];
		double[] t_array = new double[count];
		for(int arraycount=0; arraycount<count; arraycount++){
			x_array[arraycount]= x_buffer[arraycount];
			t_array[arraycount]= t_buffer[arraycount];
		}
	    System.out.println(Arrays.toString(x_array));
	    System.out.println(Arrays.toString(t_array));
	    
	    //construction of phi matrix
	    double[][] phi = new double[3][count];
	    for( int matrixcol=0; matrixcol<count; matrixcol++){
    		phi[0][matrixcol]=1;
    	}
	    for( int matrixcol=0; matrixcol<count; matrixcol++){
    		phi[1][matrixcol]=x_array[matrixcol];
    	}
	    for( int matrixrow=2; matrixrow<3; matrixrow++){
	    	for(int matrixcol=0; matrixcol<count; matrixcol++){
	    		phi[matrixrow][matrixcol]=phi[matrixrow-1][matrixcol]*phi[0][matrixcol];
	    	}
	    }
	    double beta = 12;
	    double alpha = 0.1;
	    
	    RealMatrix phi_mat = new Array2DRowRealMatrix(phi);
	 //   RealMatrix x_mat = new Array2DRowRealMatrix(x_array);
	 //   RealMatrix t_mat = new Array2DRowRealMatrix(t_array);
	    double[][] alphaIdent = new double[3][3];
	    for(int diag=0; diag<3; diag++){
	    	alphaIdent[diag][diag]=alpha;
	    }
	    double[] phiTemp = new double[3];
	    double[][] phiSum = new double[3][3];
	    RealMatrix phiSum_mat = new Array2DRowRealMatrix(phiSum);
	    for(int isum=0; isum<count; isum++){
	    	for(int matrixrow=0; matrixrow<3; matrixrow++){
	    		phiTemp[matrixrow] = phi[matrixrow][isum];    		
	    	}
    	    RealMatrix phiTemp_mat = new Array2DRowRealMatrix(phiTemp);	 
	    	phiSum_mat=phiSum_mat.add(phiTemp_mat.multiply(phiTemp_mat.transpose()));
	    }
	    
	    RealMatrix alpha_mat = new Array2DRowRealMatrix(alphaIdent);
	    RealMatrix sInv_mat = alpha_mat.add(phiSum_mat.scalarMultiply(beta));
	    double[][] sInv_array = new double[3][3];
	    for(int srow=0; srow<3; srow++){
	    	for(int scol=0; scol<3; scol++){
	    		sInv_array[srow][scol]=sInv_mat.getEntry(srow, scol);
	    	}
	    }
	    Matrix SImat = new Matrix(sInv_array);
	    Matrix Smat = SImat.inverse();
	    double[][] S_array = Smat.getArray();
	    
	    RealMatrix s_mat = new Array2DRowRealMatrix(S_array);
	    //System.out.println(phi_mat.getEntry(2, 1));
	    double[] phiTempM = new double[3];
	    double[] phiSumM = new double[3];
	    RealMatrix phiSumM_mat = new Array2DRowRealMatrix(phiSumM);
	    for(int isum=0; isum<count; isum++){
	    	for(int matrixrow=0; matrixrow<3; matrixrow++){
	    		phiTempM[matrixrow] = phi[matrixrow][isum];   
	    	}
	    	RealMatrix phiTempM_mat = new Array2DRowRealMatrix(phiTempM);
	    	phiSumM_mat= phiSumM_mat.add(phiTempM_mat.scalarMultiply(t_array[isum]));
	    }
	    RealMatrix phiT_mat = phi_mat.transpose();
	    RealMatrix mean_mat = phiT_mat.multiply(s_mat.multiply(phiSumM_mat)).scalarMultiply(beta);
	    RealMatrix variance_mat = phiT_mat.multiply(s_mat.multiply(phi_mat)).scalarAdd(1/beta);
	    double[][] mean_array = mean_mat.getData();
	    double[][] variance_array = variance_mat.getData();
	    System.out.println(("Estimated Value : "+ mean_array[count-1][0]));
    /*  System.out.println(("Variance \t: "+ variance_array[count-1][count-1]));
	    System.out.println((mean_array[3][0]));
	    System.out.println(variance_array[0][2]);
	    System.out.println("s row "+s_mat.getRowDimension());
	    System.out.println("sInv row " + sInv_mat.getRowDimension());
	    System.out.println("phi row " + phi_mat.getRowDimension());
	    System.out.println("mean row " + mean_mat.getRowDimension());
	    System.out.println("variance row " + variance_mat.getRowDimension());
	    System.out.println("s_mat col " + s_mat.getColumnDimension());
	    System.out.println("sInv col " + sInv_mat.getColumnDimension());
	    System.out.println("phi col " + phi_mat.getColumnDimension());
	    System.out.println("mean col " + mean_mat.getColumnDimension());
	    System.out.println("variance col " + variance_mat.getColumnDimension()); */
	}

}
