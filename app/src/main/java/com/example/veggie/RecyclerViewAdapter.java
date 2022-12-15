package com.example.veggie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<DataProdutos> retrievedResponses;
    private ArrayList<Carrinho> carrinho_lista;
    private Context context;
    private ItemClickListener clickListener;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;
    int selectedPosition=-1;



    public RecyclerViewAdapter(ArrayList<DataProdutos> retrievedResponses, ItemClickListener itemClickListener,Context context) {
        this.retrievedResponses = retrievedResponses;
        this.context = context;
        this.clickListener = itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setNv(retrievedResponses.get(position).getImage());
        holder.setNome_field(retrievedResponses.get(position).getNome());
        holder.setDescricao_field(retrievedResponses.get(position).getDescricao());
        holder.setQuantidade(retrievedResponses.get(position).setQuantidade(String.valueOf(holder.getQuantidade())));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get adapter position
                int position=holder.getAdapterPosition();
                // call listener
                clickListener.onClick(position,retrievedResponses.get(position));
                // update position
                selectedPosition=position;
                // notify
                notifyDataSetChanged();
                // check conditions
                if(selectedPosition==position){
                    // When current position is equal
                }
                else{
                    // when current position is different
                }
            }
        });
        Button btn_mais,btn_menos;

        final TextView[] compras = new TextView[1];
        TextView preco_field;
        final double[] temp_valor_final = new double[1];
        preco_field = holder.itemView.findViewById(R.id.preco);
        compras[0] = holder.itemView.findViewById(R.id.compras);
        compras[0].setText("0");
        temp_valor_final[0] = 0.00;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        btn_mais = holder.itemView.findViewById(R.id.mais);
        btn_menos = holder.itemView.findViewById(R.id.menos);
        btn_mais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClickBtn(position,retrievedResponses.get(position));

                String valor = compras[0].getText().toString();
                valor = valor.replaceAll("[^a-zA-Z0-9_-]","");
                int valor_final = Integer.parseInt(valor);
                valor_final = valor_final + 1;


                // check if item still exists
                if(position != RecyclerView.NO_POSITION){
                    DataProdutos clickedDataItem = retrievedResponses.get(position);


                    Double preco = Double.parseDouble(clickedDataItem.getPreco());
                    Double preco_final = preco * valor_final;
                    Log.d("valor final" , String.valueOf(temp_valor_final[0]));


                    temp_valor_final[0] = temp_valor_final[0] + Double.parseDouble(clickedDataItem.getPreco());
                    Log.d("valor final", String.valueOf(temp_valor_final[0]));


                    String preco_final_formatado;
                    preco_final_formatado = df.format(preco_final);
                    compras[0].setText(String.valueOf(valor_final));
                    preco_field.setText(preco_final_formatado  + " €");



                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return retrievedResponses == null ? 0 : retrievedResponses.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public double temp_valor_final;
        private ImageView nv;
        private TextView nome_field,compras;
        private TextView descricao_field;
        private TextView preco_field;

        private Button menos,mais;
        private List<String> items = new ArrayList<>();
        private int quantidade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carrinho_lista = new ArrayList<>();

            nv = itemView.findViewById(R.id.photo);
            nome_field = itemView.findViewById(R.id.nome_field);
            descricao_field = itemView.findViewById(R.id.descricao_field);
            preco_field = itemView.findViewById(R.id.preco);
            menos = itemView.findViewById(R.id.menos);
            mais = itemView.findViewById(R.id.mais);
            compras = itemView.findViewById(R.id.compras);
            compras.setText("0");
            temp_valor_final = 0.00;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            menos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.menosonClickBtn(getAdapterPosition(),retrievedResponses.get(getAdapterPosition()));
                    String valor = compras.getText().toString();

                    valor = valor.replaceAll("[^a-zA-Z0-9_-]","");

                    quantidade = Integer.parseInt(valor);
                    if(quantidade == 0){
                        quantidade = 0;
                    }else{
                        quantidade = quantidade - 1;
                        setQuantidade(quantidade);
                    }
                    int pos = getAdapterPosition();
                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        DataProdutos clickedDataItem = retrievedResponses.get(pos);
                        Double preco = Double.parseDouble(clickedDataItem.getPreco());
                        Double preco_final = preco * quantidade;
                        Log.d("valor final", String.valueOf(temp_valor_final));

                        temp_valor_final = temp_valor_final - Double.parseDouble(clickedDataItem.getPreco());

                        Log.d("valor final" , String.valueOf(temp_valor_final));


                        compras.setText(String.valueOf(quantidade));

                        preco_field.setText(df.format(preco_final)  + " €");
                        items.remove(clickedDataItem.getNome());
                        setPosition(quantidade);
                    }
                }
            });

        }
        public void setNv(String url) {
            new DownloadImageTask((ImageView) nv.findViewById(R.id.photo)).execute(url);
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }
        public void setNome_field(String email) {
            nome_field.setText(email);
        }
        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
        public int getQuantidade(){
            return this.quantidade;
        }
        public void setDescricao_field(String firstName) {
            descricao_field.setText(firstName);
        }


    }



}
