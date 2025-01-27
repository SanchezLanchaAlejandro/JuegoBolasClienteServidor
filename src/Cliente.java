import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Cliente {
    private static final String DIRECCION_SERVIDOR = "localhost"; // Dirección del servidor
    private static final int PUERTO = 4444; // Puerto del servidor
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private DatosJuego datosJuego;
    private Bola bola1, bola2;
    private JFrame ventana;
    private PanelCarrera panel;

    public static void main(String[] args) {
        new Cliente().iniciar();
    }

    public void iniciar() {
        try {
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Inicializamos los datos del juego y la interfaz gráfica
            datosJuego = new DatosJuego(500); // Suponiendo que la meta está en la posición 500
            bola1 = new Bola(0, 100, 20, Color.RED, 5, datosJuego.getMeta());
            bola2 = new Bola(0, 200, 20, Color.BLUE, 5, datosJuego.getMeta());

            // Configuración de la ventana y panel
            ventana = new JFrame("Carrera de Bolas");
            panel = new PanelCarrera(bola1, bola2, datosJuego);
            ventana.add(panel);
            ventana.setSize(600, 400);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);

            // Iniciar los hilos de las bolas
            bola1.start();
            bola2.start();

            // Hilo para escuchar el servidor
            new Thread(new EscucharServidor()).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enviar datos al servidor
    public void enviarDatos(DatosJuego datos) {
        try {
            out.writeObject(datos);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hilo para escuchar las actualizaciones del servidor
    private class EscucharServidor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    DatosJuego datos = (DatosJuego) in.readObject();
                    // Actualizar la interfaz gráfica con los nuevos datos
                    datosJuego = datos;
                    panel.repaint();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}