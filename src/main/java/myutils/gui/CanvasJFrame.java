package myutils.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

@SuppressWarnings({"CanBeFinal", "UnusedDeclaration"})
public abstract class CanvasJFrame extends Canvas implements Runnable {
  protected static final double DEFAULT_NS_PER_TICK = 1e9D / 60D;
  protected static final double DEFAULT_NS_PER_RENDER = 1e9D / 30D;
  protected static final Color DEFAULT_BACKGROUND_COLOR = Color.black;
  protected static Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
  public int WIDTH, HEIGHT, SCALE;
  public float cx, cy;
  public Pixels screen;
  protected int x, y, xPos, yPos;
  protected Graphics graphics;
  protected BufferStrategy bufferStrategy;
  protected KeyHandler keyHandler;
  @SuppressWarnings("FieldCanBeLocal")
  protected JFrame frame;
  protected double nsPerTick;
  protected double nsPerRender;
  protected MouseHandler mouseHandler;
  private boolean running = false;
  private long tickCount = 0;
  private int ticks = 0;
  private int renders = 0;
  private String TITLE;
  private double deltaTick = 0;
  private double deltaRender = 0;
  private BufferedImage image;

  public CanvasJFrame(int width, int height, int scale, String title, double nsPerTick, double nsPerRender) {
    WIDTH = width / scale;
    HEIGHT = height / scale;
    cx = WIDTH / 2f;
    cy = HEIGHT / 2f;
    SCALE = scale;
    TITLE = title;
    this.nsPerTick = nsPerTick;
    this.nsPerRender = nsPerRender;
    this.nsPerTick = DEFAULT_NS_PER_TICK;
    this.nsPerRender = DEFAULT_NS_PER_RENDER;
    setMinimumSize(new Dimension(WIDTH * SCALE / 2, HEIGHT * SCALE / 2));
    setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
    setMaximumSize(new Dimension(WIDTH * SCALE * 2, HEIGHT * SCALE * 2));
    frame = new JFrame(TITLE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.add(this, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setVisible(true);
    image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    screen = new Pixels(((DataBufferInt) image.getRaster().getDataBuffer()).getData(), this);
    createBufferStrategy(3);
    bufferStrategy = getBufferStrategy();
    graphics = bufferStrategy.getDrawGraphics();
    keyHandler = new KeyHandler(this);
    mouseHandler = new MouseHandler(this);
  }

  public CanvasJFrame(double widthRate, double heightRate, int scale, final String APP_NAME) {
    this((int) Math.round(screenSize.getWidth() * widthRate), (int) Math.round(screenSize.getHeight() * heightRate), scale, APP_NAME, DEFAULT_NS_PER_TICK, DEFAULT_NS_PER_RENDER);
  }

  @Override
  public void run() {
    init();
    long last = System.nanoTime();
    long current;
    long debugTimer = System.currentTimeMillis();
    boolean shouldRender = false;
    while (running) {
      current = System.nanoTime();
      deltaTick += (current - last) / nsPerTick;
      deltaRender += (current - last) / nsPerRender;
      last = current;
      if (deltaTick > 1) {
        tick();
        ticks++;
        deltaTick -= 1;
        shouldRender = true;
      }
      if ((deltaRender > 1) && (shouldRender)) {
        render();
        renders++;
        deltaRender -= 1;
        shouldRender = false;
      }
      if (System.currentTimeMillis() - debugTimer >= 1000) {
        debugInfo();
        debugTimer += 1000;
      }
    }
    System.exit(0);
  }

  protected void clearScreen() {
    screen.clear(getBackground());
  }

  protected void clearScreen(Color color) {
    screen.clear(color);
  }

  protected abstract void init();

  protected void tick() {
    tickCount++;
    keyHandling();
    mouseHandling();
    myTick();
  }

  protected void render() {
    myRender();
    graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    bufferStrategy.show();
  }

  protected void debugInfo() {
    myDebugInfo();
    ticks = renders = 0;
  }

  protected abstract void myTick();

  protected abstract void myRender();

  protected void myDebugInfo() {
    System.out.println(ticks + " TPS, " + renders + "FPS");
  }

  protected void keyHandling() {
    if (keyHandler.esc.pressed) {
      stop();
    }
    if (keyHandler.up.pressed) {
      screen.scrollY(-1);
    }
    if (keyHandler.down.pressed) {
      screen.scrollY(1);
    }
    if (keyHandler.left.pressed) {
      screen.scrollX(-1);
    }
    if (keyHandler.right.pressed) {
      screen.scrollX(1);
    }
    if (keyHandler.pageup.pressed) {
      screen.zoom(1);
    }
    if (keyHandler.pagedown.pressed) {
      screen.zoom(-1);
    }
    if (keyHandler.equal.pressed) {
      screen.resetOffsetScale();
      resetNsPerTickRender();
    }
  }

  void resetNsPerTickRender() {
    nsPerTick = DEFAULT_NS_PER_TICK;
    nsPerRender = DEFAULT_NS_PER_RENDER;
  }

  protected void mouseHandling() {
    if (mouseHandler.right.clicked) {
      screen.setOffset(mouseHandler.right.locationRelativeScaled);
      mouseHandler.right.clicked = false;
    }
    if (mouseHandler.amountScrolled != 0) {
      screen.zoom(mouseHandler.amountScrolled);
      mouseHandler.amountScrolled = 0;
    }
  }

  public synchronized void start() {
    System.out.println("CanvasShell start");
    running = true;
    new Thread(this, TITLE + "-Thread").start();
  }

  public void stop() {
    System.out.println("CanvasShell stop");
    running = false;
  }

}
