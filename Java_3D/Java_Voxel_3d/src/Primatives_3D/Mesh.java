package Primatives_3D;

import Rotation_3D.AngleHandler;
import Rotation_3D.Quaternion;

public class Mesh {
    public Polygon[] polys;
    
    public Mesh(Polygon[] polys){
        this.polys = polys;
    }   
    public void rotate(double q1, double q2, double q3){
        for(Polygon p : this.polys){
            p.p1 = AngleHandler.getRotated(p.p1, new Quaternion(Math.sqrt(p.p1.x*p.p1.x + p.p1.y*p.p1.y + p.p1.z*p.p1.z), q1, q2, q3));
            p.p2 = AngleHandler.getRotated(p.p2, new Quaternion(Math.sqrt(p.p2.x*p.p2.x + p.p2.y*p.p2.y + p.p2.z*p.p2.z), q1, q2, q3));
            p.p3 = AngleHandler.getRotated(p.p3, new Quaternion(Math.sqrt(p.p3.x*p.p3.x + p.p3.y*p.p3.y + p.p3.z*p.p3.z), q1, q2, q3));
        }
    }
}
