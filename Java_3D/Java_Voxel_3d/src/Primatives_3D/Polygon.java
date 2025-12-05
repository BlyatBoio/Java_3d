package Primatives_3D;

import java.util.Random;

public class Polygon {
    
    public Vector3D p1, p2, p3, e1, e2, normal, center;
    public int[] color = new int[3];
    private static final Random r = new Random();
    
    public Polygon(Vector3D p1, Vector3D p2, Vector3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.center  = new Vector3D((p1.x+p2.x+p3.x)/3, (p1.y+p2.y+p3.y)/3, (p1.z+p2.z+p3.z)/3);
        this.e1 = this.p2.copy().sub(p1);
        this.e2 = this.p3.copy().sub(p1);
        this.color[0] = r.nextInt(0, 255);
        this.color[1] = r.nextInt(0, 255);
        this.color[2] = r.nextInt(0, 255);
        this.normal = this.p2.copy().sub(this.p1).cross(this.p3.copy().sub(this.p1));
    }
}
