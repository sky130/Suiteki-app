package ml.sky233.suiteki.bean;

import androidx.annotation.NonNull;

public class WatchFaceObject {
    public String title = "";
    public String img = "";
    public String user = "";
    public String url = "";




    public WatchFaceObject(String title, String icon, String user,String url) {
        this.title = title;
        this.img = icon;
        this.user= user;
        this.url= url;
    }

    @NonNull
    public String toString() {
        return "title:" + title + "  tag:" + img;
    }

}
