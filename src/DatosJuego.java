import java.io.Serializable;

public class DatosJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private int posicionBola1; // Posición de la bola 1
    private int posicionBola2; // Posición de la bola 2
    private int velocidadBola1; // Velocidad de la bola 1
    private int velocidadBola2; // Velocidad de la bola 2
    private int meta; // Coordenada de la meta
    private boolean juegoTerminado; // Indica si el juego terminó
    private String ganador; // Nombre del ganador ("Jugador 1" o "Jugador 2")

    // Constructor original con meta
    public DatosJuego(int meta) {
        this.meta = meta;
        this.posicionBola1 = 0;
        this.posicionBola2 = 0;
        this.velocidadBola1 = 5;
        this.velocidadBola2 = 5;
        this.juegoTerminado = false;
        this.ganador = null;
    }

    // Constructor adicional para actualizar solo las posiciones
    public DatosJuego(int x, int y) {
        this.posicionBola1 = x;
        this.posicionBola2 = y;
        this.velocidadBola1 = 5; // O cualquier valor por defecto
        this.velocidadBola2 = 5; // O cualquier valor por defecto
    }

    // Getters y setters...
    public int getPosicionBola1() {
        return posicionBola1;
    }

    public void setPosicionBola1(int posicionBola1) {
        this.posicionBola1 = posicionBola1;
    }

    public int getPosicionBola2() {
        return posicionBola2;
    }

    public void setPosicionBola2(int posicionBola2) {
        this.posicionBola2 = posicionBola2;
    }

    public int getVelocidadBola1() {
        return velocidadBola1;
    }

    public void setVelocidadBola1(int velocidadBola1) {
        this.velocidadBola1 = velocidadBola1;
    }

    public int getVelocidadBola2() {
        return velocidadBola2;
    }

    public void setVelocidadBola2(int velocidadBola2) {
        this.velocidadBola2 = velocidadBola2;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public void setJuegoTerminado(boolean juegoTerminado) {
        this.juegoTerminado = juegoTerminado;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }
}