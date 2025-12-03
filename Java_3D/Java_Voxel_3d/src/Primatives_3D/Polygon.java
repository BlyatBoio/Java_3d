package Primatives_3D;

import java.util.Random;

import Rotation_3D.AngleHandler;

public class Polygon {
    
    public Vector3D p1, p2, p3;
    private static final double epsilon = 0.0001;
    public int[] color = new int[3];
    private static final Random r = new Random();
    
    public Polygon(Vector3D p1, Vector3D p2, Vector3D p3){
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.color[0] = r.nextInt(0, 255);
        this.color[1] = r.nextInt(0, 255);
        this.color[2] = r.nextInt(0, 255);
    }

    public boolean isRayIntersecting(Raycast ray){
        Vector3D e1 = this.p2.copy().sub(this.p1);
        Vector3D e2 = this.p3.copy().sub(this.p1);
        Vector3D rayDir = AngleHandler.getRotated(new Vector3D(0, 0, 1), ray.direction);
        Vector3D normal = rayDir.copy().cross(e2);
        double det = e1.dot(normal);

        if(det > -epsilon && det < epsilon) return false;

        double invDet = 1/det;
        Vector3D s = ray.origin.copy().sub(this.p1);
        double u = invDet * s.dot(normal);

        if ((u < 0 && Math.abs(u) > epsilon) || (u > 1 && Math.abs(u-1) > epsilon)) return false;

        Vector3D s_cross_e1 = s.copy().cross(e1);
        double v = invDet * rayDir.dot(s_cross_e1);

        if ((v < 0 && Math.abs(v) > epsilon) || (u + v > 1 && Math.abs(u + v - 1) > epsilon)) return false;

        double t = invDet * e2.copy().dot(s_cross_e1);
        if(t > epsilon) return true;
        return false;
    }
}
