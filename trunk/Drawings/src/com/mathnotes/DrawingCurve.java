package com.mathnotes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawingCurve extends DrawingPath implements ICanvasCommand {

	public DrawingCurve(Paint paint, Path path) {
		super(path, paint);
		this.paint = paint;
	}

	public Paint paint;

	public void draw(Canvas canvas) {
		final Path path = new Path();
		path.moveTo(50, 50);
		path.cubicTo(300, 50, 100, 400, 400, 400);
		canvas.drawPath(path, paint);
	}

	public void undo() {
		// TODO Auto-generated method stub
	}
}