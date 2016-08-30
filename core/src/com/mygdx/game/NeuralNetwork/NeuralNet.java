package com.mygdx.game.NeuralNetwork;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class NeuralNet {

//private variables
    private int numInputs;
    private int numOutputs;
    private int numHiddenLayers;
    private int neuronsPerHiddenLayer;
    private ArrayList<NeuronLayer> listLayers; //Storage for each layer of neurons Including output layer.

//public variables

    /**
     * Constructor
     * @param nInputs
     * @param nOutputs
     * @param nHiddenLayers
     * @param nerPerHiddenLayer
     */
    public NeuralNet(int nInputs, int nOutputs, int nHiddenLayers, int nerPerHiddenLayer){
        numInputs = nInputs;
        numOutputs = nOutputs;
        numHiddenLayers = nHiddenLayers;
        neuronsPerHiddenLayer = nerPerHiddenLayer;

        createNet();

    }

//public methods

    /**
     * Build the Neural Network. Weights are initially set to random values -1 to +1
     */
    public void createNet(){

        //create the layers of the network
        if(numHiddenLayers > 0){

            //create the first hidden layer
            listLayers.add(new NeuronLayer(neuronsPerHiddenLayer, numInputs));

            //create the other hidden layers, reference neuronsPerHiddenLayer as numInputs;
            for(int i = 0; i < numHiddenLayers - 1; i++){
                listLayers.add(new NeuronLayer(neuronsPerHiddenLayer, neuronsPerHiddenLayer));
            }

            //create output layer
            listLayers.add(new NeuronLayer(numOutputs, neuronsPerHiddenLayer));
        }else{
            //create output layer if no hiddenLayers exist
            listLayers.add(new NeuronLayer(numOutputs, numInputs));
        }
    }

    /**
     * Gets the weights from the Neural Network
     * @return
     */
    public ArrayList<Double> getWeights(){

    }

    /**
     * Returns the total number of weights in net.
     * @return
     */
    public int getNumperOfWeights(){

    }

    /**
     * Replaces the weights with new ones.
     * @param w
     */
    public void putWeights(ArrayList<Double>w){

    }

    ArrayList<Double>update(ArrayList<Double>inputs){

    }

    public Double sigmoid(double activation, double response){

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
                listWeights.add(new Double(MathUtils.random(-1f, 1f)));

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
