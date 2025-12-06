package rpg;

import java.util.List;
import java.util.Scanner;

import rpg.models.Berserker;
import rpg.models.Fuzileiro;
import rpg.models.Inimigo;
import rpg.models.Item;
import rpg.models.Mago;
import rpg.models.Personagem;
import rpg.models.Fuzileiro.ResultadoRajada;

public class SistemaCombate {
    private Personagem jogador;
    private Inimigo inimigo;
    private Scanner scanner;
    private boolean paralisia;
    private int turnosParalisia;

    public SistemaCombate(Personagem jogador, Inimigo inimigo) {
        this.jogador = jogador;
        this.inimigo = inimigo;
        this.scanner = new Scanner(System.in);
        this.paralisia = false;
        this.turnosParalisia = 0;
    }

    public boolean batalhar() {
        System.out.println("\n+------------------------------------------------+");
        System.out.println("|                      COMBATE INICIADO                        |");
        System.out.println("+------------------------------------------------+");

        if (jogador instanceof Fuzileiro) {
            ((Fuzileiro) jogador).resetarUsosRajada();
        }

        inimigo.exibirInfoCompleta();

        while (jogador.estaVivo() && inimigo.estaVivo()) {
            exibirStatusCombate();

            if (!turnoJogador()) {
                return false;
            }

            if (!inimigo.estaVivo()) {
                break;
            }

            turnoInimigo();
        }

        return finalizarCombate();
    }

