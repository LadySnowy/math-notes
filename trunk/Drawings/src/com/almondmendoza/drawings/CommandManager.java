package com.almondmendoza.drawings;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 15/11/2010
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommandManager {
    private List<ICanvasCommand> currentStack;
    private List<ICanvasCommand> redoStack;

    public  CommandManager(){
        currentStack = Collections.synchronizedList(new ArrayList<ICanvasCommand>());
        redoStack = Collections.synchronizedList(new ArrayList<ICanvasCommand>());
    }

    public void addCommand(ICanvasCommand command){
        redoStack.clear();
        currentStack.add(command);
    }

    public void undo (){
        final int length = currentStackLength();
        
        if ( length > 0) {
            final ICanvasCommand undoCommand = currentStack.get(  length - 1  );
            currentStack.remove( length - 1 );
            undoCommand.undo();
            redoStack.add( undoCommand );
        }
    }

    public int currentStackLength(){
        final int length = currentStack.toArray().length;
        return length;
    }


    public void executeAll( Canvas canvas){
        if( currentStack != null ){
            synchronized( currentStack ) {
                final Iterator i = currentStack.iterator();
                Log.d("aaa",currentStack.toArray().length + "");
                while ( i.hasNext() ){
                    final ICanvasCommand drawingPath = (ICanvasCommand) i.next();
                    drawingPath.draw( canvas );
                }
            }
        }
    }



    public boolean hasMoreRedo(){
        return  redoStack.toArray().length > 0;
    }

    public boolean hasMoreUndo(){
        return  currentStack.toArray().length > 0;
    }

    public void redo(){
        final int length = redoStack.toArray().length;
        if ( length > 0) {
            final ICanvasCommand redoCommand = redoStack.get(  length - 1  );
            redoStack.remove( length - 1 );
            currentStack.add( redoCommand );
        }
    }
}
