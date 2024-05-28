package com.jude.educate.DataModel;

public class DataModel2 {

    private int image;
    private String heading;

    int id;
    // for firebase empty constructor
    public DataModel2() {
    }

    public DataModel2(int image, String heading,int id) {
        this.image = image;
        this.heading = heading;
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
