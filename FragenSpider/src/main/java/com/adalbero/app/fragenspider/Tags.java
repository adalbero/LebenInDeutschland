package com.adalbero.app.fragenspider;

public class Tags {
    public String areaCode;
    public String area;
    public String theme;
    public String image;
    public String tags;

    public String toString() {
        String str = ";" + areaCode;
        str += ";" + area;
        str += ";" + theme;
        str += ";" + image;
        str += ";" + (tags == null ? "" : tags);

        return str;
    }
}
