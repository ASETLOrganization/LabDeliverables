package com.teamluck.drawdiculous.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.teamluck.drawdiculous.app.utils.AppConst;
import com.teamluck.drawdiculous.app.utils.AppMem;
import com.teamluck.drawdiculous.backend.model.DrawAction;

import java.util.ArrayList;

/**
 * Canvas with real-time update function.
 */
public class RealtimeCanvas extends View {
    
    private static final String TAG = RealtimeCanvas.class.getSimpleName();
    
    private final Paint brush;
    private final Paint mbrush;
    private final Path path;
    public int x, y;
    public boolean first = true;
    private Bitmap bitmap;
    private int sizeBrush, colorBrush;
    private Canvas canvas;
    
    private ArrayList<DrawAction> stroke;
    
    public RealtimeCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        brush = new Paint();
        // TODO: what is mbrush?
        mbrush = new Paint(Paint.DITHER_FLAG);
        brush.setAntiAlias(true);
        brush.setDither(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeCap(Paint.Cap.ROUND);
        sizeBrush = 1;
        brush.setStrokeWidth(2);
        colorBrush = Color.BLACK;
        brush.setColor(Color.BLACK);
        
        stroke = new ArrayList<>();
        
        //TODO: need to check, solving clear canvas problem
        bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }
    
    @Override
    // TODO: what is this for?
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(0, 0, 0, 0);
        if (first) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            first = false;
        }
        x = getWidth();
        y = getHeight();
    }
    
    @Override
    // TODO: what is this for?
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, mbrush);
        canvas.drawPath(path, brush);
    }
    
    /**
     * Returns buffer and empties it.
     *
     * @return buffer containing DrawActions saved since last call
     */
    public ArrayList<DrawAction> getStroke() {
        ArrayList<DrawAction> tempStroke = stroke;
        stroke = new ArrayList<>();
        return tempStroke;
    }
    
    /**
     * Reads from data and perform actions.
     *
     * @param stroke data
     */
    public void drawStroke(ArrayList<DrawAction> stroke) {
        
        if (!AppMem.DRAW_START) {
            return;
        }
        
        for (DrawAction drawAction : stroke) {
            float relX = drawAction.getX() * x;
            float relY = drawAction.getY() * y;
            switch (drawAction.getOp()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(relX, relY);
                    break;
                case MotionEvent.ACTION_UP:
                    path.lineTo(relX, relY);
                    canvas.drawPath(path, brush);
                    path.reset();
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(relX, relY);
                    break;
                case AppConst.OP_DRAW_ACTION_COLOR:
                    colorBrush = drawAction.getData();
                    brush.setColor(drawAction.getData());
                    break;
                case AppConst.OP_DRAW_ACTION_BRUSH:
                    sizeBrush = drawAction.getData();
                    brush.setStrokeWidth(drawAction.getData());
                    break;
                case AppConst.OP_DRAW_ACTION_CLEAR:
                    clearScreen();
                default:
                    Log.d(TAG, "drawStroke: error");
            }
            invalidate();
        }
    }
    
    /**
     * Draw on current canvas and save actions.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if (!(AppMem.PAINTER && AppMem.DRAW_START)) {
            return super.onTouchEvent(event);
        }
        
        float px = event.getX();
        float py = event.getY();
        int op = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(px, py);
                
                op = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(px, py);
                canvas.drawPath(path, brush);
                path.reset();
                
                op = MotionEvent.ACTION_UP;
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(px, py);
                
                op = MotionEvent.ACTION_MOVE;
                break;
        }
        stroke.add(new DrawAction(px / x, py / y, -1, op));
        invalidate();
        return true;
    }
    
    public void colors(int color) {
        colorBrush = color;
        brush.setColor(color);
        stroke.add(new DrawAction(-1, -1, color, AppConst.OP_DRAW_ACTION_COLOR));
    }
    
    public void sizeBrush(int size) {
        sizeBrush = size;
        brush.setStrokeWidth(size * 2);
        stroke.add(new DrawAction(-1, -1, size * 2, AppConst.OP_DRAW_ACTION_BRUSH));
    }
    
    public void eraser(int size) {
        brush.setColor(Color.WHITE);
        stroke.add(new DrawAction(-1, -1, Color.WHITE, AppConst.OP_DRAW_ACTION_COLOR));
        brush.setStrokeWidth(size * 4);
        stroke.add(new DrawAction(-1, -1, size * 4, AppConst.OP_DRAW_ACTION_BRUSH));
    }
    
    public void brushReset() {
        sizeBrush(sizeBrush);
        colors(colorBrush);
    }
    
    public void clearScreen() {
        canvas.drawColor(Color.WHITE);
    }
    
    public void requestClearScreen() {
        clearScreen();
        stroke.add(new DrawAction(-1, -1, -1, AppConst.OP_DRAW_ACTION_CLEAR));
    }
}
