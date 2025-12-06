package rpg.models;

import java.util.Random;

public class Inimigo extends Personagem {
    private String profissaoAnterior;
    private String habilidadeEspecialInimigo;
    private int chanceHabilidadeEspecial;
    private int usoHabilidadeEspecial; // Contador de vezes que usou habilidade

    public Inimigo(String nome, String profissaoAnterior, int pontosVida,
            int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.profissaoAnterior = profissaoAnterior;
        this.usoHabilidadeEspecial = 0;
        configurarHabilidadeEspecial();
        gerarLoot();
    }

    public Inimigo(Inimigo outro) {
        super(outro);
        this.profissaoAnterior = outro.profissaoAnterior;
        this.habilidadeEspecialInimigo = outro.habilidadeEspecialInimigo;
        this.chanceHabilidadeEspecial = outro.chanceHabilidadeEspecial;
        this.usoHabilidadeEspecial = outro.usoHabilidadeEspecial;
    }

    private void configurarHabilidadeEspecial() {
        switch (profissaoAnterior.toLowerCase()) {
            case "advogado":
                habilidadeEspecialInimigo = "Voz da Autoridade - Paralisa com palavras legais";
                chanceHabilidadeEspecial = 25;
                break;
            case "médico":
                habilidadeEspecialInimigo = "Regeneração Celular - Cura-se usando conhecimento médico";
                chanceHabilidadeEspecial = 30;
                break;
            case "engenheiro":
                habilidadeEspecialInimigo = "Armadura Improvisada - Constrói proteção instantânea";
                chanceHabilidadeEspecial = 20;
                break;
            case "professor":
                habilidadeEspecialInimigo = "Conhecimento Ancestral - Prevê e evita ataques";
                chanceHabilidadeEspecial = 25;
                break;
            case "soldado":
                habilidadeEspecialInimigo = "Fúria de Combate - Ataque triplo devastador";
                chanceHabilidadeEspecial = 15;
                break;
            case "cientista":
                habilidadeEspecialInimigo = "Toxina Mutagênica - Envenena com compostos químicos";
                chanceHabilidadeEspecial = 20;
                break;
            case "policial":
                habilidadeEspecialInimigo = "Algemas de Aço - Imobiliza o oponente";
                chanceHabilidadeEspecial = 22;
                break;
            case "bombeiro":
                habilidadeEspecialInimigo = "Chamas Controladas - Queima o inimigo com fogo";
                chanceHabilidadeEspecial = 18;
                break;
            case "chef":
                habilidadeEspecialInimigo = "Lâminas Afiadas - Cortes precisos com facas";
                chanceHabilidadeEspecial = 23;
                break;
            case "mecânico":
                habilidadeEspecialInimigo = "Ferramentas Pesadas - Ataque com chaves e martelos";
                chanceHabilidadeEspecial = 20;
                break;
            case "atleta":
                habilidadeEspecialInimigo = "Velocidade Sobre-Humana - Múltiplos ataques rápidos";
                chanceHabilidadeEspecial = 28;
                break;
            case "especialista em TI":
                habilidadeEspecialInimigo = "Sobrecarga Elétrica - Choque elétrico de dispositivos";
                chanceHabilidadeEspecial = 21;
                break;
            case "artista":
                habilidadeEspecialInimigo = "Tinta Corrosiva - Ácido artístico que corrói";
                chanceHabilidadeEspecial = 19;
                break;
            case "músico":
                habilidadeEspecialInimigo = "Sônica Ensurdecedora - Ondas sonoras que atordoam";
                chanceHabilidadeEspecial = 24;
                break;
            case "agricultor":
                habilidadeEspecialInimigo = "Espinhos Mutantes - Plantas venenosas atacam";
                chanceHabilidadeEspecial = 17;
                break;
            case "jornalista":
                habilidadeEspecialInimigo = "Verdade Perturbadora - Revela segredos que enfraquecem";
                chanceHabilidadeEspecial = 20;
                break;
            case "piloto":
                habilidadeEspecialInimigo = "Manobra Evasiva - Esquiva perfeita";
                chanceHabilidadeEspecial = 26;
                break;
            case "arquiteto":
                habilidadeEspecialInimigo = "Estrutura Colapsante - Derruba escombros no inimigo";
                chanceHabilidadeEspecial = 19;
                break;
            default:
                habilidadeEspecialInimigo = "Ataque Selvagem - Fúria instintiva";
                chanceHabilidadeEspecial = 20;
        }
    }

