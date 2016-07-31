package it.unibo.torsello.bluetoothpositioning.main;

/**
 * Created by federico on 24/07/16.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Parcel parcel) {
        this.x = parcel.readInt();
        this.y = parcel.readInt();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Point createFromParcel(Parcel source) {
            return new Point(source);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}