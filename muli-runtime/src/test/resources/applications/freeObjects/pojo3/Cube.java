package applications.freeObjects.pojo3;

public class Cube extends Square {
    public int getArea() {
        return this.width * this.width * this.width;
    }

    public String toString() {
        return "Cube[" + width + "³]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Cube)) return false;
        return this.width == ((Cube)o).width;
    }
}