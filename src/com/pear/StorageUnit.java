package com.pear;

public class StorageUnit {
    private int storageUnitID;
    private String city;

    public StorageUnit(int storageUnitID, String city) {
        this.storageUnitID = storageUnitID;
        this.city = city;
    }

    public int getStorageUnitID() {
        return storageUnitID;
    }

    public String getCity() {
        return city;
    }
}
