package com.quangcat.openglessimplepaint.important;

import android.graphics.Bitmap;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLUtils;


/**
 * Commonly used utilities for rendering and maintaining the canvas.
 *
 * @author <a href="mailto:david.e.shere@gmail.com">David Shere</a>
 * @version 1.0
 */
public class Utils {

    /*
     * Create an OpenGL texture and load it onto the GPU
     * Just for testing purposes for now. Returns the texture's identifier.
     *
     * x and y must be powers of 2
     *
     * @param x the width of the texture
     * @param y the height of the texture
     * @param color the color value
     */
    public static int makeTexture(int x, int y, int color, Bitmap bitmap) {
        int[] tid = new int[1];
        ByteBuffer bb = ByteBuffer.allocateDirect(x * y * 4);
        IntBuffer ib = bb.asIntBuffer();

        for (int i = 0; i < x * y; i++) {
            ib.put(color);
        }

        // Create and bind a single texture object.
        glGenTextures(1, tid, 0);
        glBindTexture(GL_TEXTURE_2D, tid[0]);

        if(bitmap != null) {
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);
            return tid[0];
        }

        // Copy the texture to the GPU
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, x, y, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return tid[0];
    }

    /* Make a texture from a bitmap or a new blank canvas if Bitmap is null
     *
     * @param bitmap the bitmap to use on the texture
     */
    public static int makeTexture(Bitmap bitmap, int w, int h) {
        int textureId = makeTexture(w, h, 0xffffffff, null);
        if (bitmap != null) {
            glBindTexture(GL_TEXTURE_2D, textureId);
            GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        return textureId;
    }

}
