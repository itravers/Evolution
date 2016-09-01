package com.mygdx.game.NeuralNetwork;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Utils {
    public static double dBias = -1;
    public static double ActivationResponse = 1;
    public static int BRAIN_INPUTs = 12;
    public static int BRAIN_OUTPUTS = 4;
    public static int BRAIN_HIDDENLAYERS = 3;
    public static int BRAIN_NEURONSPERLAYER = 5;
    public static float MUTATION_RATE = .3f;
    public static float MAX_PERTURBATION = 0.4f;
    public static int FITTEST_CREATURES_TRACKED = 10;
    public static int LIFE_SPAN = 5000;
    public static int START_POPULATION_MULTIPLIER = 1;
    public static int MINIMUM_POPULATION = 40;
    public static int MAXIMUM_POPULATION = 50;
    public static int START_FOOD = 8;
    public static float FOOD_LIMIT = 15000;
    public static int REFACTORY_LIMIT = LIFE_SPAN / 5;
    public static float FPS = 60f;
    public static int QUICKTIME = 0;
}
