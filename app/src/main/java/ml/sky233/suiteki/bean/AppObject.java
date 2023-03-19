package ml.sky233.suiteki.bean;

import androidx.annotation.NonNull;

public class AppObject {


    public String title = "";
    public int icon = 0;

    public AppObject(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    @NonNull
    public String toString() {
        return "title:" + title + "  tag:" + icon;
    }


}
