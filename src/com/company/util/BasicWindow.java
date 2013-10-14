package com.company.util;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * Created with IntelliJ IDEA.
 * User: server-user
 * Date: 04.10.13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicWindow {
    protected String title;
    private int frameRate;
    private int width;
    private int height;
    protected long getTime()
    {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private long lastFPS;
    private int FPS = 0;

    private long lastFrame = 0;
    private int getDelta()
    {
        long time = getTime();
        int delta = (int)(time - lastFrame);
        lastFrame = time;

        return delta;
    }

    private void updateFPS()
    {
        if (getTime() - lastFPS > 1000)
        {
            Display.setTitle(title + ": " + FPS);
            lastFPS += 1000;
            FPS = 0;
        }
        FPS++;
    }
    protected void initDisplay(int width, int height, int frameRate, boolean isDrawInThisCycle, String title)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
            Display.setVSyncEnabled(false);
            Display.sync(frameRate);
        }
        catch (LWJGLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        getDelta();
        lastFPS = getTime();
        GLHelper.initGL(width, height);
        goToForeverCycle(isDrawInThisCycle);
    }
    private void goToForeverCycle(boolean isDrawInThisCycle)
    {
        while (!Display.isCloseRequested())
        {
            int delta = getDelta();

            //TODO: Place update logic here
            update(delta);

            GL11.glColor3f(0.0f,0.0f,0.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            if (isDrawInThisCycle)
                draw();

            updateFPS();

            Display.update();
        }
        Display.destroy();
        destroy();
        System.exit(0);
    }

    protected void update(int deltaTime)
    {

    }
    protected void draw()
    {

    }

    protected void destroy()
    {

    }
}
