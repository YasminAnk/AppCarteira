package com.ifmg.carteiramanual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ferramentas.EventosDB;
import modelo.Evento;

public class VisualizarEventos extends AppCompatActivity {


    private TextView tituloTxt;
    private Button novoBtn;
    private Button cancelarBtn;
    private ListView listaEventos;
    private TextView totalTxt;

    private ArrayList<Evento> eventos;
    private itemListaEventos adapter;

    //operacao = 0 indica entrada e operacao = 1 idica saida
    private int operacao = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_eventos);

        tituloTxt = (TextView) findViewById(R.id.tituloTxt);
        totalTxt = (TextView) findViewById(R.id.valorTotalTxt);

        listaEventos = (ListView) findViewById(R.id.listaEventos);

        novoBtn = (Button) findViewById(R.id.novoVisualizarBtn);
        cancelarBtn = (Button) findViewById(R.id.cancelarVisaulizarBtn);

        Intent intencao = getIntent();
        operacao = intencao.getIntExtra("acao", -1);

        ajusteOperacao();
        cadastrarEventos();

        carregaEventosLista();
    }

    private void cadastrarEventos() {
        novoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operacao != -1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                    if (operacao == 0) {
                        trocaAct.putExtra("acao", 0);
                        startActivityForResult(trocaAct, 0);
                    } else {
                        trocaAct.putExtra("acao", 1);
                        startActivityForResult(trocaAct, 1);
                    }

                }
            }
        });

        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ajusteOperacao(){
        if(operacao==0){
            tituloTxt.setText("Entradas");
        } else {
            if (operacao == 1) {
                tituloTxt.setText("Saidas");
            } else {
                //erro na config. da intent
                Toast.makeText(VisualizarEventos.this, "erro no parametro acao", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void carregaEventosLista() {
        eventos = new ArrayList<>();
        //eventos.add(new Evento("Padaria", 10.6, new Date(), new Date(),
        //new Date(), null ));
        //eventos.add(new Evento("Supermercado", 150.8, new Date(), new Date(),
        // new Date(), null ));

        EventosDB db = new EventosDB(VisualizarEventos.this);
        eventos = db.buscaEventos(operacao, MainActivity.dataApp);

        //direto do banco de dados

        adapter = new itemListaEventos(getApplicationContext(), eventos);
        listaEventos.setAdapter(adapter);

        //somando todos os valorea p mostrar no final
        double total = 0.0;
        for (int i = 0; i < eventos.size(); i++) {
            total = eventos.get(i).getValor();
        }
        totalTxt.setText(String.format("¢.2f", total));


    }

    protected void onActivityResult(int cod, int result, Intent data) {

        super.onActivityResult(cod, result, data);

        carregaEventosLista();
    }


}