package applications.freeObjects.pojo3;

public class Cuboid extends Rectangle {
    public int length;

    public int getArea() {
        return this.width * this.height * this.length;
    }

    public String toString() {
        return "Suboid[" + width + "x" + height + "x" + length + "]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Cuboid)) return false;
        return this.width == ((Cuboid)o).width && this.height == ((Cuboid)o).height && this.length == ((Cuboid)o).length;
    }
}