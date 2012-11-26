package com.almondmendoza.drawings;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.almondmendoza.drawings.brush.Brush;
import com.almondmendoza.drawings.brush.PenBrush;
import com.example.android.notepad.R;


/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 07/11/2010
 * Time: 2:14 AM
 * Link: http://www.tutorialforandroid.com/
 */
public class DrawingActivity extends Activity {
    public DrawingSurface drawingSurface;
    private ICanvasCommand currentDrawingPath;
    private Paint currentPaint;
    private Path path;
    private Button redoBtn;
    private Button undoBtn;
    private Boolean isPenMode = true;
    private Boolean isMultitouchmode = false;
    int x1 = 0, y1 = 0, x2 = 0, y2 = 0, dx = 0, dy = 0;
    private Brush currentBrush;
    private Boolean isSelectMode = true;

    private File APP_FILE_PATH = new File("/sdcard/TutorialForAndroidDrawings");

    
    int x3=0, y3=0, x4 = 0, y4=0;
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		//dx = 0;
		//dy = 0;
		if(isPenMode){
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				path = new Path();
				currentDrawingPath = new DrawingPath(path, currentPaint);

				currentBrush.mouseDown(path, motionEvent.getX(),
						motionEvent.getY()-110);
				Log.d("jaltade", "Down_X "+ motionEvent.getX());
				Log.d("jaltade", "Down_Y "+ (motionEvent.getY()-110));
				
			} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
				currentBrush.mouseMove(path, motionEvent.getX(),
						motionEvent.getY()-110);

			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				currentBrush.mouseUp(path, motionEvent.getX(),
						motionEvent.getY()-110);

