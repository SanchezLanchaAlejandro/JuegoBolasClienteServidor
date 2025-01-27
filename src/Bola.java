import java.awt.Color;

public class Bola extends Thread {
    private int x; // Posición X de la bola
    private final int y; // Posición Y de la bola
    private final int tamaño; // Tamaño de la bola
    private final Color color; // Color de la bola
    private int velocidad; // Velocidad de la bola
    private boolean corriendo = true; // Control del movimiento
    private final int meta; // Coordenada de la meta

    public Bola(int x, int y, int tamaño, Color color, int velocidad, int meta) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.color = color;
        this.velocidad = velocidad;
        this.meta = meta;
    }

    @Override
    public void run() {
        while (corriendo && x < meta) { // Se mueve mientras no alcance la meta
            x += velocidad; // Mover la bola según su velocidad
            try {
                Thread.sleep(20); // Pausa para simular el movimiento
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        corriendo = false; // Detener la bola al llegar a la meta
    }

    // Métodos para controlar la bola
    public void detener() {
        corriendo = false;
    }

    public void ajustarVelocidad(int nuevaVelocidad) {
        this.velocidad = nuevaVelocidad; // Cambia la velocidad dinámicamente
    }

    // Métodos para obtener los datos de la bola
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamaño() {
        return tamaño;
    }

    public Color getColor() {
        return color;
    }

    public boolean isCorriendo() {
        return corriendo;
    }
}