import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Util {

    private Util() {}

    /**
     * Returns the class name of the installed LookAndFeel with a name
     * containing the name snippet or null if none found.
     *
     * @param nameSnippet a snippet contained in the Laf's name
     * @return the class name if installed, or null
     */
    public static String getLookAndFeelClassName(String nameSnippet) {
        UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : plafs) {
            if (info.getName().contains(nameSnippet)) {
                return info.getClassName();
            }
        }
        return null;
    }

    public static void showStackTraceDialog(Throwable throwable,
                                            String title) {
        String message = throwable.getMessage() == null ? throwable
                .toString() : throwable.getMessage();
        showStackTraceDialog(throwable, title, message);
    }

    public static void showStackTraceDialog(Throwable throwable,
                                            String title, String message) {
        Window window = DefaultKeyboardFocusManager
                .getCurrentKeyboardFocusManager().getActiveWindow();
        showStackTraceDialog(throwable, window, title, message);
    }

    /**
     * show stack trace dialog when exception throws
     * @param throwable
     * @param parentComponent
     * @param title
     * @param message
     */
    public static void showStackTraceDialog(Throwable throwable,
                                            Component parentComponent, String title, String message) {
        final String more = "More";
        // create stack strace panel
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel label = new JLabel(more + ">>");
        labelPanel.add(label);

        JTextArea straceTa = new JTextArea();
        final JScrollPane taPane = new JScrollPane(straceTa);
        taPane.setPreferredSize(new Dimension(360, 240));
        taPane.setVisible(false);
        // print stack trace into textarea
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream(out));
        straceTa.setForeground(Color.RED);
        straceTa.setText(new String(out.toByteArray()));

        final JPanel stracePanel = new JPanel(new BorderLayout());
        stracePanel.add(labelPanel, BorderLayout.NORTH);
        stracePanel.add(taPane, BorderLayout.CENTER);

        label.setForeground(Color.BLUE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JLabel tmpLab = (JLabel) e.getSource();
                if (tmpLab.getText().equals(more + ">>")) {
                    tmpLab.setText("<<" + more);
                    taPane.setVisible(true);
                } else {
                    tmpLab.setText(more + ">>");
                    taPane.setVisible(false);
                }
                SwingUtilities.getWindowAncestor(taPane).pack();
            };
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(message), BorderLayout.NORTH);
        panel.add(stracePanel, BorderLayout.CENTER);

        JOptionPane pane = new JOptionPane(panel, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog(parentComponent, title);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3;
        if (dialog.getWidth() > maxWidth) {
            dialog.setSize(new Dimension(maxWidth, dialog.getHeight()));
            setLocationRelativeTo(dialog, parentComponent);
        }
        dialog.setResizable(true);
        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * set c1 location relative to c2
     * @param c1
     * @param c2
     */
    public static void setLocationRelativeTo(Component c1, Component c2) {
        Container root = null;

        if (c2 != null) {
            if (c2 instanceof Window || c2 instanceof Applet) {
                root = (Container) c2;
            } else {
                Container parent;
                for (parent = c2.getParent(); parent != null; parent = parent
                        .getParent()) {
                    if (parent instanceof Window
                            || parent instanceof Applet) {
                        root = parent;
                        break;
                    }
                }
            }
        }

        if ((c2 != null && !c2.isShowing()) || root == null
                || !root.isShowing()) {
            Dimension paneSize = c1.getSize();

            Point centerPoint = GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getCenterPoint();
            c1.setLocation(centerPoint.x - paneSize.width / 2,
                    centerPoint.y - paneSize.height / 2);
        } else {
            Dimension invokerSize = c2.getSize();
            Point invokerScreenLocation = c2.getLocation(); // by longrm:
            // c2.getLocationOnScreen();

            Rectangle windowBounds = c1.getBounds();
            int dx = invokerScreenLocation.x
                    + ((invokerSize.width - windowBounds.width) >> 1);
            int dy = invokerScreenLocation.y
                    + ((invokerSize.height - windowBounds.height) >> 1);
            Rectangle ss = root.getGraphicsConfiguration().getBounds();

            // Adjust for bottom edge being offscreen
            if (dy + windowBounds.height > ss.y + ss.height) {
                dy = ss.y + ss.height - windowBounds.height;
                if (invokerScreenLocation.x - ss.x + invokerSize.width / 2 < ss.width / 2) {
                    dx = invokerScreenLocation.x + invokerSize.width;
                } else {
                    dx = invokerScreenLocation.x - windowBounds.width;
                }
            }

            // Avoid being placed off the edge of the screen
            if (dx + windowBounds.width > ss.x + ss.width) {
                dx = ss.x + ss.width - windowBounds.width;
            }
            if (dx < ss.x)
                dx = ss.x;
            if (dy < ss.y)
                dy = ss.y;

            c1.setLocation(dx, dy);
        }
    }

    public static void main(String[] args) {
        System.out.println(getLookAndFeelClassName("Windows"));
        System.out.println(getLookAndFeelClassName("Motif"));
        System.out.println(getLookAndFeelClassName("Metal"));

        UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : plafs) {
            System.out.println(info.getClassName());
        }
    }

}
