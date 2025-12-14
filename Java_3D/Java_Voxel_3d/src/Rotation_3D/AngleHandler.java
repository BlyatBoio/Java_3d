package Rotation_3D;

import Primatives_3D.Polygon;
import Primatives_3D.Vector3D;

public class AngleHandler {
    public static Quaternion asQuaternion(EulerAngle angle){
        double cu2 = Math.cos(angle.roll/2);
        double cv2 = Math.cos(angle.pitch/2);
        double cw2 = Math.cos(angle.yaw/2);
        
        double su2 = Math.sin(angle.roll/2);
        double sv2 = Math.sin(angle.pitch/2);
        double sw2 = Math.sin(angle.yaw/2);
        
        return new Quaternion(
            cu2*cv2*cw2+su2*sv2*sw2,
            su2*cv2*cw2+cu2*sv2*sw2,
            cu2*sv2*cw2+su2*cv2*sw2,
            cu2*cv2*sw2+su2*sv2*cw2);
    }
    public static Quaternion asQuaternion(RotationMatrix matrix){
        double mag_q0 = Math.sqrt((1+matrix.get(0,0)+matrix.get(1,1)+matrix.get(2,2))/4);
        double mag_q1 = Math.sqrt((1+matrix.get(0,0)-matrix.get(1,1)-matrix.get(2,2))/4);
        double mag_q2 = Math.sqrt((1-matrix.get(0,0)+matrix.get(1,1)-matrix.get(2,2))/4);
        double mag_q3 = Math.sqrt((1-matrix.get(0,0)-matrix.get(1,1)+matrix.get(2,2))/4);
        
        double maxValue = Math.max(Math.max(mag_q0,mag_q1),Math.max(mag_q2,mag_q3));
        
        if(maxValue == mag_q0){
            double q04 = 4*mag_q0;
            return new Quaternion(
                mag_q0, 
                (matrix.get(2,1)-matrix.get(1,2))/q04,
                (matrix.get(0,2)-matrix.get(2,0))/q04,
                (matrix.get(1,0)-matrix.get(0,1))/q04);
        }
        else if(maxValue == mag_q1){
            double q14 = 4*mag_q1;
            return new Quaternion(
                (matrix.get(2,1)-matrix.get(1,2))/q14,  
                mag_q1, 
                (matrix.get(0,1)-matrix.get(1,0))/q14,
                (matrix.get(0,2)-matrix.get(2,0))/q14);
        }
        else if(maxValue == mag_q2){
            double q24 = 4*mag_q2;
            return new Quaternion(
                (matrix.get(0,2)-matrix.get(2,0))/q24,  
                (matrix.get(0,1)-matrix.get(1,0))/q24,
                mag_q1, 
                (matrix.get(1,2)-matrix.get(2,1))/q24);
        }
        else{
            double q34 = 4*mag_q3;
            return new Quaternion(
                (matrix.get(1,0)-matrix.get(0,1))/q34,  
                (matrix.get(0,2)-matrix.get(2,0))/q34,
                (matrix.get(1,2)-matrix.get(2,1))/q34,
                mag_q3);
        }
    }
    public static Quaternion asQuaternion(AxisAngle angle){
        double s = Math.sin(angle.theta/2);
        return new Quaternion(Math.cos(angle.theta/2), angle.x*s, angle.y*s, angle.z*s);
    }
    public static Quaternion asQuaternion(Vector3D vec){
        return new Quaternion(0, vec.x, vec.y, vec.z);
    }

    public static Quaternion mult(Quaternion quat_1, Quaternion quat_2){
        return new Quaternion(
            quat_1.q0*quat_2.q0-quat_1.q1*quat_2.q1-quat_1.q2*quat_2.q2-quat_1.q3*quat_2.q3,
            quat_1.q0*quat_2.q1+quat_1.q1*quat_2.q0-quat_1.q2*quat_2.q3+quat_1.q3*quat_2.q2,
            quat_1.q0*quat_2.q2+quat_1.q1*quat_2.q3+quat_1.q2*quat_2.q0-quat_1.q3*quat_2.q1,
            quat_1.q0*quat_2.q3-quat_1.q1*quat_2.q2+quat_1.q2*quat_2.q1+quat_1.q3*quat_2.q0);
    }
    public static Quaternion getInverted(Quaternion quat){
        return new Quaternion(quat.q0, -quat.q1, -quat.q2, -quat.q3);
    }
    
