package com.almondmendoza.drawings;

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
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import android.widget.Button;
import android.widget.EditText;

import com.example.android.notepad.NoteEditor;
import com.example.android.notepad.NotePad;
import com.example.android.notepad.R;
import com.almondmendoza.drawings.brush.Brush;
import com.almondmendoza.drawings.brush.PenBrush;

/**
 * Created by IntelliJ IDEA. User: almondmendoza Date: 07/11/2010 Time: 2:14 AM
 * Link: http://www.tutorialforandroid.com/
 */
public class DrawingActivity extends Activity implements View.OnTouchListener {
	public DrawingSurface drawingSurface;
	private DrawingPath currentDrawingPath;
	private Paint currentPaint;
	private Path path;
	private Button redoBtn;
	private Button undoBtn;
	private Boolean isPenMode = true;
	private Boolean isMultitouchmode = false;
	int x1 = 0, y1 = 0, x2 = 0, y2 = 0, dx = 0, dy = 0;
	private Brush currentBrush;
	private Boolean isSelectMode = true;

	private File APP_FILE_PATH = new File(Environment.getExternalStorageDirectory().getPath() + "/savedImages");

	// Global mutable variables
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mText;
	private String mOriginalContent;

	// This Activity can be started by more than one action. Each action is
	// represented
	// as a "state" constant
	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;

	// For logging and debugging purposes
	private static final String TAG = "NoteEditor";

	// A label for the saved state of the activity
	private static final String ORIGINAL_CONTENT = "origContent";

	int x3 = 0, y3 = 0, x4 = 0, y4 = 0;

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		// dx = 0;
		// dy = 0;
		if (isPenMode) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
	            drawingSurface.isDrawing = true;
	        	
	            currentDrawingPath = new DrawingPath(path, currentPaint);
	            currentDrawingPath.paint = currentPaint;
	            currentDrawingPath.path = new Path();
	            currentBrush.mouseDown(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());
	            currentBrush.mouseDown(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());

