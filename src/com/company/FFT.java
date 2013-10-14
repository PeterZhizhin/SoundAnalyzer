package com.company;

import com.company.util.ComplexNumber;


import static org.lwjgl.opengl.GL11.*;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 09.10.13
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
public class FFT {
    private final int magicNumber = 2;

    private float maxValue = 5000;

    private ComplexNumber[] complexNumbers;
    private float[] amps;
    private ComplexNumber[] tableExp;
    private int[] indexes;
    private int deskretezationFrequency;


    private int width,heigth;

    public FFT(int width, int heigth)
    {
        complexNumbers = new ComplexNumber[1];
        tableExp = new ComplexNumber[1];
        indexes = new int[1];
        amps=new float[256];
        this.width = width;
        this.heigth = heigth;
    }

    public void draw()
    {
        //drawRawAmplitudes();
        drawNotesAmplitutes();
    }

    public void drawRawAmplitudes()
    {
        glLineWidth(1f);
        float step = (float)width / complexNumbers.length * 2;
        float scale = heigth / maxValue;
        float posX = 0;

        glBegin(GL_LINE_STRIP);
        glVertex2f(0,heigth);
        for (int i = 0; i<complexNumbers.length / 2; i++)
        {
            glVertex2d(posX, heigth-complexNumbers[i].re * scale);
            posX+=step;
        }
        glVertex2f(width,heigth);
        glEnd();
    }

    public void drawNotesAmplitutes()
    {
        glLineWidth(3f);
        float step = Math.max((float)width / amps.length*2, 1);
        float scale= 0.1f;
        float posX=0;
        glBegin(GL_LINE_STRIP);
        glVertex2f(0,heigth);
        for (int i = 0; i<amps.length; i++)
        {
            float color=amps[i]/1000f;
            glColor4f(color, color, color, 1f);
            //glVertex2d(posX, heigth);
            //glVertex2d(posX+step, heigth);
            //glVertex2d(posX+step, heigth-amps[i] * scale);
            glVertex2d(posX, heigth-amps[i] * scale);
            posX+=step;
        }
        glVertex2f(width,heigth);
        glEnd();
    }

    public void calculate(int[][] sound, int fDesk)
    {
        deskretezationFrequency = fDesk;
        int numberOfElem;
        if ((sound[0].length & (sound[0].length-1)) != 0)
        {
            numberOfElem = 1;
            do {
                numberOfElem = numberOfElem << 1;
            }
            while (numberOfElem < sound[0].length);
            numberOfElem = numberOfElem >> 1;
        }
        else
        {
            numberOfElem = sound[0].length;
        }
        float[] mySound = doTheWindowing(sound[0], numberOfElem);

        if (numberOfElem != complexNumbers.length)
        {
            complexNumbers = new ComplexNumber[numberOfElem];
            getFFTExpTable(false);
            indexes = getArrayIndexes(magicNumber);
        }
        for (int i = 0; i<complexNumbers.length; i++)
            complexNumbers[i] = new ComplexNumber(mySound[i]);

        calculateFFT();
        normalizeFFT();
        calcNotes();
    }

    private float getNoteFromFrequency(float frequency)
    {
        return (float)(12d * Math.log(frequency/27.5f)/Math.log(2));
    }

    private int getFrequencyFromNote(float note)
    {
        return (int)(27.5 * Math.pow(2, note / 12f));
    }

    private float getFrequencyAmp(float f){
        int i = Math.min((int)(f/deskretezationFrequency*complexNumbers.length/4), complexNumbers.length/2-1);
        return complexNumbers[i].re;
    }

    private void blur() {
        for (int i=1; i<amps.length-1;i++) {
            amps[i]=(amps[i-1]+amps[i]+amps[i+1])/3;
        }
    }

    private void calcNotes() {
       /*float delta=255f/amps.length;
        float pos= 0;
        for (int i=0; i<amps.length;i++) {
            int low = getFrequencyFromNote(pos);
            pos+=delta;
            int hight = Math.max(getFrequencyFromNote(pos), low+1);
            float value = getFrequencyAmp(low)*(float)Math.sqrt(hight-low);
            amps[i]=(amps[i]*0.8f+0.2f*value);
        }
        blur();
        //blur();*/

        float deltaFreq = (deskretezationFrequency / 2) / (complexNumbers.length / 2);
        float currentFreq = 27.5f;
        for (int i = 0; i<amps.length; i++)
            amps[i]=0;
        for (int i=0; i<complexNumbers.length / 2; i++)
        {
            amps[(int)(getNoteFromFrequency(currentFreq))] += complexNumbers[i].re;
            currentFreq += deltaFreq;
        }
        blur();
        blur();
    }

    private float[] doTheWindowing(int[] points, int numberOfNeededPoints)
    {
        int N = numberOfNeededPoints;
        float[] arrayToReturn = new  float[N];

        //Окно Хэмминга

        /*for (int i = 0; i<N; i++)
            arrayToReturn[i] = points[i] * (float)(0.54 - 0.46*Math.cos(2 * Math.PI * i / (N-1))); */

        //Окно Блэкмена-Наттала
        double cosArgument, cosArgument2, cosArgument3;
        double a0 = 0.3635819;
        double a1 = 0.4891775;
        double a2 = 0.1365995;
        double a3 = 0.0106411;
        for (int i = 0; i<N; i++)
        {
            cosArgument = (2 * Math.PI * i / (N-1));
            cosArgument2 = cosArgument * 2;
            cosArgument3 = cosArgument * 3;
            arrayToReturn[i] = (float)(points[i] *(a0 - a1*Math.cos(cosArgument) + a2*Math.cos(cosArgument2) - a3*Math.cos(cosArgument3)));
        }
        return arrayToReturn;
    }

