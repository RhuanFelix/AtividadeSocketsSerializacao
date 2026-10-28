import java.util.Scanner;

public class ClienteService {
    private final MenuService menu;

    public ClienteService() {
        menu = new MenuService();
    }

    public void iniciar() {
        ClientJava client = new ClientJava("127.0.0.1", 12345);
        client.conectar();
        if (!client.estaConectado()) return;

        menu.exibirMenu();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n> ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equals("0")) {
                client.enviar("0");
                break;
            }
            if (entrada.isEmpty()) continue;

            if (!entrada.equals("1")) {
                System.out.println("Comando inválido. Use 1 para pedir o Aluno ou 0 para sair.");
                continue;
            }

            if (!client.enviar("1")) break;

            Object resposta = client.receber();
            if (resposta == null) break;

            if (resposta instanceof Aluno aluno) {
                System.out.println("[Servidor] Aluno recebido:");
                System.out.println(aluno);
            } else {
                System.out.println("[Servidor] Objeto inesperado: " + resposta);
            }
        }

        client.fechar();
        scanner.close();
    }
}