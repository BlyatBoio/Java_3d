import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PixelPainter extends Component{
    private BufferedImage image;
    private WritableRaster raster;
    public PixelPainter(int screenWidth, int screenHeight){
        image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, 0, 0, null);        
    }
    public void setPixel(int x, int y, int R, int G, int B){
        raster.setPixel(x, y, new int[]{R, G, B});
    }
    public void setPixel(int x, int y, int[] color){
        raster.setPixel(x, y, color);
    }
    public void setPixelGroup(int x, int y, int w, int h, int R, int G, int B){
        for(int xP = x; xP < x + w; xP ++){
            for(int yP = y; yP < y + h; yP ++){
                raster.setPixel(xP, yP, new int[]{R, G, B});
            }
        }
    }
    public void setPixelGroup(int x, int y, int w, int h, int[] color){
        for(int xP = x; xP < x + w; xP ++){
            for(int yP = y; yP < y + h; yP ++){
                raster.setPixel(xP, yP, color);
            }
        }
    }
}