	            path = new Path();
				currentBrush.mouseDown(path, motionEvent.getX(), motionEvent.getY() - 110);
				Log.d("jaltade", "Down_X " + motionEvent.getX());
				Log.d("jaltade", "Down_Y " + (motionEvent.getY() - 110));

			} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
				drawingSurface.isDrawing = true;
				currentBrush.mouseMove(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());
				currentBrush.mouseMove(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());
				
				currentBrush.mouseMove(path, motionEvent.getX(), motionEvent.getY() - 110);

			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				currentBrush.mouseUp(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());
				drawingSurface.previewPath.path = new Path();

				currentBrush.mouseUp(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());

				currentBrush.mouseUp(path, motionEvent.getX(), motionEvent.getY() - 110);

				drawingSurface.addDrawingPath(currentDrawingPath);
				drawingSurface.isDrawing = true;
				undoBtn.setEnabled(true);
				redoBtn.setEnabled(false);
			}
			return true;
		} else if (isMultitouchmode) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				if (isSelectMode) {
					x1 = (int) motionEvent.getX();
					y1 = (int) motionEvent.getY() - 110;
					Log.d("jaltade", "here");
				}

				else {
					x3 = (int) motionEvent.getX();
					y3 = (int) motionEvent.getY();
				}
			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				if (isSelectMode) {
					x2 = (int) motionEvent.getX();
					y2 = (int) motionEvent.getY() - 110;
					Log.d("jaltade", "here2");

					dx = Math.abs(x2 - x1);
					dy = Math.abs(y2 - y1);

					DrawingSurface.mBitmap.getWidth();
					DrawingSurface.mBitmap.getHeight();

					isSelectMode = false;

					Log.d("jaltade", "has selected");
					// Log.d("jaltade","1 Bitmap width "+width_bmp);
					// Log.d("jaltade","1 dx "+(dx)+"");
					// Log.d("jaltade","1 Bitmap height "+height_bmp);
					// Log.d("jaltade","1 dy "+(dy)+"");
					// Log.d("jaltade","1 x1 "+x1);
					// Log.d("jaltade","1 y1 "+y1);
					// Log.d("jaltade","1 x2 "+x2);
					// Log.d("jaltade","1 y2 "+y2);

				} else if (!isSelectMode) {

					//Display display = getWindowManager().getDefaultDisplay();
					//Point size = new Point();
					//display.getSize(size);
					//int width_scr = size.x;
					//int height_scr = size.y;

					int width_bmp = DrawingSurface.mBitmap.getWidth();
					int height_bmp = DrawingSurface.mBitmap.getHeight();

					Log.d("jaltade", "Will now drag");
					// Log.d("jaltade","Bitmap width "+width_bmp);
					// Log.d("jaltade","dx "+(dx)+"");
					// Log.d("jaltade","Bitmap height "+height_bmp);
					// Log.d("jaltade","dy "+(dy)+"");
					// Log.d("jaltade","x1 "+x1);
					// Log.d("jaltade","y1 "+y1);
					// Log.d("jaltade","x2 "+x2);
					// Log.d("jaltade","y2 "+y2);

					Log.d("jaltade", "setting dx/dy");
					if (y1 + dy > height_bmp) {
						dy = height_bmp - y1 - 1;
					}
					if (y1 + dy < 0) {
						dy = y1 - 1;
					}

					if (x1 + dx > width_bmp) {
						dx = width_bmp - x1 - 1;
					}
					if (x1 + dx < 0) {
						dx = x1 - 1;
					}

					Log.d("jaltade", "New dx " + (dx) + "");
					Log.d("jaltade", "New dy " + (dy) + "");

					// <hack>
					// if(dx == 0 ) dx = 1;
					// if(dy == 0 ) dy = 1;
					// </hack>
					drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);

					Bitmap currSelect = Bitmap.createBitmap(drawingSurface.getBitmap(), x1, y1, dx, dy);
					Log.d("jaltade", "width " + currSelect.getWidth());
					Log.d("jaltade", "height " + currSelect.getHeight());
					DrawingSurface.setPinchWidget(currSelect);

					Bitmap.createBitmap(1000, 1110, Bitmap.Config.ARGB_8888);

					Paint p = new Paint();
					p.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
					// Canvas c = new Canvas(bmOverlay);

					Canvas c = DrawingSurface.mCanvas;
					drawingSurface.invalidate(new Rect(0, 0, 1000, 1000));
					// drawingSurface.draw(c);
					// drawingSurface.getCommandManager().executeAll(c);
					x4 = (int) motionEvent.getX();
					y4 = (int) motionEvent.getY() - 110;
					Log.d("jaltade", " new image at X: " + x4);
					Log.d("jaltade", " new image at Y: " + y4);

					// c.drawBitmap(currSelect,motionEvent.getX(),
					// motionEvent.getY()-110, p);
					c.drawBitmap(currSelect, x4, y4, p);
					// c.drawBitmap(currSelect,100, 300, p);
					// c.drawRect(x1, y1, x2, y2, p);
					// drawingSurface.invalidate(new Rect(x1,y1,x2,y2));
					// drawingSurface.invalidate(new
					// Rect((int)motionEvent.getX(), (int)
					// motionEvent.getY()-110, (int)motionEvent.getX()+dx,
					// (int)motionEvent.getY()+dy-110));
					// DrawingSurface.setPinchWidget(currSelect);
					// drawingSurface.draw(c);
					// drawingSurface.getCommandManager().executeAll(c);
					// DrawingSurface.mBitmap = currSelect;
					isSelectMode = true;
				}

				/*
				 * Canvas tempCanvas = new Canvas(drawingSurface.mBitmap); Paint
				 * myPaint = new Paint(); myPaint.setColor(Color.argb(128, 255,
				 * 255, 255)); myPaint.setStrokeWidth(10);
				 * tempCanvas.drawRect(x1, y1, dx, dy, myPaint);
				 */
			}

			return drawingSurface.mMultiTouchController.onTouchEvent(motionEvent);
		}

		return true;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		/*
		if (isPenMode) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				drawingSurface.isDrawing = true;

				currentDrawingPath = new DrawingPath(path, currentPaint);
				currentDrawingPath.paint = currentPaint;
				currentDrawingPath.path = new Path();
				currentBrush.mouseDown(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());
				currentBrush.mouseDown(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());

			} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
				drawingSurface.isDrawing = true;
				currentBrush.mouseMove(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());
				currentBrush.mouseMove(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());

			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

				currentBrush.mouseUp(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());
				drawingSurface.previewPath.path = new Path();
				drawingSurface.addDrawingPath(currentDrawingPath);

				currentBrush.mouseUp(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());

				undoBtn.setEnabled(true);
				redoBtn.setEnabled(false);

			}
		}

		return true;
		*/
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

	private Paint getPreviewPaint() {
		final Paint previewPaint = new Paint();
		previewPaint.setColor(0xFFC1C1C1);
		previewPaint.setStyle(Paint.Style.STROKE);
		previewPaint.setStrokeJoin(Paint.Join.ROUND);
		previewPaint.setStrokeCap(Paint.Cap.ROUND);
		previewPaint.setStrokeWidth(3);
		return previewPaint;
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
		drawingSurface.previewPath.paint = getPreviewPaint();

		redoBtn = (Button) findViewById(R.id.redoBtn);
		undoBtn = (Button) findViewById(R.id.undoBtn);

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

	private void setCurrentPaint() {
		currentPaint = new Paint();
		currentPaint.setDither(true);
		currentPaint.setColor(Color.BLACK);
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
		case R.id.selectBtn:
			isMultitouchmode = true;
			isPenMode = false;
			// currentBrush = new CircleBrush();
			break;
		case R.id.pathBtn:
			isPenMode = true;
			isMultitouchmode = false;
			// currentBrush = new PenBrush();
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