package com.example.veggie;

import android.view.View;

public interface ItemClickListener {
    void onClick(int position,DataProdutos value);
    void onClickBtn(int position,DataProdutos value);
    void menosonClickBtn(int position,DataProdutos value);
}
