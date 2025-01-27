import java.awt.*;
import java.io.*;
import java.net.*;

public class Bola extends Thread {
    private int x, y, tamaño, velocidad;
    private Color color;
    private DatosJuego datosJuego;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado = false;

    public Bola(int x, int y, int tamaño, Color color, int velocidad, DatosJuego datosJuego) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.color = color;
        this.velocidad = velocidad;
        this.datosJuego = datosJuego;
    }

    @Override
    public void run() {
        try {
            // Establecer la conexión con el servidor
            socket = new Socket("localhost", 4444);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;

            // Enviar datos al servidor (estado inicial)
            out.writeObject(datosJuego);
            out.flush();

            while (conectado) {
                // Simular el movimiento de la bola
                x += velocidad;
                if (x > datosJuego.getMeta()) {
                    datosJuego.setGanador("Bola " + color);
                    break;
                }

                // Enviar la nueva posición al servidor (se debe enviar la nueva posición de cada bola)
                out.writeObject(new DatosJuego(x, y));  // Enviar solo posiciones
                out.flush();

                // Dormir el hilo para simular el movimiento
                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Getters para la posición y el tamaño
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTamaño() { return tamaño; }
    public Color getColor() { return color; }

    public void desconectar() {
        try {
            conectado = false;
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}