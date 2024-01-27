package com.raghava.shopaholic.interfaces;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class DataParcelable implements Parcelable {

    private Map<String, Map<String,String>> productsMap;

    public DataParcelable(Map<String, Map<String,String>> dataMap) {
        this.productsMap = dataMap;
    }

    protected DataParcelable(Parcel in) {
        int size = in.readInt();
        productsMap = new HashMap<>(size);

        for (int i=0; i<size; i++) {
            String key = in.readString();
            int innerSize = in.readInt();
            Map<String, String> innerMap = new HashMap<>(innerSize);
            for (int j = 0; j < innerSize; j++) {
                String innerKey = in.readString();
                String innerValue = in.readString();
                innerMap.put(innerKey, innerValue);
            }
            productsMap.put(key, innerMap);
        }
    }

    public Map<String, Map<String, String>> getProductsMap() {
        return productsMap;
    }

    public static final Creator<DataParcelable> CREATOR = new Creator<DataParcelable>() {
        @Override
        public DataParcelable createFromParcel(Parcel in) {
            return new DataParcelable(in);
        }

        @Override
        public DataParcelable[] newArray(int size) {
            return new DataParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(productsMap.size());

        for(Map.Entry<String, Map<String,String>> entry : productsMap.entrySet()) {
            dest.writeString(entry.getKey());
            Map<String, String> innerMap = entry.getValue();
            dest.writeInt(innerMap.size());

            for(Map.Entry<String,String> innerEntry : innerMap.entrySet()) {
                dest.writeString(innerEntry.getKey());
                dest.writeString(innerEntry.getValue());
            }
        }
    }
}
