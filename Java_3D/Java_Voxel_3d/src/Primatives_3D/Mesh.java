package Primatives_3D;

import Rotation_3D.AngleHandler;
import Rotation_3D.AxisAngle;
import java.util.ArrayList;

public class Mesh {
    public Polygon[] polys;
    public Vector3D center;
    public Vector3D[] uniqueVectors;

    public Mesh(Polygon[] polys){
        this.polys = polys;
        this.findUniqueVerts();
        this.findCenter();
    }   

    private void findUniqueVerts(){
        ArrayList<Vector3D> tempUniqueVecs = new ArrayList<>();

        for(Polygon p: this.polys){
            boolean addP1 = true;
            boolean addP2 = true;
            boolean addP3 = true;
            
            for(Vector3D v: tempUniqueVecs){
                if(addP1 && v.x == p.v1.x && v.y == p.v1.y && v.z == p.v1.z) addP1 = false;
                if(addP2 && v.x == p.v2.x && v.y == p.v2.y && v.z == p.v2.z) addP2 = false;
                if(addP3 && v.x == p.v3.x && v.y == p.v3.y && v.z == p.v3.z) addP3 = false;
            }

            if(addP1) tempUniqueVecs.add(p.v1);
            if(addP2) tempUniqueVecs.add(p.v2);
            if(addP3) tempUniqueVecs.add(p.v3);
        }

        uniqueVectors = tempUniqueVecs.toArray(Vector3D[]::new);
    }

    private void findCenter(){
        double tx = 0;
        double ty = 0;
        double tz = 0;

        for(Vector3D v: this.uniqueVectors){
            tx += v.x;
            ty += v.y;
            tz += v.z;
        }

        this.center = new Vector3D(tx/(this.uniqueVectors.length), ty/(this.uniqueVectors.length), tz/(this.uniqueVectors.length));
    }

    public void moveBy(Vector3D v){
        for(Polygon p: this.polys){
            p.v1.add(v);
            p.v2.add(v);
            p.v3.add(v);
        }
        this.findUniqueVerts();
    }
    
    public void moveBy(double x, double y, double z){
        Vector3D v = new Vector3D(x, y, z);
        for(Polygon p: this.polys){
            p.v1.add(v);
            p.v2.add(v);
            p.v3.add(v);
        }
        this.findUniqueVerts();
        this.findCenter();
    }

    public void rotate(AxisAngle rotation){
        this.findUniqueVerts();
        this.findCenter();
        for(Polygon p : this.polys){
            AngleHandler.getRotatedPolygon(p, this.center, rotation);
        }
    }
}
