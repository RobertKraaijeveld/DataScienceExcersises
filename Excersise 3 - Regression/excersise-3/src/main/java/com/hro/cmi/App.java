package com.hro.cmi;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

import javax.sound.sampled.Line;

import com.hro.cmi.GeneticAlgorithm.GeneticAlgorithm;

import java.text.*;

public class App extends PApplet
{
    private static final int APPLET_WIDTH = 1040;
    private static final int APPLET_HEIGHT = 800;
    private static ArrayList<Vector> inputVectors = Parser.parseCsvToPoints("C:\\Projects\\DataScienceFinalRetake\\Excersise 3 - Regression\\excersise-3\\docs\\RetailMart.csv");
    
    private static final int YAXIS_OFFSET = -25;
    private static final int XAXIS_OFFSET = 18;


    public static void main( String[] args )
    {
        // ArrayList<Vector> trainingSet = new ArrayList<>();
        // ArrayList<Vector> testSet = new ArrayList<>();

        // for(int i = 0; i < inputVectors.size(); i++)
        // {
        //     if(i <= inputVectors.size() / 2) trainingSet.add(inputVectors.get(i));
        //     else testSet.add(inputVectors.get(i));
        // }
        // LinearRegression bestRegression = LinearRegression.Train(trainingSet);
        // ArrayList<Vector> predictions = bestRegression.Predict(testSet);
        //  PApplet.main(new String[] { App.class.getName() });
        GeneticAlgorithm algo = new GeneticAlgorithm(0.8, 0.1, true, 50, 100);
        algo.Run();
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


        



        // drawAxises();

        // drawBaseValuesAndAxisesValues();

        // drawSES();    
        // drawDES();
        // drawTES();

        // drawLegend();
    }


    /***
    * Methods for drawing UI 
    */

    private void drawLegend()
    {
        // textSize(16);

        // fill(255, 0, 0);
        // text("Red line: Original values", 600.0f, 90.0f);

        // fill(0, 0, 255);
        // text("Blue line: Single exponential smoothing", 600.0f, 120.0f);

        // fill(0, 128, 0);
        // text("Green line: Double exponential smoothing", 600.0f, 150.0f);

        // fill(0,0,0);
        // text("Black line: Triple exponential smoothing", 600.0f, 180.0f);

        // textSize(40);
        // fill(0,0,0);
        // text("Forecast of sword sales", 10.0f, 100.0f);       

        // textSize(9);
        // text("Source: http://eu.wiley.com/WileyCDA/WileyTitle/productCd-111866146X.html", 10.0f, 115.0f);               
    }

    private void drawAxises()
    {
        // invertYAxis();
        
        // stroke(0, 0, 0);

        // //x axis 
        // line(40, 30, 920, 30);   

        // //y axis
        // line(40, 30, 40, 430);           

        // popMatrix();                    
    }

    private void drawBaseValuesAndAxisesValues()
    {
        // double firstPointPositionY = 30.0f;

        // // X Axis values
        // int counter = 1;
        // for (Vector2 currentVector : swordSalesPoints) 
        // {
        //     if(counter % 5 == 0 || counter == 1)
        //     {
        //         double shiftedXValue = (currentVector.x * XAXIS_OFFSET) + 40;

        //         DecimalFormat decimalFormat = new DecimalFormat("#0");
        //         String numberAsString = decimalFormat.format(currentVector.x);
                
        //         text(numberAsString, (float) shiftedXValue, 788);
        //     }
        //     counter++;
        // }

        // //Y axis values 
        // float valueStep = 0;
        // float positionStep = 750.0f;

        // for(int i = 0; i < 100; i++)
        // {
        //     if(i % 5 == 0 || counter == 1)
        //     {
        //         positionStep -= 20.0f;
        //         valueStep += 20.0f;
        //         text(Float.toString(valueStep), 0.0f, (float) firstPointPositionY + positionStep);            
        //     }
        // }

        // stroke(255, 0, 0);  
        // fill(255, 0, 0);
                                        
        // drawGivenVectors(swordSalesPoints);
    }


    /***
    * Methods for drawing regressions
    */

    // private void drawSES()
    // {
    //     int foreCastAmount = 3;
    //     SES sesForecast = new SES(this.swordSalesPoints, foreCastAmount);
    //     ArrayList<Vector2> sesSwordSalesPoints = sesForecast.runForecastWithBestError();
        
    //     stroke(0, 0, 255);                                  
    //     fill(0, 0, 255);
    //     drawGivenVectors(sesSwordSalesPoints);

    //     textSize(18);
    //     fill(0, 0, 255);
    //     text("SES Measurements", 10.0f, 145.0f);

    //     textSize(13);
    //     fill(0,0,0);
    //     text("SES Alpha: " + Double.toString(sesForecast.bestVariables.alpha).substring(0, 6), 10.0f, 170.0f);
    //     text("SES Error: " + Double.toString(sesForecast.bestVariables.error).substring(0, 6), 10.0f, 185.0f);
    // }


    // private void drawGivenVectors(ArrayList<Vector2> vectors)
    // {
    //     double leftPadding = 40.0f;        
    //     double firstPointXposition = 40.0f;
    //     Vector2 previousPointVector = new Vector2(firstPointXposition, 120.0f);

    //     for (Vector2 currentVector : vectors) 
    //     {
    //         double shiftedXValue = (currentVector.x * XAXIS_OFFSET) + leftPadding;
    //         double shiftedYValue = currentVector.y - YAXIS_OFFSET;

    //         //inverting so we dont have to map/flip the y values
    //         invertYAxis();
           
    //         ellipse((float) shiftedXValue, (float) shiftedYValue, 5.0f, 5.0f);
           
    //         if(previousPointVector.x != firstPointXposition)
    //             line((float) previousPointVector.x, (float) previousPointVector.y, (float) shiftedXValue, (float) shiftedYValue);

    //         previousPointVector.x = shiftedXValue; 
    //         previousPointVector.y = shiftedYValue;

    //         popMatrix();
    //     }
    // }


    //temporarily inverting y values is used because processings' y = 0 starts at the top left         
    private void invertYAxis()
    {
        pushMatrix();
        translate(0, height);
        scale(1,-1);
    }

}
