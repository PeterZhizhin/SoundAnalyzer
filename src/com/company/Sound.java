package com.company;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_SAMPLE_OFFSET;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;
import static org.lwjgl.openal.AL11.alGetBufferi;

import it.sauronsoftware.jave.*;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;

import javax.sound.sampled.AudioInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: peter
 * Date: 09.10.13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class Sound {
    private Sound(int soundSource, int sampleRate, int format, int soundBuffer, boolean contain, ByteBuffer soundData)
    {
        this.soundSource = soundSource;
        this.sampleRate = sampleRate;
        switch (format)
        {
            case AL_FORMAT_MONO16:
                isMono = true;
                is8Bit = false;
                break;
            case AL_FORMAT_STEREO16:
                isMono = false;
                is8Bit = false;
                break;
            case AL_FORMAT_MONO8:
                isMono = true;
                is8Bit = true;
                break;
            case AL_FORMAT_STEREO8:
                isMono = false;
                is8Bit = true;
                break;
            default:
                isMono = false;
                is8Bit = false;
                break;
        }

        if (contain)
            waveData = soundData;

        this.soundBuffer = soundBuffer;
        this.contain = contain;

        this.duration = getDurationFromBuffer();
    }
    public void dispose()
    {
        alDeleteBuffers(soundBuffer);
        alDeleteSources(soundSource);
    }
    public boolean contain;
    public ByteBuffer waveData;

    public int[][] getNextSamples(int samplesCount)
    {
        if (!contain)
            return null;

        int[][] arrayToReturn;
        if (isMono)
            arrayToReturn = new int[1][samplesCount];
        else
            arrayToReturn = new int[2][samplesCount];

        int currentSample = (int)getCurrentSampleCount();
        try
        {
            if (isMono)
            {
                if (is8Bit)
                {
                    waveData.position(currentSample);
                    for (int i = 0; i<samplesCount; i++)
                        arrayToReturn[0][i] = waveData.get();
                }
                else
                {
                    waveData.position(currentSample*2);
                    for (int i = 0; i<samplesCount; i++)
                        arrayToReturn[0][i] = waveData.getShort();
                }
            }
            else
            {
                if (is8Bit)
                {
                    waveData.position(currentSample * 2);
                    for (int i = 0; i<samplesCount; i++)
                    {
                        arrayToReturn[0][i] = waveData.get();
                        arrayToReturn[1][i] = waveData.get();
                    }
                }
                else
                {
                    waveData.position(currentSample * 4);
                    for (int i = 0; i<samplesCount; i++)
                    {
                        arrayToReturn[0][i] = waveData.getShort();
                        arrayToReturn[1][i] = waveData.getShort();
                    }
                }
            }
        }
        catch (BufferUnderflowException e)
        {
            for (int i = 0; i<samplesCount; i++)
                arrayToReturn[0][i]=0;
            if (!isMono)
                for (int i = 0; i<samplesCount; i++)
                    arrayToReturn[1][i]=0;
        }

        return arrayToReturn;

    }

    public int[][] getNextSamples(float seconds)
    {
        int samplesCount = (int)(seconds * sampleRate);
        return getNextSamples(samplesCount);
    }

    private int soundSource;
    private int soundBuffer;

    private float duration;
    public float getDuration()
    {
        return duration;
    }

    private int sampleRate;
    public int getSampleRate()
    {
        return sampleRate;
    }

    private boolean isMono;
    private boolean is8Bit;

    public void setVolume(float volume)
    {
        alSourcef(soundSource, AL_VELOCITY, volume);
    }
    private float getDurationFromBuffer()
    {
        int sizeInBytes, channels, bits,frequency;
        sizeInBytes = alGetBufferi(soundBuffer, AL_SIZE);
        channels = alGetBufferi(soundBuffer, AL_CHANNELS);
        bits = alGetBufferi(soundBuffer, AL_BITS);
        int lengthInSamples = sizeInBytes * 8 / (channels * bits);
        frequency = AL10.alGetBufferi(soundBuffer, AL_FREQUENCY);
        float durationInSeconds = (float)lengthInSamples / (float)frequency;
        return durationInSeconds;
    }
    public void setPosition(float timePosition)
    {
        alSourcef(soundSource, AL_SEC_OFFSET, timePosition);
    }
    public float getPlayingTime()
    {
        return alGetSourcef(soundSource, AL_SEC_OFFSET);
    }
    public void playSound()
    {
        alSourcePlay(soundSource);
    }
    public void pauseSound()
    {
        alSourcePause(soundSource);
    }
    public void stopSound()
    {
        alSourceStop(soundSource);
    }
    private float getCurrentSampleCount()
    {
        return alGetSourcef(soundSource, AL11.AL_SAMPLE_OFFSET);
    }

    public static Sound getSoundFromStrange(String path, boolean contain) throws FileNotFoundException {
        File source = new File(path);
        String targetName = "tempWave.wav";
        File target = new File(targetName);
        AudioAttributes audio = new AudioAttributes();
        audio.setSamplingRate(44100);
        audio.setChannels(2);

        EncodingAttributes attributes = new EncodingAttributes();
        attributes.setFormat("wav");
        attributes.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(source, target, attributes);
        } catch (EncoderException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

         Sound returnSound = getSound(new BufferedInputStream(new FileInputStream(target)), contain);

         target.delete();

         return returnSound;

    }

    public static Sound getSound(BufferedInputStream fileInputStream, boolean contain) {
        WaveData waveData = WaveData.create(fileInputStream);
        ByteBuffer data = null;
        int sampleRate = waveData.samplerate;
        int format = waveData.format;
        int buffer = alGenBuffers();
        alBufferData(buffer, format, waveData.data, sampleRate);
        if (contain)
            data = waveData.data;
        waveData.dispose();
        int source = alGenSources();
        alSourcei(source, AL_BUFFER, buffer);
        if (contain)
            return new Sound(source, sampleRate, format, buffer, contain, data);
        else
            return new Sound(source, sampleRate, format, buffer, contain, null);
    }
}
