package com.company;

import com.company.util.BasicWindow;
import com.company.util.MyLittleForms.ProgressBar;
import com.company.util.MyLittleForms.ProgressBarListener;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 09.10.13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class Window extends BasicWindow {
    int width,height;

    Sound music;
    int time = 0;
    float sampleCurrent, deltaSample = 0;

    ProgressBar musicProgressBar;

    boolean wasMouseButtonPressed;

    FFT fft;
    public Window(int width, int height, int frameRate, String title)
    {
        this.width = width;
        this.height = height;
        try {
            AL.create();
        } catch (LWJGLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }


        fft = new FFT(width,height);

        long currentTime = System.currentTimeMillis();
        System.out.println("Started");
        try {
            music = Sound.getSoundFromStrange("music.mp3", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(System.currentTimeMillis() - currentTime);
        music.setVolume(1.0f);
        music.playSound();

        fft.calculate(music.getNextSamples(0.001f),music.getSampleRate());

        musicProgressBar = new ProgressBar(50, 20, width-100, 50, new ProgressBarListener() {
            @Override
            public void onPressed(float u, float v) {
                music.setPosition(music.getDuration() * u);
            }

            @Override
            public void onMouseDown(float u, float v) {

            }
        });

        initDisplay(width, height, frameRate, true, title);
    }

    @Override
    public void update(int deltaTime)
    {
        if (wasMouseButtonPressed & !Mouse.isButtonDown(0))
        {
            musicProgressBar.onMousePressed(width-Mouse.getX(), height-Mouse.getY());
        }
        time+=deltaTime;
        if (time > 50)
        {
            time = 0;
            fft.calculate(music.getNextSamples(8192), music.getSampleRate());
        }
        musicProgressBar.updatePercentPosition((double)music.getPlayingTime() / (double)music.getDuration());
        wasMouseButtonPressed = Mouse.isButtonDown(0);
    }

    @Override
    public void draw()
    {
        glColor3f(0.5f,0.5f,0.5f);
        fft.draw();
        musicProgressBar.draw();
    }

    @Override
    public void destroy()
    {
        music.dispose();
        AL.destroy();
    }
}