    public static Vector3D getRotated(Vector3D vec, Quaternion quat){
        Quaternion vecAsQuat = asQuaternion(vec);
        Quaternion outQuat = mult(mult(getInverted(quat), vecAsQuat), quat);
        return new Vector3D(outQuat.q1, outQuat.q2, outQuat.q3);
    }
    public static Vector3D getRotated(Vector3D vec, RotationMatrix matrix){
        return new Vector3D(
            (vec.x*matrix.get(0,0))+(vec.y*matrix.get(1,0))+vec.z*matrix.get(2,0),
            (vec.x*matrix.get(0,1))+(vec.y*matrix.get(1,1))+vec.z*matrix.get(2,1),
            (vec.x*matrix.get(0,2))+(vec.y*matrix.get(1,2))+vec.z*matrix.get(2,2));
    }
    public static void getRotatedPolygon(Polygon p, Vector3D centerPoint, AxisAngle angle){
        RotationMatrix rot = asRotationMatrix(angle);

        p.p1.sub(centerPoint);
        p.p2.sub(centerPoint);
        p.p3.sub(centerPoint);

        p.p1 = centerPoint.copy().add(getRotated(p.p1, rot));
        p.p2 = centerPoint.copy().add(getRotated(p.p2, rot));
        p.p3 = centerPoint.copy().add(getRotated(p.p3, rot));
    }


    public static AxisAngle asAxisAngle(Quaternion quat){
        double theta = 2*Math.acos(quat.q0);
        double t = theta/2;
        if(theta == 0) return new AxisAngle(0, 1, 0, 0);
        return new AxisAngle(theta, quat.q1/Math.sin(t), quat.q2/Math.sin(t), quat.q3/Math.sin(t));
    }
    public static AxisAngle asAxisAngle(RotationMatrix matrix){
        return normalize(new AxisAngle(
            Math.acos(matrix.get(0,0)+matrix.get(1,1)+matrix.get(2,2)-1),
            matrix.get(2,1) - matrix.get(1,2),
            matrix.get(0,2) - matrix.get(2,0),
            matrix.get(1,0) - matrix.get(0,1)));
    }
    //TODO: Add Euler Angle -> Axis Angle Function

    public static EulerAngle asEulerAngle(Quaternion quat){
        double q0s = quat.q0*quat.q0;
        double q1s = quat.q1*quat.q1;
        double q2s = quat.q2*quat.q2;
        double q3s = quat.q3*quat.q3;

        double pitch = Math.asin(2*(quat.q0*quat.q2 + quat.q1*quat.q3));

        if(pitch == Math.PI/2) return new EulerAngle(0, pitch, -2*Math.atan2(quat.q1, quat.q0));
        if(pitch == -Math.PI/2) return new EulerAngle(0, pitch, 2*Math.atan2(quat.q1, quat.q0));

        return new EulerAngle(
            Math.atan((2*(quat.q0*quat.q1 + quat.q2*quat.q3))/q0s-q1s-q2s+q3s),
            pitch,
            Math.atan((2*(quat.q0*quat.q3 + quat.q1*quat.q2))/q0s+q1s-q2s-q3s));
    }
    //TODO: Add Rot Matrix -> Euler Angle Function
    //TODO: Add Axis Angle -> Euler Angle Function

