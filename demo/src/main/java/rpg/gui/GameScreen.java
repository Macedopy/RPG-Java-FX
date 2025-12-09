package rpg.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import rpg.models.Personagem;
import rpg.models.Inimigo;
import rpg.models.Item;
import rpg.models.Berserker;
import rpg.models.Fuzileiro;
import rpg.models.Mago;
import java.util.List;
import java.util.ArrayList;

public class GameScreen {
    private Stage stage;
    private Personagem jogador;
    private String currentChapter = "Capítulo 1 - O Despertar";
    private String currentLocation = "Entrada da Floresta";
    private TextArea logArea;
    private BorderPane root;
    private Label chapterLabel;
    private Label locationLabel;
    private VBox playerInfoPanel;
    private VBox enemyInfoPanel;
    private Inimigo inimigoAtual;
    private boolean emCombate = false;
    private int progressoHistoria = 0;
    private int exploracoes = 0;
    private int inimigosDerrotadosNoCapitulo = 0;
    private List<Button> attackButtons = new ArrayList<>();
    private javafx.scene.Node savedCenter;

    public GameScreen(Stage stage, Personagem jogador) {
        this.stage = stage;
        this.jogador = jogador;
    }

    public void show() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        // Top: Chapter and location info
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        chapterLabel = new Label(currentChapter);
        chapterLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        locationLabel = new Label("Localização: " + currentLocation);
        locationLabel.setStyle("-fx-font-size: 16px;");

        topBox.getChildren().addAll(chapterLabel, locationLabel);
        root.setTop(topBox);

        // Initialize info panels
        initializeInfoPanels();

        // Center: Game log (will be replaced with split layout during combat)
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(10);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-width: 1;");
        logArea.setText("Bem-vindo à Floresta Sombria!\n" +
                       "Suas engrenagens começam a rodar e o seu corpo inteiro liga.\n" +
                       "Um holograma do Dr. Bruno aparece:\n" +
                       "'O vírus NeoCLT transformou todos...'\n\n");
        root.setCenter(logArea);

        // Bottom: Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button walkButton = new Button("Explorar");
        walkButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        walkButton.setOnAction(e -> explorar());

        // Add attack buttons based on class
        addAttackButtons(buttonBox);

        Button itemButton = new Button("Usar Item");
        itemButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        itemButton.setOnAction(e -> usarItem());

        Button inventoryButton = new Button("Inventário");
        inventoryButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        inventoryButton.setOnAction(e -> mostrarInventario());

        Button statsButton = new Button("Status");
        statsButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        statsButton.setOnAction(e -> mostrarStatus());

        Button advanceButton = new Button("Avançar");
        advanceButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        advanceButton.setOnAction(e -> avancarHistoria());

        Button backButton = new Button("Voltar");
        backButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        backButton.setOnAction(e -> {
            CharacterSelection charSelect = new CharacterSelection(stage);
            charSelect.show();
        });

        buttonBox.getChildren().addAll(walkButton, itemButton,
                                    inventoryButton, statsButton, advanceButton, backButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("RPG Game - " + currentChapter);
        stage.setScene(scene);
        stage.show();
    }

    private void explorar() {
        if (emCombate) {
            logArea.appendText("Você está em combate! Use as ações de combate.\n\n");
            return;
        }

        int evento = (int) (Math.random() * 100);
        exploracoes++;

        if (evento < 50) {
            // Encontrar inimigo
            encontrarInimigo();
        } else if (evento < 80) {
            // Encontrar item
            encontrarItem();
        } else {
            // Armadilha
            armadilha();
        }
    }

    private void encontrarInimigo() {
        String[] profissoes = {"advogado", "médico", "engenheiro", "professor",
                              "soldado", "cientista", "policial", "bombeiro", "chef", "mecânico"};
        String profissao = profissoes[(int) (Math.random() * profissoes.length)];

        String[] nomes = {"Marcus", "Sarah", "David", "Elena", "James", "Ana"};
        String nome = nomes[(int) (Math.random() * nomes.length)] + " Corrompido";

        int nivel = jogador.getNivel();
        inimigoAtual = new Inimigo(nome, profissao,
                                  80 + (nivel * 20), 12 + (nivel * 3), 8 + (nivel * 2), nivel);

        emCombate = true;
        logArea.appendText("\n⚔️ ENCONTRO HOSTIL! ⚔️\n");
        logArea.appendText("👹 Inimigo: " + inimigoAtual.getNome() + "\n");
        logArea.appendText("💼 Profissão: " + inimigoAtual.getProfissaoAnterior() + "\n");
        logArea.appendText("❤️ HP: " + inimigoAtual.getPontosVida() + "\n");
        logArea.appendText("⚔️ Ataque: " + inimigoAtual.getAtaque() + "\n");
        logArea.appendText("🛡️ Defesa: " + inimigoAtual.getDefesa() + "\n\n");

        // Switch to combat layout
        Platform.runLater(() -> {
            updateEnemyInfoPanel();
            switchToCombatLayout();
            // Habilitar botões de ataque
            for (Button btn : attackButtons) {
                btn.setDisable(false);
            }
        });
    }

