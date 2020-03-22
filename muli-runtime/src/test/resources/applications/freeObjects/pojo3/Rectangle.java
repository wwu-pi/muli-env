package applications.freeObjects.pojo3;

public class Rectangle extends Shape {
    public int width;
    public int height;

    public int getArea() {
        return this.width * this.height;
    }

    public String toString() {
        return "Rectangle[" + width + "x" + height + "]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Rectangle)) return false;
        return this.width == ((Rectangle)o).width && this.height == ((Rectangle)o).height;
    }
}