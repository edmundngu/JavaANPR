package javaanpr.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import javaanpr.analysis.EdgeDetection;
import javaanpr.analysis.GaussianFilter;
import javaanpr.analysis.ImageColorConversion;
import javaanpr.analysis.ImageEnhancement;
import javaanpr.analysis.SelectPlate;
import javaanpr.analysis.Thinning;

import javax.swing.JFrame;

public class Core {
	
	private String url;
	
	public BufferedImage runCore(String link) throws IOException {
		/*1.Load Image*/
		url = link;
		BufferedImage srcImg = ImageIO.read(new File(url));
		BufferedImage destImg = null;
		
		WritableRaster srcRaster = srcImg.getRaster();
		/*1.----------*/
		
		/*2.Color Convertion - RGB2Grayscale*/
		if(srcRaster.getNumBands()!=1) {
			ImageColorConversion icc2gs = new ImageColorConversion(srcImg); // image color conversion to grayscale
			destImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			destImg = deepCopy(icc2gs.RGB2GRAYSCALE());
		}
		/*2.----------*/
		
		/*3.Image Enhancement - Histogram Equalization*/
		ImageEnhancement ie = new ImageEnhancement(destImg);
		destImg = deepCopy(ie.HistogramEqualization());
		/*3.----------*/
		
		/*4.Noise Reduction - Gaussian Filter*/
		GaussianFilter gf = new GaussianFilter();
		destImg = deepCopy(gf.GaussianBlur(destImg, 7, 1.5));	//GaussianBlur(Image, radius, sigma)
		/*4.----------*/
		
		/*5 Edge Detection - Canny Detector*/
		EdgeDetection ed = new EdgeDetection(destImg);
		destImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		destImg = deepCopy(ed.CannyOp(0.5));	//CANNY THRESHOLD RATIO
		/*5.----------*/
		
		/*6 Thinning */
		Thinning thin = new Thinning(destImg);
		destImg = deepCopy(thin.Skeletonize());
		/*6.---------*/
		
		/*7 Morphological process && CCL */
		SelectPlate sp = new SelectPlate();
		int [][] location = sp.selectPlate(destImg);
		/*7.----------*/
		
		/*8 Draw Bounding Box*/
		Graphics2D g2d = srcImg.createGraphics();
		g2d.setColor(Color.RED);
		g2d.setStroke(new BasicStroke(3));
		int x = location[0][0];
		int y = location[0][1];
		int width = location[1][0]-location[0][0];
		int height = location[1][1]-location[0][1] + 1;
		x = 0;
		width = srcImg.getWidth();
		g2d.drawRect(x, y, width, height);
		g2d.dispose();
		/*8.----------*/
		
		return srcImg;
	}
	
	BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
}