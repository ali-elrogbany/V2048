import TUIO.TuioDemoComponent;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import TUIO.*;
import V2048Game.Client;
import V2048Game.V2048;

public class TuioDemo  {

	private final int window_width  = 640;
	private final int window_height = 480;

	private boolean fullscreen = false;
	
	private TuioDemoComponent demo;
	private JFrame frame;
	private GraphicsDevice device;
	private Cursor invisibleCursor;
	
	public TuioDemo() {
		demo = new TuioDemoComponent();
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible cursor");
		setupWindow();
		showWindow();
	}
	
	public TuioListener getTuioListener() {
		return demo;
	}
	
	public void setupWindow() {
	
		frame = new JFrame();
		frame.add(demo);

		frame.setTitle("TuioDemo");
		frame.setResizable(false);

		frame.addWindowListener( new WindowAdapter() { public void windowClosing(WindowEvent evt) {
				System.exit(0);
			} });
		
		frame.addKeyListener( new KeyAdapter() { public void keyPressed(KeyEvent evt) {
			if (evt.getKeyCode()==KeyEvent.VK_ESCAPE) System.exit(0);
			else if (evt.getKeyCode()==KeyEvent.VK_F1) {
				destroyWindow();
				setupWindow();
				fullscreen = !fullscreen;
				showWindow();
			}
			else if (evt.getKeyCode()==KeyEvent.VK_V) demo.verbose=!demo.verbose;
		} });
	}
	
	public void destroyWindow() {
	
		frame.setVisible(false);
		if (fullscreen) {
			device.setFullScreenWindow(null);		
		}
		frame = null;
	}
	
	public void showWindow() {
	
		if (fullscreen) {
			int width  = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			demo.setSize(width,height);

			frame.setSize(width,height);
			frame.setUndecorated(true);
			device.setFullScreenWindow(frame);
			frame.setCursor(invisibleCursor);
		} else {
			int width  = window_width;
			int height = window_height;
			demo.setSize(width,height);
			
			frame.pack();
			Insets insets = frame.getInsets();			
			frame.setSize(width,height +insets.top);
			frame.setCursor(Cursor.getDefaultCursor());
		}
		
		frame.setVisible(true);
		frame.repaint();
	}
	
	public static void main(String argv[]) {
		
		TuioDemo demo = new TuioDemo();
		TuioClient client = null;
 
		switch (argv.length) {
			case 1:
				try { 
					client = new TuioClient( Integer.parseInt(argv[0])); 
				} catch (Exception e) {
					System.out.println("usage: java TuioDemo [port]");
					System.exit(0);
				}
				break;
			case 0:
				client = new TuioClient();
				break;
			default: 
				System.out.println("usage: java TuioDemo [port]");
				System.exit(0);
				break;
		}
		
		if (client!=null) {
                    client.addTuioListener(demo.getTuioListener());
                    client.connect();

                    Client socketClient = new Client("127.0.0.1", 65436);
                    SwingUtilities.invokeLater(() -> {
                        V2048 game = new V2048();
                        game.setVisible(true);
                    });
		} 
                else {
			System.out.println("usage: java TuioDemo [port]");
			System.exit(0);
		}
	}
	
}
