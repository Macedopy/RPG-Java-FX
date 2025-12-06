package rpg.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventario implements Cloneable {
    private List<Item> itens;
    private int capacidadeMaxima;

    public Inventario() {
        this.itens = new ArrayList<>();
        this.capacidadeMaxima = 20;
    }

    public Inventario(int capacidade) {
        this.itens = new ArrayList<>();
        this.capacidadeMaxima = capacidade;
    }

    public Inventario(Inventario outro) {
        this.itens = new ArrayList<>();
        this.capacidadeMaxima = outro.capacidadeMaxima;
        for (Item item : outro.itens) {
            this.itens.add(new Item(item));
        }
    }

    @Override
    public Inventario clone() {
        try {
            Inventario clonado = (Inventario) super.clone();
            clonado.itens = new ArrayList<>();
            for (Item item : this.itens) {
                clonado.itens.add(item.clone());
            }
            return clonado;
        } catch (CloneNotSupportedException e) {
            return new Inventario(this);
        }
    }

    public boolean adicionarItem(Item novoItem) {
        if (contarItensTotal() >= capacidadeMaxima) {
            return false;
        }

        for (Item item : itens) {
            if (item.equals(novoItem)) {
                item.aumentarQuantidade(novoItem.getQuantidade());
                return true;
            }
        }

        itens.add(new Item(novoItem));
        return true;
    }

    public boolean removerItem(String nomeItem, int quantidade) {
        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);
            if (item.getNome().equalsIgnoreCase(nomeItem)) {
                if (item.diminuirQuantidade(quantidade)) {
                    if (item.getQuantidade() <= 0) {
                        itens.remove(i);
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public Item buscarItem(String nomeItem) {
        for (Item item : itens) {
            if (item.getNome().equalsIgnoreCase(nomeItem)) {
                return item;
            }
        }
        return null;
    }

    public Item buscarItem(int indice) {
        java.util.Collections.sort(itens);

        if (indice >= 0 && indice < itens.size()) {
            return itens.get(indice);
        }
        return null;
    }

    public java.util.List<String> listarItensLista() {
        java.util.List<String> linhas = new java.util.ArrayList<>();

        if (itens.isEmpty()) {
            linhas.add("Inventario vazio.");
            return linhas;
        }

        java.util.Collections.sort(itens);

        linhas.add("INVENTARIO");

        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);

            String itemString = item.toString().trim();

            String linhaItem = (i + 1) + ". " + itemString;

            linhaItem = linhaItem.replace("ç", "c").replace("ã", "a").replace("á", "a").replace("é", "e")
                    .replace("í", "i").replace("ó", "o").replace("ú", "u");

            linhas.add(linhaItem);
        }

        return linhas;
    }

    public List<String> listarItensListaComCodigo() {
        List<String> listaFormatada = new ArrayList<>();

        java.util.Collections.sort(itens);

        for (int i = 0; i < itens.size(); i++) {
            Item item = itens.get(i);

            int codigo = i + 1;

            String itemString = item.toString().trim();
            listaFormatada.add(
                    String.format("%d. %s", codigo, itemString));
        }
        return listaFormatada;
    }

    public String getNomeItemPeloIndice(int indice) {
        java.util.Collections.sort(itens);

        if (indice >= 0 && indice < itens.size()) {
            return itens.get(indice).getNome();
        }
        return null;
    }

    private int contarItensTotal() {
        int total = 0;
        for (Item item : itens) {
            total += item.getQuantidade();
        }
        return total;
    }

    public List<Item> getItens() {
        return new ArrayList<>(itens);
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }
}
