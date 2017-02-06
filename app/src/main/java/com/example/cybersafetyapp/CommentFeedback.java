package com.example.cybersafetyapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahatIbnRafiq on 2/3/2017.
 */

public class CommentFeedback implements Serializable{

    public ArrayList<String> comments = new ArrayList<>();
    public double[] featureValues ;
    public double predictedValue;
    public double feedbackValue;
}
