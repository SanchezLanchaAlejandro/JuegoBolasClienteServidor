import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private static final String CONFIRMAR_INICIO = "CONFIRMAR_INICIO";

    public static void main(String[] args) {
        int puerto = 4444;
        CopyOnWriteArrayList<ServidorThread> clientes = new CopyOnWriteArrayList<>();
        boolean[] confirmaciones = new boolean[3]; // Jugadores que confirman estar listos.
        int contadorClientes = 0;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en el puerto " + puerto);

            // Aceptar conexiones de hasta tres clientes
            while (clientes.size() < 3) {
                Socket clienteSocket = serverSocket.accept();

                // Asignar rol al cliente según el orden de entrada
                contadorClientes++;
                boolean soyJugador1 = (contadorClientes == 1);
                boolean soyJugador2 = (contadorClientes == 2);

                // Crear el hilo del cliente
                ServidorThread cliente = new ServidorThread(clienteSocket, clientes, confirmaciones, soyJugador1, soyJugador2);
                clientes.add(cliente);
                cliente.start();
            }

            // Esperar hasta que los tres clientes confirmen estar listos
            while (!confirmaciones[0] || !confirmaciones[1] || !confirmaciones[2]) {
                Thread.sleep(100);
            }

            // Iniciar la carrera y enviar el estado inicial del juego a todos los clientes
            EstadoJuego estadoInicial = new EstadoJuego(700);
            estadoInicial.carreraIniciada = true;
            for (ServidorThread cliente : clientes) {
                cliente.enviarEstado(estadoInicial); // Enviar estado inicial
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
