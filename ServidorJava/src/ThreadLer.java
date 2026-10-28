import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class ThreadLer implements Runnable {
    private final Socket socket;
    private final BlockingQueue<Optional<Aluno>> filaRespostas;
    private final ProcessadorComando processadorComando;

    public ThreadLer(Conexao conexao) {
        this.socket = conexao.cliente();
        this.filaRespostas = conexao.filaRespostas();
        this.processadorComando = new ProcessadorComando();
    }

    @Override
    public void run() {
        boolean fimEnviado = false;
        try (ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                Object mensagem = entrada.readObject();
                System.out.println("Recebido do cliente: " + mensagem);

                if (mensagem instanceof String s && s.equalsIgnoreCase("0")) {
                    filaRespostas.put(Optional.empty());
                    fimEnviado = true;
                    break;
                }

                Aluno aluno = processadorComando.processarComando();
                filaRespostas.put(Optional.of(aluno));
            }
        } catch (EOFException e) {
            System.out.println("Cliente desconectou.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro na leitura: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (!fimEnviado) {
                filaRespostas.clear();
                filaRespostas.offer(Optional.empty());
            }
        }
    }
}