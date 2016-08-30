package com.mygdx.game.NeuralNetwork;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Creature;
import com.mygdx.game.Food;

import java.util.ArrayList;
import java.util.Collection;
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
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).getNumInputs(); k++){
                    weights.add(listLayers.get(i).listNeurons.get(j).getWeight(k));
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
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).getNumInputs(); k++){
                    weights++;
                }
            }
        }

        return weights;
    }

    /**
     * Replaces the weights with new ones.
     * @param newListWeights
     */
    public void putWeights(ArrayList<Double>newListWeights){
        int weightCount = 0;

        //for each layer
        for(int i = 0; i < numHiddenLayers + 1; i++){
            //for each neuron
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                //for each weight
                for(int k = 0; k < listLayers.get(i).listNeurons.get(j).getNumInputs(); k++){
                   // listLayers.get(i).listNeurons.get(j).listWeights = newListWeights;
                    listLayers.get(i).listNeurons.get(j).replaceWeights(newListWeights);

                }
            }
        }
    }

    public void debugNet(){
        System.out.println(owner.toString() + "--------------------------------------------------------------------");
        System.out.print(" INPUTS [ ");
        System.out.format(" A:%.2f B:%.2f C:%.2f D:%.2f ", listInputs.get(0), listInputs.get(1), listInputs.get(2), listInputs.get(3));
        System.out.format(" E:%.2f F:%.2f G:%.2f H:%.2f ", listInputs.get(4), listInputs.get(5), listInputs.get(6), listInputs.get(7));
        System.out.format(" I:%.2f J:%.2f K:%.2f L:%.2f ", listInputs.get(8), listInputs.get(9), listInputs.get(10), listInputs.get(11));
        System.out.print(" ] ");
        System.out.println();

        //Loop through each layers
        for(int i = 0; i < listLayers.size(); i++){
            NeuronLayer l = listLayers.get(i);
            System.out.println("      LAYER " + i + " [ ");
            //Loop Through Each Neuron
            for(int j = 0; j < l.listNeurons.size(); j++){
                Neuron n = l.listNeurons.get(j);
                System.out.print("          NEURON     [");
                for(int k = 0; k < n.numWeights(); k++){
                    System.out.print(n.getWeight(k) + " ");
                }
                System.out.println();

            }
            System.out.println();
        }

    }

    public void update(){
        //update the inputs
        updateInputs();
        if(owner.isFirstCreature()){
            debugNet();
        }

        //update neural network with a copy of the updated inputs

       // ArrayList<Double>outputs = update(copyDoubleList(listInputs));
        ArrayList<Double>outputs = update(new ArrayList<Double>((ArrayList<Double>)listInputs.clone()));

        //update the outputs.


        //wire outputs[0] to thrust forward
        if(owner.isFirstCreature()){
            //list outputs
           // for(int i = 0; i < outputs.size(); i ++){
            //    System.out.println("Outputs: " + i + " " + outputs.get(i));
           // }
            //System.out.print(" out [ ");
           // System.out.format(" A:%.2f B:%.2f C:%.2f ", outputs.get(0), outputs.get(1), outputs.get(2));

        }
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

               // inputsList = outputs; // Cannot do this, need to deep copy
                inputsList = new ArrayList<Double>((ArrayList<Double>) outputs.clone());
            }
            outputs.clear();
            cWeight = 0;

            /**
             * For each neuron sum the (inputs * corresponding weights). Give the
             * total to sigmoid to get proper output.
             */
            for(int j = 0; j < listLayers.get(i).numNeurons; j++){
                double netInput = 0;
                int numWeights = listLayers.get(i).listNeurons.get(j).numWeights();
                int nInputs = listLayers.get(i).listNeurons.get(j).getNumInputs();

                //for each weight
                for(int k = 0; k < nInputs - 1; k++){
                    //sum the weights * inputs

                    int index = cWeight++;

                   // System.out.println(" inputsList.get(index): " +  inputsList.get(index));
                    //System.out.format(" A:%.2f B:%.2f C:%.2f D:%.2f ", listLayers.get(i), listLayers.get(i).listNeurons.get(j),listLayers.get(i).listNeurons.get(j).listWeights.get(k), inputsList.get(index));
                    //System.out.print("listLayers.get(i): " + listLayers.get(i));
                    //System.out.println("isFirstCreature: " + owner.isFirstCreature() + " i j k index: " + i + " " + j + " " + k + " " + index);
                    NeuronLayer nLayer = listLayers.get(i);
                    Neuron neuren = nLayer.listNeurons.get(j);
                    double weight = neuren.getWeight(k);
                    double input = inputsList.get(index);

                    netInput = netInput + weight * input;
                }

                double bias = Utils.dBias;
                //add in the bias
                netInput += listLayers.get(i).listNeurons.get(j).getWeight(nInputs-1) * Utils.dBias;

                /** we can store the outputs from each layer as we generate them.
                 *  The combined activation is filtered through the sigmoid function
                 */

                //check if this is the last layer of neurons, if so, don't use sigmoid
                //we want boolean outputs
                //if(i == numHiddenLayers){
                //    outputs.add(netInput);
               // }else{
                double sigVal = sigmoid(netInput, Utils.ActivationResponse);
                    outputs.add(sigVal);
                //}

                cWeight = 0;
            }
            //System.out.println("break");
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



}
