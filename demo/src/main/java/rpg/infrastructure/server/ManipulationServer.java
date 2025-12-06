package rpg.infrastructure.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rpg.models.Berserker;
import rpg.models.Fuzileiro;
import rpg.models.Inimigo;
import rpg.models.Item;
import rpg.models.Mago;
import rpg.models.Personagem;

public class ManipulationServer {
	private ServerSocket serverSocket;
	private Map<String, ClientSession> activeSessions;
	private int nextSessionId;

	public ManipulationServer() {
		this.activeSessions = new ConcurrentHashMap<>();
		this.nextSessionId = 1;
	}

	public void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("+------------------------------------------------+");
		System.out.println("|      RPG SERVER POS-APOCALIPSE                 |");
		System.out.println("+------------------------------------------------+");
		System.out.println("Server started on port " + port + ". Waiting for clients...\n");

		while (true) {
			Socket clientSocket = serverSocket.accept();
			String sessionId = "SESSION_" + (nextSessionId++);
			System.out.println("✓ New client connected: " + clientSocket.getInetAddress().getHostAddress());
			System.out.println("  Session ID: " + sessionId);

			Thread clientThread = new Thread(new ClientHandler(clientSocket, sessionId));
			clientThread.start();
		}
	}

	public void stop() throws IOException {
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		activeSessions.clear();
		System.out.println("Server stopped.");
	}

	private class ClientHandler implements Runnable {
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private String sessionId;
		private ClientSession session;

		public ClientHandler(Socket socket, String sessionId) {
			this.clientSocket = socket;
			this.sessionId = sessionId;
		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				session = new ClientSession(sessionId, out, in);
				activeSessions.put(sessionId, session);

				enviar("BEM_VINDO_RPG");
				enviar("+------------------------------------------------+");
				enviar("|      RPG POS-APOCALIPSE - BEM-VINDO!           |");
				enviar("+------------------------------------------------+");

				session.iniciarJogo();

			} catch (IOException e) {
				System.out.println("Error with client " + sessionId + ": " + e.getMessage());
			} finally {
				cleanup();
			}
		}

		private void enviar(String mensagem) {
			if (out != null) {
				out.println(mensagem);
			}
		}

		private void cleanup() {
			try {
				activeSessions.remove(sessionId);
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (clientSocket != null && !clientSocket.isClosed()) {
					clientSocket.close();
				}
				System.out.println("✗ Client disconnected: " + sessionId);
			} catch (IOException e) {
				System.err.println("Error closing resources: " + e.getMessage());
			}
		}
	}

	public static class ClientSession {
		private String sessionId;
		private PrintWriter out;
		private BufferedReader in;
		private Personagem personagem;
		private int progressoHistoria;
		private boolean emCombate;
		private Inimigo inimigoAtual;

		public ClientSession(String sessionId, PrintWriter out, BufferedReader in) {
			this.sessionId = sessionId;
			this.out = out;
			this.in = in;
			this.progressoHistoria = 0;
			this.emCombate = false;
		}

		public void iniciarJogo() throws IOException {
			criarPersonagem();
			iniciarHistoria();
		}

		private void criarPersonagem() throws IOException {
			enviar("\n+--------------------------------------------------------+");
			enviar("|              CRIACAO DE PERSONAGEM                     |");
			enviar("+--------------------------------------------------------+");
			enviar("\nVoce e um Humanoide avancado, programado pelo Dr. Bruno ");
			enviar("antes do Grande Colapso. Sua missao: descobrir o que");
			enviar("aconteceu com a humanidade.\n");

			enviar("Digite seu nome: ");
			String nome = in.readLine();
			if (nome == null || nome.trim().isEmpty()) {
				nome = "Humanoide-" + sessionId.substring(sessionId.length() - 3);
			}

			enviar("\nEscolha sua classe:");
			enviar("1. Berserker - Dual Wield com Furia (150 HP, 20 ATK, 15 DEF)");
			enviar("2. Mago - Alto ataque magico (100 HP, 25 ATK, 8 DEF, 100 MANA)");
			enviar("3. Fuzileiro - Combate a distancia (120 HP, 18 ATK, 10 DEF, 10 MUNICAO)");
			enviar("Escolha (1-3): ");

			String escolha = in.readLine();

			switch (escolha) {
				case "1":
					personagem = new Berserker(nome);
					break;
				case "2":
					personagem = new Mago(nome);
					break;
				case "3":
					personagem = new Fuzileiro(nome);
					break;
				default:
					personagem = new Berserker(nome);
					enviar("Opção inválida! Criando Berserker...");
			}

			personagem.getInventario().adicionarItem(
					new Item("Nanoreparador", "Dispositivo de cura", "cura", 40, 2));
			personagem.getInventario().adicionarItem(
					new Item("Bateria de Energia", "Aumenta poder", "ataque", 5, 2));

			enviar("\nPersonagem criado com sucesso!");
			enviarStatusPersonagem();
		}

		private void iniciarHistoria() throws IOException {
			while (progressoHistoria < 4) {
				switch (progressoHistoria) {
					case 0:
						capitulo1();
						break;
					case 1:
						capitulo2();
						break;
					case 2:
						capitulo3();
						break;
					case 3:
						capituloFinal();
						return;
				}

				if (!personagem.estaVivo()) {
					enviar("\nGAME OVER - Voce foi derrotado...");
					return;
				}
			}
		}

		private void capitulo1() throws IOException {
			enviar("\n+--------------------------------------------------------+");
			enviar("|              CAPITULO 1: O DESPERTAR                   |");
			enviar("+--------------------------------------------------------+");
			enviar("\nSuas engrenagens comecam a rodar e o seu corpo inteiro liga.");
			enviar("Um holograma do Dr. Bruno aparece:");
			enviar("'O virus NeoCLT transformou todos...'");

			menuExploracao();
		}

		private void capitulo2() throws IOException {
			enviar("\n+--------------------------------------------------------+");
			enviar("|      CAPITULO 2: A CARTEIRA DE TRABALHO CORROMPIDA     |");
			enviar("+--------------------------------------------------------+");
			enviar("\nA cidade esta em ruinas...");

			menuExploracao();
		}

		private void capitulo3() throws IOException {
			enviar("\n+--------------------------------------------------------+");
			enviar("|              CAPITULO 3: O LABORATÓRIO                 |");
			enviar("+--------------------------------------------------------+");
			enviar("\nVoce encontra o laboratorio do Dr. Bruno.");

			menuExploracao();
		}

		private void capituloFinal() throws IOException {
			enviar("\n+--------------------------------------------------------+");
			enviar("|            CAPITULO FINAL: A CARTEIRA CLT              |");
			enviar("+--------------------------------------------------------+");

			Inimigo chefeFinal = new Inimigo(
					"CARTEIRA DE TRABALHO",
					"carteira de trabalho",
					200, 30, 20, 5);

			if (iniciarCombate(chefeFinal)) {
				enviar("\nPARABENS! VOCE VENCEU O JOGO!");
				progressoHistoria = 4;
			}
		}

		private void menuExploracao() throws IOException {
			boolean continuar = true;
			int exploracoes = 0;

			while (continuar && personagem.estaVivo()) {
				enviar("\n=== MENU ===");
				enviar("1. Explorar");
				enviar("2. Ver Status");
				enviar("3. Ver Inventario");
				enviar("4. Usar Item");
				enviar("5. Avancar Historia");
				enviar("Escolha: ");

				String escolha = in.readLine();

				if (escolha != null) {
					escolha = escolha.trim();
				}
				if (escolha == null) {
					return;
				}

				switch (escolha) {
					case "1":
						explorar();
						exploracoes++;
						break;
					case "2":
						enviarStatusPersonagem();
						break;
					case "3":
						for (String linha : personagem.getInventario().listarItensLista()) {
							enviar(linha.trim());
						}
						break;
					case "4":
						usarItem();
						break;
					case "5":
						if (exploracoes >= 2) {
							progressoHistoria++;
							continuar = false;
						} else {
							enviar("\nExplore mais! (Minimo: 2)");
						}
						break;
					default:
						enviar("Opcao invalida!");
				}
			}
		}

		private void explorar() throws IOException {
			int evento = (int) (Math.random() * 100);

			if (evento < 50) {
				encontrarInimigo();
			} else if (evento < 80) {
				encontrarItem();
			} else {
				armadilha();
			}
		}

		private void encontrarInimigo() throws IOException {
			String[] profissoes = { "advogado", "médico", "engenheiro", "professor",
					"soldado", "cientista", "policial", "bombeiro", "chef", "mecânico" };
			String profissao = profissoes[(int) (Math.random() * profissoes.length)];

			String[] nomes = { "Marcus", "Sarah", "David", "Elena", "James", "Ana" };
			String nome = nomes[(int) (Math.random() * nomes.length)] + " Corrompido";

			int nivel = personagem.getNivel();
			Inimigo inimigo = new Inimigo(nome, profissao,
					80 + (nivel * 20), 12 + (nivel * 3), 8 + (nivel * 2), nivel);

			enviar("\nENCONTRO HOSTIL!");

			if (iniciarCombate(inimigo)) {
				personagem.setNivel(personagem.getNivel() + 1);
				enviar("\nNivel: " + personagem.getNivel());
			}
		}

		private boolean iniciarCombate(Inimigo inimigo) throws IOException {
			if (personagem instanceof Fuzileiro) {
				((Fuzileiro) personagem).resetarUsosRajada();
			}
			if (personagem instanceof Berserker) {
				((Berserker) personagem).resetarUsosAtaqueDuplo();
			}

			enviar("\n+--------------------------------------------------------+");
			enviar("|                   -- COMBATE --                        |");
			enviar("+--------------------------------------------------------+");
			enviar("Inimigo: " + inimigo.getNome());
			enviar("Profissao: " + inimigo.getProfissaoAnterior());
			enviar("HP: " + inimigo.getPontosVida());

			while (personagem.estaVivo() && inimigo.estaVivo()) {
				enviar("\n+-------------------------------------+");
				enviar("| Seu HP: " + personagem.getPontosVida() + "/" + personagem.getPontosVidaMax());
				enviar("| Inimigo HP: " + inimigo.getPontosVida());
				enviar("+-------------------------------------+");

				exibirMenuCombate();

				String acao = in.readLine();

				if (!processarCombate(acao, inimigo)) {
					return false;
				}

				if (!inimigo.estaVivo())
					break;
				turnoInimigo(inimigo);
			}

			if (personagem.estaVivo()) {
				enviar("\nVITÓRIA!");
				transferirLoot(inimigo);
				return true;
			}
			return false;
		}

		private void exibirMenuCombate() {
			enviar("\n1. Atacar | 2. Habilidade | 3. Item | 4. Fugir");

			if (personagem instanceof Berserker) {
				Berserker berserker = (Berserker) personagem;
				enviar(String.format("   [Furia: %d/%d | Combo: %d/%d]",
						berserker.getFuria(), berserker.getFuriaMax(),
						berserker.getComboAtual(), berserker.getComboMaximo()));
				enviar(String.format("   2. Ataque Duplo (%d/%d)",
						berserker.getUsosAtaqueDuplo(), berserker.getUsosAtaqueDuploMaximo()));
				enviar("   5. Redemoinho | 6. Execucao | 7. Furia | 8. Finalizador | 9. Stats");
			} else if (personagem instanceof Fuzileiro) {
				Fuzileiro fuzileiro = (Fuzileiro) personagem;
				enviar(String.format("   [Municao: %d/%d | Critico: %d%%]",
						fuzileiro.getMunicao(), fuzileiro.getMunicaoMax(),
						fuzileiro.getChanceCritico()));
				enviar("   2. Rajada Precisa | 5. Recarregar | 6. Stats");
			} else if (personagem instanceof Mago) {
				Mago mago = (Mago) personagem;
				enviar(String.format("   [Mana: %d/%d]", mago.getMana(), mago.getManaMax()));
				enviar("   2. Bola de Fogo | 5. Toque dos Fantasmas (30) | 6. Raio Arcano (25)");
			}

			enviar("Acao: ");
		}

		private boolean processarCombate(String acao, Inimigo inimigo) throws IOException {
			if (personagem instanceof Berserker) {
				Berserker berserker = (Berserker) personagem;
				int danoAplicado = 0;
				List<String> logs;

				switch (acao) {
					case "5":
						logs = berserker.redemoinhoMortal(inimigo.getNome(), inimigo.getDefesa());
						if (logs != null && logs.size() > 1) {
							String danoLine = logs.get(logs.size() - 1);
							if (danoLine.startsWith("DANO TOTAL:")) {
								try {
									danoAplicado = Integer.parseInt(danoLine.split(":")[1].trim());
								} catch (NumberFormatException ignored) {
								}
							}
						}
						if (logs != null) {
							for (String log : logs) {
								enviar(log);
							}
							if (danoAplicado > 0) {
								inimigo.receberDano(danoAplicado);
							}
						}
						return true;
					case "6":
						logs = berserker.execucaoCruzada(inimigo.getNome(), inimigo.getDefesa());
						if (logs != null && logs.size() > 1) {
							String danoLine = logs.get(logs.size() - 1);
							if (danoLine.startsWith("DANO TOTAL:")) {
								try {
									danoAplicado = Integer.parseInt(danoLine.split(":")[1].trim());
								} catch (NumberFormatException ignored) {
								}
							}
						}
						if (logs != null) {
							for (String log : logs) {
								enviar(log);
							}
							if (danoAplicado > 0) {
								inimigo.receberDano(danoAplicado);
							}
						}
						return true;
					case "7":
						logs = berserker.furiaDesenfreada(inimigo.getNome(), inimigo.getDefesa());
						if (logs != null) {
							for (String log : logs) {
								enviar(log);
							}
						}
						return true;
					case "8":
						logs = berserker.comboFinalizador(inimigo.getNome(), inimigo.getDefesa());
						if (logs != null && logs.size() > 1) {
							String danoLine = logs.get(logs.size() - 1);
							if (danoLine.startsWith("DANO TOTAL:")) {
								try {
									danoAplicado = Integer.parseInt(danoLine.split(":")[1].trim());
								} catch (NumberFormatException ignored) {
								}
							}
						}
						if (logs != null) {
							for (String log : logs) {
								enviar(log);
							}
							if (danoAplicado > 0) {
								inimigo.receberDano(danoAplicado);
							}
						}
						return true;
					case "9":
						for (String log : berserker.exibirEstatisticasCombate()) {
							enviar(log);
						}
						return true;
				}
			} else if (personagem instanceof Mago) {
				Mago mago = (Mago) personagem;
				int danoAplicado = 0;

				switch (acao) {
					case "5":
						danoAplicado = mago.toqueDosFantasmas();
						if (danoAplicado > 0) {
							enviar("🔮 TOQUE DOS FANTASMAS! Causou " + danoAplicado + " de dano!");
							inimigo.receberDano(danoAplicado);
						} else {
							enviar("⚠️ Mana insuficiente para Toque dos Fantasmas.");
						}
						return true;
					case "6":
						danoAplicado = mago.raioArcano();
						if (danoAplicado > 0) {
							enviar("⚡ RAIO ARCANO! Causou " + danoAplicado + " de dano!");
							inimigo.receberDano(danoAplicado);
						} else {
							enviar("⚠️ Mana insuficiente para Raio Arcano.");
						}
						return true;
					case "2":
						usarHabilidade(inimigo);
						return true;
				}
			}

			if (personagem instanceof Fuzileiro) {
				Fuzileiro fuzileiro = (Fuzileiro) personagem;

				if (acao.equals("5")) {
					List<String> recargaLog = fuzileiro.recarregar();
					for (String log : recargaLog) {
						enviar(log);
					}
					return true;
				} else if (acao.equals("6")) {
					for (String log : fuzileiro.exibirEstatisticasTiro()) {
						enviar(log);
					}
					return true;
				}
			}

			// Ações comuns
			switch (acao) {
				case "1":
					if (personagem instanceof Berserker) {
						Berserker berserker = (Berserker) personagem;
						List<String> ataqueSimplesLogs = berserker.ataqueSimples(
								inimigo.getNome(), inimigo.getDefesa());
						int danoSimples = 0;
						if (ataqueSimplesLogs != null) {
							for (String log : ataqueSimplesLogs) {
								enviar(log);
							}
							String danoLine = ataqueSimplesLogs.get(ataqueSimplesLogs.size() - 1);
							if (danoLine.startsWith("DANO TOTAL:")) {
								try {
									danoSimples = Integer.parseInt(danoLine.split(":")[1].trim());
								} catch (NumberFormatException ignored) {
								}
							}
						}
						if (danoSimples > 0) {
							inimigo.receberDano(danoSimples);
						}
					} else if (personagem instanceof Fuzileiro) {
						Fuzileiro fuzileiro = (Fuzileiro) personagem;
						if (fuzileiro.getMunicao() > 0) {
							Fuzileiro.ResultadoTiro resultado = fuzileiro.tiroDePrecisao(inimigo.getNome());
							if (resultado != null) {
								enviar(resultado.log.toString());
								inimigo.receberDano(resultado.danoFinal);
							}
						} else {
							enviar("Sem municao!");
						}
					} else {
						int dano = personagem.calcularDano();
						int dado = personagem.rolarDado();
						enviar("Ataque: " + personagem.getNome() + " ataca! [Dado: " + dado + "]");
						if (dado > inimigo.getDefesa()) {
							inimigo.receberDano(dano);
							enviar("Acerto! Dano: " + dano);
						} else {
							enviar("O inimigo defendeu!");
						}
					}
					return true;

				case "2":
					usarHabilidade(inimigo);
					return true;

				case "3":
					usarItem();
					return true;

				case "4":
					int dadoFuga = personagem.rolarDado();
					if (dadoFuga >= 15) {
						enviar("Voce conseguiu fugir!");
						return false;
					} else {
						enviar("Falhou em fugir! O inimigo ataca.");
						return true;
					}

				default:
					enviar("Acao invalida!");
					return true;
			}
		}

		private void usarHabilidade(Inimigo inimigo) {
			if (personagem instanceof Berserker) {
				Berserker berserker = (Berserker) personagem;
				List<String> ataqueDuploLogs = berserker.ataqueDuplo(
						inimigo.getNome(), inimigo.getDefesa());

				int danoDuplo = 0;
				if (ataqueDuploLogs != null) {
					for (String log : ataqueDuploLogs) {
						enviar(log);
					}
					String danoLine = ataqueDuploLogs.get(ataqueDuploLogs.size() - 1);
					if (danoLine.startsWith("DANO TOTAL:")) {
						try {
							danoDuplo = Integer.parseInt(danoLine.split(":")[1].trim());
						} catch (NumberFormatException ignored) {
						}
					}
				}

				if (danoDuplo > 0) {
					inimigo.receberDano(danoDuplo);
				}

			} else if (personagem instanceof Mago) {
				int dano = ((Mago) personagem).bolaDeFogo();
				if (dano > 0) {
					inimigo.receberDano(dano);
					enviar("BOLA DE FOGO! " + dano);
				} else {
					enviar("Sem mana para Bola de Fogo!");
				}
			} else if (personagem instanceof Fuzileiro) {
				Fuzileiro fuzileiro = (Fuzileiro) personagem;

				Fuzileiro.ResultadoRajada rajada = fuzileiro.rajadaPrecisa(inimigo.getNome());

				if (rajada.logCompleto != null) {
					for (String log : rajada.logCompleto) {
						enviar(log);
					}
				}

				if (rajada.danoTotal > 0) {
					inimigo.receberDano(rajada.danoTotal);
				}

				if (rajada.tiros != null && !rajada.tiros.isEmpty() && rajada.todosForamCriticos()) {
					enviar("\n RAJADA PERFEITA! Todos os tiros foram criticos!");
				}
			}
		}

		private void turnoInimigo(Inimigo inimigo) {
			enviar("\n>>> Turno do Inimigo <<<");
			int dano = inimigo.calcularDano();
			personagem.receberDano(dano);
			enviar("Voce recebeu " + dano + " de dano!");
		}

		private void transferirLoot(Inimigo inimigo) {
			enviar("\n Coletando itens...");
			for (Item item : inimigo.getInventario().getItens()) {
				personagem.getInventario().adicionarItem(item);
				enviar("Pego: " + item.getNome() + " (x" + item.getQuantidade() + ")");
			}
		}

		private void encontrarItem() {
			Item item = new Item("Nanoreparador", "Cura HP", "cura", 40, 1);
			personagem.getInventario().adicionarItem(item);
			enviar("\nItem encontrado: " + item.getNome());
		}

		private void armadilha() {
			int dano = (int) (Math.random() * 20) + 10;
			personagem.receberDano(dano);
			enviar("\nARMADILHA! -" + dano + " HP");
		}

		private void usarItem() throws IOException {
			if (personagem.getInventario().estaVazio()) {
				enviar("Inventario vazio!");
				return;
			}

			for (String linha : personagem.getInventario().listarItensLista()) {
				enviar(linha.trim());
			}

			enviar("Nome ou numero do item: ");
			String input = in.readLine();

			if (input == null)
				return;
			String nomeItemParaUso = input.trim();
			Item itemParaUso = null;

			try {
				int indice = Integer.parseInt(nomeItemParaUso);

				itemParaUso = personagem.getInventario().buscarItem(indice - 1);

			} catch (NumberFormatException e) {
				itemParaUso = personagem.getInventario().buscarItem(nomeItemParaUso);
			}

			if (itemParaUso == null) {
				enviar("Item nao encontrado!");
				return;
			}

			String nomeRealDoItem = itemParaUso.getNome();

			java.util.List<String> logsUso = personagem.usarItem(nomeRealDoItem);

			if (logsUso != null && !logsUso.isEmpty()) {
				for (String log : logsUso) {
					enviar(log);
				}
			} else {
				enviar("Falha ao usar o item!");
			}
		}

		private void enviarStatusPersonagem() {
			enviar("\n=== STATUS ===");
			enviar("Nome: " + personagem.getNome());
			enviar("Classe: " + personagem.getClasse());
			enviar("Nivel: " + personagem.getNivel());
			enviar("HP: " + personagem.getPontosVida() + "/" + personagem.getPontosVidaMax());
			enviar("Ataque: " + personagem.getAtaque());
			enviar("Defesa: " + personagem.getDefesa());

			if (personagem instanceof Mago) {
				Mago mago = (Mago) personagem;
				enviar("Mana: " + mago.getMana() + "/" + mago.getManaMax());
			} else if (personagem instanceof Fuzileiro) {
				Fuzileiro fuzileiro = (Fuzileiro) personagem;
				enviar("Municao: " + fuzileiro.getMunicao() + "/" + fuzileiro.getMunicaoMax());
				enviar("Critico: " + fuzileiro.getChanceCritico() + "%");
			} else if (personagem instanceof Berserker) {
				Berserker berserker = (Berserker) personagem;
				enviar("Furia: " + berserker.getFuria() + "/" + berserker.getFuriaMax());
				enviar("Combo: " + berserker.getComboAtual() + "/" + berserker.getComboMaximo());
			}
			enviar("==============");
		}

		private void enviar(String mensagem) {
			if (out != null) {
				out.println(mensagem);
			}
		}
	}
}