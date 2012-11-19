package com.almondmendoza.drawings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.android.notepad.NoteEditor;
import com.example.android.notepad.R;
import com.almondmendoza.drawings.brush.Brush;
import com.almondmendoza.drawings.brush.CircleBrush;
import com.almondmendoza.drawings.brush.PenBrush;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 07/11/2010
 * Time: 2:14 AM
 * Link: http://www.tutorialforandroid.com/
 */
public class DrawingActivity extends Activity implements View.OnTouchListener{
    private DrawingSurface drawingSurface;
    private ICanvasCommand currentDrawingPath;
    private Paint currentPaint;
    private Path path;
    private Button redoBtn;
    private Button undoBtn;

    private Brush currentBrush;

    private File APP_FILE_PATH = new File("/sdcard/TutorialForAndroidDrawings");

    

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
        drawingSurface.setOnTouchListener(this);

        redoBtn = (Button) findViewById(R.id.redoBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);

        redoBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        
        //multi touch stuff
        Bitmap itemBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.logo)).getBitmap();
        drawingSurface.setPinchWidget(itemBitmap);
    }

    private void setCurrentPaint(){
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(0xFFFFFF00);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);

    }




    public boolean onTouch(View view, MotionEvent motionEvent) {
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
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.colorRedBtn:
                currentPaint = new Paint();
                currentPaint.setDither(true);
                currentPaint.setColor(0xFFFF0000);
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
                currentPaint.setColor(0xFF00FF00);
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
                currentPaint.setColor(0xFF00FF00);
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
                 currentPaint.setColor(0xFF00FF00);
                 drawingSurface.addDrawingPath(new DrawAxes(currentPaint));
                 drawingSurface.isDrawing = true;
                 break;
            case R.id.triangleBtn:
           	 	currentPaint = new Paint();
                currentPaint.setStyle(Paint.Style.STROKE);
                currentPaint.setStrokeCap(Paint.Cap.ROUND);
                currentPaint.setStrokeWidth(3.0f);
                currentPaint.setAntiAlias(true);
                currentPaint.setColor(0xFF00FF00);
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
                currentBrush = new CircleBrush();
            break;
            case R.id.pathBtn:
                currentBrush = new PenBrush();
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