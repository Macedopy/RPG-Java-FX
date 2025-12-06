// ========== Personagem.java ==========
package rpg.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Personagem {
    protected String nome;
    protected int pontosVida;
    protected int pontosVidaMax;
    protected int ataque;
    protected int defesa;
    protected int nivel;
    protected Inventario inventario;
    protected Random random;

    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.pontosVida = pontosVida;
        this.pontosVidaMax = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.inventario = new Inventario();
        this.random = new Random();
    }

    public Personagem(Personagem outro) {
        this.nome = outro.nome;
        this.pontosVida = outro.pontosVida;
        this.pontosVidaMax = outro.pontosVidaMax;
        this.ataque = outro.ataque;
        this.defesa = outro.defesa;
        this.nivel = outro.nivel;
        this.inventario = new Inventario(outro.inventario);
        this.random = new Random();
    }

    public abstract String getClasse();

    public abstract String getHabilidadeEspecial();

    public int rolarDado() {
        return random.nextInt(20) + 1;
    }

    public int calcularDano() {
        return ataque + rolarDado();
    }

    public void receberDano(int dano) {
        int danoReal = Math.max(0, dano - defesa);
        pontosVida -= danoReal;
        if (pontosVida < 0)
            pontosVida = 0;
    }

    public void curar(int quantidade) {
        pontosVida += quantidade;
        if (pontosVida > pontosVidaMax) {
            pontosVida = pontosVidaMax;
        }
    }

    public List<String> usarItem(String nomeItem) {
        Item item = inventario.buscarItem(nomeItem);
        List<String> logs = new ArrayList<>();

        if (item == null || item.getQuantidade() <= 0) {
            return null;
        }

        switch (item.getEfeito().toLowerCase()) {
            case "cura":
                curar(item.getValorEfeito());
                logs.add(nome + " usou " + item.getNome() +
                        " e recuperou " + item.getValorEfeito() + " HP!");
                break;
            case "ataque":
                ataque += item.getValorEfeito();
                logs.add(nome + " usou " + item.getNome() +
                        " e aumentou ataque em " + item.getValorEfeito() + "!");
                break;
            case "defesa":
                defesa += item.getValorEfeito();
                logs.add(nome + " usou " + item.getNome() +
                        " e aumentou defesa em " + item.getValorEfeito() + "!");
                break;
            default:
                logs.add("Efeito do item não reconhecido ou não utilizável.");
                return logs;
        }

        inventario.removerItem(nomeItem, 1);

        return logs;
    }

    public boolean estaVivo() {
        return pontosVida > 0;
    }

    public void exibirStatus() {
        System.out.println("\n=== " + nome + " (" + getClasse() + ") ===");
        System.out.println("Nivel: " + nivel);
        System.out.println("HP: " + pontosVida + "/" + pontosVidaMax);
        System.out.println("Ataque: " + ataque);
        System.out.println("Defesa: " + defesa);
        System.out.println("Habilidade: " + getHabilidadeEspecial());
        System.out.println("========================\n");
    }

    // ==================== GETTERS E SETTERS ====================

    public String getNome() {
        return nome;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public int getPontosVidaMax() {
        return pontosVidaMax;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getNivel() {
        return nivel;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPontosVida(int pontosVida) {
        this.pontosVida = pontosVida;
    }

    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
}
