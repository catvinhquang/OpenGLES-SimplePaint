package com.quangcat.openglessimplepaint.important;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;

import static android.opengl.GLES20.*;

import android.util.Log;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    private AssetManager assets;
    private int program;

    public ShaderHelper(String vertexSrc, String fragmentSrc, AssetManager assets) {
        this.assets = assets;

        program = glCreateProgram();

        /* Bind the shaders to the program */
        int vs = makeShader(vertexSrc);
        int fs = makeShader(fragmentSrc);
        glAttachShader(program, vs);
        glAttachShader(program, fs);

        /* Link the program and handle any errors */
        int linkStatus[] = new int[1];
        glLinkProgram(program);
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.e(TAG, "ERROR LINKING");
            Log.e(TAG, glGetProgramInfoLog(program));
            glDeleteProgram(program);
            return;
        }
    }

    /**
     * Generate a shaderHelper from a source file.  Returns the shaderHelper's identifier.
     *
     * @param fileName The path to the fragment or vertex shaderHelper to compile.
     */
    private int makeShader(String fileName) {
        // Read the shaderHelper source code
        String buffer = "";
        try {
            InputStream is = assets.open("shaders/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                buffer += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Build the shaderHelper.
        int shaderType = 0;
        if (fileName.endsWith(".vert")) {
            shaderType = GL_VERTEX_SHADER;
        } else if (fileName.endsWith(".frag")) {
            shaderType = GL_FRAGMENT_SHADER;
        }
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, buffer);
        glCompileShader(shader);
        return shader;
    }

    public int getProgram() {
        return program;
    }

}
