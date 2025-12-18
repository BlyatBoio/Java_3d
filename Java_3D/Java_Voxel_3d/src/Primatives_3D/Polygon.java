package Primatives_3D;

import java.util.Random;

public class Polygon {
    
    public Vector3D v1, v2, v3, e1, e2, center;
    public int[] color = new int[3];
    private static final Random r = new Random();
    
    public Polygon(Vector3D v1, Vector3D v2, Vector3D v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.center  = new Vector3D((v1.x+v2.x+v3.x)/3, (v1.y+v2.y+v3.y)/3, (v1.z+v2.z+v3.z)/3);
        this.color[0] = r.nextInt(0, 255);
        this.color[1] = r.nextInt(0, 255);
        this.color[2] = r.nextInt(0, 255);
    }
}
