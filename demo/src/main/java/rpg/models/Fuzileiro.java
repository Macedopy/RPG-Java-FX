package rpg.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fuzileiro extends Personagem {
    private int municao;
    private int municaoMax;
    private int chanceCritico;
    private double multiplicadorCritico;
    private List<LogBala> historicoTiros;
    private int usosRajadaRestantes;

    public Fuzileiro(String nome) {
        super(nome, 120, 18, 10, 1);
        this.municao = 10;
        this.municaoMax = 10;
        this.chanceCritico = 9;
        this.multiplicadorCritico = 1.75;
        this.historicoTiros = new ArrayList<>();
        this.usosRajadaRestantes = 2;
    }

    public Fuzileiro(Fuzileiro outro) {
        super(outro);
        this.municao = outro.municao;
        this.municaoMax = outro.municaoMax;
        this.chanceCritico = outro.chanceCritico;
        this.multiplicadorCritico = outro.multiplicadorCritico;
        this.historicoTiros = new ArrayList<>(outro.historicoTiros);
        this.usosRajadaRestantes = outro.usosRajadaRestantes;
    }

    @Override
    public String getClasse() {
        return "Fuzileiro";
    }

    @Override
    public String getHabilidadeEspecial() {
        return "Rajada Precisa - Dispara multiplas balas com chance de critico";
    }

    private String limparAcentos(String texto) {
        if (texto == null) return "";
        return texto.replace("ç", "c").replace("ã", "a").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o");
    }

    public void resetarUsosRajada() {
        this.usosRajadaRestantes = 2; 
    }

    public ResultadoTiro dispararBala(String nomeAlvo, int numeroTiro) {
        if (municao <= 0) {
            return null;
        }
        Random random = new Random();

        municao--;

        int dado = rolarDado();
        int danoBase = ataque + dado;

        boolean foiCritico = random.nextInt(100) < chanceCritico;
        int danoFinal = danoBase;
        
        if (foiCritico) {
            danoFinal = (int) (danoBase * multiplicadorCritico);
        }

        double porcentagemDano = (danoFinal * 100.0) / pontosVidaMax;

        LogBala log = new LogBala(
            numeroTiro,
            nomeAlvo,
            danoBase,
            danoFinal,
            porcentagemDano,
            foiCritico,
            dado,
            municao
        );

        historicoTiros.add(log);

        return new ResultadoTiro(danoFinal, log);
    }

    public ResultadoRajada rajadaPrecisa(String nomeAlvo) {
        List<String> log = new ArrayList<>();
    
        if (municao < 3) {
            log.add("Municao insuficiente para Rajada Precisa (Requer 3).");
            return new ResultadoRajada(new ArrayList<>(), 0, 0, log); 
        }
        
        if (usosRajadaRestantes <= 0) {
            log.add("Limite de uso da Rajada Precisa por combate excedido! (0/2)");
            return new ResultadoRajada(new ArrayList<>(), 0, 0, log); 
        }

        int ataqueOriginal = ataque;
        ataque = (int) (ataque * 0.75);

        List<ResultadoTiro> tiros = new ArrayList<>();
        int danoTotal = 0;
        int criticosAcertados = 0;
        List<String> logRajada = new ArrayList<>();

        logRajada.add("RAJADA PRECISA ATIVADA!");

        for (int i = 1; i <= 3; i++) {
            ResultadoTiro tiro = dispararBala(nomeAlvo, historicoTiros.size());
            if (tiro != null) {
                tiros.add(tiro);
                danoTotal += tiro.danoFinal;
                
                if (tiro.log.foiCritico) {
                    criticosAcertados++;
                }

                logRajada.add(tiro.log.toString());
                
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        ataque = ataqueOriginal;
        usosRajadaRestantes--;
        
        logRajada.add("\n--- RESUMO DA RAJADA ---");
        logRajada.add(String.format("DANO TOTAL: %d", danoTotal));
        logRajada.add(String.format("Criticos: %d/3", criticosAcertados));
        logRajada.add(String.format("Municao Restante: %d/%d", municao, municaoMax));
        logRajada.add(String.format("Usos restantes da Rajada: %d/2", usosRajadaRestantes));

        return new ResultadoRajada(tiros, danoTotal, criticosAcertados, logRajada);
    }

    public ResultadoTiro tiroDePrecisao(String nomeAlvo) {
        if (municao <= 0) {
            return null;
        }

        int chanceOriginal = chanceCritico;
        chanceCritico = Math.min(50, chanceCritico + 30);

        ResultadoTiro resultado = dispararBala(nomeAlvo, historicoTiros.size());
        
        chanceCritico = chanceOriginal;

        return resultado;
    }

    public List<String> recarregar() {
        List<String> log = new ArrayList<>();
        int municaoRecarregada = municaoMax - municao;
        municao = municaoMax;
        
        log.add(String.format("RECARREGANDO... +%d balas", municaoRecarregada));
        log.add(String.format("Municao: %d/%d", municao, municaoMax));
        
        return log;
    }

    public List<String> exibirEstatisticasTiro() {
        List<String> log = new ArrayList<>();

        if (historicoTiros.isEmpty()) {
            log.add("Nenhum tiro disparado ainda.");
            return log;
        }

        int totalTiros = historicoTiros.size();
        int totalCriticos = 0;
        int danoTotal = 0;
        int danoMaximo = 0;
        int danoMinimo = Integer.MAX_VALUE;

        for (LogBala logBala : historicoTiros) {
            if (logBala.foiCritico) totalCriticos++;
            danoTotal += logBala.danoFinal;
            danoMaximo = Math.max(danoMaximo, logBala.danoFinal);
            danoMinimo = Math.min(danoMinimo, logBala.danoFinal);
        }
        
        if (danoMinimo == Integer.MAX_VALUE) danoMinimo = 0;

        double mediaRotacao = (double) danoTotal / totalTiros;
        double taxaCritico = (totalCriticos * 100.0) / totalTiros;

        log.add("ESTATISTICAS DE COMBATE");
        log.add(String.format("Total de Tiros: %d", totalTiros));
        log.add(String.format("Criticos: %d (%.1f%%)", totalCriticos, taxaCritico));
        log.add(String.format("Dano Total: %d", danoTotal));
        log.add(String.format("Dano Medio: %.1f", mediaRotacao));
        log.add(String.format("Dano Maximo: %d", danoMaximo));
        log.add(String.format("Dano Minimo: %d", danoMinimo));
        
        return log;
    }

    public void limparHistorico() {
        historicoTiros.clear();
    }

    public String aumentarChanceCritico(int aumento) {
        chanceCritico = Math.min(100, chanceCritico + aumento);
        return String.format("Chance de critico aumentada para %d%%", chanceCritico);
    }

    public String aumentarMultiplicadorCritico(double aumento) {
        multiplicadorCritico += aumento;
        return String.format("Multiplicador critico: %.2fx", multiplicadorCritico);
    }

    public int getMunicao() { return municao; }
    public int getMunicaoMax() { return municaoMax; }
    public int getChanceCritico() { return chanceCritico; }
    public double getMultiplicadorCritico() { return multiplicadorCritico; }
    public List<LogBala> getHistoricoTiros() { return new ArrayList<>(historicoTiros); }
    public int getUsosRajadaRestantes() { return usosRajadaRestantes; }

    public void setMunicao(int municao) { this.municao = Math.min(municao, municaoMax); }
    public void setMunicaoMax(int municaoMax) { 
        this.municaoMax = municaoMax;
        if (this.municao > municaoMax) this.municao = municaoMax;
    }

    public static class LogBala {
        public final int numeroTiro;
        public final String alvo;
        public final int danoBase;
        public final int danoFinal;
        public final double porcentagemDano;
        public final boolean foiCritico;
        public final int valorDado;
        public final int municaoRestante;
        public final long timestamp;

        public LogBala(int numeroTiro, String alvo, int danoBase, int danoFinal, 
                        double porcentagemDano, boolean foiCritico, int valorDado, 
                        int municaoRestante) {
            this.numeroTiro = numeroTiro;
            this.alvo = alvo;
            this.danoBase = danoBase;
            this.danoFinal = danoFinal;
            this.porcentagemDano = porcentagemDano;
            this.foiCritico = foiCritico;
            this.valorDado = valorDado;
            this.municaoRestante = municaoRestante;
            this.timestamp = System.currentTimeMillis();
        }

        private String limparTexto(String texto) {
            return texto.replace("ç", "c").replace("ã", "a").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o");
        }

        @Override
        public String toString() {
            String statusCritico = foiCritico ? " CRITICO!" : "";
            
            return String.format("Bala #%d -> %s | Dano: %d%s | Municao: %d", 
                numeroTiro, limparTexto(alvo), danoFinal, statusCritico, municaoRestante);
        }
    }

    public static class ResultadoTiro {
        public final int danoFinal;
        public final LogBala log;

        public ResultadoTiro(int danoFinal, LogBala log) {
            this.danoFinal = danoFinal;
            this.log = log;
        }
    }

    public static class ResultadoRajada {
        public final List<ResultadoTiro> tiros;
        public final int danoTotal;
        public final int criticosAcertados;
        public final List<String> logCompleto;

        public ResultadoRajada(List<ResultadoTiro> tiros, int danoTotal, int criticosAcertados, List<String> logCompleto) {
            this.tiros = tiros;
            this.danoTotal = danoTotal;
            this.criticosAcertados = criticosAcertados;
            this.logCompleto = logCompleto;
        }

        public boolean todosForamCriticos() {
            if (tiros == null) return false;
            return criticosAcertados == tiros.size();
        }

        public double porcentagemCriticos() {
            if (tiros == null || tiros.isEmpty()) return 0.0;
            return (criticosAcertados * 100.0) / tiros.size();
        }
    }
}
