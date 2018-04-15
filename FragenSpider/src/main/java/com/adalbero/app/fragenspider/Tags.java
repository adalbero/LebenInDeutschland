package com.adalbero.app.fragenspider;

public class Tags {
    public String areaCode;
    public String area;
    public String thema;
    public String image;
    public String tags;

    public String toString() {
        String str = ";" + areaCode;
        str += ";" + area;
        str += ";" + thema;
        str += ";" + image;
        str += ";" + (tags == null ? "" : tags);

        return str;
    }
}
