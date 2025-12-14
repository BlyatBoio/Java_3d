package Primatives_3D;

import Rotation_3D.AngleHandler;
import Rotation_3D.AxisAngle;

public class Mesh {
    public Polygon[] polys;
    public Vector3D center;

    public Mesh(Polygon[] polys){
        this.polys = polys;
        this.findCenter();
    }   
    private void findCenter(){
        double tx = 0;
        double ty = 0;
        double tz = 0;

        for(Polygon p: this.polys){
            tx += p.p1.x + p.p2.x + p.p3.x;
            ty += p.p1.y + p.p2.y + p.p3.y;
            tz += p.p1.z + p.p2.z + p.p3.z;
        }

        this.center = new Vector3D(tx/(this.polys.length*3), ty/(this.polys.length*3), tz/(this.polys.length*3));
    }
    public void rotate(AxisAngle rotation){
        for(Polygon p : this.polys){
            AngleHandler.getRotatedPolygon(p, this.center, rotation);
        }
    }
}
