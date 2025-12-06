package rpg.models;

import java.util.ArrayList;
import java.util.List;

public class Berserker extends Personagem {

    private int furia;
    private final int furiaMax = 100;
    private int comboAtual;
    private final int comboMaximo = 5;

    private int usosAtaqueDuplo;
    private final int usosAtaqueDuploMaximo = 2;

    private String nomeEspadaEsquerda = "Espada Esquerda";
    private int danoEspadaEsquerda = 8;
    private String nomeEspadaDireita = "Espada Direita";
    private int danoEspadaDireita = 8;

    private List<LogAtaque> historicoAtaques;

    public Berserker(String nome, int pontosVidaMax, int forca, int defesa, int nivelInicial) {
        super(nome, pontosVidaMax, forca, defesa, nivelInicial);
        this.furia = 0;
        this.comboAtual = 0;
        this.historicoAtaques = new ArrayList<>();
        this.usosAtaqueDuplo = usosAtaqueDuploMaximo;
    }
    
    public Berserker(String nome) {
        this(nome, 150, 20, 15, 1);
    }
    
    @Override
    public String getClasse() {
        return "Berserker";
    }

    @Override
    public String getHabilidadeEspecial() {
        return "Ataque Duplo (Opcao 2)";
    }
    
    public void resetarUsosAtaqueDuplo() {
        this.usosAtaqueDuplo = usosAtaqueDuploMaximo;
    }

    private LogAtaque executarAtaqueInterno(String nomeArma, int danoBase, String nomeAlvo, int defesaAlvo, boolean habilidade) {
        int dado = rolarDado();
        int danoFinal = 0;
        boolean acertou = false;

        if (dado > defesaAlvo) {
            danoFinal = ataque + danoBase + random.nextInt(6) + 1;
            furia = Math.min(furia + 5, furiaMax);
            acertou = true;
        }

        return new LogAtaque(nomeArma, danoFinal, dado, acertou);
    }

    public List<String> ataqueSimples(String nomeAlvo, int defesaAlvo) {
        List<String> logAcoes = new ArrayList<>();
        logAcoes.add("--- ATAQUE NORMAL (Opcao 1) ---");

        String nomeEspada = (Math.random() < 0.5) ? nomeEspadaEsquerda : nomeEspadaDireita;
        int danoEspada = (nomeEspada.equals(nomeEspadaEsquerda)) ? danoEspadaEsquerda : danoEspadaDireita;
        
        LogAtaque ataque = executarAtaqueInterno(
            nomeEspada, 
            danoEspada, 
            nomeAlvo, 
            defesaAlvo,
            false
        );
        logAcoes.add(ataque.toString());
        historicoAtaques.add(ataque);

        if (ataque.acertou) {
            logAcoes.add("✓ Seu movimento foi fluido. (Ganho de Furia)");
            comboAtual = Math.min(comboAtual + 1, comboMaximo);
        } else {
            logAcoes.add("✗ Seu golpe foi lento. (Combo resetado)");
            comboAtual = 0;
        }
        
        int danoTotal = ataque.danoFinal;
        logAcoes.add("DANO TOTAL: " + danoTotal);

        return logAcoes;
    }

