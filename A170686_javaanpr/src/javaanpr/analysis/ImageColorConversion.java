package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageColorConversion {
	
	private final int W, H;
	private BufferedImage srcImg, destImg;
	private WritableRaster srcRaster, destRaster;
	
	public ImageColorConversion(BufferedImage bi) {
		this.srcImg = bi;
		this.W = bi.getWidth();
		this.H = bi.getHeight();
		srcRaster = srcImg.getRaster();
	}
	
	/* Weighted Method -> RGB to Grayscale */
	public BufferedImage RGB2GRAYSCALE() {
		
		destImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
		destRaster = destImg.getRaster();
		
		for(int row=0; row < H; row++) {
			for(int col=0; col < W; col++) {
				float gray = 0;
				for(int band=0; band<srcRaster.getNumBands(); band++) {
					int sample = srcRaster.getSample(col, row, band);
					
					if (band == 0) { //red
						gray += 0.299 * sample;
					}
					else if (band == 1) {	//green
						gray += 0.587 * sample;
					}
					
					else if (band == 2) {	//blue
						gray += 0.114 * sample;
						gray = Math.round(gray);
						destRaster.setSample(col, row, 0, gray);	//gray
					}
				}	
			}
		}
		
		return destImg;
	}
		
}
