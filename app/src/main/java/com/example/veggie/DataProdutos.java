package com.example.veggie;

import java.util.List;

public class DataProdutos {
    private String id;



    public String getQuantidade() {
        return quantidade;
    }

    public int setQuantidade(String quantidade) {
        this.quantidade = quantidade;
        return 0;
    }

    private String quantidade;
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;
    public DataProdutos(String id, String nome, String descricao, String preco, String image,String quantidade ) {
        this.image = image;
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
    }
    public String toString() {
        return "DataProdutos {" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco='" + preco + '\'' +
                '}';
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    private String nome;
    private String descricao;
    private String preco;
}
