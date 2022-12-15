package com.example.veggie;

import android.util.Log;

import java.util.ArrayList;

public class Carrinho {
    public ArrayList<DataProdutos> getProdutos() {
        return produtos;
    }

    public void setProdutos(ArrayList<DataProdutos> produtos) {
        this.produtos = produtos;
    }

    public Carrinho(ArrayList<DataProdutos> produtos) {
        this.produtos = produtos;
    }
    public boolean removerProduto(DataProdutos produto){
        if(produtos.contains(produto)){
            produtos.remove(produto);
            return true;
        }
        return false;
    }
    public boolean adicionarProduto(DataProdutos produto){

        if(produto.getNome() != null){
            produtos.add(produto);
            setProdutos(produtos);
            return true;
        }
        return false;
    }
    public int contarCarrinho(){
        return produtos.size();
    }
    public double calcularValor(){
        double valor_final = 0;
        for (DataProdutos value : produtos) {
            valor_final += Double.parseDouble(value.getPreco());
        }
        Log.d("Carrinho valor:" , String.valueOf(valor_final));
        return valor_final;
    }
    private ArrayList<DataProdutos> produtos;
}
