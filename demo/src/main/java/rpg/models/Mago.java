package rpg.models;

public class Mago extends Personagem {
    private int mana;
    private int manaMax;
    private static final int CUSTO_FANTASMAS = 30;
    private static final int CUSTO_RAIO = 25;
    private static final int CUSTO_FOGO = 20;

    public Mago(String nome) {
        super(nome, 100, 25, 8, 1);
        this.mana = 100;
        this.manaMax = 100;
    }

    public Mago(Mago outro) {
        super(outro);
        this.mana = outro.mana;
        this.manaMax = outro.manaMax;
    }

    @Override
    public String getClasse() {
        return "Mago";
    }

    @Override
    public String getHabilidadeEspecial() {
        return "Bola de Fogo (Custo: " + CUSTO_FOGO + " mana) | " +
               "Toque dos Fantasmas (Custo: " + CUSTO_FANTASMAS + " mana) | " +
               "Raio Arcano (Custo: " + CUSTO_RAIO + " mana)";
    }

    public int bolaDeFogo() {
        if (mana >= CUSTO_FOGO) {
            mana -= CUSTO_FOGO;
            return ataque + rolarDado() + 15; 
        }
        return 0;
    }

    public int toqueDosFantasmas() {
        if (mana >= CUSTO_FANTASMAS) {
            mana -= CUSTO_FANTASMAS;
            
            int resultadoDado = rolarDado();
            int acertos;

            if (resultadoDado <= 2) {
                acertos = 1;
            } else if (resultadoDado <= 4) {
                acertos = 2;
            } else {
                acertos = 3;
            }

            int danoPorFantasma = ataque + 5; 
            int danoTotal = danoPorFantasma * acertos;
            
            System.out.println(this.getNome() + " conjura 3 fantasmas e acerta " + acertos + " deles!");
            
            return danoTotal;
        }
        return 0;
    }

    public int raioArcano() {
        if (mana >= CUSTO_RAIO) {
            mana -= CUSTO_RAIO;
            return ataque + rolarDado() + 20; 
        }
        return 0;
    }

    public int getMana() { return mana; }
    public int getManaMax() { return manaMax; }
    
    public void restaurarMana(int quantidade) {
        this.mana = Math.min(this.mana + quantidade, this.manaMax);
    }
}
