package com.mygdx.game.NeuralNetwork;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Creature;
import com.mygdx.game.Food;

import java.util.ArrayList;
import java.util.Collections;

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
    private ArrayList<Double>listInputs;
//public variables
    private Creature owner;

    /**
     * Constructor
     * @param nInputs
     * @param nOutputs
     * @param nHiddenLayers
     * @param nerPerHiddenLayer
     */
    public NeuralNet(Creature owner, int nInputs, int nOutputs, int nHiddenLayers, int nerPerHiddenLayer){
        this.owner = owner;
        numInputs = nInputs;
        numOutputs = nOutputs;
        numHiddenLayers = nHiddenLayers;
        neuronsPerHiddenLayer = nerPerHiddenLayer;
        listLayers = new ArrayList<NeuronLayer>();
        listInputs = new ArrayList<Double>();
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
     * @return A List Containing the weights
     */
    public ArrayList<Double> getWeights(){
        //This holds the weights
        ArrayList<Double>weights = new ArrayList<Double>();

        //interate through each layer
        for(int i = 0; i < numHiddenLayers + 1; i++){
            //for each neuron in layer
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                //for each weight
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).numInputs; k++){
                    weights.add(listLayers.get(i).listNeurons.get(j).listWeights.get(k));
                }
            }
        }
        return weights;
    }

    /**
     * Returns the total number of weights in net.
     * @return
     */
    public int getNumperOfWeights(){
        int weights = 0;

        //for each layer
        for(int i = 0; i < numHiddenLayers + 1; i++){
            //for each neuron
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                //for each weight
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).numInputs; k++){
                    weights++;
                }
            }
        }

        return weights;
    }

    /**
     * Replaces the weights with new ones.
     * @param w
     */
    public void putWeights(ArrayList<Double>w){
        int weightCount = 0;

        //for each layer
        for(int i = 0; i < numHiddenLayers + 1; i++){
            //for each neuron
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                //for each weight
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).numInputs; k++){
                    listLayers.get(i).listNeurons.get(j).listWeights = w;

                }
            }
        }
    }

    public void update(){
        //update the inputs
        updateInputs();

        //update neural network with a copy of the updated inputs

        ArrayList<Double>outputs = update(copyDoubleList(listInputs));

        //update the outputs.
        //list outputs
        //for(int i = 0; i < outputs.size(); i ++){
        //    System.out.println("Outputs: " + i + " " + outputs.get(i));
        //}

        //wire outputs[0] to thrust forward
        if(outputs.get(0) < 0.5f){
            owner.setThrustForwardPressed(false);
        }else{
            owner.setThrustForwardPressed(true);
        }

        if(outputs.get(1) < 0.5f){
            owner.setRotateLeftPressed(false);
        }else{
            owner.setRotateLeftPressed(true);
        }

        if(outputs.get(2) < 0.5f){
            owner.setRotateRightPressed(false);
        }else{
            owner.setRotateRightPressed(true);
        }
    }

    private ArrayList<Double>copyDoubleList(ArrayList<Double>l){
        ArrayList<Double>newList = new ArrayList<Double>();
        for(int i = 0 ; i<l.size(); i++){
            newList.add(l.get(i).doubleValue());
        }
        return newList;
    }

    /**
     * Given an input List, we create and return an output list.
     * @param inputsList
     * @return
     */
    public ArrayList<Double>update(ArrayList<Double>inputsList){
        //Store the resulting outputs from each layer
        ArrayList<Double> outputs = new ArrayList<Double>();
        int cWeight = 0;

        //First check that we have a good amount of inputs
        if(inputsList.size() != numInputs){
            //return an emtpy vector
            System.out.println("updating neural network: input Num not correct");
            return outputs;
        }

        //for Each Layer
        for(int i = 0; i < numHiddenLayers + 1; i++){
            if( i > 0){
                inputsList = outputs;
            }
            outputs.clear();
            cWeight = 0;

            /**
             * For each neuron sum the (inputs * corresponding weights). Give the
             * total to sigmoid to get proper output.
             */
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                double netInput = 0;
                int numWeights = listLayers.get(i).listNeurons.get(j).listWeights.size();

                //for each weight
                for(int k = 0; k < numWeights - 1; k++){
                    //sum the weights * inputs

                    int index = cWeight++;

                   // System.out.println(" inputsList.get(index): " +  inputsList.get(index));
                    netInput = netInput + listLayers.get(i).listNeurons.get(j).listWeights.get(k) * inputsList.get(index);
                }

                //add in the bias
                netInput += listLayers.get(i).listNeurons.get(j).listWeights.get(numWeights-1) * Utils.dBias;

                /** we can store the outputs from each layer as we generate them.
                 *  The combined activation is filtered through the sigmoid function
                 */

                //check if this is the last layer of neurons, if so, don't use sigmoid
                //we want boolean outputs
                //if(i == numHiddenLayers){
                //    outputs.add(netInput);
               // }else{
                    outputs.add(sigmoid(netInput, Utils.ActivationResponse));
                //}

                cWeight = 0;
            }
        }
        return outputs;
    }

    public Double sigmoid(double netInput, double response){
        return (1 / (1 + Math.exp(-netInput / response)));
    }

    public void addInput(double i){
        this.listInputs.add(i);
    }

    //finds the actual value for inputs and stores them in inputList
    private void updateInputs(){
        Vector2 toClosestCreature = owner.getVectorToClosestCreature();
        Creature closestCreature = owner.getClosestCreature();
        Vector2 toClosestFood = owner.toClosestFood();
        Food closestFood = owner.getClosestFood();

        listInputs.set(0, (double)owner.getBody().getLinearVelocity().x);
        listInputs.set(1,  (double)owner.getBody().getLinearVelocity().y);
        listInputs.set(2, (double) owner.getBody().getAngularVelocity());

        listInputs.set(3, (double)toClosestCreature.x);
        listInputs.set(4, (double)toClosestCreature.y);
        listInputs.set(5, (double)closestCreature.getREFACTORY_TIME_LEFT());
        listInputs.set(6, (double)closestCreature.getFitness());

        listInputs.set(7, (double)toClosestFood.x);
        listInputs.set(8, (double)toClosestFood.y);
        listInputs.set(9, (double)closestFood.getLIFE_VALUE());

        listInputs.set(10, (double)owner.getLIFE_LEFT());
        listInputs.set(11, (double)owner.getREFACTORY_TIME_LEFT());

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
                listWeights = new ArrayList<Double>();
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
            listNeurons = new ArrayList<Neuron>();
            numNeurons = numNeur;

            //create neurons in layer.
            for(int i = 0; i < numNeurons; i++){
                listNeurons.add(new Neuron(numInputsPerNeuron));
            }
        }
    }
}
