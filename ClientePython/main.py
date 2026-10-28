import socket
import io
import javaobj.v2 as javaobj  # implementação v2, conforme solicitado

HOST = 'localhost'
PORTA = 12345

def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((HOST, PORTA))
        print("Conectado ao servidor.")

        # Lê todos os bytes até o servidor fechar a conexão
        buffer = b''
        while True:
            dados = sock.recv(4096)
            if not dados:
                break
            buffer += dados

        # Desserializa o objeto Java a partir dos bytes recebidos
        # A v2 fornece a função load(), que espera um arquivo-like object
        aluno = javaobj.load(io.BytesIO(buffer))

        print("\n=== Aluno recebido (Cliente Python) ===")
        print(f"Nome:      {aluno.nome}")
        print(f"Matrícula: {aluno.matricula}")
        print(f"Curso:     {aluno.curso}")

        # Opcional: inspecionar a estrutura completa
        # print("\nDump do objeto:")
        # aluno.dump()

if __name__ == '__main__':
    main()