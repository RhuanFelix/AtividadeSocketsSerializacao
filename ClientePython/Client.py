import socket
import struct
import io

import javaobj.v1 as javaobj


# Cabeçalho do ObjectOutputStream Java (STREAM_MAGIC + STREAM_VERSION).
JAVA_STREAM_HEADER = b'\xac\xed\x00\x05'
TC_STRING = 0x74
TC_LONGSTRING = 0x7C


class Client:
    def __init__(self, host, porta):
        self.host = host
        self.porta = porta
        self.socket = None
        self._header_enviado = False

    def conectar(self):
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((self.host, self.porta))
            print("O cliente se conectou ao servidor!")
        except ConnectionRefusedError:
            print("Erro ao conectar: Conexão recusada.")
            self.socket = None
        except Exception as e:
            print(f"Erro inesperado ao conectar: {e}")
            self.socket = None

    def enviar_string(self, mensagem):
        if not self.esta_conectado():
            print("Cliente não está conectado. Chame conectar() primeiro.")
            return False
        try:
            dados = b''
            if not self._header_enviado:
                dados += JAVA_STREAM_HEADER
                self._header_enviado = True

            encoded = mensagem.encode('utf-8')
            if len(encoded) <= 0xFFFF:
                dados += bytes([TC_STRING]) + struct.pack('>H', len(encoded)) + encoded
            else:
                dados += bytes([TC_LONGSTRING]) + struct.pack('>Q', len(encoded)) + encoded

            self.socket.sendall(dados)
            return True
        except (ConnectionResetError, BrokenPipeError):
            print("Erro ao enviar: O servidor encerrou a conexão.")
            return False
        except Exception as e:
            print(f"Erro ao enviar: {e}")
            return False

    def receber_objeto(self):
        if not self.esta_conectado():
            return None
        dados = b""
        while True:
            try:
                parte = self.socket.recv(4096)
            except Exception as e:
                print(f"Erro no recv: {e}")
                return None
            if not parte:
                print("O servidor encerrou a sessão (socket fechado).")
                return None
            dados += parte
            print(f"[debug] recebido {len(parte)} bytes (total {len(dados)})")
            try:
                # ------------------------------------------------------------------
                # ALTERAÇÃO: uso de javaobj.v1 + io.BytesIO. BytesIO é seekable,
                # então o erro "seek" original não acontece. Se faltarem bytes,
                # o parser lança exceção e continuamos lendo. Quando a
                # desserialização tem sucesso, retornamos o objeto.
                # ------------------------------------------------------------------
                unm = javaobj.JavaObjectUnmarshaller(io.BytesIO(dados))
                obj = unm.readObject()
                print(f"[debug] objeto desserializado com sucesso: {type(obj).__name__}")
                return obj
            except Exception as e:
                print(f"[debug] load falhou ({type(e).__name__}: {e}); "
                      f"{len(dados)} bytes — continuando a ler...")
                continue

    def fechar(self):
        if self.socket:
            try:
                self.socket.close()
            except Exception:
                pass
            self.socket = None
            print("Conexão encerrada.")

    def esta_conectado(self):
        return self.socket is not None