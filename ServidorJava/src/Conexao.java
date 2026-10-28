import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public record Conexao(
        Socket cliente,
        BlockingQueue<Optional<Aluno>> filaRespostas
) {
    public Conexao(Socket cliente) {
        this(cliente, new ArrayBlockingQueue<>(10));
    }
}