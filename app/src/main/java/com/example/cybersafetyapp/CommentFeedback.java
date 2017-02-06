package com.example.cybersafetyapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RahatIbnRafiq on 2/3/2017.
 */

public class CommentFeedback implements Parcelable{

    public ArrayList<String> comments = new ArrayList<>();
    public double[] featureValues ;
    public double predictedValue;
    public double feedbackValue;

    public CommentFeedback() {
    }

    protected CommentFeedback(Parcel in) {
        comments = in.createStringArrayList();
        featureValues = in.createDoubleArray();
        predictedValue = in.readDouble();
        feedbackValue = in.readDouble();
    }

    public static final Creator<CommentFeedback> CREATOR = new Creator<CommentFeedback>() {
        @Override
        public CommentFeedback createFromParcel(Parcel in) {
            return new CommentFeedback(in);
        }

        @Override
        public CommentFeedback[] newArray(int size) {
            return new CommentFeedback[size];
        }
    };

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(featureValues);
        dest.writeDouble(predictedValue);
        dest.writeDouble(feedbackValue);
        dest.writeStringList(comments);

    }
}
