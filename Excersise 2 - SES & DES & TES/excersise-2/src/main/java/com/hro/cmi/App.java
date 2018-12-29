package com.hro.cmi;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.text.*;

public class App extends PApplet
{
    private static final int APPLET_WIDTH = 1040;
    private static final int APPLET_HEIGHT = 800;
    private ArrayList<Vector2> swordSalesPoints = Parser.parseCsvToPoints("C:/Projects/DataMiningExcersises-master/DataMiningExcersises-master/Excersise 3 - SES& DES/excersise-3/docs/SwordForecasting.csv");
    
    private static final int YAXIS_OFFSET = -25;
    private static final int XAXIS_OFFSET = 18;


    public static void main( String[] args )
    {
         PApplet.main(new String[] { App.class.getName() });
    }

    public void setup() {
        noSmooth();
        noLoop();
        size(APPLET_WIDTH, APPLET_HEIGHT);
    }

    public void draw()
    {
        background(225, 225, 225);
        fill(0,0,0);

        drawAxises();

        drawBaseValuesAndAxisesValues();

        drawSES();    
        drawDES();
        drawTES();

        drawLegend();
    }


    /***
    * Methods for drawing UI 
    */

    private void drawLegend()
    {
        textSize(16);

        fill(255, 0, 0);
        text("Red line: Original values", 600.0f, 90.0f);

        fill(0, 0, 255);
        text("Blue line: Single exponential smoothing", 600.0f, 120.0f);

        fill(0, 128, 0);
        text("Green line: Double exponential smoothing", 600.0f, 150.0f);

        fill(0,0,0);
        text("Black line: Triple exponential smoothing", 600.0f, 180.0f);

        textSize(40);
        fill(0,0,0);
        text("Forecast of sword sales", 10.0f, 100.0f);       

        textSize(9);
        text("Source: http://eu.wiley.com/WileyCDA/WileyTitle/productCd-111866146X.html", 10.0f, 115.0f);               
    }

    private void drawAxises()
    {
        invertYAxis();
        
        stroke(0, 0, 0);

        //x axis 
        line(40, 30, 920, 30);   

        //y axis
        line(40, 30, 40, 430);           

        popMatrix();                    
    }

    private void drawBaseValuesAndAxisesValues()
    {
        double firstPointPositionY = 30.0f;

        // X Axis values
        int counter = 1;
        for (Vector2 currentVector : swordSalesPoints) 
        {
            if(counter % 5 == 0 || counter == 1)
            {
                double shiftedXValue = (currentVector.x * XAXIS_OFFSET) + 40;

                DecimalFormat decimalFormat = new DecimalFormat("#0");
                String numberAsString = decimalFormat.format(currentVector.x);
                
                text(numberAsString, (float) shiftedXValue, 788);
            }
            counter++;
        }

        //Y axis values 
        float valueStep = 0;
        float positionStep = 750.0f;

        for(int i = 0; i < 100; i++)
        {
            if(i % 5 == 0 || counter == 1)
            {
                positionStep -= 20.0f;
                valueStep += 20.0f;
                text(Float.toString(valueStep), 0.0f, (float) firstPointPositionY + positionStep);            
            }
        }

        stroke(255, 0, 0);  
        fill(255, 0, 0);
                                        
        drawGivenVectors(swordSalesPoints);
    }


    /***
    * Methods for drawing SES/DES/TES
    */

    private void drawSES()
    {
        int foreCastAmount = 10;
        SES sesForecast = new SES(this.swordSalesPoints, foreCastAmount);

        // (Values are from Data Smart book)            
        VariableHolder vars = new VariableHolder();
        vars.levelSmoothing = 0.73;
        
        ArrayList<Vector2> allSesSwordSalesPoints = sesForecast.forecastFunction(vars);
        ArrayList<Vector2> sesForecastedVectors = new ArrayList<Vector2>(allSesSwordSalesPoints.subList(allSesSwordSalesPoints.size() - (foreCastAmount), allSesSwordSalesPoints.size()));

        textSize(18);
        fill(0, 0, 255);
        text("SES Measurements", 10.0f, 145.0f);

        textSize(13);
        fill(0,0,0);
        text("SES Alpha: " + Double.toString(vars.levelSmoothing), 10.0f, 170.0f);
        text("SES Error: " + Double.toString(sesForecast.computeError(allSesSwordSalesPoints)), 10.0f, 185.0f);

        stroke(0, 0, 255);                                  
        fill(0, 0, 255);
        
        drawGivenVectors(sesForecastedVectors);
    }

