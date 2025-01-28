import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente extends JFrame {
    private JFrame frame;
    private JPanel panel;
    private EstadoJuego estadoJuego;
    private ClienteThread clienteThread;
    private boolean carreraIniciada = false;
    private boolean soyJugador1;

    // Imágenes
    private ImageIcon fondoImagen;
    private ImageIcon bola1Imagen;
    private ImageIcon bola2Imagen;
    private BufferedImage buffer;

    public Cliente() {
        estadoJuego = new EstadoJuego(700); // Línea de meta en la posición 700

        try {
            // Cargar imágenes
            fondoImagen = new ImageIcon("res/imagen_fondo.jpg");
            bola1Imagen = new ImageIcon("res/balon1.png");
            bola2Imagen = new ImageIcon("res/balon2.png");

            // Conexión al servidor
            Socket socket = new Socket("127.0.0.1", 4444);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Recibir rol desde el servidor antes de configurar la interfaz gráfica del cliente para que no pete
            soyJugador1 = in.readBoolean();

            // Configurar la interfaz gráfica
            frame = new JFrame("Juego de Carreras");
            frame.setSize(800, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    if (buffer == null) {
                        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                    }
                    Graphics2D g2 = buffer.createGraphics();
                    g2.clearRect(0, 0, getWidth(), getHeight());



                    // Dibujar fondo
                    g2.drawImage(fondoImagen.getImage(), 0, 0, getWidth(), getHeight(), null);

                    // Dibujar los cohetes
                    g2.drawImage(bola1Imagen.getImage(), estadoJuego.ball1X, 100, 80, 80, null);
                    g2.drawImage(bola2Imagen.getImage(), estadoJuego.ball2X, 250, 80, 80, null);

                    // Dibujar información del jugador
                    g2.setFont(new Font("Commic Sans", Font.BOLD, 25));
                    g2.setColor(Color.WHITE); // Contraste sobre el fondo
                    String jugador = soyJugador1 ? "Jugador 1" : "Jugador 2";
                    g2.drawString(jugador, 10, 30); // Dibuja la información en la esquina superior izquierda

                    g2.dispose();
                    // Dibujar el buffer en la pantalla
                    g.drawImage(buffer, 0, 0, null);

                }

            };

            JButton botonIniciar = new JButton("Iniciar Carrera");
            botonIniciar.addActionListener(e -> {
                clienteThread.enviarConfirmacionInicio(); // Confirmar inicio al servidor
                botonIniciar.setEnabled(false);
            });

            JButton botonVelocidad = new JButton("Más Velocidad!!!");
            botonVelocidad.setEnabled(false); // Habilitar cuando la carrera inicie
            botonVelocidad.addActionListener(e -> {
                // Incrementar velocidad de la bola correspondiente
                if (soyJugador1) {
                    estadoJuego.ball1Speed += 2;
                } else {
                    estadoJuego.ball2Speed += 2;
                }
                clienteThread.enviarEstado(estadoJuego); // Enviar estado actualizado al servidor
            });

            panel.add(botonIniciar);
            panel.add(botonVelocidad);
            frame.add(panel);
            frame.setVisible(true);

            // Iniciar hilo para manejar la comunicación con el servidor
            clienteThread = new ClienteThread(this, botonVelocidad, socket, in, out);
            clienteThread.start();

            // Temporizador para actualizar la animación
            new Timer(30, e -> {
                if (carreraIniciada) {
                    estadoJuego.ball1X += estadoJuego.ball1Speed;
                    estadoJuego.ball2X += estadoJuego.ball2Speed;
                    panel.repaint();

                    // Verificar si alguna bola cruza la línea de meta
                    if (estadoJuego.ball1X >= estadoJuego.finishLine || estadoJuego.ball2X >= estadoJuego.finishLine) {
                        ((Timer) e.getSource()).stop();
                        String ganador = estadoJuego.ball1X >= estadoJuego.finishLine ? "Jugador 1" : "Jugador 2";
                        JOptionPane.showMessageDialog(frame, ganador + " ha ganado!");
                    }
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    public void actualizarEstado(EstadoJuego estado) {
        this.estadoJuego = estado;
        this.carreraIniciada = estado.carreraIniciada;
        panel.repaint();
    }

    public static void main(String[] args) {
        new Cliente(); // Iniciar cliente
    }
}