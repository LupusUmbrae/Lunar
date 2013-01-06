package org.moss.lunar.palette.types;

public class InterpolationException extends Exception
{

    public InterpolationException(String msg)
    {
        super(msg);
    }

    public InterpolationException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
