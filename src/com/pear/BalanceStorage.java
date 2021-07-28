package com.pear;

public class BalanceStorage {
    private StorageUnit storageUnit;
    private Product product;
    private int inStock;
    private int reserved;
    private int incoming;

    public BalanceStorage(StorageUnit storageUnit, Product product, int inStock, int reserved, int incoming) {
        this.storageUnit = storageUnit;
        this.product = product;
        this.inStock = inStock;
        this.reserved = reserved;
        this.incoming = incoming;
    }

    public StorageUnit getStorageUnit() {
        return storageUnit;
    }

    public Product getProduct() {
        return product;
    }

    public int getInStock() {
        return inStock;
    }

    public int getReserved() {
        return reserved;
    }

    public int getIncoming() {
        return incoming;
    }
}
