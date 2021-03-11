package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;



public class GaussianFilter {
    //This seems like a very costly operation, only doing this once.
    //private final double SQRT2PI = Math.sqrt(2 * Math.PI);
        
    public BufferedImage GaussianBlur(BufferedImage bi, int rad, double sigma) {
    	BufferedImage destImg = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
    	WritableRaster biRaster = bi.getRaster();
    	WritableRaster destRaster = destImg.getRaster();
    	
    	int height = bi.getHeight();
    	int width = bi.getWidth();
    	double norm = 0.0;
    	double sigma2 = sigma * sigma;
    	float[] kernel = new float[2 * rad + 1];
    	
    	// Create Kernel
    	for (int x = -rad; x < rad + 1; x++) {
    		float exp = (float)Math.exp(-0.5 * (x * x) / sigma2);
    		
    		kernel[x + rad]  = (float)(1 / (2 * Math.PI * sigma2)) * exp;
    		norm += kernel[x + rad];
    	}
    	
    	//Convolve image with kernel horizontally
        for (int row = 0; row < height; row++) {
            for (int col = rad; col < width - rad; col++) {
                double sum = 0.0;
                
                for (int y = -rad; y < rad + 1; y++) {
                	int sample = biRaster.getSample(col + y, row, 0);
                    sum += (kernel[y + rad] * sample); //raw[row][col + mr])
                }
                
                //Normalize channel after blur
                sum /= norm;
                destRaster.setSample(col, row, 0, Math.round(sum));
            }
        }
        
        //Convolve image with kernel vertically
        for (int row = rad; row < height - rad; row++) {
            for (int col = 0; col < width; col++) {
                double sum = 0.;
                
                for(int x = -rad; x < rad + 1; x++) {
                	int sample = biRaster.getSample(col, row + x, 0);
                	sum += (kernel[x + rad] * sample);
                }
                
                //Normalize channel after blur
                sum /= norm;
                destRaster.setSample(col, row, 0, Math.round(sum));
            }
        }
        
        // Retain the border values
        for(int row = 0; row < rad; row++) {
        	for (int col = 0; col < rad; col++) {
        		destRaster.setSample(col, row, 0, biRaster.getSample(col, row, 0));	// [0,0], [0,1], [1,0], [1,1] <- Location in 2D array
        		destRaster.setSample(biRaster.getWidth()-1-col, row, 0, biRaster.getSample(biRaster.getWidth()-1-col, row, 0));	// [0,-1], [0,-2], [1,-1], [1,-2]
        		destRaster.setSample(col, biRaster.getHeight()-1-row, 0, biRaster.getSample(col, biRaster.getHeight()-1-row, 0));	// [-1,0], [-2,0], [-1,1], [-2,1]
        		destRaster.setSample(biRaster.getWidth()-1-col, biRaster.getHeight()-1-row, 0, biRaster.getSample(biRaster.getWidth()-1-col, biRaster.getHeight()-1-row, 0));	// [-1,-1], [-1,-2], [-2,-1], [-2,-2]
        	}
        }
        
        return destImg;

    }
}

