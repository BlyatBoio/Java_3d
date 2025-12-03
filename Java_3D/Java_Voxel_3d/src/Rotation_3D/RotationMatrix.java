package Rotation_3D;

public class RotationMatrix {
    public double[][] matrix = new double[3][3];
    
    public RotationMatrix(double[][] matrix){
        this.matrix = matrix;
    }
    public RotationMatrix(double[] col1, double[] col2, double[] col3){
        this.matrix[0] = col1;
        this.matrix[1] = col2;
        this.matrix[2] = col3;
    }
    public RotationMatrix(double m11, double m12, double m13, double m21, double m22, double m23, double m31, double m32, double m33){
        this.matrix[0][0] = m11;
        this.matrix[0][1] = m12;
        this.matrix[0][2] = m13;
        this.matrix[1][0] = m21;
        this.matrix[1][1] = m22;
        this.matrix[1][2] = m23;
        this.matrix[2][0] = m31;
        this.matrix[2][1] = m32;
        this.matrix[2][2] = m33;
    }

    public void normalize(){
        this.set(AngleHandler.asRotationMatrix(AngleHandler.asQuaternion(this)));
    }
    
    public void set(RotationMatrix matrix){
        this.matrix = matrix.matrix;
    }

    public double get(int x, int y){
        return this.matrix[x][y];
    }
}
