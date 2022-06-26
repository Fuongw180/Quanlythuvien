package model;

import java.io.Serializable;

public class Reader implements Serializable {

    private static int currentId = 9999;
    private int id;
    private String name, address, phoneNum;

    public Reader() {
    }

    public Reader(String name, String address, String phoneNum) {
        this.id = ++currentId;
        this.name = name;
        this.address = address;
        this.phoneNum = phoneNum;
    }

    public static int getCurrentId() {
        return currentId;
    }

    public static void setCurrentId(int currentId) {
        Reader.currentId = currentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] toObject() {
        return new Object[]{id, name, address, phoneNum};
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Reader) {
            Reader r = (Reader) obj;
            if (this.name.equals(r.name)
                    && this.address.equals(r.address)
                    && this.phoneNum.equals(r.phoneNum)) {
                return true;
            }
        }
        return false;
    }
}
