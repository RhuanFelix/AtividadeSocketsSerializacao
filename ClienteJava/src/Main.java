import java.io.*;
import java.net.*;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORTA)) {
            System.out.println("Conectado ao servidor.");

            // Lê o objeto serializado do fluxo de entrada
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Aluno aluno = (Aluno) ois.readObject();

            // Exibe os dados no terminal
            System.out.println("=== Aluno recebido (Cliente Java) ===");
            System.out.println("Nome:      " + aluno.getNome());
            System.out.println("Matrícula: " + aluno.getMatricula());
            System.out.println("Curso:     " + aluno.getCurso());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro no cliente Java: " + e.getMessage());
        }
    }
}