package com.quangcat.openglessimplepaint;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.SeekBar;

import com.quangcat.openglessimplepaint.important.Brush;
import com.quangcat.openglessimplepaint.important.MyRenderer;
import com.quangcat.openglessimplepaint.important.MySurfaceView;

public class MainActivity extends Activity {

    private MySurfaceView mMySurfaceView;
    private MyRenderer mMyRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion >= 0x20000) {
            mMySurfaceView = new MySurfaceView(this);
            mMyRenderer = mMySurfaceView.getRenderer();
            setContentView(mMySurfaceView);
        } else {
            // No OpenGL ES 2.0
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_canvas, menu);
        return true;
    }

    public void onClickBrushSettings(MenuItem mi) {
        final Brush brush = mMyRenderer.getBrush();
        if (brush == null) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Brush Settings");
        alert.setMessage("Size");
        final View v = getLayoutInflater().inflate(R.layout.dialog_brush_settings, null);
        SeekBar sb = (SeekBar) v.findViewById(R.id.brush_setting_size);
        sb.setProgress((int) (brush.getSize()));
        sb = (SeekBar) v.findViewById(R.id.brush_setting_red);
        sb.setProgress((brush.getColor() & 0xff000000) >>> 24);
        sb = (SeekBar) v.findViewById(R.id.brush_setting_green);
        sb.setProgress((brush.getColor() & 0xff0000) >>> 16);
        sb = (SeekBar) v.findViewById(R.id.brush_setting_blue);
        sb.setProgress((brush.getColor() & 0xff00) >>> 8);
        sb = (SeekBar) v.findViewById(R.id.brush_setting_dabs);
        sb.setProgress((brush.getDabSteps()));
        alert.setView(v);

        final SharedPreferences settings = getPreferences(0);
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final float size = ((SeekBar) v.findViewById(R.id.brush_setting_size)).getProgress();
                brush.setSize(size);
                final int dabs = ((SeekBar) v.findViewById(R.id.brush_setting_dabs)).getProgress();
                brush.setDabSteps(dabs);
                int color = ((SeekBar) v.findViewById(R.id.brush_setting_red)).getProgress() << 24;
                color |= ((SeekBar) v.findViewById(R.id.brush_setting_green)).getProgress() << 16;
                color |= ((SeekBar) v.findViewById(R.id.brush_setting_blue)).getProgress() << 8;
                final int c = color;
                mMySurfaceView.queueEvent(new Runnable() {
                    public void run() {
//                        brush.setColor(c);
                    }
                });
                SharedPreferences.Editor edit = settings.edit();
                edit.putFloat("BRUSH_SIZE", size);
                edit.putInt("BRUSH_COLOR", color);
                edit.putInt("BRUSH_DABS", dabs);
                edit.apply();
            }
        });
        alert.show();
    }

    public void onClickClearCanvas(MenuItem mi) {
        mMyRenderer.clear();
    }

}
