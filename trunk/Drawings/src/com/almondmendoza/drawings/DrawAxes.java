package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawAxes implements ICanvasCommand {
	
	public DrawAxes(Paint paint) {
		super();
		this.paint = paint;
	}

	private Paint paint;
	public void draw(Canvas canvas) {
		int width = canvas.getWidth()/2;
    	int height = canvas.getHeight()/2;
		canvas.drawLine(width, height, width, height-150, paint);
		canvas.drawLine(width, height, width+150, height, paint);

	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