    private void drawDES()
    {
        int foreCastAmount = 11;
        DES desForecast = new DES(this.swordSalesPoints, foreCastAmount);

        // (Values are from Data Smart book)            
        VariableHolder vars = new VariableHolder();
        vars.levelSmoothing = 0.659100046560163;
        vars.trendSmoothing = 0.053117180460981;
        ArrayList<Vector2> allDesSwordSalesPoints = desForecast.forecastFunction(vars);
        ArrayList<Vector2> desForecastedVectors = new ArrayList<Vector2>(allDesSwordSalesPoints.subList(allDesSwordSalesPoints.size() - (foreCastAmount), allDesSwordSalesPoints.size()));

        
        textSize(18);
        fill(0, 128, 0);
        text("DES Measurements", 200.0f, 145.0f);

        textSize(13);
        fill(0,0,0);
        text("DES Alpha: " + Double.toString(vars.levelSmoothing), 200.0f, 170.0f);
        text("DES Beta: " + Double.toString(vars.trendSmoothing), 200.0f, 185.0f);   
        text("DES Error: " + Double.toString(desForecast.computeError(allDesSwordSalesPoints)), 200.0f, 200.0f);     
        
        stroke(0, 128, 0);                                  
        fill(0, 128, 0);

        drawGivenVectors(desForecastedVectors);
    }

    private void drawTES()
    {
        int foreCastAmount = 12;
        int seasonLength = 12;
        TES tesForecast = new TES(this.swordSalesPoints, foreCastAmount, seasonLength);

        // (Values are from Data Smart book)            
        VariableHolder tesVars = new VariableHolder();
        tesVars.levelSmoothing = 0.307003546945751;
        tesVars.trendSmoothing = 0.228914336546831;
        tesVars.seasonalSmoothing = 0;
        ArrayList<Vector2> allTesSwordSalesPoints = tesForecast.forecastFunction(tesVars);
        ArrayList<Vector2> tesForecastedVectors = new ArrayList<Vector2>(allTesSwordSalesPoints.subList(allTesSwordSalesPoints.size() - (foreCastAmount), allTesSwordSalesPoints.size()));


        textSize(18);
        fill(0, 128, 0);
        text("TES Measurements", 400.0f, 145.0f);

        textSize(13);
        fill(0,0,0);
        text("TES Alpha: " + Double.toString(tesVars.levelSmoothing), 400.0f, 170.0f);
        text("TES Beta: " + Double.toString(tesVars.trendSmoothing), 400.0f, 185.0f);   
        text("TES Gamma: " + Double.toString(tesVars.seasonalSmoothing), 400.0f, 200.0f);   
        text("TES Error: " + Double.toString(tesForecast.computeError(allTesSwordSalesPoints)).substring(0, 6), 400.0f, 215.0f); 

        stroke(0,0,0);                                  
        fill(0,0,0);
        drawGivenVectors(tesForecastedVectors);
    }

    private void drawGivenVectors(ArrayList<Vector2> vectors)
    {
        double leftPadding = 40.0f;        
        double firstPointXposition = 40.0f;
        Vector2 previousPointVector = new Vector2(firstPointXposition, 120.0f);

        for (Vector2 currentVector : vectors) 
        {
            double shiftedXValue = (currentVector.x * XAXIS_OFFSET) + leftPadding;
            double shiftedYValue = currentVector.y - YAXIS_OFFSET;

            //inverting so we dont have to map/flip the y values
            invertYAxis();
           
            ellipse((float) shiftedXValue, (float) shiftedYValue, 5.0f, 5.0f);
           
            if(previousPointVector.x != firstPointXposition)
                line((float) previousPointVector.x, (float) previousPointVector.y, (float) shiftedXValue, (float) shiftedYValue);

            previousPointVector.x = shiftedXValue; 
            previousPointVector.y = shiftedYValue;

            popMatrix();
        }
    }


    //temporarily inverting y values is used because processings' y = 0 starts at the top left         
    private void invertYAxis()
    {
        pushMatrix();
        translate(0, height);
        scale(1,-1);
    }

}
