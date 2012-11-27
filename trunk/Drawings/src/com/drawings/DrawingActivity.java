package com.drawings;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.drawings.brush.Brush;
import com.drawings.brush.PenBrush;
import com.example.android.notepad.NoteEditor;
import com.example.android.notepad.NotePad;
import com.example.android.notepad.R;

/**
 * Created by IntelliJ IDEA. User: almondmendoza Date: 07/11/2010 Time: 2:14 AM
 * Link: http://www.tutorialforandroid.com/
 */
public class DrawingActivity extends Activity implements View.OnTouchListener, OnClickListener {
	public DrawingSurface drawingSurface;
	private DrawingPath currentDrawingPath;
	private Paint currentPaint;
	private Path path;
	private ImageButton redoBtn;
	private ImageButton undoBtn;
	private Boolean isPenMode = true;
	private Boolean isMultitouchMode = false;
	private Boolean isSelectMode = false;
	private Boolean isEraseMode = false;

	int x1 = 0, y1 = 0, x2 = 0, y2 = 0, dx = 0, dy = 0;
	private Brush currentBrush;
	
	private File APP_FILE_PATH = new File(Environment.getExternalStorageDirectory().getPath() + "/savedImages");

	// Global mutable variables
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mText;
	private String mOriginalContent;

	// This Activity can be started by more than one action. Each action is
	// represented as a "state" constant
	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;

	// For logging and debugging purposes
	private static final String TAG = "NoteEditor";

	// A label for the saved state of the activity
	private static final String ORIGINAL_CONTENT = "origContent";

	int x3 = 0, y3 = 0, x4 = 0, y4 = 0 , xold = 0, yold = 0;

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		// dx = 0;
		// dy = 0;
		if (isPenMode) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
	            drawingSurface.isDrawing = true;
	            
	            setCurrentPaint();
	            drawingSurface.previewPath.paint = getPreviewPaint(Color.BLACK);
	        	
	            currentDrawingPath = new DrawingPath(path, currentPaint);
	            currentDrawingPath.paint = currentPaint;
	            currentDrawingPath.path = new Path();
	            currentBrush.mouseDown(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);
	            currentBrush.mouseDown(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);

