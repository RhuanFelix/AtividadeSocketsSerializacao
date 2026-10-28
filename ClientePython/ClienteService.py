from Client import Client
from MenuService import exibir_menu


def iniciar_cliente():
    client = Client("127.0.0.1", 12345)
    client.conectar()

    if not client.esta_conectado():
        return

    exibir_menu()

    while True:
        entrada = input("\n> ").strip()

        if entrada == "0":
            client.enviar_string("0")
            break

        if not entrada:
            continue

        if entrada != "1":
            print("Comando inválido. Use 1 para pedir o Aluno ou 0 para sair.")
            continue

        if not client.enviar_string("1"):
            break

        aluno = client.receber_objeto()
        if aluno is None:
            break

        _exibir_aluno(aluno)

    client.fechar()


def _exibir_aluno(aluno):
    """
    Exibe campos do objeto desserializado. O javaobj v2 pode expor os
    campos como atributos (.nome) ou via get_field('nome'), dependendo
    da versão do pacote instalada — cobrimos os dois casos.
    """
    print("\n[SERVIDOR] Objeto Aluno recebido e desserializado:")
    print("----------------------------------------")
    try:
        print(f"Nome:      {aluno.nome}")
        print(f"Matrícula: {aluno.matricula}")
        print(f"Curso:     {aluno.curso}")
    except AttributeError:
        try:
            print(f"Nome:      {aluno.get_field('nome')}")
            print(f"Matrícula: {aluno.get_field('matricula')}")
            print(f"Curso:     {aluno.get_field('curso')}")
        except Exception as e:
            print(f"[SERVIDOR] Objeto recebido, mas não foi possível ler os campos: {e}")
            print(f"[SERVIDOR] Objeto bruto: {aluno}")
    print("----------------------------------------")