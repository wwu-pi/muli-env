package applications.freeObjects.pojo;

public class Rectangle implements Shape {
    public int width;
    public int height;

    public int getArea() {
        return this.width * this.height;
    }
}