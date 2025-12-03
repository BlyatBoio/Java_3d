package Primatives_3D;

public class Vector3D {
    public double x, y, z = 0;

    // possible constructors
    public Vector3D(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public Vector3D(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D copy(){
        return new Vector3D(this.x, this.y, this.z);
    }

    public Vector3D add(Vector3D vector){
        this.x += vector.x; 
        this.y += vector.y;
        this.z += vector.z;
        return this;
    }
    public Vector3D add(double value){
        this.x += value; 
        this.y += value;
        this.z += value;
        return this;
    }
    public Vector3D sub(Vector3D vector){
        this.x -= vector.x; 
        this.y -= vector.y;
        this.z -= vector.z;
        return this;
    }
    public Vector3D mult(Vector3D vector){
        this.x *= vector.x; 
        this.y *= vector.y;
        this.z *= vector.z;
        return this;
    }
    public Vector3D mult(double value){
        this.x *= value; 
        this.y *= value;
        this.z *= value;
        return this;
    }
    public Vector3D cross(Vector3D vector){
        double value1 = (this.y * vector.z) - (this.z * vector.y);
        double value2 = (this.z * vector.x) - (this.x * vector.z);
        double value3 = (this.x * vector.y) - (this.y * vector.x);
        this.x = value1;
        this.y = value2;
        this.z = value3;
        return this;
    }
    public double dot(Vector3D vector){
        double value1 = this.x * vector.x; 
        double value2 = this.y * vector.y;
        double value3 = this.z * vector.z;
        
        return value1 + value2 + value3;
    }
}