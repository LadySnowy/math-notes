package com.almondmendoza.drawings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.twodwarfs.multitouchcontroller.MultiTouchController;
import com.twodwarfs.multitouchcontroller.MultiTouchController.MultiTouchObjectCanvas;
import com.twodwarfs.multitouchcontroller.MultiTouchController.PointInfo;
import com.twodwarfs.multitouchcontroller.MultiTouchController.PositionAndScale;
import com.twodwarfs.multitouchcontroller.PinchWidget;

/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 07/11/2010
 * Time: 2:15 AM
 * Link: http://www.tutorialforandroid.com/
 */
public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback{
    private Boolean _run;
    protected DrawThread thread;
    private Bitmap mBitmap;
    public boolean isDrawing = true;
    public boolean isDrawCircle = false;

    private CommandManager commandManager;
    public static final int UI_MODE_ROTATE = 1;
 	public static final int UI_MODE_ANISOTROPIC_SCALE = 2;
 	public int mUIMode = UI_MODE_ROTATE;

 	

 	public int mWidth, mHeight;

 	public PinchWidget mPinchWidget;
 	public Context mContext;

	public DrawingSurface(Context context) {
		super(context);
	}
    
    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);

        commandManager = new CommandManager();
        thread = new DrawThread(getHolder());
        
        mContext = context;
    }
    
	public DrawingSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    class DrawThread extends  Thread{
        private SurfaceHolder mSurfaceHolder;


        public DrawThread(SurfaceHolder surfaceHolder){
            mSurfaceHolder = surfaceHolder;

        }

        public void setRunning(boolean run) {
            _run = run;
        }


        @Override
        public void run() {
            Canvas canvas = null;
            while (_run){
                if(isDrawing == true){
                    try{
                        canvas = mSurfaceHolder.lockCanvas(null);
                        if(mBitmap == null){
                            mBitmap =  Bitmap.createBitmap (1, 1, Bitmap.Config.ARGB_8888);
                        }
                        final Canvas c = new Canvas (mBitmap);

                        c.drawColor(0, PorterDuff.Mode.CLEAR);
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        canvas.drawColor(0xffffffff);
                        
                        commandManager.executeAll(c);
                        Log.d("hi", "maath");

                        canvas.drawBitmap (mBitmap, 0,  0,null);
                    } finally {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    isDrawing = false;
                }

            }

        }
    }


    public void addDrawingPath (ICanvasCommand drawingPath){
        commandManager.addCommand(drawingPath);
    }

    public boolean hasMoreRedo(){
        return commandManager.hasMoreRedo();
    }

    public void redo(){
        isDrawing = true;
        commandManager.redo();


    }

    public void undo(){
        isDrawing = true;
        commandManager.undo();
    }

    public boolean hasMoreUndo(){
        return commandManager.hasMoreUndo();
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,  int height) {
        // TODO Auto-generated method stub
        mBitmap =  Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);;
    }


    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }
    
    //multi touch stuff
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mWidth = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		mHeight = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//canvas.drawColor(Color.WHITE);
		mPinchWidget.draw(canvas);
	}
    
	
	
	
	

}
