package com.quangcat.openglessimplepaint.important;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {

    private MyRenderer mRenderer;

    public MySurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyRenderer(context);
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        boolean newEvent = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                newEvent = true;
            case MotionEvent.ACTION_MOVE:
                final boolean newEventFinal = newEvent;
                final int i = event.getActionIndex();
                final float x = event.getX(i);
                final float y = event.getY(i);
                queueEvent(new Runnable() {
                    public void run() {
                        mRenderer.addCanvasDab((int) x, (int) y, newEventFinal);
                    }
                });
                return true;
        }
        return false;
    }

    public MyRenderer getRenderer() {
        return mRenderer;
    }

}
