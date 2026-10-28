import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor aguardando conexões na porta " + PORTA + "...");

            while (true) {
                try (Socket socketCliente = serverSocket.accept()) {
                    System.out.println("Cliente conectado: " + socketCliente.getInetAddress());

                    Aluno aluno = new Aluno("João Silva", "2024001", "Sistemas Distribuídos");

                    ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                    oos.writeObject(aluno);
                    oos.flush();

                    System.out.println("Objeto Aluno enviado com sucesso.");
                } catch (IOException e) {
                    System.err.println("Erro ao atender cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}