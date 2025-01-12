package com.savaava.mytvskeeper.models;

public record Genre(String name, int id) implements java.io.Serializable {

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;

        Genre objGenre = (Genre) obj;
        return objGenre.id == id;
    }

    @Override
    public String toString() {
        return "(" + name + "-" + id + ")";
    }
}
