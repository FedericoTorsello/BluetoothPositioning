package it.unibo.torsello.bluetoothpositioning.activity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by federico on 24/07/16.
 */
public final class CircleOptions implements Parcelable {
    //    public static final zzb CREATOR = new zzb();
    private final int mVersionCode;
    //    private LatLng zzaGU = null;
    private double zzaGV = 0.0D;
    private float zzaGW = 10.0F;
    private int zzaGX = -16777216;
    private int zzaGY = 0;
    private float zzaGZ = 0.0F;
    private boolean zzaHa = true;

    public CircleOptions() {
        this.mVersionCode = 1;
    }

    CircleOptions(int versionCode, double radius, float strokeWidth, int strokeColor, int fillColor, float zIndex, boolean visible) {
        this.mVersionCode = versionCode;
//        this.zzaGU = center;
        this.zzaGV = radius;
        this.zzaGW = strokeWidth;
        this.zzaGX = strokeColor;
        this.zzaGY = fillColor;
        this.zzaGZ = zIndex;
        this.zzaHa = visible;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

//    public void writeToParcel(Parcel out, int flags) {
//        zzb.zza(this, out, flags);
//    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mVersionCode);
        dest.writeDouble(zzaGV);
        dest.writeFloat(zzaGW);
        dest.writeInt(zzaGX);
        dest.writeInt(zzaGY);
        dest.writeFloat(zzaGZ);
//        dest.writeBooleanArray(zzaHa);
    }

    public int describeContents() {
        return 0;
    }

//    public CircleOptions center(LatLng center) {
//        this.zzaGU = center;
//        return this;
//    }

    public CircleOptions radius(double radius) {
        this.zzaGV = radius;
        return this;
    }

    public CircleOptions strokeWidth(float width) {
        this.zzaGW = width;
        return this;
    }

    public CircleOptions strokeColor(int color) {
        this.zzaGX = color;
        return this;
    }

    public CircleOptions fillColor(int color) {
        this.zzaGY = color;
        return this;
    }

    public CircleOptions zIndex(float zIndex) {
        this.zzaGZ = zIndex;
        return this;
    }

    public CircleOptions visible(boolean visible) {
        this.zzaHa = visible;
        return this;
    }

//    public LatLng getCenter() {
//        return this.zzaGU;
//    }

    public double getRadius() {
        return this.zzaGV;
    }

    public float getStrokeWidth() {
        return this.zzaGW;
    }

    public int getStrokeColor() {
        return this.zzaGX;
    }

    public int getFillColor() {
        return this.zzaGY;
    }

    public float getZIndex() {
        return this.zzaGZ;
    }

    public boolean isVisible() {
        return this.zzaHa;
    }


    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public CircleOptions createFromParcel(Parcel source) {
            return new CircleOptions();
        }

        @Override
        public CircleOptions[] newArray(int size) {
            return new CircleOptions[size];
        }
    };
}