    private void exibirStatusCombate() {
        System.out.println("\n+-------------------------------------+");

        System.out.println("| " + jogador.getNome() + " HP: " + jogador.getPontosVida() + "/" +
                jogador.getPontosVidaMax());
        System.out.println("| " + inimigo.getNome() + " HP: " + inimigo.getPontosVida() + "/" +
                inimigo.getPontosVidaMax());

        System.out.println("|-------------------------------------|");

        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            System.out.println("| Furia: " + berserker.getFuria() + "/" + berserker.getFuriaMax() +
                    " | Combo: " + berserker.getComboAtual() + "/" + berserker.getComboMaximo());
        } else if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;
            System.out.println("| Municao: " + fuzileiro.getMunicao() + "/" + fuzileiro.getMunicaoMax() +
                    " | Critico: " + fuzileiro.getChanceCritico() + "%");
        } else if (jogador instanceof Mago) {
            Mago mago = (Mago) jogador;
            System.out.println("| Mana: " + mago.getMana() + "/" + mago.getManaMax());
        }

        System.out.println("+-------------------------------------+\n");
    }

    private boolean turnoJogador() {
        if (paralisia) {
            turnosParalisia--;
            System.out.println("\n⚠️ Voce esta paralisado! Nao pode agir este turno!");
            if (turnosParalisia <= 0) {
                paralisia = false;
                System.out.println("✓ Voce se libertou da paralisia!");
            }
            aguardarEnter();
            return true;
        }

        System.out.println("\n>>> Seu turno <<<");
        exibirMenuAcoes();

        int escolha = -1;
        try {
            escolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("⚠️ Entrada invalida!");
            return true;
        }

        return processarAcao(escolha);
    }

    private void exibirMenuAcoes() {
        System.out.println("1. Atacar");
        System.out.println("2. Usar Habilidade Principal (Bola de Fogo)");
        System.out.println("3. Usar Item");
        System.out.println("4. Fugir");

        if (jogador instanceof Berserker) {
            System.out.println("5. Redemoinho Mortal (30 Furia)");
            System.out.println("6. Execucao Cruzada (50 Furia)");
            System.out.println("7. Furia Desenfreada (70 Furia)");
            System.out.println("8. Combo Finalizador (3+ Combo)");
            System.out.println("9. Ver Estatisticas");
        } else if (jogador instanceof Fuzileiro) {
            System.out.println("5. Recarregar Municao");
            System.out.println("6. Ver Estatisticas de Tiro");
        } else if (jogador instanceof Mago) {
            System.out.println("5. Toque dos Fantasmas (30 Mana)");
            System.out.println("6. Raio Arcano (25 Mana)");
        }

        System.out.print("Escolha: ");
    }

    private boolean processarAcao(int escolha) {
        switch (escolha) {
            case 1:
                atacarNormal();
                break;
            case 2:
                usarHabilidadePrincipal();
                break;
            case 3:
                usarItemCombate();
                break;
            case 4:
                return tentarFugir();
            case 5:
                usarOpcaoExtra1();
                break;
            case 6:
                usarOpcaoExtra2();
                break;
            case 7:
                usarOpcaoExtra3();
                break;
            case 8:
                usarOpcaoExtra4();
                break;
            case 9:
                usarOpcaoExtra5();
                break;
            default:
                System.out.println("Acao invalida! Voce perde o turno.");
        }

        return true;
    }

    private void imprimirLog(List<String> log) {
        if (log != null) {
            for (String linha : log) {
                System.out.println(linha);
            }
        }
    }

    private void atacarNormal() {
        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            List<String> logAcoes = berserker.ataqueDuplo(
                    inimigo.getNome(),
                    inimigo.getDefesa());

            int danoTotal = extrairDanoTotalDoLog(logAcoes);
            inimigo.receberDano(danoTotal);

            imprimirLog(logAcoes);

        } else if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;

            if (fuzileiro.getMunicao() > 0) {
                Fuzileiro.ResultadoTiro resultado = fuzileiro.tiroDePrecisao(inimigo.getNome());
                if (resultado != null) {
                    System.out.println(resultado.log.toString());
                    inimigo.receberDano(resultado.danoFinal);
                }
            } else {
                System.out.println("\n Sem municao! Use a opcao 5 para recarregar.");
            }

        } else {
            int danoJogador = jogador.calcularDano();
            int dadoJogador = jogador.rolarDado();

            System.out.println("\n " + jogador.getNome() + " ataca! [Dado: " + dadoJogador + "]");

            if (dadoJogador > inimigo.getDefesa()) {
                inimigo.receberDano(danoJogador);
                System.out.println(" Acerto! Causou " + danoJogador + " de dano!");
            } else {
                System.out.println(" O inimigo defendeu o ataque!");
            }
        }
    }

    private void usarHabilidadePrincipal() {
        if (jogador instanceof Berserker) {
            System.out.println(
                    "O Berserker usa o Ataque Duplo na Opcao 1. Use as Opcoes 5 a 8 para Habilidades Especiais.");
        } else if (jogador instanceof Mago) {
            Mago mago = (Mago) jogador;
            int dano = mago.bolaDeFogo();

            if (dano > 0) {
                System.out.println("\n BOLA DE FOGO!");
                inimigo.receberDano(dano);
                System.out.println(" Causou " + dano + " de dano!");
            } else {
                System.out.println("\nMana insuficiente! Voce ataca normalmente.");
                atacarNormal();
            }

        } else if (jogador instanceof Fuzileiro) {
            Fuzileiro fuzileiro = (Fuzileiro) jogador;

            ResultadoRajada resultado = fuzileiro.rajadaPrecisa(inimigo.getNome());

            if (resultado != null && resultado.logCompleto != null) {
                imprimirLog(resultado.logCompleto);

                if (resultado.danoTotal > 0) {
                    inimigo.receberDano(resultado.danoTotal);
                }

                if (resultado.todosForamCriticos()) {
                    System.out.println("\nRAJADA PERFEITA! Todos os tiros foram criticos!");
                }
            }
        }
    }

    private void usarOpcaoExtra1() {
        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            List<String> logAcoes = berserker.redemoinhoMortal(
                    inimigo.getNome(),
                    inimigo.getDefesa());

            int danoTotal = extrairDanoTotalDoLog(logAcoes);

            if (danoTotal > 0) {
                inimigo.receberDano(danoTotal);
            }
            imprimirLog(logAcoes);

        } else if (jogador instanceof Fuzileiro) {
            List<String> log = ((Fuzileiro) jogador).recarregar();
            imprimirLog(log);
        } else if (jogador instanceof Mago) {
            Mago mago = (Mago) jogador;
            int dano = mago.toqueDosFantasmas();
            
            if (dano > 0) {
                System.out.println("🔮 TOQUE DOS FANTASMAS! Causou " + dano + " de dano!");
                inimigo.receberDano(dano);
            } else {
                System.out.println("⚠️ Mana insuficiente para usar Toque dos Fantasmas.");
            }
        } else {
            System.out.println(" Opcao nao disponivel para sua classe.");
        }
    }

    private void usarOpcaoExtra2() {
        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            List<String> logAcoes = berserker.execucaoCruzada(
                    inimigo.getNome(),
                    inimigo.getDefesa());

            int danoTotal = extrairDanoTotalDoLog(logAcoes);

            if (danoTotal > 0) {
                inimigo.receberDano(danoTotal);
            }
            imprimirLog(logAcoes);

        } else if (jogador instanceof Fuzileiro) {
            List<String> log = ((Fuzileiro) jogador).exibirEstatisticasTiro();
            imprimirLog(log);
        } else if (jogador instanceof Mago) {
            Mago mago = (Mago) jogador;
            int dano = mago.raioArcano();
            
            if (dano > 0) {
                System.out.println("⚡ RAIO ARCANO! Causou " + dano + " de dano!");
                inimigo.receberDano(dano);
            } else {
                System.out.println("⚠️ Mana insuficiente para usar Raio Arcano.");
            }
        } else {
            System.out.println("Opcao nao disponivel para sua classe.");
        }
    }

    private void usarOpcaoExtra3() {
        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            List<String> logAcoes = berserker.furiaDesenfreada(
                    inimigo.getNome(),
                    inimigo.getDefesa());

            int danoTotal = extrairDanoTotalDoLog(logAcoes);

            if (danoTotal > 0) {
                inimigo.receberDano(danoTotal);
            }
            imprimirLog(logAcoes);

        } else {
            System.out.println("Opcao nao disponivel para sua classe.");
        }
    }

    private void usarOpcaoExtra4() {
        if (jogador instanceof Berserker) {
            Berserker berserker = (Berserker) jogador;
            List<String> logAcoes = berserker.comboFinalizador(
                    inimigo.getNome(),
                    inimigo.getDefesa());

            int danoTotal = extrairDanoTotalDoLog(logAcoes);

            if (danoTotal > 0) {
                inimigo.receberDano(danoTotal);
            }
            imprimirLog(logAcoes);

        } else {
            System.out.println("Opcao nao disponivel para sua classe.");
        }
    }

    private void usarOpcaoExtra5() {
        if (jogador instanceof Berserker) {
            List<String> log = ((Berserker) jogador).exibirEstatisticasCombate();
            imprimirLog(log);
        } else {
            System.out.println("Opcao nao disponivel para sua classe.");
        }
    }

    private int extrairDanoTotalDoLog(List<String> logAcoes) {
        if (logAcoes == null || logAcoes.isEmpty())
            return 0;

        for (String linha : logAcoes) {
            if (linha.trim().startsWith("DANO TOTAL:")) {
                try {
                    String parteNumerica = linha.split(":")[1].trim();
                    return Integer.parseInt(parteNumerica);
                } catch (Exception e) {
                }
            } else if (linha.trim().startsWith("DANO TOTAL DO REDEMOINHO:")) {
                try {
                    String parteNumerica = linha.split(":")[1].trim();
                    return Integer.parseInt(parteNumerica);
                } catch (Exception e) {
                }
            } else if (linha.trim().startsWith("DANO TOTAL DA FURIA:")) {
                try {
                    String parteNumerica = linha.split(":")[1].trim();
                    return Integer.parseInt(parteNumerica);
                } catch (Exception e) {
                }
            }
        }

        return 0;
    }

    private void usarItemCombate() {
        if (jogador.getInventario().estaVazio()) {
            System.out.println("\n Inventario vazio!");
            return;
        }

        System.out.println("\n--- ITENS DE COMBATE ---");

        List<String> itensComCodigo = jogador.getInventario().listarItensListaComCodigo();

        if (itensComCodigo.isEmpty()) {
            System.out.println(" Nenhum item utilizavel no inventario.");
            return;
        }

        for (String linha : itensComCodigo) {
            System.out.println(linha.trim());
        }

        System.out.println("0. Voltar");
        System.out.print("Digite o codigo do item para usar (ou 0 para voltar): ");

        int codigoEscolha = -1;
        try {
            codigoEscolha = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("⚠️ Entrada invalida! Tente novamente.");
            return;
        }

        if (codigoEscolha == 0) {
            System.out.println("Acao cancelada.");
            return;
        }

        int indiceItem = codigoEscolha - 1;

        String nomeItem = jogador.getInventario().getNomeItemPeloIndice(indiceItem);

        if (nomeItem != null) {
            List<String> logsUso = jogador.usarItem(nomeItem);

            if (logsUso != null && !logsUso.isEmpty()) {
                imprimirLog(logsUso);
            } else {
                System.out.println(" Item nao pode ser usado (verifique o código, quantidade ou tipo)!");
            }
        } else {
            System.out.println(" Codigo de item invalido!");
        }
    }

    private boolean tentarFugir() {
        int dadoFuga = jogador.rolarDado();
        System.out.println("\n Tentando fugir... [Dado: " + dadoFuga + "]");

        if (dadoFuga >= 15) {
            System.out.println("Voce conseguiu fugir do combate");
            return false;
        } else {
            System.out.println("Falhou em fugir O inimigo te ataca");
            int danoInimigo = inimigo.calcularDano();
            jogador.receberDano(danoInimigo);
            System.out.println("Voce recebeu " + danoInimigo + " de dano");
            return true;
        }
    }

    private void turnoInimigo() {
        System.out.println("\n>>> Turno do Inimigo <<<");

        if (inimigo.tentarHabilidadeEspecial()) {
            int resultado = inimigo.usarHabilidadeEspecial();

            if (resultado > 0) {
                jogador.receberDano(resultado);
                System.out.println("Voce recebeu " + resultado + " de dano");
            }
        } else {
            int danoInimigo = inimigo.calcularDano();
            int dadoInimigo = inimigo.rolarDado();

            System.out.println(inimigo.getNome() + " ataca! [Dado: " + dadoInimigo + "]");

            if (dadoInimigo > jogador.getDefesa()) {
                jogador.receberDano(danoInimigo);
                System.out.println("Voce recebeu " + danoInimigo + " de dano");
            } else {
                System.out.println("Voce defendeu o ataque");
            }
        }

        aguardarEnter();
    }

    private boolean finalizarCombate() {
        if (jogador.estaVivo()) {
            System.out.println("\n+------------------------------------------------+");
            System.out.println("|                      VITORIA!                                |");
            System.out.println("+------------------------------------------------+");
            System.out.println("Voce derrotou " + inimigo.getNome() + "!");

            System.out.println("\nColetando itens do inimigo...");
            for (Item item : inimigo.getInventario().getItens()) {
                if (jogador.getInventario().adicionarItem(item)) {
                    System.out.println("Obteve: " + item.getNome() + " (x" + item.getQuantidade() + ")");
                }
            }

            if (jogador instanceof Berserker) {
                System.out.println("\nEstatisticas do Combate:");
                imprimirLog(((Berserker) jogador).exibirEstatisticasCombate());
            } else if (jogador instanceof Fuzileiro) {
                System.out.println("\nEstatisticas de Tiro:");
                imprimirLog(((Fuzileiro) jogador).exibirEstatisticasTiro());
            }

            return true;
        } else {
            System.out.println("\n+------------------------------------------------+");
            System.out.println("|                      DERROTA!                                |");
            System.out.println("+------------------------------------------------+");
            System.out.println("Voce foi derrotado por " + inimigo.getNome() + "...");
            return false;
        }
    }

    private void aguardarEnter() {
        System.out.println("\n[Pressione ENTER para continuar]");
        scanner.nextLine();
    }

    public void fecharScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
