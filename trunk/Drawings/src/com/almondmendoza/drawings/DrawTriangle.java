package com.almondmendoza.drawings;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;



public class DrawTriangle extends DrawingPath implements ICanvasCommand {

	private Paint paint;
	public DrawTriangle(Paint paint, Path path) {
		super(path, paint);
		this.paint = paint;
	}

	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		int width = canvas.getWidth()/2;
    	int height = canvas.getHeight()/2;
		canvas.drawLine(width, height, width, height-150, paint);
		canvas.drawLine(width, height, width+150, height, paint);
		canvas.drawLine(width, height-150, width+150, height, paint);
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

}
