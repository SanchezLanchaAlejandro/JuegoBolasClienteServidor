import javax.swing.*;
import java.awt.*;

public class PanelCarrera extends JPanel {
    private Bola bola1, bola2;
    private DatosJuego datosJuego;

    public PanelCarrera(Bola bola1, Bola bola2, DatosJuego datosJuego) {
        this.bola1 = bola1;
        this.bola2 = bola2;
        this.datosJuego = datosJuego;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja la primera bola
        g.setColor(bola1.getColor());
        g.fillOval(bola1.getX(), bola1.getY(), bola1.getTamaño(), bola1.getTamaño());

        // Dibuja la segunda bola
        if (bola2 != null) {
            g.setColor(bola2.getColor());
            g.fillOval(bola2.getX(), bola2.getY(), bola2.getTamaño(), bola2.getTamaño());
        }

        // Dibuja la línea de la meta
        g.setColor(Color.BLACK);
        g.drawLine(datosJuego.getMeta(), 0, datosJuego.getMeta(), getHeight()); // Meta

        // Mostrar ganador si el juego terminó
        if (datosJuego.isJuegoTerminado()) {
            g.drawString("Ganador: " + datosJuego.getGanador(), getWidth() / 2, getHeight() / 2);
        }
    }
}