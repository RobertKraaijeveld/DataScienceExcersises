package com.hro.cmi;

public class Tuple<X, Y>
{
    public final X Item1;
    public final Y Item2;

    public Tuple(X x, Y y) 
    {
        this.Item1 = x;
        this.Item2 = y;
    }
}