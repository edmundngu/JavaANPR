package javaanpr.analysis;

import ij.ImagePlus;
import ij.*;
import ij.gui.*;
import ij.process.*;


public class Morpho {
	
	public void FillHole(ImageProcessor ip) {

		int foreground = 0;
		int background = 1;
		ip.setSnapshotCopyMode(true);
		
		int height = ip.getHeight();
		int width = ip.getWidth();
		
		FloodFiller ff = new FloodFiller(ip);
        ip.setColor(127);
        for (int y=0; y<height; y++) {
            if (ip.getPixel(0,y)==background) ff.fill(0, y);
            if (ip.getPixel(width-1,y)==background) ff.fill(width-1, y);
        }
        for (int x=0; x<width; x++){
            if (ip.getPixel(x,0)==background) ff.fill(x, 0);
            if (ip.getPixel(x,height-1)==background) ff.fill(x, height-1);
        }

        byte[] pixels = (byte[])ip.getPixels();
        int n = width*height;
        for (int i=0; i<n; i++) {
        	if (pixels[i]==127)
        		pixels[i] = (byte)background;
        	else
        		pixels[i] = (byte)foreground;
        }
        
        ip.setSnapshotCopyMode(false);
        ip.setBinaryThreshold();
		
	}
	
	public void Erode(ImageProcessor ip, int loop) {
		for(int i = 0; i<loop;i++) {
			ip.erode();
		}
	}
	
	public void Dilate(ImageProcessor ip, int loop) {
		for(int i = 0; i<loop;i++) {
			ip.dilate();
		}
	}
	
	public void Opening(ImageProcessor ip, int loop) {
		for(int i = 0; i<loop;i++) {
			ip.erode();
			ip.dilate();
		}
	}
	public void Closing(ImageProcessor ip, int loop) {
		for(int i = 0; i<loop;i++) {
			ip.dilate();
			ip.erode();
		}
	}
	
}
