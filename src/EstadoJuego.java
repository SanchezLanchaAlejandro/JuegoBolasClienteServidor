import java.io.Serializable;

public class EstadoJuego implements Serializable {
    public int ball1X;
    public int ball2X;
    public int ball3X;
    public int finishLine;
    public int ball1Speed;
    public int ball2Speed;
    public int ball3Speed;
    public boolean carreraIniciada;

    public EstadoJuego(int finishLine) {
        this.ball1X = 0;
        this.ball2X = 0;
        this.ball3X = 0;
        this.finishLine = finishLine;
        this.ball1Speed = 1;
        this.ball2Speed = 1;
        this.ball3Speed = 1;
        this.carreraIniciada = false;
    }
}