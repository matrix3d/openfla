import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class Bitmap extends Sprite {
	public BufferedImage image;
	public void render(Graphics2D g,World world){
		if(image!=null){
			super.doTransorm(g);
			g.drawImage(image,0,0,world);
		}
		super.renderChild(g,world);
	}
}
