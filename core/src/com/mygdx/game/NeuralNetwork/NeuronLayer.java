package com.mygdx.game.NeuralNetwork;

/**
 * Created by Isaac Assegai on 8/30/16.
 */

import java.util.ArrayList;

/**
 * NeuronLayer
 */
public class NeuronLayer{
    //The number of neurons in this layer.
    int numNeurons;

    //A list of the neurons in this layer.
    ArrayList<Neuron> listNeurons;
    int numInputsPerNeuron;

    //Constructor
    public NeuronLayer(int numNeur, int numInputsPerNeuron){
        listNeurons = new ArrayList<Neuron>();
        this.numInputsPerNeuron = numInputsPerNeuron;
        numNeurons = numNeur;

        //create neurons in layer.
        for(int i = 0; i < numNeurons; i++){
            addNeuronToLayer(new Neuron(numInputsPerNeuron));
        }
    }

    private void addNeuronToLayer(Neuron n){
        listNeurons.add(n);
    }
}