    public static RotationMatrix asRotationMatrix(Quaternion quat){

        double q0s = quat.q0*quat.q0;
        double q1s = quat.q1*quat.q1;
        double q2s = quat.q2*quat.q2;
        double q3s = quat.q3*quat.q3;

        double q1q2 = 2*quat.q1*quat.q2;
        double q0q3 = 2*quat.q0*quat.q3;
        double q1q3 = 2*quat.q1*quat.q3;
        double q0q2 = 2*quat.q0*quat.q2;
        double q2q3 = 2*quat.q2*quat.q3;
        double q0q1 = 2*quat.q0*quat.q1;

        return new RotationMatrix(
            q0s+q1s-q2s-q3s, q1q2-q0q3, q1q3+q0q2,
            q1q2+q0q3, q0s-q1s+q2s-q3s, q2q3-q0q1,
            q1q3-q0q2, q2q3+q0q1, q0s-q1s-q2s+q3s);
    }
    public static RotationMatrix asRotationMatrix(EulerAngle angle, EulerAxis axis){
        switch (axis) {
            case X -> {
                return new RotationMatrix(
                        1, 0, 0,
                        0, Math.cos(angle.roll), -Math.sin(angle.roll),
                        0, Math.sin(angle.roll), Math.cos(angle.roll));
            }
            case Y -> {
                return new RotationMatrix(
                        Math.cos(angle.pitch), 0, Math.sin(angle.pitch),
                        0, 1, 0,
                        -Math.sin(angle.pitch), 0, Math.cos(angle.pitch));
            }
            case Z -> {
                return new RotationMatrix(
                        Math.cos(angle.yaw), -Math.sin(angle.yaw), 0,
                        Math.sin(angle.yaw), Math.cos(angle.yaw), 0,
                        0, 0, 1);
            }
            default -> {
                return new RotationMatrix(null);
            }
        }
    }
    public static RotationMatrix asRotationMatrix(AxisAngle angle){
        double c = Math.cos(angle.theta);
        double s = Math.sin(angle.theta);
        double t = 1-c;

        double xy = angle.x * angle.y;
        double yz = angle.y * angle.z;
        double xz = angle.x * angle.z;

        return new RotationMatrix(
            c+angle.x*angle.x*t, xy*t-angle.z*s, angle.x*angle.z*t+angle.y*s,
            xy*t+angle.z*s, c+angle.y*angle.y*t, yz*t-angle.x*s,
            xz*t-angle.y*s, yz*t+angle.x*s, c+angle.z*angle.z*t);
    }
    
    public static Quaternion normalize(Quaternion quat){
        double mag = Math.sqrt(quat.q0*quat.q0+quat.q1*quat.q1+quat.q2*quat.q2+quat.q3*quat.q3);
        return new Quaternion(quat.q0/mag, quat.q1/mag, quat.q2/mag, quat.q3/mag);
    }
    public static AxisAngle normalize(AxisAngle angle){
        double mag = Math.sqrt(angle.x*angle.x+ angle.y*angle.y+angle.z*angle.z);
        return new AxisAngle(angle.theta, angle.x/mag, angle.y/mag, angle.z/mag);
    }

    public static void printOut(Quaternion quat){
        System.out.println("Quaternion:\nQ0: "+quat.q0+"\nQ1: "+quat.q1+"\nQ2: "+quat.q2+"\nQ3: "+quat.q3);
    }
    public static void printOut(EulerAngle angle){
        System.out.println("Euler Angle:\nRoll: "+angle.roll+"\nPitch: "+angle.pitch+"\nYaw: "+angle.yaw);
    }
    public static void printOut(AxisAngle angle){
        System.out.println("Axis Angle:\nTheta: "+angle.theta+"\nX: "+angle.x+"\nY: "+angle.y+"\nZ: "+angle.z);
    }
    public static void printOut(RotationMatrix matrix){
        System.out.println(
            "Rotation Matrix:\n("
            +matrix.get(0,0)+", "+matrix.get(0,1)+", "+matrix.get(0,2)+")\n("
            +matrix.get(1,0)+", "+matrix.get(1,1)+", "+matrix.get(1,2)+")\n("
            +matrix.get(2,0)+", "+matrix.get(2,1)+", "+matrix.get(2,2)+")");
    }
    
    public enum EulerAxis{
        X,Y,Z
    }
}
