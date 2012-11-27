package com.drawings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.multitouchcontroller.MultiTouchController;
import com.multitouchcontroller.PinchWidget;
import com.multitouchcontroller.MultiTouchController.MultiTouchObjectCanvas;
import com.multitouchcontroller.MultiTouchController.PointInfo;
import com.multitouchcontroller.MultiTouchController.PositionAndScale;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback, MultiTouchObjectCanvas<PinchWidget> {
	private Boolean _run;
	protected DrawThread thread;
	public static Bitmap mBitmap;
	public boolean isDrawing = true;
	public boolean isDrawCircle = false;
	public DrawingPath previewPath;

	private CommandManager commandManager;
	public static final int UI_MODE_ROTATE = 1;
	public static final int UI_MODE_ANISOTROPIC_SCALE = 2;
	public int mUIMode = UI_MODE_ROTATE;

	public MultiTouchController<PinchWidget> mMultiTouchController = new MultiTouchController<PinchWidget>(this);

	public int mWidth, mHeight;

	public static PinchWidget mPinchWidget;
	public static Context mContext;
	public static Canvas mCanvas;

	public DrawingSurface(Context context) {
		super(context);
	}

	public DrawingSurface(Context context, AttributeSet attrs) {
		super(context, attrs);

		getHolder().addCallback(this);

		setCommandManager(new CommandManager());
		thread = new DrawThread(getHolder());

		mContext = context;
	}

	public DrawingSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private Handler previewDoneHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isDrawing = false;
		}
	};

	class DrawThread extends Thread {
		private SurfaceHolder mSurfaceHolder;

		public DrawThread(SurfaceHolder surfaceHolder) {
			mSurfaceHolder = surfaceHolder;
		}

		public void setRunning(boolean run) {
			_run = run;
		}

		@Override
		public void run() {
			Canvas canvas = null;
			while (_run) {
				if (isDrawing == true) {
					try {
						canvas = mSurfaceHolder.lockCanvas(null);
						if (mBitmap == null) {
							mBitmap = Bitmap.createBitmap(1280, 768, Bitmap.Config.ARGB_8888);
						}
						final Canvas c = new Canvas(mBitmap);

						c.drawColor(0, PorterDuff.Mode.CLEAR);
						canvas.drawColor(0, PorterDuff.Mode.CLEAR);
						canvas.drawColor(0xffffffff);

						commandManager.executeAll(c, previewDoneHandler);
						previewPath.draw(c);

						canvas.drawBitmap(mBitmap, 0, 0, null);

						mCanvas = canvas;
					} finally {
						mSurfaceHolder.unlockCanvasAndPost(canvas);
					}
					isDrawing = false;
				}
			}
		}
	}

	public void addDrawingPath(ICanvasCommand drawingPath) {
		getCommandManager().addCommand(drawingPath);
	}

	public boolean hasMoreRedo() {
		return getCommandManager().hasMoreRedo();
	}

	public void redo() {
		isDrawing = true;
		getCommandManager().redo();
	}

	public void undo() {
		isDrawing = true;
		getCommandManager().undo();
	}

	public boolean hasMoreUndo() {
		return getCommandManager().hasMoreUndo();
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
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

	// stuff for MultiTouchController and PinchWidget
	public static void setPinchWidget(Bitmap bitmap) {
		mPinchWidget = new PinchWidget(bitmap);
		mPinchWidget.init(mContext.getResources());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mWidth = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		mHeight = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mPinchWidget != null) {
			mPinchWidget.draw(canvas);
		}
	}

	public PinchWidget getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();

		if (mPinchWidget != null) {
			if (mPinchWidget.containsPoint(x, y)) {
				return mPinchWidget;
			}
		}
		return null;
	}

	public void getPositionAndScale(PinchWidget pinchWidget, PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(pinchWidget.getCenterX(), pinchWidget.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(pinchWidget.getScaleFactor() + pinchWidget.getScaleFactor()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0,
				pinchWidget.getScaleFactor(), pinchWidget.getScaleFactor(), (mUIMode & UI_MODE_ROTATE) != 0, pinchWidget.getAngle());
	}

	public boolean setPositionAndScale(PinchWidget pinchWidget, PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		boolean ok = pinchWidget.setPos(newImgPosAndScale, mUIMode, UI_MODE_ANISOTROPIC_SCALE, touchPoint.isMultiTouch());
		if (ok) {
			invalidate();
		}
		return ok;
	}

	public void selectObject(PinchWidget pinchWidget, PointInfo touchPoint) {
		if (touchPoint.isDown()) {
			mPinchWidget = pinchWidget;
		}
		invalidate();
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public void setCommandManager(CommandManager commandManager) {
		this.commandManager = commandManager;
	}
}