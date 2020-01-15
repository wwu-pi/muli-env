package applications.freeObjects.pojo;

public class Square implements Shape {
    public int width;

    public int getArea() {
        return this.width * this.width;
    }

    public String toString() {
        return "Square[" + width + "²]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Square)) return false;
        return super.equals(o) && this.width == ((Square)o).width;
    }
}