    private void encontrarItem() {
        Item item = new Item("Nanoreparador", "Cura HP", "cura", 40, 1);
        jogador.getInventario().adicionarItem(item);
        logArea.appendText("\n🎁 Item encontrado: " + item.getNome() + " 🎁\n\n");
    }

    private void armadilha() {
        int dano = (int) (Math.random() * 20) + 10;
        jogador.receberDano(dano);
        logArea.appendText("\n💥 ARMADILHA! Você recebeu " + dano + " de dano! 💥\n");
        logArea.appendText("❤️ HP restante: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMax() + "\n\n");

        // Screen flash effect for danger
        String originalStyle = root.getStyle();
        root.setStyle("-fx-background-color: #ffcccc;");
        Timeline flashTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), e -> root.setStyle("-fx-background-color: #ffffff;")),
            new KeyFrame(Duration.millis(300), e -> root.setStyle(originalStyle))
        );
        flashTimeline.play();

        // Update player info panel
        Platform.runLater(() -> updatePlayerInfoPanel());

        if (!jogador.estaVivo()) {
            gameOver();
        }
    }

    private void atacar() {
        if (!emCombate || inimigoAtual == null) {
            logArea.appendText("Não há inimigos próximos para atacar.\n\n");
            return;
        }

        // Player turn
        turnoJogador();

        // Check if enemy is dead
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }

        // Enemy turn
        turnoInimigo();

        // Check if player is dead
        if (!jogador.estaVivo()) {
            derrotaCombate();
        }
    }

    private void turnoJogador() {
        if (!jogador.estaVivo()) {
            logArea.appendText("Você está morto e não pode atacar.\n\n");
            return;
        }

        logArea.appendText(">>> Seu turno <<<\n");

        if (jogador instanceof Berserker) {
            // Simple attack for Berserker
            Berserker berserker = (Berserker) jogador;
            List<String> logs = berserker.ataqueSimples(inimigoAtual.getNome(), inimigoAtual.getDefesa());
            if (logs != null) {
                for (String log : logs) {
                    logArea.appendText(log + "\n");
                }
                // Extract damage from last line
                String lastLine = logs.get(logs.size() - 1);
                if (lastLine.startsWith("DANO TOTAL:")) {
                    try {
                        int dano = Integer.parseInt(lastLine.split(":")[1].trim());
                        inimigoAtual.receberDano(dano);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } else if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;
            if (fuzileiro.getMunicao() > 0) {
                Fuzileiro.ResultadoTiro resultado = fuzileiro.tiroDePrecisao(inimigoAtual.getNome());
                if (resultado != null) {
                    logArea.appendText(resultado.log.toString() + "\n");
                    inimigoAtual.receberDano(resultado.danoFinal);
                }
            } else {
                logArea.appendText("Sem munição!\n");
            }
        } else if (jogador instanceof Mago) {
            int dano = ((Mago) jogador).bolaDeFogo();
            if (dano > 0) {
                inimigoAtual.receberDano(dano);
                logArea.appendText("BOLA DE FOGO! Causou " + dano + " de dano!\n");
            } else {
                logArea.appendText("Sem mana para Bola de Fogo!\n");
            }
        } else {
            // Generic attack
            int dano = jogador.calcularDano();
            int dado = jogador.rolarDado();
            logArea.appendText("Ataque: " + jogador.getNome() + " ataca! [Dado: " + dado + "]\n");
            if (dado > inimigoAtual.getDefesa()) {
                inimigoAtual.receberDano(dano);
                logArea.appendText("Acerto! Dano: " + dano + "\n");
            } else {
                logArea.appendText("O inimigo defendeu!\n");
            }
        }

        if (inimigoAtual.estaVivo()) {
            logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");
        }
    }

    private void turnoInimigo() {
        if (!jogador.estaVivo()) {
            return;
        }

        // Disable attack buttons during enemy turn
        for (Button btn : attackButtons) {
            btn.setDisable(true);
        }

        logArea.appendText("\n🔴 **TURNO DO INIMIGO** 🔴\n");
        int dano = inimigoAtual.calcularDano();
        jogador.receberDano(dano);
        logArea.appendText("💥 Você recebeu " + dano + " de dano! 💥\n");
        logArea.appendText("❤️ Seu HP: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMax() + "\n\n");

        // Screen flash effect for damage taken
        String originalStyle = root.getStyle();
        root.setStyle("-fx-background-color: #ffcccc;");
        Timeline flashTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), e -> root.setStyle("-fx-background-color: #ffffff;")),
            new KeyFrame(Duration.millis(300), e -> {
                // Re-enable attack buttons after enemy turn
                for (Button btn : attackButtons) {
                    btn.setDisable(false);
                }
                root.setStyle(originalStyle);
            })
        );
        flashTimeline.play();

        // Update player info panel
        Platform.runLater(() -> updatePlayerInfoPanel());
    }

    private void vitoriaCombate() {
        logArea.appendText("\nVITÓRIA!\n");
        transferirLoot();
        jogador.setNivel(jogador.getNivel() + 1);
        logArea.appendText("Nível aumentado para: " + jogador.getNivel() + "\n\n");

        // Reset special abilities after defeating enemy
        if (jogador instanceof Fuzileiro) {
            ((Fuzileiro) jogador).resetarUsosRajada();
        }
        if (jogador instanceof Berserker) {
            ((Berserker) jogador).resetarUsosAtaqueDuplo();
        }

        // Increment enemies defeated counter
        inimigosDerrotadosNoCapitulo++;

        // Check if chapter should advance (2 enemies defeated)
        if (inimigosDerrotadosNoCapitulo >= 2 && progressoHistoria < 3) {
            // Delay before advancing story
            Timeline advanceTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                avancarHistoria();
            }));
            advanceTimeline.play();
        }

        // Update panels to show defeated status
        Platform.runLater(() -> {
            updateEnemyInfoPanel();
            updatePlayerInfoPanel();
        });

        // Delay before hiding combat screens
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            emCombate = false;
            inimigoAtual = null;
            // Switch back to normal layout
            Platform.runLater(() -> {
                switchToNormalLayout();
                updatePlayerInfoPanel();
                // Disable attack buttons
                for (Button btn : attackButtons) {
                    btn.setDisable(true);
                }
            });
        }));
        timeline.play();
    }

    private void derrotaCombate() {
        logArea.appendText("\nDERROTA! Você foi derrotado...\n\n");

        // Update panels to show defeated status
        Platform.runLater(() -> {
            updatePlayerInfoPanel();
            updateEnemyInfoPanel();
        });

        // Delay before hiding combat screens and game over
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            // Disable attack buttons
            Platform.runLater(() -> {
                for (Button btn : attackButtons) {
                    btn.setDisable(true);
                }
            });
            gameOver();
        }));
        timeline.play();
    }

    private void transferirLoot() {
        logArea.appendText("Coletando itens...\n");
        for (Item item : inimigoAtual.getInventario().getItens()) {
            jogador.getInventario().adicionarItem(item);
            logArea.appendText("Pegou: " + item.getNome() + " (x" + item.getQuantidade() + ")\n");
        }
        logArea.appendText("\n");
    }

    private void usarItem() {
        if (jogador.getInventario().estaVazio()) {
            logArea.appendText("Inventário vazio!\n\n");
            return;
        }

        // For simplicity, use first available healing item
        for (Item item : jogador.getInventario().getItens()) {
            if ("cura".equals(item.getEfeito())) {
                List<String> logs = jogador.usarItem(item.getNome());
                if (logs != null) {
                    for (String log : logs) {
                        logArea.appendText(log + "\n");
                    }
                }
                logArea.appendText("\n");

                // Update player info panel
                Platform.runLater(() -> updatePlayerInfoPanel());

                return;
            }
        }

        logArea.appendText("Nenhum item de cura encontrado!\n\n");
    }

    private void mostrarInventario() {
        if (jogador.getInventario().estaVazio()) {
            logArea.appendText("Seu inventário está vazio.\n\n");
            return;
        }

        // Save current center content
        savedCenter = root.getCenter();

        // Create inventory pane
        BorderPane inventoryPane = new BorderPane();
        inventoryPane.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("INVENTÁRIO - Selecione um item para usar:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        inventoryPane.setTop(titleLabel);

        // Inventory list
        ListView<Item> itemListView = new ListView<>();
        itemListView.getItems().addAll(jogador.getInventario().getItens());
        itemListView.setPrefHeight(200);
        inventoryPane.setCenter(itemListView);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button useButton = new Button("Usar Item");
        useButton.setOnAction(e -> {
            Item selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                usarItemSelecionado(selectedItem);
                // Return to previous layout
                root.setCenter(savedCenter);
            }
        });

        Button backButton = new Button("Voltar ao Jogo");
        backButton.setOnAction(e -> {
            // Return to previous layout
            root.setCenter(savedCenter);
        });

        buttonBox.getChildren().addAll(useButton, backButton);
        inventoryPane.setBottom(buttonBox);

        // Replace center content with inventory
        root.setCenter(inventoryPane);
    }

    private void usarItemSelecionado(Item item) {
        List<String> logs = jogador.usarItem(item.getNome());
        if (logs != null) {
            logArea.appendText("Usando " + item.getNome() + ":\n");
            for (String log : logs) {
                logArea.appendText(log + "\n");
            }
            logArea.appendText("\n");

            // Update player info panel
            Platform.runLater(() -> updatePlayerInfoPanel());

        } else {
            logArea.appendText("Não foi possível usar o item.\n\n");
        }
    }

    private void mostrarStatus() {
        logArea.appendText("\n=== STATUS ===\n");
        logArea.appendText("Nome: " + jogador.getNome() + "\n");
        logArea.appendText("Classe: " + jogador.getClasse() + "\n");
        logArea.appendText("Nível: " + jogador.getNivel() + "\n");
        logArea.appendText("HP: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMax() + "\n");
        logArea.appendText("Ataque: " + jogador.getAtaque() + "\n");
        logArea.appendText("Defesa: " + jogador.getDefesa() + "\n");

        if (jogador instanceof Mago) {
            Mago mago = (Mago) jogador;
            logArea.appendText("Mana: " + mago.getMana() + "/" + mago.getManaMax() + "\n");
        } else if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;
            logArea.appendText("Munição: " + fuzileiro.getMunicao() + "/" + fuzileiro.getMunicaoMax() + "\n");
            logArea.appendText("Crítico: " + fuzileiro.getChanceCritico() + "%\n");
        } else if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            logArea.appendText("Fúria: " + berserker.getFuria() + "/" + berserker.getFuriaMax() + "\n");
            logArea.appendText("Combo: " + berserker.getComboAtual() + "/" + berserker.getComboMaximo() + "\n");
        }
        logArea.appendText("==============\n\n");
    }

    private void avancarHistoria() {
        if (exploracoes < 2) {
            logArea.appendText("Explore mais! (Mínimo: 2 explorações)\n\n");
            return;
        }

        progressoHistoria++;
        exploracoes = 0;
        inimigosDerrotadosNoCapitulo = 0;

        switch (progressoHistoria) {
            case 1:
                capitulo2();
                break;
            case 2:
                capitulo3();
                break;
            case 3:
                capituloFinal();
                break;
            default:
                logArea.appendText("Parabéns! Você completou o jogo!\n\n");
        }
    }

    private void capitulo2() {
        currentChapter = "Capítulo 2 - A Carteira de Trabalho Corrompida";
        currentLocation = "Cidade em Ruínas";
        logArea.appendText("\n+--------------------------------------------------------+\n");
        logArea.appendText("|      CAPÍTULO 2: A CARTEIRA DE TRABALHO CORROMPIDA     |\n");
        logArea.appendText("+--------------------------------------------------------+\n");
        logArea.appendText("A cidade está em ruínas...\n\n");

        Platform.runLater(() -> {
            chapterLabel.setText(currentChapter);
            locationLabel.setText("Localização: " + currentLocation);
            stage.setTitle("RPG Game - " + currentChapter);
        });
    }

    private void capitulo3() {
        currentChapter = "Capítulo 3 - O Laboratório";
        currentLocation = "Laboratório do Dr. Bruno";
        logArea.appendText("\n+--------------------------------------------------------+\n");
        logArea.appendText("|              CAPÍTULO 3: O LABORATÓRIO                 |\n");
        logArea.appendText("+--------------------------------------------------------+\n");
        logArea.appendText("Você encontra o laboratório do Dr. Bruno.\n\n");

        Platform.runLater(() -> {
            chapterLabel.setText(currentChapter);
            locationLabel.setText("Localização: " + currentLocation);
            stage.setTitle("RPG Game - " + currentChapter);
        });
    }

    private void capituloFinal() {
        currentChapter = "Capítulo Final - A Carteira CLT";
        currentLocation = "Sala do Chefe Final";
        logArea.appendText("\n+--------------------------------------------------------+\n");
        logArea.appendText("|            CAPÍTULO FINAL: A CARTEIRA CLT              |\n");
        logArea.appendText("+--------------------------------------------------------+\n");

        // Create final boss
        Inimigo chefeFinal = new Inimigo("CARTEIRA DE TRABALHO", "carteira de trabalho", 200, 30, 20, 5);
        inimigoAtual = chefeFinal;
        emCombate = true;

        logArea.appendText("ENCONTRO HOSTIL!\n");
        logArea.appendText("Inimigo: " + chefeFinal.getNome() + "\n");
        logArea.appendText("HP: " + chefeFinal.getPontosVida() + "\n");
        logArea.appendText("Ataque: " + chefeFinal.getAtaque() + "\n");
        logArea.appendText("Defesa: " + chefeFinal.getDefesa() + "\n\n");

        Platform.runLater(() -> {
            chapterLabel.setText(currentChapter);
            locationLabel.setText("Localização: " + currentLocation);
            stage.setTitle("RPG Game - " + currentChapter);
            for (Button btn : attackButtons) {
                btn.setDisable(false);
            }
        });
    }

    private void gameOver() {
        logArea.appendText("GAME OVER - Você foi derrotado...\n\n");

        // Disable all buttons except back
        Platform.runLater(() -> {
            HBox buttonBox = (HBox) ((BorderPane) logArea.getParent()).getBottom();
            for (int i = 0; i < buttonBox.getChildren().size() - 1; i++) {
                buttonBox.getChildren().get(i).setDisable(true);
            }
        });
    }

    private void addAttackButtons(HBox buttonBox) {
        attackButtons.clear();

        if (jogador instanceof Berserker) {
            // Berserker buttons
            Button ataqueSimplesBtn = new Button("Ataque Simples");
            ataqueSimplesBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            ataqueSimplesBtn.setOnAction(e -> ataqueSimplesBerserker());
            ataqueSimplesBtn.setDisable(true);
            attackButtons.add(ataqueSimplesBtn);

            Button ataqueDuploBtn = new Button("Ataque Duplo");
            ataqueDuploBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            ataqueDuploBtn.setOnAction(e -> ataqueDuploBerserker());
            ataqueDuploBtn.setDisable(true);
            attackButtons.add(ataqueDuploBtn);

            Button redemoinhoBtn = new Button("Redemoinho Mortal");
            redemoinhoBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            redemoinhoBtn.setOnAction(e -> redemoinhoMortalBerserker());
            redemoinhoBtn.setDisable(true);
            attackButtons.add(redemoinhoBtn);

            Button comboBtn = new Button("Combo Finalizador");
            comboBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            comboBtn.setOnAction(e -> comboFinalizadorBerserker());
            comboBtn.setDisable(true);
            attackButtons.add(comboBtn);

        } else if (jogador instanceof Fuzileiro) {
            // Fuzileiro buttons
            Button tiroPrecisaoBtn = new Button("Tiro de Precisão");
            tiroPrecisaoBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            tiroPrecisaoBtn.setOnAction(e -> tiroPrecisaoFuzileiro());
            tiroPrecisaoBtn.setDisable(true);
            attackButtons.add(tiroPrecisaoBtn);

            Button rajadaBtn = new Button("Rajada Precisa");
            rajadaBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            rajadaBtn.setOnAction(e -> rajadaPrecisaFuzileiro());
            rajadaBtn.setDisable(true);
            attackButtons.add(rajadaBtn);

            Button recarregarBtn = new Button("Recarregar");
            recarregarBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            recarregarBtn.setOnAction(e -> recarregarFuzileiro());
            recarregarBtn.setDisable(true);
            attackButtons.add(recarregarBtn);

        } else if (jogador instanceof Mago) {
            // Mago buttons
            Button bolaFogoBtn = new Button("Bola de Fogo");
            bolaFogoBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            bolaFogoBtn.setOnAction(e -> bolaDeFogoMago());
            bolaFogoBtn.setDisable(true);
            attackButtons.add(bolaFogoBtn);

            Button toqueFantasmasBtn = new Button("Toque dos Fantasmas");
            toqueFantasmasBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            toqueFantasmasBtn.setOnAction(e -> toqueDosFantasmasMago());
            toqueFantasmasBtn.setDisable(true);
            attackButtons.add(toqueFantasmasBtn);

            Button raioArcanoBtn = new Button("Raio Arcano");
            raioArcanoBtn.setStyle("-fx-font-size: 12px; -fx-pref-width: 120px;");
            raioArcanoBtn.setOnAction(e -> raioArcanoMago());
            raioArcanoBtn.setDisable(true);
            attackButtons.add(raioArcanoBtn);
        }

        // Add all attack buttons to the buttonBox
        for (Button btn : attackButtons) {
            buttonBox.getChildren().add(btn);
        }
    }

    // Berserker attack methods
    private void ataqueSimplesBerserker() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Berserker berserker = (Berserker) jogador;
        List<String> logs = berserker.ataqueSimples(inimigoAtual.getNome(), inimigoAtual.getDefesa());
        for (String log : logs) {
            logArea.appendText(log + "\n");
        }
        String lastLine = logs.get(logs.size() - 1);
        if (lastLine.startsWith("DANO TOTAL:")) {
            try {
                int dano = Integer.parseInt(lastLine.split(":")[1].trim());
                inimigoAtual.receberDano(dano);
                Platform.runLater(() -> updateEnemyInfoPanel());
            } catch (NumberFormatException ignored) {}
        }
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }
        logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");
        turnoInimigo();
    }

    private void ataqueDuploBerserker() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        // Green screen flash for player attack
        String originalStyle = root.getStyle();
        root.setStyle("-fx-background-color: #e8f5e8;");
        Timeline flashTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), e -> root.setStyle("-fx-background-color: #ffffff;")),
            new KeyFrame(Duration.millis(300), e -> root.setStyle(originalStyle))
        );
        flashTimeline.play();

        Berserker berserker = (Berserker) jogador;
        List<String> logs = berserker.ataqueDuplo(inimigoAtual.getNome(), inimigoAtual.getDefesa());
        for (String log : logs) {
            logArea.appendText(log + "\n");
        }
        String lastLine = logs.get(logs.size() - 1);
        if (lastLine.startsWith("💥 DANO TOTAL:")) {
            try {
                int dano = Integer.parseInt(lastLine.split(":")[1].trim().replace(" 💥", ""));
                inimigoAtual.receberDano(dano);
                Platform.runLater(() -> updateEnemyInfoPanel());
            } catch (NumberFormatException ignored) {}
        }
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }
        logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");

        // Delay before enemy turn
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            turnoInimigo();
            if (!jogador.estaVivo()) {
                derrotaCombate();
            }
        }));
        delayTimeline.play();
    }

    private void redemoinhoMortalBerserker() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        // Green screen flash for player attack
        String originalStyle = root.getStyle();
        root.setStyle("-fx-background-color: #e8f5e8;");
        Timeline flashTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), e -> root.setStyle("-fx-background-color: #ffffff;")),
            new KeyFrame(Duration.millis(300), e -> root.setStyle(originalStyle))
        );
        flashTimeline.play();

        Berserker berserker = (Berserker) jogador;
        List<String> logs = berserker.redemoinhoMortal(inimigoAtual.getNome(), inimigoAtual.getDefesa());
        for (String log : logs) {
            logArea.appendText(log + "\n");
        }
        String lastLine = logs.get(logs.size() - 1);
        if (lastLine.startsWith("💥 DANO TOTAL:")) {
            try {
                int dano = Integer.parseInt(lastLine.split(":")[1].trim().replace(" 💥", ""));
                inimigoAtual.receberDano(dano);
                Platform.runLater(() -> updateEnemyInfoPanel());
            } catch (NumberFormatException ignored) {}
        }
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }
        logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");

        // Delay before enemy turn
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            turnoInimigo();
            if (!jogador.estaVivo()) {
                derrotaCombate();
            }
        }));
        delayTimeline.play();
    }

    private void comboFinalizadorBerserker() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        // Green screen flash for player attack
        String originalStyle = root.getStyle();
        root.setStyle("-fx-background-color: #e8f5e8;");
        Timeline flashTimeline = new Timeline(
            new KeyFrame(Duration.millis(150), e -> root.setStyle("-fx-background-color: #ffffff;")),
            new KeyFrame(Duration.millis(300), e -> root.setStyle(originalStyle))
        );
        flashTimeline.play();

        Berserker berserker = (Berserker) jogador;
        List<String> logs = berserker.comboFinalizador(inimigoAtual.getNome(), inimigoAtual.getDefesa());
        for (String log : logs) {
            logArea.appendText(log + "\n");
        }
        String lastLine = logs.get(logs.size() - 1);
        if (lastLine.startsWith("💥 DANO TOTAL:")) {
            try {
                int dano = Integer.parseInt(lastLine.split(":")[1].trim().replace(" 💥", ""));
                inimigoAtual.receberDano(dano);
                Platform.runLater(() -> updateEnemyInfoPanel());
            } catch (NumberFormatException ignored) {}
        }
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }
        logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");

        // Delay before enemy turn
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            turnoInimigo();
            if (!jogador.estaVivo()) {
                derrotaCombate();
            }
        }));
        delayTimeline.play();
    }

    // Fuzileiro attack methods
    private void tiroPrecisaoFuzileiro() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Fuzileiro fuzileiro = (Fuzileiro) jogador;
        Fuzileiro.ResultadoTiro resultado = fuzileiro.tiroDePrecisao(inimigoAtual.getNome());
        if (resultado != null) {
            logArea.appendText(resultado.log.toString() + "\n");
            inimigoAtual.receberDano(resultado.danoFinal);
            Platform.runLater(() -> updateEnemyInfoPanel());
            logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");
            if (!inimigoAtual.estaVivo()) {
                vitoriaCombate();
                return;
            }
        } else {
            logArea.appendText("Sem munição!\n");
        }
        turnoInimigo();
    }

    private void rajadaPrecisaFuzileiro() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Fuzileiro fuzileiro = (Fuzileiro) jogador;
        Fuzileiro.ResultadoRajada resultado = fuzileiro.rajadaPrecisa(inimigoAtual.getNome());
        for (String log : resultado.logCompleto) {
            logArea.appendText(log + "\n");
        }
        inimigoAtual.receberDano(resultado.danoTotal);
        Platform.runLater(() -> updateEnemyInfoPanel());
        logArea.appendText("HP Inimigo: " + inimigoAtual.getPontosVida() + "\n\n");
        if (!inimigoAtual.estaVivo()) {
            vitoriaCombate();
            return;
        }
        turnoInimigo();
    }

    private void recarregarFuzileiro() {
        if (!emCombate || !jogador.estaVivo()) return;

        Fuzileiro fuzileiro = (Fuzileiro) jogador;
        List<String> logs = fuzileiro.recarregar();
        for (String log : logs) {
            logArea.appendText(log + "\n");
        }
        turnoInimigo();
    }

    // Mago attack methods
    private void bolaDeFogoMago() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Mago mago = (Mago) jogador;
        int dano = mago.bolaDeFogo();
        if (dano > 0) {
            inimigoAtual.receberDano(dano);
            logArea.appendText("BOLA DE FOGO! Causou " + dano + " de dano!\n");
            if (!inimigoAtual.estaVivo()) {
                vitoriaCombate();
                return;
            }
        } else {
            logArea.appendText("Sem mana para Bola de Fogo!\n");
        }
        turnoInimigo();
    }

    private void toqueDosFantasmasMago() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Mago mago = (Mago) jogador;
        int dano = mago.toqueDosFantasmas();
        if (dano > 0) {
            inimigoAtual.receberDano(dano);
            logArea.appendText("TOQUE DOS FANTASMAS! Causou " + dano + " de dano!\n");
            if (!inimigoAtual.estaVivo()) {
                vitoriaCombate();
                return;
            }
        } else {
            logArea.appendText("Sem mana para Toque dos Fantasmas!\n");
        }
        turnoInimigo();
    }

    private void raioArcanoMago() {
        if (!emCombate || inimigoAtual == null || !jogador.estaVivo()) return;

        Mago mago = (Mago) jogador;
        int dano = mago.raioArcano();
        if (dano > 0) {
            inimigoAtual.receberDano(dano);
            logArea.appendText("RAIO ARCANO! Causou " + dano + " de dano!\n");
            if (!inimigoAtual.estaVivo()) {
                vitoriaCombate();
                return;
            }
        } else {
            logArea.appendText("Sem mana para Raio Arcano!\n");
        }
        turnoInimigo();
    }

    private void initializeInfoPanels() {
        // Player info panel
        playerInfoPanel = new VBox(10);
        playerInfoPanel.setPadding(new Insets(10));
        playerInfoPanel.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #4caf50; -fx-border-width: 2;");

        Label playerTitle = new Label("JOGADOR");
        playerTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label playerName = new Label("Nome: " + jogador.getNome());
        Label playerClass = new Label("Classe: " + jogador.getClasse());
        Label playerLevel = new Label("Nível: " + jogador.getNivel());
        Label playerHP = new Label("HP: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMax());
        Label playerAttack = new Label("Ataque: " + jogador.getAtaque());
        Label playerDefense = new Label("Defesa: " + jogador.getDefesa());

        playerInfoPanel.getChildren().addAll(playerTitle, playerName, playerClass, playerLevel, playerHP, playerAttack, playerDefense);

        // Add ammunition label for Fuzileiro
        if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;
            Label playerAmmo = new Label("Munição: " + fuzileiro.getMunicao() + "/" + fuzileiro.getMunicaoMax());
            playerInfoPanel.getChildren().add(playerAmmo);
        }

        // Enemy info panel (initially empty)
        enemyInfoPanel = new VBox(10);
        enemyInfoPanel.setPadding(new Insets(10));
        enemyInfoPanel.setStyle("-fx-background-color: #ffebee; -fx-border-color: #f44336; -fx-border-width: 2;");

        Label enemyTitle = new Label("INIMIGO");
        enemyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        enemyInfoPanel.getChildren().add(enemyTitle);
    }

    private void updatePlayerInfoPanel() {
        if (playerInfoPanel == null) return;

        // Update HP and other stats
        Label hpLabel = (Label) playerInfoPanel.getChildren().get(4);
        if (jogador.getPontosVida() <= 0) {
            hpLabel.setText("DERROTADO");
            hpLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            hpLabel.setText("HP: " + jogador.getPontosVida() + "/" + jogador.getPontosVidaMax());
            hpLabel.setStyle(""); // Reset style
        }

        Label levelLabel = (Label) playerInfoPanel.getChildren().get(3);
        levelLabel.setText("Nível: " + jogador.getNivel());

        // Update ammunition for Fuzileiro
        if (jogador instanceof Fuzileiro && playerInfoPanel.getChildren().size() > 7) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;
            Label ammoLabel = (Label) playerInfoPanel.getChildren().get(7);
            ammoLabel.setText("Munição: " + fuzileiro.getMunicao() + "/" + fuzileiro.getMunicaoMax());
        }
    }

    private void updateEnemyInfoPanel() {
        if (enemyInfoPanel == null || inimigoAtual == null) return;

        enemyInfoPanel.getChildren().clear();

        Label enemyTitle = new Label("INIMIGO");
        enemyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label enemyName = new Label("Nome: " + inimigoAtual.getNome());
        Label enemyProfession = new Label("Profissão: " + inimigoAtual.getProfissaoAnterior());

        Label enemyHP;
        if (inimigoAtual.getPontosVida() <= 0) {
            enemyHP = new Label("DERROTADO");
            enemyHP.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            enemyHP = new Label("HP: " + inimigoAtual.getPontosVida());
        }

        Label enemyAttack = new Label("Ataque: " + inimigoAtual.getAtaque());
        Label enemyDefense = new Label("Defesa: " + inimigoAtual.getDefesa());

        enemyInfoPanel.getChildren().addAll(enemyTitle, enemyName, enemyProfession, enemyHP, enemyAttack, enemyDefense);
    }

    private void switchToCombatLayout() {
        BorderPane root = (BorderPane) logArea.getParent();
        if (root == null) return;

        // Create HBox for split layout
        HBox combatLayout = new HBox(10);
        combatLayout.setPadding(new Insets(10));

        // Left side: Player info
        combatLayout.getChildren().add(playerInfoPanel);
        HBox.setHgrow(playerInfoPanel, Priority.ALWAYS);

        // Center: Game log
        combatLayout.getChildren().add(logArea);
        HBox.setHgrow(logArea, Priority.ALWAYS);

        // Right side: Enemy info
        combatLayout.getChildren().add(enemyInfoPanel);
        HBox.setHgrow(enemyInfoPanel, Priority.ALWAYS);

        root.setCenter(combatLayout);
    }

    private void switchToNormalLayout() {
        BorderPane root = (BorderPane) logArea.getParent();
        if (root == null) return;

        root.setCenter(logArea);
    }
}
