package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawingCircle implements ICanvasCommand {

	public DrawingCircle(Paint paint) {
		super();
		this.paint = paint;
	}

	public Paint paint;
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		int width = canvas.getWidth();
    	int height = canvas.getHeight();
    	canvas.drawCircle(width/2, height/2, 20, paint);
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
