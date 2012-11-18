package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawingCurve implements ICanvasCommand {

	public DrawingCurve(Paint paint) {
		super();
		this.paint = paint;
	}

	public Paint paint;
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		final Path path = new Path();
    	path.moveTo(50,50);
    	path.cubicTo(300, 50, 100, 400, 400, 400);
    	canvas.drawPath(path, paint);
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
