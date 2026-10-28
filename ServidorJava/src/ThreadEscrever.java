import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class ThreadEscrever implements Runnable {
    private final Socket socket;
    private final BlockingQueue<Optional<Aluno>> filaRespostas;

    public ThreadEscrever(Conexao conexao) {
        this.socket = conexao.cliente();
        this.filaRespostas = conexao.filaRespostas();
    }

    @Override
    public void run() {
        try (ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream())) {
            saida.flush(); // envia o header do stream imediatamente
            while (true) {
                Optional<Aluno> resposta = filaRespostas.take();

                if (resposta.isEmpty()) break;

                saida.writeObject(resposta.get());
                saida.flush();
                saida.reset(); // evita a handle table crescer indefinidamente
            }
        } catch (IOException e) {
            System.out.println("Erro na escrita: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}