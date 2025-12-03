package Primatives_3D;

import Rotation_3D.Quaternion;

public class Raycast {
    public Vector3D origin;
    public Quaternion direction;

    public Raycast(Vector3D origin, Quaternion direction){
        this.origin = origin;
        this.direction = direction;
    }
    public void set(Vector3D origin, Quaternion direction){
        this.origin = origin;
        this.direction = direction;
    }
}
