package com.mygdx.game.NeuralNetwork;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by slack on 8/29/16.
 */
public class NeuralNet {



    public NeuralNet(){

    }


    /*
    PRIVATE CLASSES
     */

    /**
     * Neuron
     */
    private class Neuron{
        // number of inputs into the neuron
        int numInputs;

        //the wedights for each input
        ArrayList<Double> listWeights;

        public Neuron(int ni){
            this.numInputs = ni+1; //increase by one because we are going to store bias

            //setup weights with initial random value.
            for(int i = 0; i < numInputs; i++){
                listWeights.add(new Double(MathUtils.random(0f, 100f)));

            }

        }
    }

    /**
     * NeuronLayer
     */
    private class NeuronLayer{
        //The number of neurons in this layer.
        int numNeurons;

        //A list of the neurons in this layer.
        ArrayList<Neuron>listNeurons;

        //Constructor
        public NeuronLayer(int numNeur, int numInputsPerNeuron){
            numNeurons = numNeur;

            //create neurons in layer.
            for(int i = 0; i < numNeurons; i++){
                listNeurons.add(new Neuron(numInputsPerNeuron));
            }
        }
    }
}
