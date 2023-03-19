package ml.sky233.suiteki.bean;

import androidx.annotation.NonNull;

public class GraphObject {
    public String title = "";
    public String img = "";

    public String style = "";

    public GraphObject(String title, String icon, String style) {
        this.title = title;
        this.img = icon;
        this.style = style;
    }

    @NonNull
    public String toString() {
        return "title:" + title + "  tag:" + img;
    }

}
