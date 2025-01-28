import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteThread extends Thread {
    private static final String CONFIRMAR_INICIO = "CONFIRMAR_INICIO";

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Cliente cliente;
    private JButton botonVelocidad;

    public ClienteThread(Cliente cliente, JButton botonVelocidad, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.cliente = cliente;
        this.botonVelocidad = botonVelocidad;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object mensaje = in.readObject(); // Leer mensajes del servidor
                if (CONFIRMAR_INICIO.equals(mensaje)) {
                    cliente.actualizarEstado(new EstadoJuego(700)); // Reiniciar estado del juego
                } else if (mensaje instanceof EstadoJuego) {
                    EstadoJuego estado = (EstadoJuego) mensaje;
                    cliente.actualizarEstado(estado); // Actualizar estado recibido
                    botonVelocidad.setEnabled(estado.carreraIniciada); // Habilitar botón si la carrera comenzó
                }
            }
        } catch (Exception e) {
            System.out.println("Desconectado del servidor.");
        }
    }

    public void enviarConfirmacionInicio() {
        try {
            out.writeObject(CONFIRMAR_INICIO); // Enviar confirmación de inicio al servidor
            out.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar confirmación de inicio.");
        }
    }

    public void enviarEstado(EstadoJuego estado) {
        try {
            out.writeObject(estado); // Enviar estado actualizado al servidor
            out.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar estado.");
        }
    }
}