	            path = new Path();
				currentBrush.mouseDown(path, motionEvent.getX(), motionEvent.getY()-110);
				Log.d("jaltade", "Down_X " + motionEvent.getX());
				Log.d("jaltade", "Down_Y " + (motionEvent.getY()-110));

			} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
				drawingSurface.isDrawing = true;
				currentBrush.mouseMove(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);
				currentBrush.mouseMove(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);
				
				currentBrush.mouseMove(path, motionEvent.getX(), motionEvent.getY()-110);

			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				currentBrush.mouseUp(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);
				drawingSurface.previewPath.path = new Path();

				currentBrush.mouseUp(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);

				currentBrush.mouseUp(path, motionEvent.getX(), motionEvent.getY()-110);

				drawingSurface.addDrawingPath(currentDrawingPath);
				drawingSurface.isDrawing = true;
				undoBtn.setEnabled(true);
				redoBtn.setEnabled(false);
			}
			return true;
		} else if (isMultitouchMode) {
			if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
				if(isSelectMode)
				{
					Log.d("action down", "isselect");
				x1 = (int)motionEvent.getX();
				y1 = (int)motionEvent.getY()-110;
				xold=x1;
				yold=y1;
				}
				else
				{
					Log.d("action down", "!isselect");
				x3 = (int)motionEvent.getX();
				y3 = (int)motionEvent.getY();
				}
			}
			else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
				if(isSelectMode)
				{
					Log.d("action up", "isselect");
				x2 = (int)motionEvent.getX();
				y2 = (int)motionEvent.getY()-110;
				}
				else if(!isSelectMode)
				{
					Log.d("action up", "!isselect");
					dx= Math.abs(xold-x2);
					dy = Math.abs(yold-y2);
					Log.d("dx",""+dx);
					Log.d("dy", ""+dy);
					Log.d("x1",""+x1);
					Log.d("y1", ""+y1);
					Log.d("x2",""+x2);
					Log.d("y2", ""+y2);
					Paint p  = new Paint();
					p.setColor(Color.WHITE);
					p.setDither(true);
		            p.setStyle(Style.FILL);
		            p.setStrokeJoin(Paint.Join.ROUND);
		            p.setStrokeCap(Paint.Cap.ROUND);
		            p.setStrokeWidth(3);
		            p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
					
					Bitmap currSelect = Bitmap.createBitmap(drawingSurface.getBitmap(),xold,yold,dx,dy);
					DrawingSurface.setPinchWidget(currSelect);
					drawingSurface.addDrawingPath(new DrawingRectangle(p,path,xold,yold,x2,y2));
					drawingSurface.isDrawing = true;
					isMultitouchMode = true;
					isPenMode = false;
					isSelectMode = true;
					
				}
			}
			return drawingSurface.mMultiTouchController.onTouchEvent(motionEvent);
		} else if(isEraseMode){
			if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
	            drawingSurface.isDrawing = true;

	            setEraserBrush();
	            drawingSurface.previewPath.paint = getPreviewPaint(Color.WHITE);
	            
	            currentDrawingPath = new DrawingPath(path, currentPaint);
	            currentDrawingPath.paint = currentPaint;
	            currentDrawingPath.path = new Path();
	            currentBrush.mouseDown(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);
	            currentBrush.mouseDown(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);
	            
	        }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
	            drawingSurface.isDrawing = true;
	            currentBrush.mouseMove( currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);
	            currentBrush.mouseMove(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);

	        }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
	            currentBrush.mouseUp(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY()-110);
	            drawingSurface.previewPath.path = new Path();
	            drawingSurface.addDrawingPath(currentDrawingPath);

	            currentBrush.mouseUp( currentDrawingPath.path, motionEvent.getX(), motionEvent.getY()-110);

	            undoBtn.setEnabled(true);
	            redoBtn.setEnabled(false);
	        }
		}

		return true;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu from XML resource
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editor_options_menu, menu);

		// Only add extra menu items for a saved note
		if (mState == STATE_EDIT) {
			// Append to the
			// menu items for any other activities that can do stuff with it
			// as well. This does a query on the system for any activities that
			// implement the ALTERNATIVE_ACTION for our data, adding a menu item
			// for each one that is found.
			Intent intent = new Intent(null, mUri);
			intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new ComponentName(this, NoteEditor.class), null, intent, 0, null);
		}

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * This method is called when a menu item is selected. Android passes in the
	 * selected item. The switch statement in this method calls the appropriate
	 * method to perform the action the user chose.
	 * 
	 * @param item
	 *            The selected MenuItem
	 * @return True to indicate that the item was processed, and no further work
	 *         is necessary. False to proceed to further processing as indicated
	 *         in the MenuItem object.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle all of the possible menu actions.
		switch (item.getItemId()) {
		case R.id.menu_save:
			// String text = mText.getText().toString();
			String text = "this is a test";
			// updateNote(text, null);
			final Activity currentActivity = this;
			Handler saveHandler = new Handler() {
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
			};
			new ExportBitmapToFile(this, saveHandler, drawingSurface.getBitmap()).execute();
			updateNote(text, text);
			finish();
			break;
		case R.id.menu_delete:
			deleteNote();
			finish();
			break;
		case R.id.menu_revert:
			cancelNote();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Replaces the current note contents with the text and title provided as
	 * arguments.
	 * 
	 * @param text
	 *            The new note contents to use.
	 * @param title
	 *            The new note title to use
	 */
	private final void updateNote(String text, String title) {

		// Sets up a map to contain values to be updated in the provider.
		ContentValues values = new ContentValues();
		values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

		// If the action is to insert a new note, this creates an initial title
		// for it.
		if (mState == STATE_INSERT) {

			/*
			 * // If no title was provided as an argument, create one from the
			 * note text. if (title == null) {
			 * 
			 * // Get the note's length int length = text.length();
			 * 
			 * // Sets the title by getting a substring of the text that is 31
			 * characters long // or the number of characters in the note plus
			 * one, whichever is smaller. title = text.substring(0, Math.min(30,
			 * length));
			 * 
			 * // If the resulting length is more than 30 characters, chops off
			 * any // trailing spaces if (length > 30) { int lastSpace =
			 * title.lastIndexOf(' '); if (lastSpace > 0) { title =
			 * title.substring(0, lastSpace); } } }
			 */
			// In the values map, sets the value of the title
			values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
		} else if (title != null) {
			// In the values map, sets the value of the title
			values.put(NotePad.Notes.COLUMN_NAME_TITLE, title);
		}

		// This puts the desired notes text into the map.
		values.put(NotePad.Notes.COLUMN_NAME_NOTE, text);

		/*
		 * Updates the provider with the new values in the map. The ListView is
		 * updated automatically. The provider sets this up by setting the
		 * notification URI for query Cursor objects to the incoming URI. The
		 * content resolver is thus automatically notified when the Cursor for
		 * the URI changes, and the UI is updated. Note: This is being done on
		 * the UI thread. It will block the thread until the update completes.
		 * In a sample app, going against a simple provider based on a local
		 * database, the block will be momentary, but in a real app you should
		 * use android.content.AsyncQueryHandler or android.os.AsyncTask.
		 */

		/*
		 * getContentResolver().update( getIntent().getData(), // The URI for
		 * the record to update. values, // The map of column names and new
		 * values to apply to them. null, // No selection criteria are used, so
		 * no where columns are necessary. null // No where columns are used, so
		 * no where arguments are necessary. );
		 */

	}

	/**
	 * This helper method cancels the work done on a note. It deletes the note
	 * if it was newly created, or reverts to the original text of the note i
	 */
	private final void cancelNote() {
		if (mCursor != null) {
			if (mState == STATE_EDIT) {
				// Put the original note text back into the database
				mCursor.close();
				mCursor = null;
				ContentValues values = new ContentValues();
				values.put(NotePad.Notes.COLUMN_NAME_NOTE, mOriginalContent);
				getContentResolver().update(mUri, values, null, null);
			} else if (mState == STATE_INSERT) {
				// We inserted an empty note, make sure to delete it
				deleteNote();
			}
		}
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Take care of deleting a note. Simply deletes the entry.
	 */
	private final void deleteNote() {
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
			getContentResolver().delete(mUri, null, null);
			mText.setText("");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawing_activity);

		setCurrentPaint();
		currentBrush = new PenBrush();

		drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);
		drawingSurface.setOnTouchListener(this);
		drawingSurface.previewPath = new DrawingPath(path, currentPaint);
		drawingSurface.previewPath.path = new Path();
		drawingSurface.previewPath.paint = getPreviewPaint(0xFFC1C1C1);

		redoBtn = (ImageButton) findViewById(R.id.redoBtn);
		undoBtn = (ImageButton) findViewById(R.id.undoBtn);

		redoBtn.setOnClickListener(this);
		undoBtn.setOnClickListener(this);

		redoBtn.setEnabled(false);
		undoBtn.setEnabled(false);

		// multi touch stuff
		// Bitmap itemBitmap =
		// ((BitmapDrawable)getResources().getDrawable(R.drawable.logo)).getBitmap();
		// DrawingSurface.setPinchWidget(itemBitmap);

		// notepad stuff
		/*
		 * Creates an Intent to use when the Activity object's result is sent
		 * back to the caller.
		 */
		final Intent intent = getIntent();

		/*
		 * Sets up for the edit, based on the action specified for the incoming
		 * Intent.
		 */

		// Gets the action that triggered the intent filter for this Activity
		final String action = intent.getAction();

		// For an edit action:
		if (Intent.ACTION_EDIT.equals(action)) {

			// Sets the Activity state to EDIT, and gets the URI for the data to
			// be edited.
			mState = STATE_EDIT;
			mUri = intent.getData();

			// For an insert or paste action:
		} else if (Intent.ACTION_INSERT.equals(action) || Intent.ACTION_PASTE.equals(action)) {

			// Sets the Activity state to INSERT, gets the general note URI, and
			// inserts an
			// empty record in the provider
			mState = STATE_INSERT;
			mUri = getContentResolver().insert(intent.getData(), null);

			/*
			 * If the attempt to insert the new note fails, shuts down this
			 * Activity. The originating Activity receives back RESULT_CANCELED
			 * if it requested a result. Logs that the insert failed.
			 */
			if (mUri == null) {

				// Writes the log identifier, a message, and the URI that
				// failed.
				Log.e(TAG, "Failed to insert new note into " + getIntent().getData());

				// Closes the activity.
				finish();
				return;
			}

			// Since the new entry was created, this sets the result to be
			// returned
			// set the result to be returned.
			setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
			// If the action was other than EDIT or INSERT:

		}

		// For a paste, initializes the data from clipboard.
		// (Must be done after mCursor is initialized.)
		if (Intent.ACTION_PASTE.equals(action)) {
			// Does the paste
			// performPaste();
			// Switches the state to EDIT so the title can be modified.
			mState = STATE_EDIT;
		}

		// Gets a handle to the EditText in the the layout.
		mText = (EditText) findViewById(R.id.note);

		/*
		 * If this Activity had stopped previously, its state was written the
		 * ORIGINAL_CONTENT location in the saved Instance state. This gets the
		 * state.
		 */
		if (savedInstanceState != null) {
			mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
		}

	}
	
	private Paint getPreviewPaint(int color) {
		final Paint previewPaint = new Paint();
		previewPaint.setColor(color);
		previewPaint.setStyle(Paint.Style.STROKE);
		previewPaint.setStrokeJoin(Paint.Join.ROUND);
		previewPaint.setStrokeCap(Paint.Cap.ROUND);
		previewPaint.setStrokeWidth(3);
		return previewPaint;
	}

	private void setCurrentPaint() {
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(Color.BLACK);
		currentPaint.setStyle(Paint.Style.STROKE);
		currentPaint.setStrokeJoin(Paint.Join.ROUND);
		currentPaint.setStrokeCap(Paint.Cap.ROUND);
		currentPaint.setStrokeWidth(3);
	}
	
	private void setEraserBrush() {
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(Color.WHITE);
		currentPaint.setStyle(Paint.Style.STROKE);
		currentPaint.setStrokeJoin(Paint.Join.ROUND);
		currentPaint.setStrokeCap(Paint.Cap.ROUND);
		currentPaint.setStrokeWidth(3);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.circleBtn:
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(Color.BLACK);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(3);
			drawingSurface.addDrawingPath(new DrawingCircle(currentPaint, path));
			drawingSurface.isDrawing = true;
			break;
		case R.id.rectangleBtn:
			currentPaint = new Paint();
			currentPaint.setDither(true);
			currentPaint.setColor(Color.BLACK);
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeJoin(Paint.Join.ROUND);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(3);
			drawingSurface.addDrawingPath(new DrawingRectangle(currentPaint, path));
			drawingSurface.isDrawing = true;
			break;
		case R.id.curveBtn:
			currentPaint = new Paint();
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(3.0f);
			currentPaint.setAntiAlias(true);
			currentPaint.setColor(Color.BLACK);
			drawingSurface.addDrawingPath(new DrawingCurve(currentPaint, path));
			drawingSurface.isDrawing = true;
			break;
		case R.id.axesBtn:
			currentPaint = new Paint();
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(3.0f);
			currentPaint.setAntiAlias(true);
			currentPaint.setColor(Color.BLACK);
			drawingSurface.addDrawingPath(new DrawAxes(currentPaint, path));
			drawingSurface.isDrawing = true;
			break;
		case R.id.triangleBtn:
			currentPaint = new Paint();
			currentPaint.setStyle(Paint.Style.STROKE);
			currentPaint.setStrokeCap(Paint.Cap.ROUND);
			currentPaint.setStrokeWidth(3.0f);
			currentPaint.setAntiAlias(true);
			currentPaint.setColor(Color.BLACK);
			drawingSurface.addDrawingPath(new DrawTriangle(currentPaint, path));
			drawingSurface.isDrawing = true;
			break;
		case R.id.undoBtn:
			drawingSurface.undo();
			if (drawingSurface.hasMoreUndo() == false) {
				undoBtn.setEnabled(false);
			}
			redoBtn.setEnabled(true);
			break;

		case R.id.redoBtn:
			drawingSurface.redo();
			if (drawingSurface.hasMoreRedo() == false) {
				redoBtn.setEnabled(false);
			}

			undoBtn.setEnabled(true);
			break;
		case R.id.pathBtn:
			isMultitouchMode = false;
			isSelectMode = false;
			isPenMode = true;
			isEraseMode = false;
			break;
		case R.id.selectBtn:
			DrawingSurface.mPinchWidget = null;
			isMultitouchMode = true;
			isPenMode = false;
			isSelectMode = true;
			isEraseMode = false;
			break;
		case R.id.moveBtn:
			isPenMode = false;
			isSelectMode = false;
			isEraseMode = false;
			isMultitouchMode = true;
			break;
		case R.id.eraseBtn:
			isPenMode = false;
			isSelectMode = false;
			isMultitouchMode = false;
			isEraseMode = true;
			break;
		}
	}

	private class ExportBitmapToFile extends AsyncTask<Intent, Void, Boolean> {
		private Handler mHandler;
		private Bitmap nBitmap;

		public ExportBitmapToFile(Context context, Handler handler, Bitmap bitmap) {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			// mHandler.post(completeRunnable);
			return false;
		}

		@Override
		protected void onPostExecute(Boolean bool) {
			super.onPostExecute(bool);
			if (bool) {
				mHandler.sendEmptyMessage(1);
			}
		}
	}
}