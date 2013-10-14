package com.company.util.MyLittleForms;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 13.10.13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public interface Component {

    public void draw();

    public void onMousePressed(int x, int y);

    public void onMouseDown(int x, int y);
}
