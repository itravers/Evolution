package com.mygdx.game.NeuralNetwork;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Utils {
    public static double dBias = -1;
    public static double ActivationResponse = 1;
    public static int BRAIN_INPUTs = 12;
    public static int BRAIN_OUTPUTS = 4;
    public static int BRAIN_HIDDENLAYERS = 4;
    public static int BRAIN_NEURONSPERLAYER = 8;
    public static float MUTATION_RATE = .3f;
    public static float MAX_PERTURBATION = 0.8f;
    public static int FITTEST_CREATURES_TRACKED = 10;
    public static int LIFE_SPAN = 15000;
    public static int START_POPULATION_MULTIPLIER = 1;
    public static int MINIMUM_POPULATION = 80;
    public static int MAXIMUM_POPULATION = 180;
    public static int START_FOOD = 15;
    public static float FOOD_LIMIT = 10000;
    public static int REFACTORY_LIMIT = LIFE_SPAN / 5;
}
