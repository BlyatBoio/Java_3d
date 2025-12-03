package Rotation_3D;

public class AxisAngle{
    public double theta, x, y, z;
    
    public AxisAngle(double theta, double x, double y, double z){
        this.theta = theta;
        this.x = x;
        this.y = y;
        this.z = z;
        
        double mag = Math.sqrt(x*x+ y*y+z*z);
        this.x /= mag;
        this.y /= mag;
        this.z /= mag;
    }
}