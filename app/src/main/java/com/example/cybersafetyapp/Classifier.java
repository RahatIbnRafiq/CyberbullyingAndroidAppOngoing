package com.example.cybersafetyapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by RahatIbnRafiq on 2/2/2017.
 */

public class Classifier {

    private static Classifier instance = null;
    private static ArrayList<String> negativeWordList;

    private double [] coefficients;

    Classifier(Context context) throws Exception
    {
        InputStream is = context.getResources().openRawResource(R.raw.negative_words_list);
        this.negativeWordList = new ArrayList<>();
        streamToString(is);
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

    private void streamToString(InputStream is) throws IOException {
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


    public static Classifier getInstance(Context context) throws Exception
    {
        if(instance == null) {
            instance = new Classifier(context);
        }
        return instance;
    }
}
