package com.company.util.MyLittleForms;


import static org.lwjgl.opengl.GL11.*;
/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 13.10.13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class ProgressBar implements Component {

    private ProgressBarListener progressBarListener;
    private int x,y;
    private int width, height;
    private double percentPosition;

    private float barHeight;
    private float panelWidth = 10f;

    private float currentPositionWidth;
    private float currentPositionHeigth;

    public ProgressBar(int x, int y, int width, int height, ProgressBarListener progressBarListener)
    {
        this.progressBarListener = progressBarListener;
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        percentPosition = 0;

        barHeight = height / 8;
        currentPositionWidth = panelWidth / 2;
        currentPositionHeigth = barHeight * 2;
    }

    public void updatePercentPosition(double percents)
    {
        percentPosition = percents;
    }

    public void onMousePressed(int x, int y) {
        float u,v;
        u = (this.x + this.width - x) / (float)width;
        v = (this.y + this.height - y) / (float)height;
        if (u >= 0 & u <= 1 & v>=0 & v<=1)
        {
            percentPosition = u;
            progressBarListener.onPressed(u,v);
        }
    }

    public void onMouseDown(int x, int y)
    {
        float u,v;
        u = (this.x + this.width - x) / (float)width;
        v = (this.y + this.height - y) / (float)height;
        if (u >= 0 & u <= 1 & v>=0 & v<=1)
            progressBarListener.onMouseDown(u,v);
    }

    public void draw()
    {
        glBegin(GL_QUADS);
            //Рисуем прогрессбар
            glColor3f(0.7f,0.7f,0.7f);
            glVertex2f(x, y + (height / 2) - barHeight);
            glVertex2f(x + width, y + (height / 2) - barHeight);
            glVertex2f(x + width, y + (height / 2) + barHeight);
            glVertex2f(x, y + (height / 2) + barHeight);
            //Рисуем левый край
            glColor4f(1.0f,1f,1f,1.0f);
            glVertex2f(x, y);
            glVertex2f(x + panelWidth, y);
            glVertex2f(x + panelWidth, y + height);
            glVertex2f(x, y + height);
            //Рисуем правый край
            glVertex2f(x+width-panelWidth, y);
            glVertex2f(x+width, y);
            glVertex2f(x+width, y + height);
            glVertex2f(x+width-panelWidth, y+height);
            //Рисуем текущее положение
            float positionX = (float)(x + width*percentPosition);
            glVertex2f(positionX-(currentPositionWidth / 2) - panelWidth / 2, y + (currentPositionHeigth / 2));
            glVertex2f(positionX+(currentPositionWidth / 2) - panelWidth / 2, y + (currentPositionHeigth / 2));
            glVertex2f(positionX+(currentPositionWidth / 2) - panelWidth / 2, y + height - (currentPositionHeigth/2));
            glVertex2f(positionX-(currentPositionWidth / 2) - panelWidth / 2, y + height - (currentPositionHeigth/2));
        glEnd();
    }

}
