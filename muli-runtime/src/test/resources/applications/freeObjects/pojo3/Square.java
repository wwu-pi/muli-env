package applications.freeObjects.pojo3;

public class Square extends Shape {
    public int width;

    public int getArea() {
        return this.width * this.width;
    }

    public String toString() {
        return "Square[" + width + "²]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Square)) return false;
        return this.width == ((Square)o).width;
    }
}