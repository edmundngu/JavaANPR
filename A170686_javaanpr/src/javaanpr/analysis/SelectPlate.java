package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class SelectPlate {

	public int[][] selectPlate(BufferedImage bi){
		BufferedImage srcImg = deepCopy(bi);
		BufferedImage tmpImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_BINARY);		// convert to binary image type
		int x1, y1, x2, y2;
		int [][]location;
		int condition = 0;
		double ratio;
		int tmp_x1 = 0, tmp_x2 = 0, tmp_y1 = 0, tmp_y2 = 0;
		
		do{
			condition++;
			tmpImg = deepCopy(srcImg);

			ImagePlus iplus = new ImagePlus("img", tmpImg);
			ImageProcessor ip = iplus.getProcessor();
			Morpho mph = new Morpho();
						
			if(condition == 1) {
				mph.FillHole(ip);
				mph.Opening(ip, 1);
				mph.Closing(ip, 1);
				mph.FillHole(ip);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 10);
			}
			
			else if(condition == 2) {	// 2
				mph.Closing(ip, 1);
				mph.Dilate(ip, 3);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 7);
			}
			
			else if(condition == 3) {
				mph.Closing(ip, 1);
				mph.Dilate(ip, 3);
				mph.Closing(ip, 1);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 7);
			}
			
			else if(condition == 4) {
				mph.FillHole(ip);
				mph.Dilate(ip, 1);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 7);
			}
			
			else if(condition == 5) { //5
				mph.FillHole(ip);
				mph.Closing(ip, 1);
				mph.FillHole(ip);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 10);
			}
			
			else if(condition == 6) {
				mph.FillHole(ip);
				mph.Dilate(ip, 1);
				mph.Closing(ip, 1);
				mph.FillHole(ip);
				mph.Opening(ip, 1);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 9);
			}
		
			else if(condition == 7) {
				mph.Dilate(ip, 1);
				mph.Closing(ip, 1);
				mph.Erode(ip, 1);
				mph.Opening(ip, 1);
				mph.FillHole(ip);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 10);
			}
			else if(condition == 9) { // 8
				mph.FillHole(ip);
				mph.Opening(ip, 1);
				mph.Dilate(ip, 3);
				mph.Closing(ip, 1);
				mph.FillHole(ip);
				mph.Erode(ip, 10);
				mph.Dilate(ip, 8);
			}
		
			else if (condition == 10) {	// 9
				mph.FillHole(ip);
				mph.Opening(ip, 1);
				mph.Erode(ip, 5);
				mph.Dilate(ip, 5);
			}
		
			else if (condition == 8) {// 10
				mph.FillHole(ip);
				mph.Dilate(ip, 3);
				mph.Closing(ip, 1);
				mph.Erode(ip, 3);
				mph.Opening(ip, 1);
			}
					
//			(new ImagePlus("IMG from "+condition, ip)).show();
			
			// Convert back to BufferedImage()
			tmpImg = iplus.getBufferedImage();
			WritableRaster tmpRas = tmpImg.getRaster();
			
			// invert color
			for(int row = 0; row<tmpRas.getHeight();row++) {
				for(int col=0; col<tmpRas.getWidth();col++) {
					int sample = tmpRas.getSample(col, row, 0);
					if(sample == 0) sample =1;
					else sample = 0;
					tmpRas.setSample(col, row, 0, sample);
				}
			}
			
			EightConnectedComponent ecc = new EightConnectedComponent(tmpImg);
			location = ecc.EightCCL();
			
			x1 = location[0][0];
			y1 = location[0][1];
			x2 = location[1][0];
			y2 = location[1][1];
			
			ratio = (double)(x2-x1+1) / (double)(y2-y1+1);
			if(ratio < 2.9 && ratio >2) {
				tmp_x1 = x1;
				tmp_x2 = x2;
				tmp_y1 = y1;
				tmp_y2 = y2;
				x1 = 0;
				x2 = 0;
				y1 = 0;
				y2 = 0;
			}
			
			if(location[0][0] == 0 && location[0][1] == 0 && location[1][0] == 0 && location[1][1] == 0 && condition == 10) {
				x1 = tmp_x1;
				x2 = tmp_x2;
				y1 = tmp_y1;
				y2 = tmp_y2;
				location[0][0] = tmp_x1;
				location[0][1] = tmp_y1;
				location[1][0] = tmp_x2;
				location[1][1] = tmp_y2;
			}
			
			if(x1 != 0 || y1 != 0 || x2 != 0 || y2 != 0) break;

		} while(x1 == 0 && y1 == 0 && x2 == 0 && y2 == 0 && condition<10);
				
		return location;
	}
	
	BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
