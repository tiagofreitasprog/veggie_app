package com.example.veggie;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.veggie.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<DataProdutos> jsonResponses;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter rc;
    private ArrayList<DataProdutos>produtos;
    Carrinho carrinho;
    ItemClickListener itemClickListener;
    DecimalFormat df;
    private TextView valor_final_txt,quantidade_txt;
    LinearLayout carrinho_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("0.00");
        setContentView(R.layout.activity_main);


        recyclerView = new RecyclerView(this);
        recyclerView = findViewById(R.id.destaques);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        rc = new RecyclerViewAdapter((ArrayList<DataProdutos>) listaDefault(), itemClickListener, this);
        carrinho_layout = findViewById(R.id.carrinho_layout);
        valor_final_txt = findViewById(R.id.valor_total);
        quantidade_txt = findViewById(R.id.quantidade_txt);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        produtos = new ArrayList<>();
        carrinho = new Carrinho(produtos);
        volleyGet();
      ;

        // Initialize listener
        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(int position, DataProdutos value) {
                Toast.makeText(MainActivity.this,value.getNome(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onClickBtn(int position, DataProdutos value) {
                carrinho.adicionarProduto(value);
                Toast.makeText(getApplicationContext(), "Produto: " + value.getNome() + " adicionado", Toast.LENGTH_LONG).show();
                Log.d("Valor carrinho", String.valueOf(carrinho.calcularValor()));
                if (carrinho.contarCarrinho() > 0){
                    quantidade_txt.setText("Produtos:" + String.valueOf(carrinho.contarCarrinho()));
                    valor_final_txt.setText("Valor:" + df.format(carrinho.calcularValor()) + "€");
                    carrinho_layout.setVisibility(View.VISIBLE);
                }else
                    carrinho_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void menosonClickBtn(int position, DataProdutos value) {
                int pos = jsonResponses.indexOf(value);
                if(carrinho.removerProduto(value) == true){
                   Toast.makeText(getApplicationContext(),"Produto: "+value.getNome()+" removido", Toast.LENGTH_LONG).show();
                }else{
                    Log.d("Falha ão romover","Falha ao remover");
                }
                Log.d("Valor retirado carrinho", String.valueOf(carrinho.calcularValor()));
                if (carrinho.contarCarrinho() > 0){
                    quantidade_txt.setText("Produtos:" + String.valueOf(carrinho.contarCarrinho()));
                    valor_final_txt.setText("Valor:" + df.format(carrinho.calcularValor()) + "€");
                    carrinho_layout.setVisibility(View.VISIBLE);
                }else
                    carrinho_layout.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List listaDefault(){
        DataProdutos produtos;
        produtos = new DataProdutos("0","maça","teste","20","www.google.com","1");
        jsonResponses = new ArrayList<>();
        jsonResponses.add(produtos);
        return jsonResponses;
    }
    public void volleyGet() {
        String url = "http://alugamadeira.pt/api/index.php/produto/list?limit=20";
        jsonResponses = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("produtos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String id = jsonObject.getString("id_produtos");
                        String nome = jsonObject.getString("nome");
                        String descricao = jsonObject.getString("descricao");
                        String preco = jsonObject.getString("preco");
                        String image = jsonObject.getString("img");

                        jsonResponses.add(new DataProdutos(id, nome, descricao,preco,image ,"1"));
                        recyclerView.setAdapter(new RecyclerViewAdapter((ArrayList<DataProdutos>) jsonResponses,itemClickListener,MainActivity.this));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Erro","Falha");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("Erro","erro");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }



}