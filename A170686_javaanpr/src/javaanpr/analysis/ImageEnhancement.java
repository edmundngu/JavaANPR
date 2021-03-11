package javaanpr.analysis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageEnhancement {

	private final int W, H;
	private BufferedImage srcImg, destImg;
	private WritableRaster srcRaster, destRaster;
	private final int K = 256; // number of intensity values;
	
	public ImageEnhancement(BufferedImage bi) {
		this.srcImg = bi;
		this.W = bi.getWidth();
		this.H = bi.getHeight();
		srcRaster = srcImg.getRaster();
	}
	
	public BufferedImage HistogramEqualization() {
		
		destImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
		destRaster = destImg.getRaster();
		
		int [] histogram = standardHistogram();
		int [] culHistogram = cumulativeHistogram(histogram);
		for(int row=0; row < H; row++) {
			for(int col=0; col < W; col++) {
				int sample = srcRaster.getSample(col, row, 0);
				int HE = culHistogram[sample] * (K - 1) / (W * H);
				destRaster.setSample(col, row, 0, HE);
			}
		}
		
		return destImg;
	}
	
	// Calculate Histogram
	public int[] standardHistogram() {
		
		int[] histogram = new int[256];
		
		for(int i=0; i<histogram.length; i++) {
			histogram[i] = 0;
		}
			
		for(int row=0; row < H; row++) {
			for(int col=0; col < W; col++) {
				int sample = srcRaster.getSample(col, row, 0);
				histogram[sample]++;
			}
		}
		
		return histogram;
	}
	
	// Calculate cumulative Histogram
	public int[] cumulativeHistogram(int[] H) {
		int[] culHistogram = H.clone();
		
		for(int j = 1; j < culHistogram.length; j++) {
			culHistogram[j] = culHistogram[j-1] + culHistogram[j];
		}
		
		return culHistogram;
	}
	
	
}
