package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class DrawingRectangle implements ICanvasCommand {
	
	public DrawingRectangle(Paint paint) {
		super();
		this.paint = paint;
	}

	public Paint paint;
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		int width = canvas.getWidth()/2;
    	int height = canvas.getHeight()/2;
    	canvas.drawRect(width/2,height/2,width/2+100,height/2-100,paint);
    	
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
