package com.drawings;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawingPath implements ICanvasCommand {
	public DrawingPath(Path path, Paint paint) {
		super();
		this.path = path;
		this.paint = paint;
	}

	public Path path;
	public Paint paint;

	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	public void undo() {
		// Todo this would be changed later
	}
}