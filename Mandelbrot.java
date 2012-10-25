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
	Button resetButton, applyButton;
	Label lbX1, lbY1, lbX2, lbY2;
	TextField tfConvergent, tfDivergence;

	int times = 200;
	int hassan = 2;
	double rmin, rmax, imin, imax;
	boolean m_bDragging = false;

	int width = 800, height = 800;
	Image imgBuf;
	Point m_pBegin, m_pEnd;

	private void init_range() { rmin = -2; rmax = 1; imin = -1.5; imax = 1.5; }

	public void init() {
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		init_range();
		renew();

		// reset button
		resetButton = new Button("Reset");
		add(resetButton);
		resetButton.addActionListener(new ActionAdp());

		// labels
		lbX1 = new Label("x1 =     ");
		lbY1 = new Label("y1 =     ");
		lbX2 = new Label("x2 =     ");
		lbY2 = new Label("y2 =     ");
		add(lbX1);
		add(lbY1);
		add(lbX2);
		add(lbY2);

		// text fields
		tfConvergent = new TextField(5);
		tfDivergence = new TextField(5);
		tfConvergent.setText(""+times);
		tfDivergence.setText(""+hassan);
		add(new Label("Convergent: "));
		add(tfConvergent);
		add(new Label("Divergence: "));
		add(tfDivergence);

		// apply button
		applyButton = new Button("apply");
		add(applyButton);
		applyButton.addActionListener(new ActionAdp());
	}

	private void update_label()
	{
		lbX1.setText("x1=" + rmin);
		lbY1.setText("y1=" + imin);
		lbX2.setText("x2=" + rmax);
		lbY2.setText("y2=" + imax);
	}

	// KeyEvent Handler
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_LEFT
				|| k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN)
		{
			renew();
			repaint();
		}
	}
	public void keyPressed(KeyEvent e)
	{
		int k = e.getKeyCode();
		double delta_re = get_re_delta()*(width/10);
		double delta_im = get_im_delta()*(height/10);

		if (k == KeyEvent.VK_RIGHT) {
			rmin += delta_re;
			rmax += delta_re;
		}
		else if (k == KeyEvent.VK_LEFT) {
			rmin -= delta_re;
			rmax -= delta_re;
		}
		else if (k == KeyEvent.VK_DOWN) {
			imin += delta_im;
			imax += delta_im;
		}
		else if (k == KeyEvent.VK_UP) {
			imin -= delta_im;
			imax -= delta_im;
		}
		else if (k == KeyEvent.VK_ESCAPE) {
			m_bDragging = false;
		}
		else {
			return;
		}
		renew();
		repaint();
	}

	// MouseEvent Handler
	public void mousePressed(MouseEvent e) {
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
		update_label();

		if (m_pBegin.x == m_pEnd.x && m_pBegin.y == m_pEnd.y)
			return;

		double re_delta = get_re_delta();
		double im_delta = get_im_delta();
		rmin = rmin + re_delta * m_pBegin.x;
		rmax = rmin + re_delta * (m_pEnd.x - m_pBegin.x);
		imin = imin + im_delta * m_pBegin.y;
		imax = imin + im_delta * (m_pEnd.y - m_pBegin.y);

		renew();
		repaint();
	}
	public void mouseDragged(MouseEvent e) {
		if (!m_bDragging)
			return;
		m_pEnd = e.getPoint();
		normalize_point();
		update_label();

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
		double window_ratio = 1.0*width/height;
		double area_ratio = 1.0*area_w/area_h;

		if (window_ratio != area_ratio) {
			if (width/area_w > height/area_h)
				m_pEnd.y = (int) (m_pBegin.y + height * (1.0*area_w/width));
			else
				m_pEnd.x = (int) (m_pBegin.x + width * (1.0*area_h/height));
		}
	}

	class ActionAdp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == resetButton) {
				init_range();
				renew();
				repaint();
			}
			else if (e.getSource() == applyButton) {
				hassan = (new Integer(tfDivergence.getText())).intValue();
				times = (new Integer(tfConvergent.getText())).intValue();
				renew();
				repaint();
			}
		}
	}

	private void renew()
	{
		imgBuf = createImage(width, height);
		mandel();
	}

	private void mandel()
	{
		Graphics g = imgBuf.getGraphics();

		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		paintMandelbrot(g, width, height);
	}

	public void paint(Graphics g)
	{
		g.drawImage(imgBuf, 0, 0, this);

		if (m_bDragging) {
			g.setColor(new Color(100, 100, 255, 100));
			g.fillRect(m_pBegin.x, m_pBegin.y, m_pEnd.x - m_pBegin.x, m_pEnd.y - m_pBegin.y);
		}
	}

	private Color getColor(int l, double rF, double iF){
		l = l/2;
		Color[] c = {Color.red, Color.pink, Color.green, Color.yellow, Color.orange, Color.magenta, Color.blue, Color.cyan };
		return c[l%c.length];
	}

	private double get_re_delta() { return (rmax - rmin) / width; }
	private double get_im_delta() { return (imax - imin) / height; }

	private void paintMandelbrot(Graphics g, int width, int height)
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
					if (rF * rF + iF * iF > hassan*hassan) {
						g.setColor(getColor(l, rF, iF));
						g.drawLine(j, k, j, k);
						break;
					}
					r = rF;
					i = iF;
				}
			}
		}
	}
}

