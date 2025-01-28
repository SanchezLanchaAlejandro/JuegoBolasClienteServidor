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
    private String nombreJugador;
    private String nombrePantalla;
    private boolean carreraIniciada = false;
    private boolean soyJugador1;
    private boolean soyJugador2;


    // Imágenes
    private ImageIcon fondoImagen;
    private ImageIcon bola1Imagen;
    private ImageIcon bola2Imagen;
    private ImageIcon bola3Imagen;
    private BufferedImage buffer;

    public Cliente() {
        estadoJuego = new EstadoJuego(700); // Línea de meta en la posición 700

        nombreJugador = JOptionPane.showInputDialog("Por favor, ingresa tu nombre:");

        try {
            // Cargar imágenes
            fondoImagen = new ImageIcon("res/imagen_fondo.jpg");
            bola1Imagen = new ImageIcon("res/balon1.png");
            bola2Imagen = new ImageIcon("res/balon2.png");
            bola3Imagen = new ImageIcon("res/balon3.png");

            // Conexión al servidor
            Socket socket = new Socket("127.0.0.1", 4444);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            soyJugador1 = in.readBoolean();
            soyJugador2 = in.readBoolean();

            // Configurar la interfaz gráfica
            frame = new JFrame("Juego de Carreras");
            frame.setSize(800, 600);
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

                    // Dibujar las bolas
                    g2.drawImage(bola1Imagen.getImage(), estadoJuego.ball1X, 100, 80, 80, null);
                    g2.drawImage(bola2Imagen.getImage(), estadoJuego.ball2X, 250, 80, 80, null);
                    g2.drawImage(bola3Imagen.getImage(), estadoJuego.ball3X, 400, 80, 80, null);

                    // Dibujar información del jugador
                    g2.setFont(new Font("Commic Sans", Font.BOLD, 25));
                    g2.setColor(Color.WHITE);

                    if (soyJugador1){
                        nombrePantalla = "J1: " + nombreJugador;
                    }
                    else if (soyJugador2){
                        nombrePantalla = "J2: " + nombreJugador;
                    }else {
                        nombrePantalla = "J3: " + nombreJugador;
                    }

                    g2.drawString(nombrePantalla, 10, 30); // Dibuja la información en la esquina superior izquierda

                    g2.dispose();
                    // Dibujar el buffer en la pantalla
                    g.drawImage(buffer, 0, 0, null);

                }

            };

            JButton botonIniciar = new JButton("Iniciar Carrera");
            botonIniciar.addActionListener(e -> {
                clienteThread.enviarConfirmacionInicio(); // Enviar confirmación de inicio al servidor
                botonIniciar.setEnabled(false);
            });

            JButton botonVelocidad = new JButton("Más Velocidad!!!");
            botonVelocidad.setEnabled(false); // Desactivar botón inicialmente
            botonVelocidad.addActionListener(e -> {
                // Incrementar velocidad del balon correspondiente
                if (soyJugador1) {
                    estadoJuego.ball1Speed += 2;
                } else if (soyJugador2) {
                    estadoJuego.ball2Speed += 2;
                }else {
                    estadoJuego.ball3Speed += 2;
                }
                clienteThread.enviarEstado(estadoJuego); // Enviar estado actualizado al servidor
            });

            panel.add(botonIniciar);
            panel.add(botonVelocidad);
            frame.add(panel);
            frame.setVisible(true);

            // Crear e iniciar el hilo del cliente
            clienteThread = new ClienteThread(this, botonVelocidad, socket, in, out);
            clienteThread.start();

            // Iniciar la carrera
            new Timer(30, e -> {
                if (carreraIniciada) {
                    estadoJuego.ball1X += estadoJuego.ball1Speed;
                    estadoJuego.ball2X += estadoJuego.ball2Speed;
                    estadoJuego.ball3X += estadoJuego.ball3Speed;

                    panel.repaint();

                    // Verificar si alguna bola cruza la línea de meta
                    if (estadoJuego.ball1X >= estadoJuego.finishLine ||
                            estadoJuego.ball2X >= estadoJuego.finishLine ||
                            estadoJuego.ball3X >= estadoJuego.finishLine) {

                        ((Timer) e.getSource()).stop();

                        String ganador;
                        if (estadoJuego.ball1X >= estadoJuego.finishLine) {
                            ganador = "🏆 El jugador 1";
                        } else if (estadoJuego.ball2X >= estadoJuego.finishLine) {
                            ganador = "🏆 El jugador 2";
                        } else {
                            ganador = "🏆 El jugador 3";
                        }

                        JOptionPane.showMessageDialog(frame, ganador + " ha ganado!! 🥳🏁");
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
        new Cliente();
    }
}