// Mandelbrot.java

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Mandelbrot extends Applet
	implements MouseListener, MouseMotionListener, KeyListener
{
	Button drawButton;
	Choice rminChoice, rmaxChoice, iminChoice, imaxChoice;
	int times = 2000;
	double rmin, rmax, imin, imax; // 範囲
	boolean m_bDragging = false;

	int width = 800, height = 800;
	Image imgBuf;
	Point m_pBegin, m_pEnd;

	public void init() {
		rmin = -2.0;
		rmax = 1.0;
		imin = -1.5;
		imax = 1.5;

		renew();

		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		/*
		drawButton = new Button("draw");//{{{

		rminChoice = new Choice();
		for (int i = -30; i < 0; i++) {
			rminChoice.addItem("" + (i / 10.0));
		}

		rmaxChoice = new Choice();
		for (int i = 0; i <= 20; i++) {
			rmaxChoice.addItem("" + (i / 10.0));
		}

		iminChoice = new Choice();
		for (int i = -20; i < 0; i++) {
			iminChoice.addItem("" + (i / 10.0));
		}

		imaxChoice = new Choice();
		for (int i = 0; i <= 20; i++) {
			imaxChoice.addItem("" + (i / 10.0));
		}

		add(drawButton);
		add(new Label("Real: min:"));
		add(rminChoice);
		add(new Label("max:"));
		add(rmaxChoice);
		add(new Label("Image: min"));
		add(iminChoice);
		add(new Label("max"));
		add(imaxChoice);

		drawButton.addActionListener(new ActionAdp());
		rminChoice.addItemListener(new ItemAdp());
		rmaxChoice.addItemListener(new ItemAdp());
		iminChoice.addItemListener(new ItemAdp());
		imaxChoice.addItemListener(new ItemAdp());

		rminChoice.select(10);
		rmaxChoice.select(10);
		iminChoice.select(5);
		imaxChoice.select(15);//}}}
		*/
	}

	// KeyEvent Handler
	public void keyTyped(KeyEvent e)
	{
	}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}

	// MouseEvent Handler
	public void mousePressed(MouseEvent e) {//{{{
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		m_bDragging = true;
		m_pBegin = e.getPoint();
	}
	public void mouseReleased(MouseEvent e) {
		if (!m_bDragging)
			return;
		m_bDragging = false;
		m_pEnd = e.getPoint();
		normalize_point();

		rmin = rmin + get_re_delta() * m_pBegin.x;
		rmax = rmin + get_re_delta() * (m_pEnd.x - m_pBegin.x);
		imin = imin + get_im_delta() * m_pBegin.y;
		imax = imin + get_im_delta() * (m_pEnd.y - m_pBegin.y);

		renew();
		repaint();
	}
	public void mouseDragged(MouseEvent e) {
		if (!m_bDragging)
			return;
		m_pEnd = e.getPoint();
		normalize_point();

		repaint();
	}
	public void mouseExited(MouseEvent e) { /* do nothing */ }
	public void mouseEntered(MouseEvent e) { /* do nothing */ }
	public void mouseClicked(MouseEvent e) { /* do nothing */ }
	public void mouseMoved(MouseEvent e) { /* do nothing */ }
	// 始点が左上、終点が右下になるように調整する。
	// また、選択範囲の縦横比と、width,heightの縦横比が一致するように終点調整する。
	private void normalize_point()
	{
		double x1 = Math.min(m_pBegin.x, m_pEnd.x);
		double x2 = Math.max(m_pBegin.x, m_pEnd.x);
		double y1 = Math.min(m_pBegin.y, m_pEnd.y);
		double y2 = Math.max(m_pBegin.y, m_pEnd.y);
		m_pBegin.setLocation(x1,y1);
		m_pEnd.setLocation(x2,y2);

		double area_w = m_pEnd.x-m_pBegin.x;
		double area_h = m_pEnd.y-m_pBegin.y;
		if (1.0*width/height != 1.0*area_w/area_h) {
			if (width/area_w > height/area_h)
				m_pEnd.y = (int) (m_pBegin.y + height * (1.0*area_w/width));
			else
				m_pEnd.x = (int) (m_pBegin.x + width * (1.0*area_h/height));
		}
	}
//}}}

	class ActionAdp implements ActionListener {//{{{
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == drawButton) {
				renew();
				repaint();
			}
		}		
	}//}}}

	class ItemAdp implements ItemListener {//{{{
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if (source == rminChoice) {
				rmin = -3.0 + rminChoice.getSelectedIndex() * 0.1;
			} else if (source == rmaxChoice) {
				rmax = rmaxChoice.getSelectedIndex() * 0.1;
			} else if (source == iminChoice) {
				imin = -2.0 + iminChoice.getSelectedIndex() * 0.1;
			} else if (source == imaxChoice) {
				imax = imaxChoice.getSelectedIndex() * 0.1;
			}
		}
	}//}}}

	private void renew()//{{{
	{
		imgBuf = createImage(width, height);
		mandel();
	}//}}}

	private void mandel()//{{{
	{
		Graphics g = imgBuf.getGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		paintMandelbrot(g, width, height);
	}//}}}

	public void paint(Graphics g)//{{{
	{
		g.drawImage(imgBuf, 0, 0, this);

		if (m_bDragging) {
			g.setColor(new Color(100, 100, 255));
			g.drawRect(m_pBegin.x, m_pBegin.y, m_pEnd.x - m_pBegin.x, m_pEnd.y - m_pBegin.y);
		}
	}//}}}

	private Color getColor(int l, double rF, double iF){//{{{
		Color[] c = {Color.red, Color.pink, Color.green, Color.yellow, Color.orange, Color.magenta, Color.blue, Color.cyan };
		return c[l%c.length];
	}//}}}

	private double get_re_delta() { return (rmax - rmin) / width; }
	private double get_im_delta() { return (imax - imin) / height; }

	private void paintMandelbrot(Graphics g, int width, int height)//{{{
	{
		double r, i, rF, iF, cR, cI;
		double rd = get_re_delta();
		double id = get_im_delta();

		for (int j = 0; j < width; j++) {
			cR = rmin + rd * j;
			for (int k = 0; k < height; k++) {
				cI = imin + id * k;
				r = i = 0.0;
				for (int l = 0; l < times; l++) {
					rF = r * r - i * i + cR;
					iF = 2 * r * i + cI;
					if (rF * rF + iF * iF > 4.0) {
						g.setColor(getColor(l, rF, iF));
						g.drawLine(j, k, j, k);
						break;
					}
					r = rF;
					i = iF;
				}
			}
		}
	}//}}}
}

