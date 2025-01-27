// Clase Servidor
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PUERTO = 4444;
    private static List<ClienteHandler> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado, esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());
                ClienteHandler clienteHandler = new ClienteHandler(socket);
                clientes.add(clienteHandler);
                new Thread(clienteHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Esta clase maneja la comunicación con cada cliente
    private static class ClienteHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private DatosJuego datosJuego;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
            this.datosJuego = new DatosJuego(500); // Meta en 500, por ejemplo
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Enviar el estado del juego al cliente
                enviarDatos(datosJuego);

                while (true) {
                    // Recibir datos del cliente (posiciones de las bolas)
                    DatosJuego datos = (DatosJuego) in.readObject();
                    // Actualizar el estado del juego aquí

                    // Enviar actualización a todos los clientes
                    for (ClienteHandler cliente : clientes) {
                        cliente.enviarDatos(datos);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void enviarDatos(DatosJuego datos) {
            try {
                out.writeObject(datos);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}