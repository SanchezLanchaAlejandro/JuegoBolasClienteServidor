import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Cliente extends Frame {
    private Bola bola1, bola2;
    private PanelCarrera panel;
    private DatosJuego datosJuego;

    public Cliente() {
        // Inicializamos los datos del juego y las bolas
        datosJuego = new DatosJuego(500);  // Suponiendo que la meta está en la posición 500
        bola1 = new Bola(0, 100, 20, Color.RED, 5, datosJuego);
        bola2 = new Bola(0, 200, 20, Color.BLUE, 5, datosJuego);

        // Configuración del panel y la ventana
        panel = new PanelCarrera(bola1, bola2, datosJuego);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        setSize(600, 400);
        setVisible(true);
        setTitle("Carrera de Bolas");

        // Iniciar los hilos de las bolas
        bola1.start();
        bola2.start();
    }

    public static void main(String[] args) {
        new Cliente();
    }
}