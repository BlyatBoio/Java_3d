package Primatives_3D;

import java.util.Random;

public class Polygon {
    
    public Vector3D p1, p2, p3, normal;
    public int[] color = new int[3];
    private static final Random r = new Random();
    
    public Polygon(Vector3D p1, Vector3D p2, Vector3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.color[0] = r.nextInt(0, 255);
        this.color[1] = r.nextInt(0, 255);
        this.color[2] = r.nextInt(0, 255);
        this.normal = this.p2.copy().sub(this.p1).cross(this.p3.copy().sub(this.p1));
    }
}
