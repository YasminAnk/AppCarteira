package com.ifmg.carteiramanual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VisualizarEventos extends AppCompatActivity {



    private TextView tituloTxt;
    private Button novoBtn;
    private Button cancelarBtn;
    private ListView listaEventos;
    private TextView totalTxt;

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

    }

    private void cadastrarEventos() {
        novoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operacao != -1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                    if (operacao == 0) {
                        trocaAct.putExtra("acao", 0);
                    } else {
                        trocaAct.putExtra("acao", 1);
                    }
                    startActivity(trocaAct);
                }
            }
        });
    }

    private void ajusteOperacao(){
        if(operacao==0){
            tituloTxt.setText("Entrada");
        }else {
            if (operacao == 1){
                tituloTxt.setText("Saida");
            }else{
                //erro na config. da intent
                Toast.makeText(VisualizarEventos.this, "erro no parametro acao", Toast.LENGTH_LONG).show();
            }
        }
    }


}