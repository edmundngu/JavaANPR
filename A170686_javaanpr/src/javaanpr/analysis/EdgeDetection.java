package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class EdgeDetection {
    //The masks for each Sobel convolution
    private static final int[][] KERNEL_H = { {-1, -2, -1}, {0, 0, 0}, {1, 2, 1} };
    private static final int[][] KERNEL_V = { {-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1} };
        
    private final int W, H;
    private BufferedImage srcImg, destImg;
    private WritableRaster srcRaster, destRaster;
    private int [][] Gy;
    private int [][] Gx;
    private double [][] mag;	//magnitude
    private int stdDev, mean;	//standard deviation & mean
    private int [][] dir;			//direction
    private double tHigh, tLow, tRatio;
    
    public EdgeDetection(BufferedImage bi) {
    	this.srcImg = bi;
    	this.W = bi.getWidth();
    	this.H = bi.getHeight();
    	srcRaster = srcImg.getRaster();
    }
    
    public BufferedImage CannyOp(double thresRatio) {
        
        tRatio = thresRatio;
        SobelFilter();
        Suppression();  //Using the direction and magnitude images, identify candidate points
    	Hysteresis();
        
        return destImg;
    }
    
    
    public void SobelFilter() {
    	
    	destImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
    	destRaster = destImg.getRaster();
    	
    	Gx = new int [H][W];
    	Gy = new int [H][W];
    	
    	Horizontal();
    	Vertical();
    	Magnitude();
    	Direction();
    	
    }
/*-------------Sobel Area------------------*/
    public void Horizontal() {
    	
    	if(H > 2 && W > 2) {	// make sure the image size is big enough
    		for(int row=1; row<H-1; row++) {
    			for(int col=1; col<W-1; col++) {
    				int sum = 0;
    				
    				for (int krow = -1; krow < 2; krow++) {
                        for (int kcol = -1; kcol < 2; kcol++) {
                            sum += (KERNEL_H[krow + 1][kcol + 1] * srcRaster.getSample(col + kcol, row + krow, 0));
                        }
                    }
    				Gy[row][col] = sum;
    			}
    		}
    	}
    	
    }
    
    public void Vertical() {
    	
    	if(H > 2 && W > 2) {	// make sure the image size is big enough
    		for(int row=1; row<H-1; row++) {
    			for(int col=1; col<W-1; col++) {
    				int sum = 0;
    				
    				for (int krow = -1; krow < 2; krow++) {
                        for (int kcol = -1; kcol < 2; kcol++) {
                            sum += (KERNEL_V[krow + 1][kcol + 1] * srcRaster.getSample(col + kcol, row + krow, 0));
                        }
                    }
    				Gx[row][col] = sum;
    			}
    		}
    	}
    	
    }
    
    private void Magnitude() {
        double sum = 0;
        double var = 0;	//variance
        double totalPixel = (H-1) * (W-1); // Avoid calculate the border since the border is 0
        mag = new double[H][W];
        
        
    	for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
    			mag[row][col] = Math.sqrt((Gx[row][col] * Gx[row][col]) + (Gy[row][col] * Gy[row][col]));
    			
    			sum +=mag[row][col];
    		}
    	}
        
        mean = (int) Math.round(sum / totalPixel);
        
        //Get variance
        
        for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
    			double diff = mag[row][col] - (double)mean;
    			
    			var += (diff * diff);
    		}
    	}
        double tmpSTD = Math.sqrt(var / totalPixel);
        stdDev = (int)Math.round(tmpSTD);
    }
    
    private void Direction() {
    	
        double piRad = 180 / Math.PI;
        dir = new int[H][W];
        
        for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
                double theta = Math.atan2(Gy[row][col], Gx[row][col]) * piRad;    //Convert radian to degree
                
                //Avoid negative angles
                if (theta < 0) {
                    theta += 360.;
                }
                
                //Each pixels ACTUAL angle is examined and placed in 1 of four groups (for the four searched 45-degree neighbors)
                if (theta <= 22.5 || (theta > 157.5 && theta <= 202.5) || theta > 337.5) {
                    dir[row][col] = 0;      //Left and right direction
                } else if ((theta > 22.5 && theta <= 67.5) || (theta > 202.5 && theta <= 247.5)) {
                    dir[row][col] = 45;     //Diagonal -> upper right and lower left  direction
                } else if ((theta > 67.5 && theta <= 112.5) || (theta > 247.5 && theta <= 292.5)) {
                    dir[row][col] = 90;     //Top and bottom direction
                } else {
                    dir[row][col] = 135;    //Diagonal -> upper left and lower right direction
                }
                
            }
        }
    }
    
    public BufferedImage getHorizontal() {
    	BufferedImage hImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster hRaster = hImg.getRaster();
    	
    	Horizontal();
    	
    	// Use to check vertical or horizontal edge detection
    	for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
    			double edge_strength = Gy[row][col];
    			if(edge_strength > 255) edge_strength = 255;
    			if(edge_strength < 0) edge_strength = 0;
    			hRaster.setSample(col, row, 0, Math.round(edge_strength));
    		}
    	}

    	return hImg;
    }
    
    public BufferedImage getVertical() {
    	BufferedImage vImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster vRaster = vImg.getRaster();    	
    	
    	Vertical();
    	
    	// Use to check vertical or horizontal edge detection
    	for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
    			double edge_strength = Gx[row][col];
    			if(edge_strength > 255) edge_strength = 255;
    			if(edge_strength < 0) edge_strength = 0;
    			vRaster.setSample(col, row, 0, Math.round(edge_strength));
    		}
    	}

    	return vImg;
    }
    
