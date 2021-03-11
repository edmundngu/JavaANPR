package javaanpr.analysis;


public class BoundingBox {	
	int minrow;
	int mincol;
	int maxrow;
	int maxcol;
	
	BoundingBox(){
		minrow = 0;
		mincol = 0;
		maxrow = 0;
		maxcol = 0;
		
	}
	
	BoundingBox(int count){
		minrow = count;
		mincol = count;
		maxrow = 0;
		maxcol = 0;
	}
	
	BoundingBox(int minr, int minc, int maxr, int maxc){
		minrow = minr;
		mincol = minc;
		maxrow = maxr;
		maxcol = maxc;
	}
	
}