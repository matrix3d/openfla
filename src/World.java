import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class World extends JPanel implements Runnable{
	private Thread thread;
	public Sprite root=new Sprite();
	public World(){
		setBackground(Color.white);
		setPreferredSize(new Dimension(400,400));
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d =(Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		root.render(g2d,this);
	}

	public void start(){
		if(thread==null)thread=new Thread(this);
		thread.start();
	}

	public void stop(){
		if(thread!=null)
			try {
				thread.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
	}

	public void run(){
		while (true){
			repaint();
			try{
				thread.sleep(1000/60);
			}catch (Exception e){

			}
		}
	}
}
