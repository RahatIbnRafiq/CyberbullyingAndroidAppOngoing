package com.example.cybersafetyapp.HelperClassesPackage;

/**
 * Created by RahatIbnRafiq on 1/20/2017.
 */

public class Post {
    public String postid;
    public String createdtime;
    public String lastcheckedtime;
    public String link;

    Post(String postid,String createdtime,String lastcheckedtime,String link)
    {
        this.postid = postid;
        this.createdtime = createdtime;
        this.lastcheckedtime = lastcheckedtime;
        this.link = link;
    }

}
