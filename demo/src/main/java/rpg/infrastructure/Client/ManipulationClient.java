package rpg.infrastructure.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import rpg.infrastructure.Constants;

public class ManipulationClient implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private volatile boolean conectado;
    private Thread threadLeitura;
    private volatile boolean aguardandoInput;

    public ManipulationClient() {
        this.scanner = new Scanner(System.in);
        this.conectado = false;
        this.aguardandoInput = false;
    }

    @Override
    public void run() {
        exibirBanner();
        
        try {
            System.out.println("🔌 Conectando ao servidor...");
            startConnection("127.0.0.1", Constants.hostId);
            System.out.println("✓ Conectado com sucesso!\n");
            
            conectado = true;
            
            // Inicia thread para receber mensagens do servidor
            threadLeitura = new Thread(new LeitorMensagens());
            threadLeitura.start();
            
            // Loop principal para enviar comandos
            loopPrincipal();
            
        } catch (UnknownHostException e) {
            System.err.println("✗ Servidor não encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("✗ Erro de conexão: " + e.getMessage());
        } finally {
            stopConnection();
        }
    }

    private void exibirBanner() {
        System.out.println("+------------------------------------------------+");
        System.out.println("|         RPG POS-APOCALIPSE - CLIENTE           |");
        System.out.println("+------------------------------------------------+");
        System.out.println("| Bem-vindo ao mundo apos o Grande Colapso!      |");
        System.out.println("| Voce e um Humanoide programado pelo Dr. Kenji  |");
        System.out.println("| Sua missao: descobrir o que aconteceu...        |");
        System.out.println("+------------------------------------------------+\n");
    }

    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private void loopPrincipal() {
        System.out.println("Dica: Digite 'SAIR', 'EXIT' ou 'QUIT' para desconectar\n");
        
        while (conectado && clientSocket != null && clientSocket.isConnected()) {
            try {
                if (System.in.available() > 0 && scanner.hasNextLine()) {
                    String comando = scanner.nextLine().trim();
                    
                    if (comando.isEmpty()) {
                        continue;
                    }
                    
                    if (comando.equalsIgnoreCase("SAIR") || 
                        comando.equalsIgnoreCase("EXIT") || 
                        comando.equalsIgnoreCase("QUIT")) {
                        System.out.println("\nDesconectando do servidor...");
                        conectado = false;
                        break;
                    }
                    
                    if (comando.equalsIgnoreCase("HELP") || comando.equalsIgnoreCase("AJUDA")) {
                        exibirAjuda();
                        continue;
                    }
                    
                    if (comando.equalsIgnoreCase("STATUS")) {
                        exibirStatus();
                        continue;
                    }
                    
                    if (comando.equalsIgnoreCase("CLEAR") || comando.equalsIgnoreCase("LIMPAR")) {
                        limparTela();
                        continue;
                    }
                    
                    // Envia comando para o servidor
                    sendMessage(comando);
                }
                
                // Pequeno delay para não sobrecarregar a CPU
                Thread.sleep(100);
                
            } catch (IOException e) {
                if (conectado) {
                    System.err.println("\nErro de comunicacao: " + e.getMessage());
                    conectado = false;
                }
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        if (out != null && msg != null && !msg.trim().isEmpty()) {
            out.println(msg);
            out.flush();
        }
    }

    public void stopConnection() {
        conectado = false;
        
        try {
            System.out.println("\nEncerrando conexao...");
            
            if (threadLeitura != null && threadLeitura.isAlive()) {
                threadLeitura.interrupt();
                threadLeitura.join(1000); // Aguarda até 1 segundo
            }
            
            if (scanner != null) {
                scanner.close();
            }
            
            if (out != null) {
                out.close();
            }
            
            if (in != null) {
                in.close();
            }
            
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            
            System.out.println("Desconectado com sucesso.");
            System.out.println("-> Desconectado com sucesso.");
            System.out.println("\n+------------------------------------------------+");
            System.out.println("|           Obrigado por jogar!                  |");
            System.out.println("|           Ate a proxima aventura!              |");
            System.out.println("+------------------------------------------------+");
            
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void exibirAjuda() {
        System.out.println("\n+------------------------------------------------+");
        System.out.println("|             COMANDOS DO CLIENTE                |");
        System.out.println("+------------------------------------------------+");
        System.out.println("| HELP / AJUDA    - Exibe esta mensagem de ajuda");
        System.out.println("| STATUS          - Exibe status da conexao");
        System.out.println("| CLEAR / LIMPAR  - Limpa a tela");
        System.out.println("| SAIR / EXIT     - Desconecta do servidor");
        System.out.println("+------------------------------------------------+\n");
    }

    private void exibirStatus() {
        System.out.println("\n+------------------------------------------------+");
        System.out.println("|              STATUS DA CONEXAO                 |");
        System.out.println("+------------------------------------------------+");
        System.out.println("| Conectado: " + (conectado ? "SIM" : "NAO"));
        
        if (clientSocket != null) {
            System.out.println("| Endereco: " + clientSocket.getInetAddress().getHostAddress());
            System.out.println("| Porta: " + clientSocket.getPort());
            System.out.println("| Socket Ativo: " + (!clientSocket.isClosed() ? "SIM" : "NAO"));
        }
        
        System.out.println("+------------------------------------------------+\n");
    }

    private void limparTela() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                // Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Unix/Linux/Mac
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Se falhar, apenas imprime várias linhas em branco
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
        
        exibirBanner();
    }

    // Thread interna para ler mensagens do servidor
    private class LeitorMensagens implements Runnable {
        @Override
        public void run() {
            try {
                String mensagem;
                
                while (conectado && (mensagem = in.readLine()) != null) {
                    // Tratamento de mensagens especiais do servidor
                    if (mensagem.equals("BEM_VINDO_RPG")) {
                        continue; // Ignora mensagem de controle
                    }
                    
                    if (mensagem.equals("DESCONECTAR")) {
                        System.out.println("\nServidor encerrou a conexão.");
                        conectado = false;
                        break;
                    }
                    
                    if (mensagem.trim().isEmpty()) {
                        continue; // Ignora linhas vazias
                    }
                    
                    // Processa mensagens com formatação especial
                    if (mensagem.startsWith("AGUARDAR_INPUT:")) {
                        aguardandoInput = true;
                        System.out.print(mensagem.substring(15)); // Remove o prefixo
                    } else if (mensagem.startsWith("ERRO:")) {
                        System.err.println("❌ " + mensagem.substring(5));
                    } else if (mensagem.startsWith("AVISO:")) {
                        System.out.println("⚠️  " + mensagem.substring(6));
                    } else if (mensagem.startsWith("SUCESSO:")) {
                        System.out.println("Sucesso: " + mensagem.substring(8));
                    } else {
                        // Exibe a mensagem normal do servidor
                        System.out.println(mensagem);
                    }
                }
                
            } catch (IOException e) {
                if (conectado) {
                    System.err.println("\nConexão perdida com o servidor.");
                    System.err.println("   Motivo: " + e.getMessage());
                    conectado = false;
                }
            }
        }
    }

    // Método main para executar o cliente standalone
    public static void main(String[] args) {
        ManipulationClient cliente = new ManipulationClient();
        
        // Hook para encerramento gracioso
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (cliente.conectado) {
                System.out.println("\n\nInterrupção detectada. Encerrando...");
                cliente.stopConnection();
            }
        }));
        
        // Executa o cliente
        Thread clientThread = new Thread(cliente);
        clientThread.start();
        
        try {
            clientThread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interrompida: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        
        System.exit(0);
    }
}
