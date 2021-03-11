package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Thinning {
	
	private BufferedImage srcImg, destImg;
	private WritableRaster srcRaster, destRaster;
	private final int height, width;
	private boolean changeFlag = true;
	private int [][]firstAry;
	private int [][]secondAry;
	
	private int [] EQAry;
	private BoundingBox []boxes;
	private int[] pixelCount;
	int[][] LP;
	private int newLabel;
	
	
	public Thinning(BufferedImage bi) {
		this.srcImg = bi;
		this.width = bi.getWidth();
		this.height = bi.getHeight();
		srcRaster = srcImg.getRaster();
	}
	
	public BufferedImage Skeletonize() {
		firstAry = new int[height+2][width+2];
		secondAry = new int[height+2][width+2];
		
		initAry();
		loadImage();
		
		while(changeFlag != false) {
			changeFlag = false;
			
			// northThinning
			for(int row = 1; row<height+1; row++){
				for(int col=1; col<width+1; col++) {
					if((firstAry[row][col] > 0) && (firstAry[row-1][col] == 0)) {
						doThinning(row, col, changeFlag);
					}
				}
			}
			
			copyAry();
			
			// southThinning
			for(int row = 1; row<height+1; row++){
				for(int col=1; col<width+1; col++) {
					if((firstAry[row][col] > 0) && (firstAry[row+1][col] == 0)) {
						doThinning(row, col, changeFlag);
					}
				}
			}

			copyAry();
			
			// westThinning
			for(int row = 1; row<height+1; row++){
				for(int col=1; col<width+1; col++) {
					if((firstAry[row][col] > 0) && (firstAry[row][col-1] == 0)) {
						doThinning(row, col, changeFlag);
					}
				}
			}
			
			copyAry();
			
			// eastThinning
			for(int row = 1; row<height+1; row++){
				for(int col=1; col<width+1; col++) {
					if((firstAry[row][col] > 0) && (firstAry[row][col+1] == 0)) {
						doThinning(row, col, changeFlag);
					}
				}
			}
			
			copyAry();
		}
		
		setImage();
		
		return destImg;
		
	}
	
	public void initAry() {
		for(int i = 0;i<height+2;i++) {
			for(int j=0; j<width+2;j++) {
				firstAry[i][j] = 0;
				secondAry[i][j] = 0;
			}
		}
	}
	
	public void loadImage() {
		for(int row=0; row<height; row++) {
			for(int col=0; col<width; col++) {
				int sample = srcRaster.getSample(col, row, 0);
				firstAry[row+1][col+1] = sample;
				secondAry[row+1][col+1] = sample;
			}
		}
	}
	
	public void doThinning(int r, int c, boolean flag) {
		int nonZero = -1;
		boolean valid = false;
		
		for(int kr = -1; kr<=1; kr++) {
			for(int kc = -1; kc<=1; kc++) {
				if(firstAry[r+kr][c+kc] != 0) nonZero++;
			}
		}
		
		int p1 = firstAry[r-1][c-1];
		int p2 = firstAry[r-1][c];
		int p3 = firstAry[r-1][c+1];
		int p4 = firstAry[r][c-1];
		int p6 = firstAry[r][c+1];
		int p7 = firstAry[r+1][c-1];
		int p8 = firstAry[r+1][c];
		int p9 = firstAry[r+1][c+1];
		
		if( (p2 == 0 && p8 == 0) || (p4 == 0 && p6 == 0) || 
				(p1 == 1 && p2 == 0 && p4 == 0) || (p6 == 0 && p8 == 0 && p9 == 1) ||
				(p2 == 0 && p3 == 1 && p6 == 0) || (p4 == 0 && p7 == 1 && p8 == 0) )
			valid = true;
		
		if( (p1 == 0 && p9 == 0 && ((p6 !=1 && p8 != 1) || (p2 != 1 && p4 != 1))) || 
				(p3 == 0 && p7 == 0 && ((p2 != 1 && p4 != 1) || (p6 != 1 && p8 != 1))) )
			valid = true;
		
		if (nonZero >= 4 && valid == false) {
			secondAry[r][c] = 0;
			changeFlag = true;
		}
		else {
			secondAry[r][c]	= 1;
		}
	}
	
	public void copyAry() {
		for(int row = 1; row<height+1; row++){
			for(int col=1; col<width+1; col++) {
				firstAry[row][col] = secondAry[row][col];
			}
		}
			
	}
	
	public void setImage() {
		destImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		destRaster = destImg.getRaster();
		
		for(int row = 1; row<height+1; row++){
			for(int col=1; col<width+1; col++) {
				int sample = firstAry[row][col];
				if(sample == 0) sample = 1;
				else sample = 0;
				
				destRaster.setSample(col-1, row-1, 0, sample);
			}
		}
	}
	
	
}
