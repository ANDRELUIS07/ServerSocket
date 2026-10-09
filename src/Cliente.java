import java.io.*;
import java.net.*;

/**
 * Cliente da calculadora remota.
 * Lê uma operação do teclado (ex: 10 + 5), envia ao servidor
 * e mostra a resposta recebida. Digite "sair" para encerrar.
 */
public class Cliente {
    private static final String HOST = "localhost"; // trocar pelo IP do servidor se for outra máquina
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        // Socket e fluxos são fechados automaticamente ao sair do try
        try (Socket socket = new Socket(HOST, PORTA);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado ao servidor " + HOST + ":" + PORTA);
            System.out.println("Digite uma operação no formato: numero operador numero (ex: 10 + 5)");
            System.out.println("Operadores: + - * /   |   Digite \"sair\" para encerrar.");

            String linha;
            // Repete até o usuário digitar "sair"
            while ((linha = teclado.readLine()) != null && !linha.equals("sair")) {
                // Envia a requisição ao servidor
                out.println(linha);

                // Aguarda a resposta; null significa que o servidor encerrou a conexão
                String resposta = in.readLine();
                if (resposta == null) {
                    System.out.println("Servidor encerrou a conexão.");
                    break;
                }
                System.out.println(resposta);
            }
            System.out.println("Conexão encerrada.");

        } catch (ConnectException e) {
            // Subclasse de IOException, por isso vem antes dela
            System.out.println("Não foi possível conectar: servidor desligado ou porta errada.");
        } catch (IOException e) {
            System.out.println("Erro de comunicação: " + e.getMessage());
        }
    }
}