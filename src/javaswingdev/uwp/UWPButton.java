package javaswingdev.uwp;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class UWPButton extends JButton {

    private Animator animatorOver;
    private Animator animatorPress;
    private float animateOver;
    private float animatePress;
    private boolean mouseOver;
    private boolean mousePress;
    private int borderSize = 2;
    private Point mousePoint;
    private Color selectedColor = new Color(200, 200, 200);
    private Color effectColor = new Color(255, 255, 255);

    public UWPButton() {
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setBackground(new Color(52, 153, 252));
        setForeground(Color.WHITE);
        initAnimator();
        MouseAdapter mouseEvent = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                startAnimationOver();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                startAnimationOver();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    mousePress = true;
                    startAnimationPress();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    startAnimationPress();
                    mousePress = false;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                repaint();
            }
        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);

    }

    private void initAnimator() {
        animatorOver = new Animator(250, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animateOver = mouseOver ? fraction : 1f - fraction;
                repaint();
            }
        });
        animatorPress = new Animator(250, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animatePress = mousePress ? fraction : 1f - fraction;
                repaint();
            }
        });
        animatorOver.setResolution(0);
        animatorOver.setAcceleration(.5f);
        animatorOver.setDeceleration(.5f);
        animatorPress.setResolution(0);
        animatorPress.setAcceleration(.5f);
        animatorPress.setDeceleration(.5f);
    }

    private void startAnimationOver() {
        if (animatorOver.isRunning()) {
            float f = animatorOver.getTimingFraction();
            animatorOver.stop();
            animatorOver.setStartFraction(1f - f);
        } else {
            animatorOver.setStartFraction(0);
        }
        animatorOver.start();
    }

    private void startAnimationPress() {
        if (animatorPress.isRunning()) {
            float f = animatorPress.getTimingFraction();
            animatorPress.stop();
            animatorPress.setStartFraction(1f - f);
        } else {
            animatorPress.setStartFraction(0);
        }
        animatorPress.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();
        Rectangle rec = new Rectangle(x, y, width, height);
        if (isEnabled()) {
            g2.setColor(getBackground());
            g2.fill(rec);
            if (animateOver > 0 || animatePress > 0) {
                Area area = new Area(rec);
                Rectangle rec_in = new Rectangle(x + borderSize, y + borderSize, width - borderSize * 2, height - borderSize * 2);
                area.subtract(new Area(rec_in));
                if (animateOver > 0 && mousePoint != null) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animateOver));
                    g2.setPaint(getGradient(mousePoint, 255, 1.5f));
                    g2.fill(area);
                    g2.setPaint(getGradient(mousePoint, 70, 0.3f));
                    g2.fill(rec_in);
                }
                if (animatePress > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animatePress));
                    g2.setColor(selectedColor);
                    g2.fill(rec_in);
                }
            }
        } else {
            g2.setColor(Color.GRAY);
            g2.fill(rec);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private RadialGradientPaint getGradient(Point point, int alpha, float size) {
        int width = getWidth();
        int height = getHeight();
        Point2D center = point;
        float radius = (float) Math.max(width, height) * size;
        float[] dist = {0.0f, 1.0f};
        int red = effectColor.getRed();
        int green = effectColor.getGreen();
        int blue = effectColor.getBlue();
        Color[] colors = {new Color(red, green, blue, alpha), new Color(red, green, blue, 0)};
        return new RadialGradientPaint(center, radius, dist, colors);
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        repaint();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        repaint();
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
        repaint();
    }
}
