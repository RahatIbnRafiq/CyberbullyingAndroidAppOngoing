package com.example.cybersafetyapp;

/**
 * Created by RahatIbnRafiq on 1/23/2017.
 */

public class Comment {

    public String commentid;
    public String createdtime;
    public String commentText;
    public String fromUserName;

    Comment(String commentid,String createdtime,String commentText,String fromUserName)
    {
        this.commentid = commentid;
        this.createdtime = createdtime;
        this.commentText = commentText;
        this.fromUserName = fromUserName;
    }
}
