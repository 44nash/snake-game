	import java.applet.Applet;
	import java.awt.Dimension;
	import java.awt.Graphics;

	public class SnakeApplet extends Applet{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5153340417652689043L;
		private PortalCanvas c;
		public void init(){
			c = new PortalCanvas();
			c.setPreferredSize(new Dimension(640, 515));
			c.setVisible(true);
			c.setFocusable(true);
			this.add(c);
			this.setVisible(true);
			this.setSize(new Dimension(640,515));
		}
		public void paint(Graphics g){
			this.setSize(new Dimension(640, 515));
	}
	}
