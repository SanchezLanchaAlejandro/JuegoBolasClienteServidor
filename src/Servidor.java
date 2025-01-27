import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PUERTO = 4444; // Puerto del servidor
    private static Set<ClientHandler> clientes = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Servidor iniciado. Esperando conexiones...");
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Aceptar conexión
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Crear un manejador para el cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientes.add(clientHandler); // Agregar cliente al conjunto
                new Thread(clientHandler).start(); // Iniciar hilo para el cliente
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    // Enviar un mensaje a todos los clientes conectados
    public static void broadcast(DatosJuego datos) {
        synchronized (clientes) {
            for (ClientHandler cliente : clientes) {
                cliente.enviarDatos(datos);
            }
        }
    }

    // Clase para manejar cada cliente
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Configurar flujos de entrada y salida
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Escuchar datos del cliente
                while (true) {
                    DatosJuego datos = (DatosJuego) in.readObject();
                    System.out.println("Datos recibidos del cliente: " + datos);

                    // Reenviar datos a todos los clientes
                    Servidor.broadcast(datos);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Cliente desconectado: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientes.remove(this); // Eliminar cliente del conjunto
            }
        }

        public void enviarDatos(DatosJuego datos) {
            try {
                out.writeObject(datos);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error al enviar datos al cliente: " + e.getMessage());
            }
        }
    }
}