package com.hro.cmi;

import java.util.ArrayList;

public class ErrorMeasurer
{
    public ArrayList<VariableHolder> variableSets;

    public ErrorMeasurer(ArrayList<VariableHolder> variableSets)
    {
        this.variableSets = variableSets;
    }

    // Returns the alpha/beta/gamma combination with the smallest error.
    public VariableHolder getBestAlphaAndBeta()
    {
        VariableHolder bestCombinationOfVariables = new VariableHolder();

        double smallestErrorYet = Double.MAX_VALUE;          
        for (VariableHolder variables : this.variableSets) 
        {
            if(variables.error < smallestErrorYet)
            {
                smallestErrorYet = variables.error;
                bestCombinationOfVariables = variables;
            }
        }        

        return bestCombinationOfVariables;
    }
}