    public List<String> ataqueDuplo(String nomeAlvo, int defesaAlvo) {
        List<String> logAcoes = new ArrayList<>();
        
        if (usosAtaqueDuplo <= 0) {
            logAcoes.add("Ataque Duplo indisponivel! Usos restantes: 0.");
            logAcoes.add("DANO TOTAL: 0"); // Necessário para o servidor saber que o dano é zero
            return logAcoes;
        }

        logAcoes.add("--- HABILIDADE: ATAQUE DUPLO (Usos: " + usosAtaqueDuplo + "/" + usosAtaqueDuploMaximo + ") ---");
        
        LogAtaque ataqueEsquerdo = executarAtaqueInterno(
            nomeEspadaEsquerda, 
            danoEspadaEsquerda, 
            nomeAlvo, 
            defesaAlvo,
            true
        );
        logAcoes.add(ataqueEsquerdo.toString());

        LogAtaque ataqueDireito = executarAtaqueInterno(
            nomeEspadaDireita, 
            danoEspadaDireita, 
            nomeAlvo, 
            defesaAlvo,
            true
        );
        logAcoes.add(ataqueDireito.toString());

        historicoAtaques.add(ataqueEsquerdo);
        historicoAtaques.add(ataqueDireito);

        int danoBaseTotal = ataqueEsquerdo.danoFinal + ataqueDireito.danoFinal;
        int danoTotalAposCritico = danoBaseTotal;
        int acertos = (ataqueEsquerdo.acertou ? 1 : 0) + (ataqueDireito.acertou ? 1 : 0);

        String resultadoAtaque;

        if (acertos == 2) {
            int bonusCritico = danoBaseTotal; 
            danoTotalAposCritico += bonusCritico; 
            resultadoAtaque = String.format("DANO CRITICO! As duas espadas acertaram! (Bonus: +%d)", bonusCritico);
            comboAtual = Math.min(comboAtual + 2, comboMaximo);
        } else if (acertos == 1) {
            resultadoAtaque = "Um acerto, um bloqueio. (Dano Normal)";
            comboAtual = Math.min(comboAtual + 1, comboMaximo);
        } else {
            resultadoAtaque = "Ambas espadas bloqueadas! Nenhum dano causado.";
            comboAtual = 0;
        }
        
        logAcoes.add(">> Resultado: " + resultadoAtaque + " <<");
        logAcoes.add("DANO TOTAL: " + danoTotalAposCritico);

        usosAtaqueDuplo--;
        
        return logAcoes;
    }
    
    // --- HABILIDADES DE FURIA/COMBO: DANO É RETORNADO NO PRIMEIRO ITEM DA LISTA ---
    // Alterado: Removemos "DANO TOTAL: X" dos logs para que o servidor possa aplicar o dano.

    public List<String> redemoinhoMortal(String nomeAlvo, int defesaAlvo) {
        if (furia < 30) return List.of("0", "Furia insuficiente (30 requerido).");
        furia -= 30;
        int dano = 50; 
        return List.of(String.valueOf(dano), "Redemoinho Mortal! (Furia consumida)");
    }

    public List<String> execucaoCruzada(String nomeAlvo, int defesaAlvo) {
        if (furia < 50) return List.of("0", "Furia insuficiente (50 requerido).");
        furia -= 50;
        int dano = 80;
        return List.of(String.valueOf(dano), "Execucao Cruzada! (Furia consumida)");
    }

    public List<String> furiaDesenfreada(String nomeAlvo, int defesaAlvo) {
        if (furia < 70) return List.of("0", "Furia insuficiente (70 requerido).");
        furia -= 70;
        return List.of("0", "Furia Desenfreada: Aumento de Ataque por 3 turnos! (Furia consumida)");
    }

    public List<String> comboFinalizador(String nomeAlvo, int defesaAlvo) {
        if (comboAtual < 3) return List.of("0", "Combo insuficiente (3 requerido).");
        comboAtual = 0;
        int dano = 100;
        return List.of(String.valueOf(dano), "Combo Finalizador! Dano baseado no combo! (Combo resetado)");
    }
    
    // --- FIM HABILIDADES ---

    public List<String> exibirEstatisticasCombate() {
        List<String> log = new ArrayList<>();
        log.add("--- ESTATÍSTICAS BERSERKER ---");
        log.add("Total de Ataques no Combate: " + historicoAtaques.size());
        log.add("Furia Atual: " + furia + "/" + furiaMax);
        log.add("Combo Atual: " + comboAtual + "/" + comboMaximo);
        log.add("Usos Ataque Duplo Restantes: " + usosAtaqueDuplo);
        return log;
    }
    
    public int getFuria() { return furia; }
    public int getFuriaMax() { return furiaMax; }
    public int getComboAtual() { return comboAtual; }
    public int getComboMaximo() { return comboMaximo; }
    public int getUsosAtaqueDuplo() { return usosAtaqueDuplo; }
    public int getUsosAtaqueDuploMaximo() { return usosAtaqueDuploMaximo; }

    private static class LogAtaque {
        String nomeArma;
        int danoFinal;
        int dado;
        boolean acertou;

        public LogAtaque(String nomeArma, int danoFinal, int dado, boolean acertou) {
            this.nomeArma = nomeArma;
            this.danoFinal = danoFinal;
            this.dado = dado;
            this.acertou = acertou;
        }

        @Override
        public String toString() {
            return String.format(
                " [%s] Dado: %d | Dano: %d (%s)", 
                nomeArma, 
                dado, 
                danoFinal, 
                acertou ? "ACERTOU" : "BLOQUEOU"
            );
        }
    }
}
