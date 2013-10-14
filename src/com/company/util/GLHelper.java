package com.company.util;


import org.lwjgl.util.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * User: server-user
 * Date: 04.10.13
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class GLHelper {
    public static void initGL(int width, int height)
    {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
       // glLineWidth(3f);
    }
}
