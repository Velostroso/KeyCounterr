import javax.swing.*;
import java.awt.*;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.NativeHookException;

public class KeyCounter implements NativeKeyListener {

    private int count = 0;
    private JTextField fieldCount;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!SystemTray.isSupported()) {
                System.out.println("System Tray não é suportado neste sistema.");
                return;
            }
            new KeyCounter().start();
        });
    }

    public void start() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            createTrayIcon();
        } catch (NativeHookException e) {
            System.err.println("Erro ao registrar o Native Hook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createTrayIcon() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

            PopupMenu popupMenu = new PopupMenu();

            MenuItem openItem = new MenuItem("Abrir");
            openItem.addActionListener(e -> createFrame());
            popupMenu.add(openItem);

            MenuItem exitItem = new MenuItem("Sair");
            exitItem.addActionListener(e -> {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    System.err.println("Erro ao desregistrar o Native Hook: " + ex.getMessage());
                    ex.printStackTrace();
                }
                System.exit(0);
            });
            popupMenu.add(exitItem);

            TrayIcon trayIcon = new TrayIcon(image, "Key Counter", popupMenu);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

        } catch (AWTException e) {
            System.err.println("Erro ao criar o ícone da bandeja: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createFrame() {
        JFrame frame = new JFrame("Key Counter");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new FlowLayout());
        Color lightBlue = new Color(173, 216, 230);
        frame.getContentPane().setBackground(lightBlue);

        JLabel labelQuantity = new JLabel("Contagem");
        fieldCount = new JTextField(20);
        fieldCount.setEditable(false);

        JButton closeButton = new JButton("Fechar");

        frame.add(labelQuantity);
        frame.add(fieldCount);
        frame.add(closeButton);

        frame.setFocusable(true);
        frame.setVisible(true);

        closeButton.addActionListener(e -> frame.setVisible(false));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        count++;
        if (fieldCount != null) {
            fieldCount.setText(String.valueOf(count));
        }
        System.out.println("Tecla pressionada: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }
}
