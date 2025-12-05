package Primatives_3D;

import Rotation_3D.AngleHandler;
import Rotation_3D.Quaternion;

public class Raycast {
    public Vector3D origin, vect;
    public Quaternion direction;

    public Raycast(Vector3D origin, Quaternion direction){
        this.origin = origin;
        this.direction = direction;
        this.vect = AngleHandler.getRotated(new Vector3D(0, 0, 1), direction);
    }
    public void set(Vector3D origin, Quaternion direction){
        this.origin = origin;
        this.direction = direction;
        this.vect = AngleHandler.getRotated(new Vector3D(0, 0, 1), direction);
    }
}
