package Rotation_3D;

public class Quaternion {
    public double q0;
    public double q1;
    public double q2;
    public double q3;

    public Quaternion(double q0, double q1, double q2, double q3){
        this.q0 = q0;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        
        double mag = Math.sqrt(q0*q0+q1*q1+q2*q2+q3*q3);
        this.q0 /= mag;
        this.q1 /= mag;
        this.q2 /= mag;
        this.q3 /= mag;
        
    }
}
