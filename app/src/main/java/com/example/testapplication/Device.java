package com.example.testapplication;

import android.widget.TextView;

public interface Device {

    void setID(int ID);

    void setHostname(TextView hostname);

    int getCenterX();

    int getCenterY();

    boolean getIsSet();

    int getID();

    TextView getHostname();
}
