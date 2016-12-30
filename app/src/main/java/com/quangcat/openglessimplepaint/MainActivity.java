package com.quangcat.openglessimplepaint;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends Activity {

    private Canvas mCanvas;
    private String mSavePath;
    private String openFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            openFile = extras.getString("openFile");
        }

        mCanvas = new Canvas(this);
        mSavePath = Environment.getExternalStorageDirectory() + "/" + Constants.NAME + "/";
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* openFile is the filename provided when the user selects an image from
         * the OpenActivity activity.  If they didn't the last canvas state
         * will be loaded instead. */
        final String fileName;
        if (openFile != null)
            fileName = openFile;
        else
            fileName = Constants.AUTOSAVE;

        /* Reload the last image worked on */
        mCanvas.getSurfaceView().onResume();
        Log.d(Constants.NAME, mSavePath + fileName);
        final File file = new File(mSavePath, fileName);
        mCanvas.getSurfaceView().queueEvent(new Runnable() {
            public void run() {
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(mSavePath + fileName);
                    mCanvas.getRenderer().setCanvasBitmap(bitmap);
                } else {
                    mCanvas.getRenderer().setCanvasBitmap(null);
                }
            }
        });
        openFile = null;

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(Constants.NAME, "onPause");

        /* This should get moved to the SurfaceView onPause method. */
        mCanvas.getSurfaceView().queueEvent(new Runnable() {
            public void run() {
                mCanvas.save(mSavePath, Constants.AUTOSAVE);
            }
        });

        mCanvas.getSurfaceView().onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_canvas, menu);
        return true;
    }

    /**
     * Prompt user for a file name and save the canvas to disk.
     * Alert prompt example from:
     * http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
     */
    public void onClickSave(MenuItem mi) {
        // Setup the EditText view for our save dialog
        final EditText input = new EditText(this);
        input.setSingleLine();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save Image");
        alert.setMessage("Name");

        alert.setView(input);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String fileName = input.getText().toString() + ".png";
                Log.d(Constants.NAME, "saving: " + fileName);
                mCanvas.getSurfaceView().queueEvent(new Runnable() {
                    public void run() {
                        mCanvas.save(mSavePath, fileName);
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled
            }
        });

        alert.show();

    }

    /**
     * Opens the settings window for the brush.
     */
    public void onClickBrushSettings(MenuItem mi) {

        // Make sure the Brush is available for editing.
        if (mCanvas.getBrush() == null)
            return;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Brush Settings");
        alert.setMessage("Size");

        final View v = getLayoutInflater().inflate(R.layout.dialog_brush_settings, null);

        /* Size slider */
        SeekBar sb = (SeekBar) v.findViewById(R.id.brush_setting_size);
        sb.setProgress((int) (mCanvas.getBrush().getSize()));

        /* Color sliders */
        sb = (SeekBar) v.findViewById(R.id.brush_setting_red);
        sb.setProgress((mCanvas.getBrush().getColor() & 0xff000000) >>> 24);
        sb = (SeekBar) v.findViewById(R.id.brush_setting_green);
        sb.setProgress((mCanvas.getBrush().getColor() & 0xff0000) >>> 16);
        sb = (SeekBar) v.findViewById(R.id.brush_setting_blue);
        sb.setProgress((mCanvas.getBrush().getColor() & 0xff00) >>> 8);
        
        /* Steps between brush dabs to interpolate*/
        sb = (SeekBar) v.findViewById(R.id.brush_setting_dabs);
        sb.setProgress((mCanvas.getBrush().getDabSteps()));

        alert.setView(v);

        final SharedPreferences settings = getPreferences(0);

        /* Set and save settings when the user clicks Done */
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final float size = ((SeekBar) v.findViewById(R.id.brush_setting_size)).getProgress();
                mCanvas.getBrush().setSize(size);
                final int dabs = ((SeekBar) v.findViewById(R.id.brush_setting_dabs)).getProgress();
                mCanvas.getBrush().setDabSteps(dabs);
                int color = ((SeekBar) v.findViewById(R.id.brush_setting_red)).getProgress() << 24;
                color |= ((SeekBar) v.findViewById(R.id.brush_setting_green)).getProgress() << 16;
                color |= ((SeekBar) v.findViewById(R.id.brush_setting_blue)).getProgress() << 8;
                final int c = color;
                mCanvas.getSurfaceView().queueEvent(new Runnable() {
                    public void run() {
                        mCanvas.getBrush().setColor(c);
                    }
                });
                SharedPreferences.Editor edit = settings.edit();
                edit.putFloat("BRUSH_SIZE", size);
                edit.putInt("BRUSH_COLOR", color);
                edit.putInt("BRUSH_DABS", dabs);
                edit.commit();
            }
        });

        /* Do nothing if the user clicks Cancel */
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled
            }
        });

        alert.show();

    }

    /**
     * Clears the canvas.
     */
    public void onClickClearCanvas(MenuItem mi) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Clear Canvas?");

        /* Do nothing if the user clicks Cancel */
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mCanvas.getSurfaceView().queueEvent(new Runnable() {
                    public void run() {
                        mCanvas.clear();
                    }
                });
            }
        });

        /* Do nothing if the user clicks Cancel */
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled
            }
        });

        alert.show();
    }

}
