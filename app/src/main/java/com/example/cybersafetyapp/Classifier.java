package com.example.cybersafetyapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by RahatIbnRafiq on 2/2/2017.
 */

public class Classifier {
    private static final int EPOCH = 50;
    private static final double LEARNING_RATE = 0.1;

    private static Classifier instance = null;
    private static ArrayList<String> negativeWordList;
    private ArrayList<ClassifierTrainingData> trainingDataList;

    private double [] coefficients;

    Classifier(Context context) throws Exception
    {
        InputStream is = context.getResources().openRawResource(R.raw.negative_words_list);
        this.negativeWordList = new ArrayList<>();
        loadNegativeWords(is);

        this.trainingDataList = new ArrayList<>();
        is = context.getResources().openRawResource(R.raw.training_data_for_android);
        loadTrainingData(is);

        this.coefficients = new double[4];
        this.coefficients[0]= -0.878;
        this.coefficients[1]= 0.00277;
        this.coefficients[2]= 2.0747;
        this.coefficients[3]= 2.0639;

    }

    public double predict(double[]featureValues)
    {
        float yhat = 0;
        for(int i=0;i<this.coefficients.length;i++)
        {
            yhat += (this.coefficients[i]*featureValues[i]);
        }
        return 1/(1+Math.exp(-1*yhat));

    }

    public double[] getFeatureValues(ArrayList<String>comments)
    {
        double [] featureValues = new double[4];
        double negativeCommentCount  = 0;
        double totalNegativeWord = 0;
        double negativeWordPerNegativeComment = 0;
        for(String comment:comments)
        {
            //Log.i(UtilityVariables.tag,comment);
            boolean isNegative = false;
            comment = comment.toLowerCase().trim();
            comment = comment.replaceAll("[^a-z]+", " ");
            StringTokenizer st = new StringTokenizer(comment);
            while (st.hasMoreTokens()) {
                String word = st.nextToken().trim();
                if (this.negativeWordList.contains(word))
                {
                    isNegative = true;
                    totalNegativeWord++;
                }
            }
            if(isNegative)
                negativeCommentCount++;

        }

        double negativeCommentPercentage = (negativeCommentCount)/(comments.size());
        if(negativeCommentCount > 0)
            negativeWordPerNegativeComment = (totalNegativeWord)/(negativeCommentCount);
        featureValues[0] = 1;
        featureValues[1] = negativeCommentCount;
        featureValues[2] = negativeCommentPercentage;
        featureValues[3] = negativeWordPerNegativeComment;
        return featureValues;
    }


    public void updateClassifier(ArrayList<CommentFeedback> feedbacks)
    {
        //Log.i(UtilityVariables.tag,"Feature values in "+this.getClass().getSimpleName()+" : "+ Arrays.toString(feedback.featureValues));
        //Log.i(UtilityVariables.tag,"Prediction value comming  in "+this.getClass().getSimpleName()+" : "+ feedback.predictedValue);
        //Log.i(UtilityVariables.tag,"Update classifier: feedback value "+feedback.feedbackValue+", predicted value: "+feedback.predictedValue);
        Log.i(UtilityVariables.tag,"before feedback, training data list size is: "+this.trainingDataList.size());
        for(int i=0;i<feedbacks.size();i++)
        {
            ClassifierTrainingData tr = new ClassifierTrainingData();
            tr.featurevalues = feedbacks.get(i).featureValues;
            tr.truePrediction = feedbacks.get(i).feedbackValue;
            this.trainingDataList.add(tr);
        }
        Log.i(UtilityVariables.tag,"After feedback, training data list size is: "+this.trainingDataList.size());

        for(int epoch=0;epoch<this.EPOCH;epoch++)
        {
            double sum_error = 0.0;
            for(int j=0;j<this.trainingDataList.size();j++)
            {
                ClassifierTrainingData tr = this.trainingDataList.get(j);
                double yhat = this.predict(tr.featurevalues);
                double error = tr.truePrediction - yhat;
                sum_error += error*error;
                for(int i=0;i<tr.featurevalues.length;i++)
                {
                    this.coefficients[i] = this.coefficients[i]*this.LEARNING_RATE*error*tr.featurevalues[i]*yhat*(1.0-yhat);
                }
                /*CommentFeedback feedback = feedbacks.get(j);
                double yhat = this.predict(feedback.featureValues);
                double error = feedback.feedbackValue - yhat;
                sum_error += error*error;
                for(int i=0;i<feedback.featureValues.length;i++)
                {
                    this.coefficients[i] = this.coefficients[i]*this.LEARNING_RATE*error*feedback.featureValues[i]*yhat*(1.0-yhat);
                }*/

            }
            Log.i(UtilityVariables.tag,"Updating classifier: epoch: "+epoch+" sum error: "+sum_error);

        }
    }

    private void loadNegativeWords(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    if (!this.negativeWordList.contains(line)) {
                        this.negativeWordList.add(line.toString());
                    }
                }

                reader.close();
            }
            finally {
                is.close();
            }
        }
    }

    private void loadTrainingData(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    String []tokens = line.split(",");
                    //Log.i(UtilityVariables.tag,tokens[0].toString()+","+tokens[1].toString()+","+tokens[2].toString()+","+tokens[3].toString());
                    ClassifierTrainingData tr = new ClassifierTrainingData();
                    tr.featurevalues[0] = 1.0;
                    tr.featurevalues[1] = Double.parseDouble(tokens[0].toString());
                    tr.featurevalues[2] = Double.parseDouble(tokens[1].toString());
                    tr.featurevalues[3] = Double.parseDouble(tokens[2].toString());
                    tr.truePrediction = Double.parseDouble(tokens[3].toString());
                    this.trainingDataList.add(tr);
                }

                reader.close();
            }
            finally {
                is.close();
            }
        }
    }


    public static Classifier getInstance(Context context) throws Exception
    {
        if(instance == null) {
            instance = new Classifier(context);
        }
        return instance;
    }
}
