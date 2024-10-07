package com.jude.educate.DataModel;

public class DataModel1 {
    int image;
    String heading;
    String buttonText;
    int id;

    // for firebase empty constructor
    public DataModel1() {
    }



    public DataModel1(String heading, String buttonText, int id, int image) {
        this.image = image;
        this.heading = heading;
        this.buttonText = buttonText;
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

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
