package rpg.models;

public class Item implements Comparable<Item>, Cloneable {
    private String nome;
    private String descricao;
    private String efeito;
    private int valorEfeito;
    private int quantidade;

    public Item(String nome, String descricao, String efeito, int valorEfeito) {
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.valorEfeito = valorEfeito;
        this.quantidade = 1;
    }

    public Item(String nome, String descricao, String efeito, int valorEfeito, int quantidade) {
        this(nome, descricao, efeito, valorEfeito);
        this.quantidade = quantidade;
    }

    public Item(Item outro) {
        this.nome = outro.nome;
        this.descricao = outro.descricao;
        this.efeito = outro.efeito;
        this.valorEfeito = outro.valorEfeito;
        this.quantidade = outro.quantidade;
    }

    @Override
    public Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Item(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return nome.equalsIgnoreCase(item.nome);
    }

    @Override
    public int compareTo(Item outro) {
        return this.nome.compareToIgnoreCase(outro.nome);
    }

    public void aumentarQuantidade(int qtd) {
        this.quantidade += qtd;
    }

    public boolean diminuirQuantidade(int qtd) {
        if (quantidade >= qtd) {
            quantidade -= qtd;
            return true;
        }
        return false;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getEfeito() { return efeito; }
    public int getValorEfeito() { return valorEfeito; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    private String limparAcentos(String texto) {
        if (texto == null) return "";
        return texto.replace("ç", "c")
                    .replace("Ç", "C")
                    .replace("á", "a")
                    .replace("Á", "A")
                    .replace("é", "e")
                    .replace("É", "E")
                    .replace("í", "i")
                    .replace("Í", "I")
                    .replace("ó", "o")
                    .replace("Ó", "O")
                    .replace("ú", "u")
                    .replace("Ú", "U")
                    .replace("ã", "a")
                    .replace("õ", "o")
                    .replace("â", "a")
                    .replace("ê", "e")
                    .replace("ô", "o")
                    .replace("à", "a")
                    .replace("À", "A")
                    .replace("'", "")
                    .trim();
    }

    @Override
    public String toString() {
        String nomeLimpo = limparAcentos(nome);
        String descricaoLimpa = limparAcentos(descricao);
        String efeitoLimpo = limparAcentos(efeito);

        String resultado = String.format("%s (x%d) - %s [%s: %d]", 
            nomeLimpo, quantidade, descricaoLimpa, efeitoLimpo, valorEfeito);

        int maxLen = 70; 
        if (resultado.length() > maxLen) {
            return resultado.substring(0, maxLen - 3) + "...";
        }
        
        return resultado;
    }
}
