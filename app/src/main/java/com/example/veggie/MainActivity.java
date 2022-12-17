package com.example.veggie;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<DataProdutos> jsonResponses;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter rc;
    private ArrayList<DataProdutos>produtos;
    Carrinho carrinho;
    ItemClickListener itemClickListener;
    DecimalFormat df;
    private TextView valor_final_txt,quantidade_txt;
    LinearLayout carrinho_layout;
    TextView id,userName,userEmail,gender;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        df = new DecimalFormat("0.00");
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            userName = findViewById(R.id.user_login);
            btnLogout = findViewById(R.id.buttonLogout);
            User user = SharedPrefManager.getInstance(this).getUser();
            userName.setText(user.getName());
            btnLogout.setOnClickListener(this);
        }
        else{
            Intent  intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        // RecyclerView da lista de produtos
        recyclerView = new RecyclerView(this);
        recyclerView = findViewById(R.id.destaques);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        // Adapter da lista de produtos
        rc = new RecyclerViewAdapter((ArrayList<DataProdutos>) listaDefault(), itemClickListener, this);
        // Inicializar as views pelas variaveis ID
        carrinho_layout = findViewById(R.id.carrinho_layout);
        valor_final_txt = findViewById(R.id.valor_total);
        quantidade_txt = findViewById(R.id.quantidade_txt);
        // Inicialiar animação do RecyclerView
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // Inicilizar Lista de produtos juntamente com o objeto carrinho
        produtos = new ArrayList<>();
        carrinho = new Carrinho(produtos);

        volleyGet();

        // Define o adapter com a resposta em Json
        recyclerView.setAdapter(new RecyclerViewAdapter((ArrayList<DataProdutos>) jsonResponses,itemClickListener,MainActivity.this));
        // Initialize listener
        itemClickListener = new ItemClickListener() {
            // CallBacks
            @Override
            public void onClick(int position, DataProdutos value) {
                Toast.makeText(MainActivity.this,value.getNome(), Toast.LENGTH_LONG).show();
            }
            // Ao adicionar um novo produto
            @Override
            public void onClickBtn(int position, DataProdutos value) {
                // Adiciona ao objeto carrinho o produto selecionado
                carrinho.adicionarProduto(value);
                Toast.makeText(getApplicationContext(), "Produto: " + value.getNome() + " adicionado", Toast.LENGTH_LONG).show();
                Log.d("Valor carrinho", String.valueOf(carrinho.calcularValor()));
                // Verifica se o objeto carrinho tem já produtos
                if (carrinho.contarCarrinho() > 0){
                    // Mostra na view o numero de produtos do carrinho
                    quantidade_txt.setText("Produtos:" + String.valueOf(carrinho.contarCarrinho()));
                    // Calcula toda a soma do objeto value.getPreco();
                    valor_final_txt.setText("Valor:" + df.format(carrinho.calcularValor()) + "€");
                    // Mostra o carrinho
                    carrinho_layout.setVisibility(View.VISIBLE);
                }else
                    // Se não tiver produtos esconde o carrinho
                    carrinho_layout.setVisibility(View.INVISIBLE);
            }
            // Remove produtos do carrinho
            @Override
            public void menosonClickBtn(int position, DataProdutos value) {
                int pos = jsonResponses.indexOf(value);
                // A função removerProduto verifica se o produto existe na lista e remove o respetivo produto
                if(carrinho.removerProduto(value) == true){
                   Toast.makeText(getApplicationContext(),"Produto: "+value.getNome()+" removido", Toast.LENGTH_LONG).show();
                }else{
                    Log.d("Falha ão romover","Falha ao remover");
                }
                Log.d("Valor retirado carrinho", String.valueOf(carrinho.calcularValor()));
                // Mesma verificação
                if (carrinho.contarCarrinho() > 0){
                    quantidade_txt.setText("Produtos:" + String.valueOf(carrinho.contarCarrinho()));
                    valor_final_txt.setText("Valor:" + df.format(carrinho.calcularValor()) + "€");
                    carrinho_layout.setVisibility(View.VISIBLE);
                }else
                    carrinho_layout.setVisibility(View.INVISIBLE);
            }
        };
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
    // Inicialização por defeito caso o utilizador não tiver conectado a rede
    public List listaDefault(){
        DataProdutos produtos;
        produtos = new DataProdutos("0","maça","teste","20","www.google.com","1");
        jsonResponses = new ArrayList<>();
        jsonResponses.add(produtos);
        return jsonResponses;
    }

    @Override
    protected void onStart() {
        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);*/
        super.onStart();
    }
    private void updateUI(Object o) {
        if(o == null){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnLogout)){
            SharedPrefManager.getInstance(getApplicationContext()).logout();
        }
    }
}