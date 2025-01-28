import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorThread extends Thread {
    private static final String CONFIRMAR_INICIO = "CONFIRMAR_INICIO";

    private Socket socket; // Conexión con el cliente
    private ObjectOutputStream out; // Para enviar objetos al cliente
    private ObjectInputStream in; // Para recibir objetos del cliente
    private CopyOnWriteArrayList<ServidorThread> clientes; // Lista compartida de clientes
    private boolean[] confirmaciones; // Array que indica si los clientes están listos
    private boolean soyJugador1; // Indica si este cliente es el Jugador 1
    private boolean soyJugador2; // Indica si este cliente es el jugador 2

    public ServidorThread(Socket socket, CopyOnWriteArrayList<ServidorThread> clientes, boolean[] confirmaciones, boolean soyJugador1, boolean soyJugador2) throws IOException {
        this.socket = socket;
        this.clientes = clientes;
        this.confirmaciones = confirmaciones;
        this.soyJugador1 = soyJugador1;
        this.soyJugador2 = soyJugador2;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {

            // Enviar confirmación de inicio al cliente
            out.writeBoolean(soyJugador1);
            out.writeBoolean(soyJugador2);
            out.flush();

            // Escuchar mensajes del cliente
            while (true) {
                Object mensaje = in.readObject();
                if (CONFIRMAR_INICIO.equals(mensaje)) {
                    int indice = clientes.indexOf(this);
                    confirmaciones[indice] = true; // Cliente ha confirmado estar listo
                } else if (mensaje instanceof EstadoJuego) {
                    EstadoJuego estado = (EstadoJuego) mensaje;
                    for (ServidorThread cliente : clientes) {
                        cliente.enviarEstado(estado); // Enviar estado actualizado a todos los clientes
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado.");
        }
    }



    public void enviarEstado(EstadoJuego estado) {
        try {
            out.writeObject(estado); // Enviar objeto de estado al cliente
            out.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar estado al cliente.");
        }
    }
}