    private void gerarLoot() {
        switch (profissaoAnterior.toLowerCase()) {
            case "advogado":
                inventario.adicionarItem(new Item("Algemas Antigas", "Algemas de metal oxidado", "ataque", 5, 1));
                inventario.adicionarItem(new Item("Codigo Civil Rasgado", "Paginas de leis antigas", "defesa", 3, 2));
                inventario.adicionarItem(new Item("Gravata de Seda", "Pode ser usada como corda", "ataque", 2, 1));
                break;

            case "medico":
                inventario.adicionarItem(new Item("Seringa Medicinal", "Seringa com liquido curativo", "cura", 30, 2));
                inventario.adicionarItem(new Item("Bisturi Afiado", "Lamina cirurgica precisa", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Atadura Esteril", "Curativo medico", "cura", 20, 3));
                inventario.adicionarItem(new Item("Antibiotico Raro", "Remedio poderoso", "cura", 50, 1));
                break;

            case "engenheiro":
                inventario.adicionarItem(new Item("Placa de Metal", "Protecao improvisada", "defesa", 8, 1));
                inventario.adicionarItem(new Item("Chave Inglesa", "Ferramenta pesada", "ataque", 6, 1));
                inventario.adicionarItem(new Item("Capacete de Seguranca", "Protecao para cabeca", "defesa", 5, 1));
                inventario.adicionarItem(new Item("Blueprint Tecnico", "Projeto de arma", "ataque", 4, 2));
                break;

            case "professor":
                inventario.adicionarItem(new Item("Livro Antigo", "Conhecimento preservado", "cura", 20, 1));
                inventario.adicionarItem(new Item("Oculos Quebrados", "Aumenta percepcao", "ataque", 4, 1));
                inventario.adicionarItem(new Item("Regua de Metal", "Pode ser usada como arma", "ataque", 3, 1));
                inventario.adicionarItem(new Item("Enciclopedia Rasgada", "Sabedoria antiga", "defesa", 6, 1));
                break;

            case "soldado":
                inventario.adicionarItem(
                        new Item("Fuzil Enferrujado", "Arma de longo alcance enferrujada", "ataque", 25, 1));
                inventario.adicionarItem(new Item("Kit Militar", "Suprimentos de combate", "cura", 25, 1));
                inventario.adicionarItem(new Item("Faca de Combate", "Lamina militar afiada", "ataque", 10, 1));
                inventario.adicionarItem(new Item("Colete Balistico", "Protecao militar", "defesa", 12, 1));
                inventario.adicionarItem(new Item("Municao 7.62", "Balas para fuzil", "ataque", 8, 5));
                inventario.adicionarItem(new Item("Granada Velha", "Explosivo instavel", "ataque", 30, 1));
                break;

            case "cientista":
                inventario.adicionarItem(new Item("Frasco Quimico", "Substancia curativa", "cura", 35, 1));
                inventario.adicionarItem(new Item("Acido Corrosivo", "Liquido perigoso", "ataque", 8, 2));
                inventario.adicionarItem(new Item("Luvas de Latex", "Protecao quimica", "defesa", 4, 1));
                inventario.adicionarItem(new Item("Composto Experimental", "Droga nao testada", "ataque", 12, 1));
                inventario.adicionarItem(new Item("Jaleco Reforcado", "Protecao contra quimicos", "defesa", 7, 1));
                break;

            case "policial":
                inventario.adicionarItem(new Item("Algemas de Aco", "Para imobilizar", "ataque", 6, 1));
                inventario.adicionarItem(new Item("Cassetete", "Arma nao letal", "ataque", 8, 1));
                inventario.adicionarItem(new Item("Escudo Tatico", "Protecao policial", "defesa", 10, 1));
                inventario.adicionarItem(new Item("Kit Primeiros Socorros", "Suprimentos medicos", "cura", 30, 1));
                inventario.adicionarItem(new Item("Colete a Prova de Balas", "Protecao balistica", "defesa", 15, 1));
                break;

            case "bombeiro":
                inventario.adicionarItem(new Item("Machado de Bombeiro", "Ferramenta pesada", "ataque", 15, 1));
                inventario
                        .adicionarItem(new Item("Mangueira Reforcada", "Pode ser usada como chicote", "ataque", 5, 1));
                inventario.adicionarItem(new Item("Mascara de Gas", "Protecao respiratoria", "defesa", 8, 1));
                inventario.adicionarItem(new Item("Extintor", "Conteudo quimico util", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Roupa Termica", "Protecao contra calor", "defesa", 12, 1));
                break;

            case "chef":
                inventario.adicionarItem(new Item("Faca de Chef", "Lamina extremamente afiada", "ataque", 12, 1));
                inventario.adicionarItem(new Item("Cutelo", "Corta ate ossos", "ataque", 14, 1));
                inventario.adicionarItem(new Item("Espeto de Metal", "Arma perfurante", "ataque", 8, 2));
                inventario.adicionarItem(new Item("Avental Reforcado", "Protecao leve", "defesa", 6, 1));
                inventario.adicionarItem(new Item("Tempero Curativo", "Ervas medicinais", "cura", 25, 2));
                break;

            case "mecanico":
                inventario.adicionarItem(new Item("Chave de Fenda Gigante", "Ferramenta pesada", "ataque", 10, 1));
                inventario.adicionarItem(new Item("Corrente de Metal", "Pode ser usada como chicote", "ataque", 9, 1));
                inventario.adicionarItem(new Item("Oleo de Motor", "Liquido inflamavel", "ataque", 6, 2));
                inventario.adicionarItem(new Item("Luvas de Trabalho", "Protecao para maos", "defesa", 5, 1));
                inventario.adicionarItem(new Item("Peca de Armadura", "Metal resistente", "defesa", 11, 1));
                break;

            case "atleta":
                inventario.adicionarItem(new Item("Barra de Proteina", "Energia rapida", "cura", 20, 3));
                inventario.adicionarItem(new Item("Peso de Academia", "Arma contundente", "ataque", 11, 1));
                inventario.adicionarItem(new Item("Tenis Reforcados", "Mobilidade aumentada", "defesa", 7, 1));
                inventario.adicionarItem(new Item("Bandagem Elastica", "Suporte e cura", "cura", 15, 2));
                inventario.adicionarItem(new Item("Luvas de Boxe", "Protecao e ataque", "ataque", 9, 1));
                break;

            case "especialista em ti":
                inventario.adicionarItem(new Item("Dispositivo Eletronico", "Gadget quebrado", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Cabo USB Reforcado", "Pode ser usado como corda", "ataque", 4, 2));
                inventario.adicionarItem(new Item("Bateria Portatil", "Fonte de energia", "ataque", 8, 1));
                inventario.adicionarItem(new Item("Placa-Mae Afiada", "Componente cortante", "ataque", 6, 1));
                inventario.adicionarItem(new Item("Manual Tecnico", "Conhecimento util", "defesa", 5, 1));
                break;

            case "artista":
                inventario.adicionarItem(new Item("Pincel com Tinta Toxica", "Pincel venenoso", "ataque", 6, 2));
                inventario.adicionarItem(new Item("Espatula Afiada", "Ferramenta cortante", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Tela Reforcada", "Protecao improvisada", "defesa", 5, 1));
                inventario.adicionarItem(new Item("Tinta Medicinal", "Pigmentos curativos", "cura", 18, 1));
                inventario.adicionarItem(new Item("Cinzel de Escultura", "Arma perfurante", "ataque", 9, 1));
                break;

            case "musico":
                inventario.adicionarItem(new Item("Corda de Violao", "Pode estrangular", "ataque", 5, 3));
                inventario.adicionarItem(new Item("Bateria de Instrumento", "Arma contundente", "ataque", 8, 1));
                inventario.adicionarItem(new Item("Flauta de Metal", "Arma improvisada", "ataque", 6, 1));
                inventario.adicionarItem(new Item("Amplificador Quebrado", "Componentes uteis", "defesa", 7, 1));
                inventario.adicionarItem(new Item("Partitura Antiga", "Conhecimento musical", "cura", 15, 1));
                break;

            case "agricultor":
                inventario.adicionarItem(new Item("Foice Enferrujada", "Lamina curva afiada", "ataque", 13, 1));
                inventario.adicionarItem(new Item("Sementes Mutantes", "Plantas venenosas", "ataque", 8, 2));
                inventario.adicionarItem(new Item("Ervas Medicinais", "Cura natural", "cura", 28, 2));
                inventario.adicionarItem(new Item("Enxada Pesada", "Ferramenta agricola", "ataque", 10, 1));
                inventario.adicionarItem(new Item("Luvas de Jardinagem", "Protecao basica", "defesa", 4, 1));
                break;

            case "jornalista":
                inventario.adicionarItem(new Item("Camera Pesada", "Equipamento contundente", "ataque", 8, 1));
                inventario.adicionarItem(new Item("Caneta de Metal", "Arma perfurante", "ataque", 4, 3));
                inventario.adicionarItem(new Item("Gravador Antigo", "Componentes uteis", "defesa", 5, 1));
                inventario.adicionarItem(new Item("Notebook Quebrado", "Placa de protecao", "defesa", 7, 1));
                inventario.adicionarItem(new Item("Artigo Inspirador", "Motivacao que cura", "cura", 22, 1));
                break;

            case "piloto":
                inventario.adicionarItem(new Item("Oculos de Aviador", "Protecao e visao", "defesa", 6, 1));
                inventario.adicionarItem(new Item("Peca de Aviao", "Metal resistente", "ataque", 11, 1));
                inventario.adicionarItem(new Item("Colete Salva-Vidas", "Protecao flutuante", "defesa", 8, 1));
                inventario.adicionarItem(new Item("Kit de Sobrevivencia", "Suprimentos variados", "cura", 30, 1));
                inventario.adicionarItem(new Item("Sinalizador", "Luz e calor", "ataque", 7, 2));
                break;

            case "arquiteto":
                inventario.adicionarItem(new Item("Esquadro de Metal", "Ferramenta afiada", "ataque", 9, 1));
                inventario.adicionarItem(new Item("Prancha de Desenho", "Escudo improvisado", "defesa", 10, 1));
                inventario.adicionarItem(new Item("Compasso Gigante", "Arma perfurante", "ataque", 8, 1));
                inventario.adicionarItem(new Item("Capacete de Obra", "Protecao de cabeca", "defesa", 7, 1));
                inventario.adicionarItem(new Item("Blueprint Raro", "Conhecimento construtivo", "ataque", 6, 1));
                break;

            case "carteira de trabalho":
                inventario.adicionarItem(new Item("Cracha Rasgado", "Identificacao danificada", "defesa", 5, 1));
                inventario.adicionarItem(new Item("Onibus lotado", "Joga um onibus lotado de clt", "ataque", 4, 2));
                inventario.adicionarItem(new Item("Carteira Deteriorada", "Contem documentos velhos", "defesa", 6, 1));
                inventario.adicionarItem(new Item("6x1", "Coloca o trabalhador no Mc donalds", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Imposto", "A arma mais letal de todas", "ataque", 50, 1));
                break;

            default:
                inventario.adicionarItem(new Item("Pedra Afiada", "Arma primitiva", "ataque", 5, 2));
                inventario.adicionarItem(new Item("Madeira", "Clava improvisada", "ataque", 7, 1));
                inventario.adicionarItem(new Item("Pele de Animal", "Protecao basica", "defesa", 4, 1));
                inventario.adicionarItem(new Item("Erva Selvagem", "Cura primitiva", "cura", 15, 1));
                break;
        }
    }

    public boolean tentarHabilidadeEspecial() {
        Random random = new Random();
        return random.nextInt(100) < chanceHabilidadeEspecial;
    }

    public int usarHabilidadeEspecial() {
        usoHabilidadeEspecial++;
        Random random = new Random();

        switch (profissaoAnterior.toLowerCase()) {
            case "médico":
                int cura = random.nextInt(20) + 15;
                curar(cura);
                System.out.println("Vida " + nome + " usa REGENERACAO CELULAR e recupera " + cura + " HP!");
                return 0;

            case "engenheiro":
                defesa += 10;
                System.out.println("Defesa " + nome + " usa ARMADURA IMPROVISADA! Defesa +10");
                return 0;

            case "soldado":
                int danoSoldado = calcularDano() * 3;
                System.out.println("Dano " + nome + " usa FURIA DE COMBATE! Ataque triplo!");
                return danoSoldado;

            case "cientista":
                int danoCientista = calcularDano() + 5;
                System.out.println("Habilidade " + nome + " usa TOXINA MUTAGENICA! Veneno aplicado!");
                return danoCientista;

            case "advogado":
                System.out.println("Habilidade " + nome + " usa VOZ DA AUTORIDADE! Tentativa de paralisia!");
                return calcularDano() + 8;

            case "policial":
                System.out.println("Habilidade " + nome + " usa ALGEMAS DE ACO! Tentativa de imobilizacao!");
                return calcularDano() + 7;

            case "bombeiro":
                int danoBombeiro = calcularDano() + 12;
                System.out.println("Habilidade " + nome + " usa CHAMAS CONTROLADAS! Queimadura severa!");
                return danoBombeiro;

            case "chef":
                int danoChef = calcularDano() + 10;
                System.out.println("Habilidade " + nome + " usa LAMINAS AFIADAS! Cortes precisos!");
                return danoChef;

            case "atleta":
                int danoAtleta = calcularDano() * 2;
                System.out.println("Habilidade " + nome + " usa VELOCIDADE SOBRE-HUMANA! Ataque duplo!");
                return danoAtleta;

            case "especialista em TI":
                int danoTI = calcularDano() + 9;
                System.out.println("Habilidade " + nome + " usa SOBRECARGA ELTERICA! Choque!");
                return danoTI;

            case "músico":
                System.out.println("Habilidade " + nome + " usa SONICA ENSURDECEDORA! Ondas sonoras!");
                return calcularDano() + 8;

            case "agricultor":
                int danoAgricultor = calcularDano() + 6;
                System.out.println("Habilidade " + nome + " usa ESPINHOS MUTANTES! Plantas venenosas!");
                return danoAgricultor;

            default:
                int danoGenerico = calcularDano() + 10;
                System.out.println("Habilidade " + nome + " usa ATAQUE SELVAGEM! Furia instintiva!");
                return danoGenerico;
        }
    }

    @Override
    public String getClasse() {
        return "Monstro (" + profissaoAnterior + ")";
    }

    @Override
    public String getHabilidadeEspecial() {
        return habilidadeEspecialInimigo;
    }

    public String getProfissaoAnterior() {
        return profissaoAnterior;
    }

    public int getUsoHabilidadeEspecial() {
        return usoHabilidadeEspecial;
    }

    /**
     * Exibe informações detalhadas do inimigo
     */
    public void exibirInfoCompleta() {
        System.out.println("\n+------------------------------------------------+");
        System.out.println("| " + nome); // Mantenho a estrutura simples, sem emoji
        System.out.println("| Profissao Anterior: " + profissaoAnterior);
        System.out.println("| Nivel: " + nivel);
        System.out.println("| HP: " + pontosVida + "/" + pontosVidaMax);
        System.out.println("| Ataque: " + ataque + " | Defesa: " + defesa);
        System.out.println("| Habilidade: " + habilidadeEspecialInimigo);
        System.out.println("| Chance Habilidade: " + chanceHabilidadeEspecial + "%");
        System.out.println("| Itens no Inventario: " + inventario.getItens().size());
        System.out.println("+------------------------------------------------+\n");
    }
}
