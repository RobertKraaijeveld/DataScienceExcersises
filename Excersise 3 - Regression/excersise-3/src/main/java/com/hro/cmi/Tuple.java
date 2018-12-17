package com.hro.cmi;

public class Tuple<X, Y>
{
    public X Item1;
    public Y Item2;

    public Tuple(X x, Y y) 
    {
        this.Item1 = x;
        this.Item2 = y;
    }
}