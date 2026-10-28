import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientJava {
    private final String host;
    private final int porta;
    private Socket socket;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;

    public ClientJava(String host, int porta) {
        this.host = host;
        this.porta = porta;
    }

    public void conectar() {
        try {
            socket = new Socket(host, porta);

            // 1) Abre o OOS e força o flush do header antes de criar o OIS.
            //    Sem esse flush, o OIS do servidor pode ficar bloqueado para sempre.
            saida = new ObjectOutputStream(socket.getOutputStream());
            saida.flush();

            // 2) Só então abre o OIS (vai bloquear lendo o header do servidor).
            entrada = new ObjectInputStream(socket.getInputStream());

            System.out.println("O cliente se conectou ao servidor!");
        } catch (IOException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            fechar();
        }
    }

    public boolean enviar(Object mensagem) {
        if (!estaConectado()) {
            System.out.println("Cliente não está conectado. Chame conectar() primeiro.");
            return false;
        }
        try {
            saida.writeObject(mensagem);
            saida.flush();
            saida.reset();
            return true;
        } catch (IOException e) {
            System.out.println("Erro ao enviar: " + e.getMessage());
            return false;
        }
    }

    public Object receber() {
        if (!estaConectado()) return null;
        try {
            return entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao receber: " + e.getMessage());
            return null;
        }
    }

    public void fechar() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        socket = null;
        System.out.println("Conexão encerrada.");
    }

    public boolean estaConectado() {
        return socket != null && !socket.isClosed();
    }
}