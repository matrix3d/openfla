import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class Main {
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e){

		}

		View view = new View();
		view.setSize(400,400);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
	}
}
