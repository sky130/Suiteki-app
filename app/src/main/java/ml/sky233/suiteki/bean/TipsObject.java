package ml.sky233.suiteki.bean;

import androidx.annotation.NonNull;

public class TipsObject {


    public String title = "";
    public String url = "";

    public TipsObject(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @NonNull
    public String toString() {
        return "title:" + title + "  tag:" + url;
    }


}