				drawingSurface.addDrawingPath(currentDrawingPath);
				drawingSurface.isDrawing = true;
				undoBtn.setEnabled(true);
				redoBtn.setEnabled(false);
			}
			return true;
		}
		else if (isMultitouchmode){
			
			
			if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
				if(isSelectMode)
				{
				x1 = (int)motionEvent.getX();
				y1 = (int)motionEvent.getY()-110;
				Log.d("jaltade","here");
				}
				
				else
				{
				x3 = (int)motionEvent.getX();
				y3 = (int)motionEvent.getY();
				}
			}  
			else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
				if(isSelectMode)
				{
				x2 = (int)motionEvent.getX();
				y2 = (int)motionEvent.getY()-110;
				Log.d("jaltade","here2");
				
				dx = Math.abs(x2 - x1);
				dy = Math.abs(y2 - y1);
				
				int width_bmp = DrawingSurface.mBitmap.getWidth();
				int height_bmp = DrawingSurface.mBitmap.getHeight();
				
				isSelectMode = false;
				
				Log.d("jaltade", "has selected");
//				Log.d("jaltade","1 Bitmap width "+width_bmp);
//				Log.d("jaltade","1 dx "+(dx)+"");
//				Log.d("jaltade","1 Bitmap height "+height_bmp);
//				Log.d("jaltade","1 dy "+(dy)+"");
//				Log.d("jaltade","1 x1 "+x1);
//				Log.d("jaltade","1 y1 "+y1);
//				Log.d("jaltade","1 x2 "+x2);
//				Log.d("jaltade","1 y2 "+y2);
				
				}
				else if(!isSelectMode){
				
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				//display.getSize(size);
				int width_scr = size.x;
				int height_scr = size.y;
				
				int width_bmp = DrawingSurface.mBitmap.getWidth();
				int height_bmp = DrawingSurface.mBitmap.getHeight();
				
					Log.d("jaltade", "Will now drag");
//					Log.d("jaltade","Bitmap width "+width_bmp);
//					Log.d("jaltade","dx "+(dx)+"");
//					Log.d("jaltade","Bitmap height "+height_bmp);
//					Log.d("jaltade","dy "+(dy)+"");
//					Log.d("jaltade","x1 "+x1);
//					Log.d("jaltade","y1 "+y1);
//					Log.d("jaltade","x2 "+x2);
//					Log.d("jaltade","y2 "+y2);
					
					Log.d("jaltade", "setting dx/dy");
					if(y1+dy > height_bmp) { dy = height_bmp - y1-1; }
					if(y1+dy < 0) { dy = y1-1; }
					
					if(x1+dx > width_bmp) { dx = width_bmp - x1-1; }
					if(x1+dx < 0) { dx = x1-1; }
					
					Log.d("jaltade","New dx "+(dx)+"");
					Log.d("jaltade","New dy "+(dy)+"");
					
					//<hack>
					//if(dx == 0 ) dx = 1;
					//if(dy == 0 ) dy = 1;
					//</hack>
				    drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);
				    
					Bitmap currSelect = Bitmap.createBitmap(drawingSurface.getBitmap(), x1, y1, dx, dy);
					Log.d("jaltade", "width "+currSelect.getWidth());
					Log.d("jaltade", "height "+currSelect.getHeight());
					DrawingSurface.setPinchWidget(currSelect);
					
					Bitmap bmOverlay = Bitmap.createBitmap(1000, 1110, Bitmap.Config.ARGB_8888);
				
					Paint p = new Paint();
					p.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));    
					//Canvas c = new Canvas(bmOverlay);

					Canvas c = DrawingSurface.mCanvas;
					drawingSurface.invalidate(new Rect(0,0, 1000, 1000));
					//drawingSurface.draw(c);
				//	drawingSurface.getCommandManager().executeAll(c);
					x4 = (int)motionEvent.getX();
					y4 = (int)motionEvent.getY()-110;
					Log.d("jaltade"," new image at X: "+ x4 );
					Log.d("jaltade"," new image at Y: "+ y4 );
					
				   // c.drawBitmap(currSelect,motionEvent.getX(), motionEvent.getY()-110, p); 
				    c.drawBitmap(currSelect, x4, y4, p);
				   // c.drawBitmap(currSelect,100, 300, p);
				  //  c.drawRect(x1, y1, x2, y2, p);
				  //  drawingSurface.invalidate(new Rect(x1,y1,x2,y2));
				  //  drawingSurface.invalidate(new Rect((int)motionEvent.getX(), (int) motionEvent.getY()-110, (int)motionEvent.getX()+dx, (int)motionEvent.getY()+dy-110));
				    //DrawingSurface.setPinchWidget(currSelect);
				    //drawingSurface.draw(c);
				   // drawingSurface.getCommandManager().executeAll(c);
				    //DrawingSurface.mBitmap = currSelect;
				    isSelectMode = true;
				}
				
				
				/*Canvas tempCanvas = new Canvas(drawingSurface.mBitmap);
				Paint myPaint = new Paint();
				myPaint.setColor(Color.argb(128, 255, 255, 255));
				myPaint.setStrokeWidth(10);
				tempCanvas.drawRect(x1, y1, dx, dy, myPaint);*/
			}

			
			return drawingSurface.mMultiTouchController.onTouchEvent(motionEvent);
		}
		
		
		return true;
	}



	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
    	 MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.list_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_INSERT. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
        	 Intent drawIntent = new Intent(this, DrawingActivity.class);
             startActivity( drawIntent);
           return true;
        case R.id.menu_paste:
          /*
           * Launches a new Activity using an Intent. The intent filter for the Activity
           * has to have action ACTION_PASTE. No category is set, so DEFAULT is assumed.
           * In effect, this starts the NoteEditor Activity in NotePad.
           */
          startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
          return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_activity);

        setCurrentPaint();
        currentBrush = new PenBrush();
        
        drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);
       // drawingSurface.setOnTouchListener(this);

        redoBtn = (Button) findViewById(R.id.redoBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);

        redoBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        
        //multi touch stuff
        //Bitmap itemBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.logo)).getBitmap();
       // DrawingSurface.setPinchWidget(itemBitmap);
    }

    private void setCurrentPaint(){
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(Color.BLACK);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);

    }




  /*  public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
        	path = new Path();
            currentDrawingPath = new DrawingPath(path,currentPaint);
  
            currentBrush.mouseDown(path, motionEvent.getX(), motionEvent.getY());


        }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            currentBrush.mouseMove(path, motionEvent.getX(), motionEvent.getY() );

        }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            currentBrush.mouseUp(path, motionEvent.getX(), motionEvent.getY() );
            

            drawingSurface.addDrawingPath(currentDrawingPath);
            drawingSurface.isDrawing = true;
            undoBtn.setEnabled(true);
            redoBtn.setEnabled(false);
        }
        
        return true;
    }*/


    public void onClick(View view){
        switch (view.getId()){
            case R.id.colorRedBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(Color.BLACK);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                drawingSurface.addDrawingPath(new DrawingCircle(currentPaint));
                drawingSurface.isDrawing = true;
            break;
            case R.id.colorBlueBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(Color.BLACK);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);
                drawingSurface.addDrawingPath(new DrawingRectangle(currentPaint));
                drawingSurface.isDrawing = true;
            break;
            case R.id.colorGreenBtn:
                currentPaint = new Paint();
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3.0f);
                currentPaint.setAntiAlias(true);
                currentPaint.setColor(Color.BLACK);
                drawingSurface.addDrawingPath(new DrawingCurve(currentPaint));
                drawingSurface.isDrawing = true;
                /*currentPaint.setDither(true);
                currentPaint.setColor(0xFF0000FF);
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeJoin(Paint.Join.ROUND);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3);*/
            break;
            case R.id.axesBtn:
            	 currentPaint = new Paint();
                 currentPaint.setStyle(Paint.Style.STROKE);
                 currentPaint.setStrokeCap(Paint.Cap.ROUND);
                 currentPaint.setStrokeWidth(3.0f);
                 currentPaint.setAntiAlias(true);
                 currentPaint.setColor(Color.BLACK);
                 drawingSurface.addDrawingPath(new DrawAxes(currentPaint));
                 drawingSurface.isDrawing = true;
                 break;
            case R.id.triangleBtn:
           	 	currentPaint = new Paint();
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3.0f);
                currentPaint.setAntiAlias(true);
                currentPaint.setColor(Color.BLACK);
                drawingSurface.addDrawingPath(new DrawTriangle(currentPaint));
                drawingSurface.isDrawing = true;
                break;
            case R.id.undoBtn:
                drawingSurface.undo();
                if( drawingSurface.hasMoreUndo() == false ){
                    undoBtn.setEnabled( false );
                }
                redoBtn.setEnabled( true );
            break;

            case R.id.redoBtn:
                drawingSurface.redo();
                if( drawingSurface.hasMoreRedo() == false ){
                    redoBtn.setEnabled( false );
                }

                undoBtn.setEnabled( true );
            break;
            case R.id.saveBtn:
                final Activity currentActivity  = this;
                Handler saveHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(currentActivity).create();
                        alertDialog.setTitle("Saved 1");
                        alertDialog.setMessage("Your drawing had been saved :)");
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        alertDialog.show();
                    }
                } ;
               new ExportBitmapToFile(this,saveHandler, drawingSurface.getBitmap()).execute();
            break;
            case R.id.circleBtn:
            	isMultitouchmode = true;
            	isPenMode = false;
                //currentBrush = new CircleBrush();
            break;
            case R.id.pathBtn:
            	isPenMode = true;
            	isMultitouchmode = false;
                //currentBrush = new PenBrush();
            break;
        }
    }


    private class ExportBitmapToFile extends AsyncTask<Intent,Void,Boolean> {
        private Context mContext;
        private Handler mHandler;
        private Bitmap nBitmap;

        public ExportBitmapToFile(Context context,Handler handler,Bitmap bitmap) {
            mContext = context;
            nBitmap = bitmap;
            mHandler = handler;
        }

        @Override
        protected Boolean doInBackground(Intent... arg0) {
            try {
                if (!APP_FILE_PATH.exists()) {
                    APP_FILE_PATH.mkdirs();
                }

                final FileOutputStream out = new FileOutputStream(new File(APP_FILE_PATH + "/myAwesomeDrawing.png"));
                nBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
            //mHandler.post(completeRunnable);
            return false;
        }


        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if ( bool ){
                mHandler.sendEmptyMessage(1);
            }
        }
    }



}