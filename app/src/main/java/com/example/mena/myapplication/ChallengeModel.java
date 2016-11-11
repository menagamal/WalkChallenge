package com.example.mena.myapplication;

import android.net.Uri;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Mena on 11/10/16.
 */
public class ChallengeModel implements Serializable {
    String image ;
    String title ;
    String desc ;
   private int FootSteps ;
    String username ;
    String pic ;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpic() {
        return pic;
    }

    public void setUserpic(String userpic) {
        this.pic = userpic;
    }





    public int getFootSteps() {
        return FootSteps;
    }

    public void setFootSteps(int footSteps) {
        FootSteps = footSteps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
