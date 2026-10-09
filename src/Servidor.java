import java.io.*;
import java.net.*;

/**
 * Servidor da calculadora remota.
 * Recebe operações no formato "numero operador numero" (ex: 10 + 5),
 * calcula e devolve a resposta em uma linha de texto.
 */
public class Servidor {
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        // O ServerSocket é fechado automaticamente ao sair do try
        try (ServerSocket server = new ServerSocket(PORTA)) {
            System.out.println("Servidor aguardando na porta " + PORTA + "...");

            while (true) {
                // Bloqueia até um cliente se conectar
                Socket cliente = server.accept();
                System.out.println("Cliente conectado: " + cliente.getRemoteSocketAddress());

                // Cada cliente é atendido em uma thread própria (vários clientes ao mesmo tempo)
                new Thread(() -> atender(cliente)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    /**
     * Atende um cliente: lê requisições linha a linha até ele desconectar.
     */
    private static void atender(Socket cliente) {
        // Socket e fluxos são fechados automaticamente ao final do try
        try (cliente;
             BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {

            String msg;
            // readLine() retorna null quando o cliente encerra a conexão
            while ((msg = in.readLine()) != null) {
                System.out.println("Recebido: " + msg);

                // Processa a requisição e envia a resposta ao cliente
                String resposta = calcular(msg);
                out.println(resposta);
                System.out.println("Enviado: " + resposta);
            }
        } catch (IOException e) {
            System.out.println("Erro com cliente: " + e.getMessage());
        }
        System.out.println("Cliente desconectado.");
    }

    /**
     * Processa uma operação recebida como texto e devolve a resposta.
     * Formato esperado: "numero operador numero", separados por espaço.
     * Respostas: "Resultado: valor" ou "Erro: motivo".
     */
    private static String calcular(String msg) {
        // Separa a linha em 3 partes: [número, operador, número]
        String[] partes = msg.trim().split(" ");
        if (partes.length != 3) {
            return "Erro: formato inválido";
        }

        try {
            // Converte o texto das posições 0 e 2 em números
            double num1 = Double.parseDouble(partes[0]);
            double num2 = Double.parseDouble(partes[2]);

            // O operador (posição 1) decide qual conta fazer
            return switch (partes[1]) {
                case "+" -> "Resultado: " + (num1 + num2);
                case "-" -> "Resultado: " + (num1 - num2);
                case "*" -> "Resultado: " + (num1 * num2);
                case "/" -> (num2 == 0) ? "Erro: divisão por zero" : "Resultado: " + (num1 / num2);
                default -> "Erro: operador inválido";
            };
        } catch (NumberFormatException e) {
            // Um dos valores digitados não é um número
            return "Erro: números inválidos";
        }
    }
}