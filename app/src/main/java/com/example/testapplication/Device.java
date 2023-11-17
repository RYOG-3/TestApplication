package com.example.testapplication;

import android.widget.TextView;

public interface Device {

    void setID(int ID);

    void setHostname(TextView hostname);

    float getCenterX();

    float getCenterY();

    boolean getIsSet();

    int getID();

    TextView getHostname();

    void setCable(Cable cable);

    Cable getCable();

}
