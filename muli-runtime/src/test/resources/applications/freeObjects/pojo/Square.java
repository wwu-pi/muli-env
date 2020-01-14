package applications.freeObjects.pojo;

public class Square implements Shape {
    public int width;

    public int getArea() {
        return this.width * this.width;
    }
}