    private void getFFTExpTable(boolean inverseFFT)
    {
        int CountFFTPoints = complexNumbers.length;
        tableExp = new ComplexNumber[CountFFTPoints];

        int startIndex,n,blockLength;
        ComplexNumber w, wn;
        float mnozhitel;
        if (inverseFFT)
            mnozhitel = (float)(2 * Math.PI);
        else
            mnozhitel = (float)(-2 * Math.PI);

        blockLength = 1;

        while (blockLength<CountFFTPoints)
        {
            n = blockLength;
            blockLength = blockLength << 1;

            wn = new ComplexNumber(0, mnozhitel / blockLength);
            wn = ComplexNumber.exp(wn);

            w = new ComplexNumber(1);

            startIndex = n;
            for (int i=0; i<n; i++)
            {
                tableExp[startIndex+i]=w;
                w = w.multiply(wn);
            }
        }
    }

    private int[] getArrayIndexes(int minIteration)
    {
        int[] tempIndexes, arrayToReturn;

        int startIndex, ChIndex, NChIndex, EndIndex;
        int countPoints = complexNumbers.length;

        arrayToReturn = new int[countPoints];
        for (int i = 0; i<countPoints; i++)
            arrayToReturn[i] = i;

        int lenBlock = countPoints;
        int halfBlock = lenBlock >> 1;
        int halfBlock_1 = halfBlock - 1;

        while (halfBlock > minIteration)
        {
            startIndex = 0;
            tempIndexes = new int[countPoints];
            for (int i = 0; i<countPoints; i++)
                tempIndexes[i] = arrayToReturn[i];

            do {
                ChIndex = startIndex;
                NChIndex = ChIndex+1;
                EndIndex = startIndex + halfBlock_1;

                for (int i = startIndex; i<=EndIndex; i++)
                {
                    arrayToReturn[i] = tempIndexes[ChIndex];
                    arrayToReturn[i+halfBlock] = tempIndexes[NChIndex];

                    ChIndex = ChIndex + 2;
                    NChIndex = NChIndex + 2;
                }
                startIndex = startIndex+lenBlock;
            }
            while (startIndex<countPoints);

            lenBlock = lenBlock >> 1;
            halfBlock = lenBlock >> 1;
            halfBlock_1 = halfBlock - 1;
        }

        return arrayToReturn;
    }

    private void makeArrayGood()
    {
        ComplexNumber[] arrayToReturn = new ComplexNumber[complexNumbers.length];
        for (int i = 0; i<indexes.length; i++)
            arrayToReturn[i] = complexNumbers[indexes[i]];
        complexNumbers = arrayToReturn;
    }

    private void calculateFFT()
    {
        int tableExpIndex,j, i, startIndex, NChIndex;
        ComplexNumber tempBn;

        makeArrayGood();

        int length = complexNumbers.length;
        int lengthBlock;
        int halfOfBlock;

        /*lengthBlock = 2;
        halfOfBlock = 1;

        while (lengthBlock <= length)
        {
            tableExpIndex = halfOfBlock;

            for (j = 0; j<halfOfBlock; j++)
            {
                for (i = 0; i<length; i+=lengthBlock)
                {
                    startIndex = i+j;
                    NChIndex = startIndex+halfOfBlock;

                    tempBn = complexNumbers[NChIndex].multiply(tableExp[tableExpIndex]);
                    complexNumbers[NChIndex] = complexNumbers[startIndex].substract(tempBn);
                    complexNumbers[startIndex] = complexNumbers[startIndex].add(tempBn);

                }
                tableExpIndex++;
            }

            halfOfBlock = lengthBlock;
            lengthBlock = lengthBlock << 1;
        }*/

        for (lengthBlock = 2, halfOfBlock = 1; lengthBlock <= length; halfOfBlock = lengthBlock, lengthBlock = lengthBlock << 1)
        {
            tableExpIndex = halfOfBlock;

            for (j = 0; j<halfOfBlock; j++)
            {
                for (i = 0; i<length; i+=lengthBlock)
                {
                    startIndex = i+j;
                    NChIndex = startIndex+halfOfBlock;

                    tempBn = complexNumbers[NChIndex].multiply(tableExp[tableExpIndex]);
                    complexNumbers[NChIndex] = complexNumbers[startIndex].substract(tempBn);
                    complexNumbers[startIndex] = complexNumbers[startIndex].add(tempBn);

                }
                tableExpIndex++;
            }
        }
    }

    private void normalizeFFT()
    {
        normalizeFFT((double)2 / (double)complexNumbers.length);
    }


    private void normalizeFFT(double mnozhitel)
    {
        int length = complexNumbers.length;
        length = length >> 1;

        for (int i = 0; i<length; i++)
        {
            complexNumbers[i].re = (float)(ComplexNumber.abs(complexNumbers[i])*mnozhitel);
        }
    }
}
