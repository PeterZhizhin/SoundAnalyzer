package com.company.util;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 09.10.13
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class ComplexNumber {
    public float re;
    public float im;

    public ComplexNumber(float re, float im)
    {
        this.re = re;
        this.im = im;
    }

    public ComplexNumber(float re)
    {
        this.re = re;
        this.im = 0;
    }
    public ComplexNumber()
    {
        this.re = 0;
        this.im = 0;
    }

    public ComplexNumber add(ComplexNumber whatToAdd)
    {
        return new ComplexNumber(this.re + whatToAdd.re, this.im + whatToAdd.im);
    }

    public ComplexNumber substract(ComplexNumber whatToSubstract)
    {
        return new ComplexNumber(this.re - whatToSubstract.re, this.im - whatToSubstract.im);
    }

    public ComplexNumber multiply(ComplexNumber whatToMultiply)
    {
        ComplexNumber temp = new ComplexNumber();
        temp.re = this.re * whatToMultiply.re - this.im * whatToMultiply.im;
        temp.im = this.re * whatToMultiply.im + this.im * whatToMultiply.re;
        return temp;
    }

    public ComplexNumber divide(ComplexNumber whatToDivide)
    {
        ComplexNumber temp = new ComplexNumber();
        temp.re = this.re / whatToDivide.re - this.im / whatToDivide.im;
        temp.im = this.re / whatToDivide.im + this.im / whatToDivide.re;
        return temp;
    }

    public static ComplexNumber exp(ComplexNumber pok)
    {
        double module = Math.exp(pok.re);
        return new ComplexNumber((float)(module*Math.cos(pok.im)), (float)(module*Math.sin(pok.im)));
    }
    public static float abs(ComplexNumber input)
    {
        //return (float)Math.sqrt(input.re * input.re + input.im * input.im);
        return (float)Math.hypot(input.re, input.im);
    }

}
