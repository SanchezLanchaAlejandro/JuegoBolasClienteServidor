import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private static final String CONFIRMAR_INICIO = "CONFIRMAR_INICIO";

    public static void main(String[] args) {
        int puerto = 4444;
        CopyOnWriteArrayList<ServidorThread> clientes = new CopyOnWriteArrayList<>();
        boolean[] confirmaciones = new boolean[3]; // Almacena si los dos jugadores han confirmado estar listos.
        int contadorClientes = 0; // Contador de clientes conectados,
        // para asignar roles a los clientes en función del orden de conexión (Jugador 1 o Jugador 2).

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en el puerto " + puerto);

            // Aceptar conexiones de hasta tres clientes
            while (clientes.size() < 3) {
                Socket clienteSocket = serverSocket.accept();

                // Asignar rol al cliente según el orden de entrada
                contadorClientes++;
                boolean soyJugador1 = (contadorClientes == 1); // El primer cliente es Jugador 1
                boolean soyJugador2 = (contadorClientes == 2); // El segundo cliente es Jugador 2

                // Crear el hilo del cliente
                ServidorThread cliente = new ServidorThread(clienteSocket, clientes, confirmaciones, soyJugador1, soyJugador2);
                clientes.add(cliente); // Añadir cliente a la lista compartida
                cliente.start(); // Iniciar el hilo para manejar la comunicación con el cliente
            }

            // Esperar hasta que ambos clientes confirmen estar listos
            while (!confirmaciones[0] || !confirmaciones[1] || !confirmaciones[2]) {
                Thread.sleep(100);
            }

            // Iniciar la carrera y enviar el estado inicial del juego a ambos clientes
            EstadoJuego estadoInicial = new EstadoJuego(700); // Línea de meta en la posición 700
            estadoInicial.carreraIniciada = true;
            for (ServidorThread cliente : clientes) {
                cliente.enviarEstado(estadoInicial); // Enviar estado inicial a cada cliente
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
