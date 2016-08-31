package com.mygdx.game.NeuralNetwork;

/**
 * Created by Isaac Assegai on 8/30/16.
 */

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Neuron
 */
public class Neuron{
    // number of inputs into the neuron
    private int numInputs;

    //the wedights for each input
    private ArrayList<Double> listWeights;

    public Neuron(int ni){
        this.numInputs = ni+1; //increase by one because we are going to store bias
        listWeights = new ArrayList<Double>();
        //setup weights with initial random value.
        for(int i = 0; i < numInputs; i++){

            //listWeights.add(new Double(MathUtils.random(-1f, 1f)));
            addWeight(new Double(MathUtils.random(-1f, 1f)));
        }
    }

    public Double getWeight(int index){
        return listWeights.get(index);
    }

    public void setWeight(int index, double weight){
        listWeights.set(index, weight);
    }

    public void addWeight(double w){
        listWeights.add(w);
    }

    public void replaceWeights(ArrayList<Double>newWeights){
        listWeights = new ArrayList<Double>((ArrayList<Double>)newWeights.clone());
    }

    public int getNumInputs(){
        return numInputs;
    }

    public int numWeights(){
        return listWeights.size();
    }
}