/*-------------------EOF Sobel Area-----------------------*/
    
    /**
     * Use gradient direction and magnitude to suppress lesser pixels.
     */
    private void Suppression() {
    	
    	for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
    			double magnitude = mag[row][col];
    			
    			switch(dir[row][col]) {
    				case 0 :
    					if (magnitude < mag[row][col - 1] && magnitude < mag[row][col + 1]) {
    						mag[row][col] = 0;
    					}
    					break;
    					
    				case 45 :
    					if (magnitude < mag[row - 1][col + 1] && magnitude < mag[row + 1][col - 1]) {
    						mag[row][col] = 0;
    					}
    					break;
    					
    				case 90 :
    					if (magnitude < mag[row - 1][col] && magnitude < mag[row + 1][col]) {
    						mag[row][col] = 0;
    					}
    					break;
    					
    				case 135 :
    					if (magnitude < mag[row - 1][col - 1] && magnitude < mag[row + 1][col + 1]) {
    						mag[row][col] = 0;
    					}
    					break;
    			}
    			
    		}
    	}
    	
    }
    
    /**
     * Use Otsu's threshold to decided which non-suppressed pixels are edges.
     */
    private void Hysteresis() {
    	
    	destImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_BINARY);
    	destRaster = destImg.getRaster();
        
        tHigh = otsuThreshold();
        tLow = tHigh * tRatio;
        
        for(int row = 1;row<H-1;row++) {
    		for(int col=1;col<W-1;col++) {
                double magnitude = mag[row][col];
                int sample = 0;
                
                if (magnitude >= tHigh) {
                	sample = 1;
                } 
                
                else if (magnitude < tLow) {
                	sample = 0;
                } 
                
                else {
                    boolean connected = false;
                    
                    for (int krow = -1; krow < 2; krow++) {
                        for (int kcol = -1; kcol < 2; kcol++) {
                            if (mag[row + krow][col + kcol] >= tHigh) {
                                connected = true;
                            }
                        }
                    }
                    
                    sample = (connected) ? 1 : 0;
                }
                
                destRaster.setSample(col, row, 0, sample);
            }
        }
                
    }
    
    private int otsuThreshold() {
    	// use otsu binarization
    	ImageEnhancement IEHist = new ImageEnhancement(srcImg);
    	int [] hist = IEHist.standardHistogram();
    			
    	int total_pix = W * H;	// Total number of pixel
    			
    	float totalPVal = 0;	// Total value of all pixel
    	for(int i=0;i<hist.length;i++) {
    		totalPVal += i * hist[i];
    	}
    			
    	int nB = 0; // Number of Background pixel
    	int nF = 0;	// Number of Foreground pixel
    	float wB = 0;	// Weight Background
    	float wF = 0;	// Weight Foreground
    			
    	float sumB = 0;	// sum of all background pixel value
    	float varMax = 0; // Max Variance (between)
    	int threshold = 0;
    			
    	for(int i=0;i<256;i++) {
    		nB += hist[i];		// Number of Background pixel
    		if(nB == 0) continue;
    				
    		nF = total_pix - nB;	// Number of Foreground pixel
    		if (nF == 0) {
    			break;
    		}
    				
    		sumB += (float)(i * hist[i]);
    				
    		wB = (float)nB / (float)total_pix;	// Weight Background
    		wF = (float)nF / (float)total_pix;	// Weight Foreground
    				
    		float meanB = sumB / (float)nB;	// Mean of Background Pixel
    		float meanF = ((float)totalPVal - sumB) / (float)nF;	// Mean of Foreground Pixel
    				
    		// Variance Between Background and Foreground Class
    		float varBetween = (float)wB * (float)wF * (meanB-meanF) * (meanB-meanF);
    				
    		if(varBetween > varMax) {
    			varMax = varBetween;
    			threshold = i;
    		}
    	}
    	
    	return threshold;
    }
    
        
}

