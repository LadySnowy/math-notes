package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class DrawingRectangle extends DrawingPath implements ICanvasCommand {
	
	public DrawingRectangle(Paint paint, Path path) {
		super(path, paint);
		this.paint = paint;
		this.x1 = 0;
		this.y1 = 0;
		this.x2 = 0;
		this.y2 = 0;
	}
	public DrawingRectangle(Paint paint,Path path,int x1,int y1, int x2, int y2) {
		super(path, paint);
		this.paint = paint;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Paint paint;
	public int x1,y1,x2,y2;
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		int width = canvas.getWidth()/2;
    	int height = canvas.getHeight()/2;
    	if (x1 == 0 && y1 == 0 && x2 == 0 && y2 == 0)
    	canvas.drawRect(width/2,height/2,width/2+100,height/2-100,paint);
    	else
    	canvas.drawRect(x1,y1,x2,y2,paint);
    	
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
