package proj_task2;

public class matManipulation {
	
	public static void mat2Vect (short [][] mat, int width, int height, short[] vect) {
		for(int i=0;i<height; i++)
			for (int j=0; j<width; j++)
				vect[j+i*width]=mat[i][j];
	}

}
