package com.ifmg.carteiramanual;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView titulo;
    private TextView entrada;
    private TextView saida;
    private TextView saldo;
    private ImageButton entradaButton;
    private ImageButton saidaButton;
    private Button anteriorButton;
    private Button proximoButton;
    private Button novoButton;

    private Calendar hoje;
    static Calendar dataApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //crinado o link entre os componentes hava x xml
        titulo =(TextView) findViewById(R.id.tituloMain);
        entrada=(TextView) findViewById(R.id.entradaTxt);
        saida = (TextView) findViewById(R.id.saidaTxt);
        saldo = (TextView) findViewById(R.id.saldoTxt);

        entradaButton = (ImageButton) findViewById(R.id.entradaButton);
        saidaButton = (ImageButton) findViewById(R.id.saidaButton);

        anteriorButton = (Button) findViewById(R.id.anteriorButton);
        proximoButton = (Button) findViewById(R.id.proximoButton);
        novoButton = (Button) findViewById(R.id.novoButton);

        dataApp = Calendar.getInstance();
        hoje = Calendar.getInstance();

        cadastraEventos();

        mostraDataApp();


    }

    private void mostraDataApp(){
        //0-janeiro, 1-fevereiro .. 11-dezembro
        String nomeMes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto",
                "Setembro","Outubro", "Novembro", "Dezembro"};

        int mes = dataApp.get(Calendar.MONTH);
        int ano = dataApp.get(Calendar.YEAR);

        titulo.setText(nomeMes[mes]+ "/"+ano);
    }


    private void atualizaMes(int ajuste){

        dataApp.add(Calendar.MONTH, ajuste);

        //proximo mes nao pode passar do mes atual
        if(ajuste>0){
            if(dataApp.after(hoje)){
                dataApp.add(Calendar.MONTH,-1);
            }
        }
        //aqui temos que fazer uma busca no banco de dados (avaliar se existem meses anteriores cadastrados)
        mostraDataApp();
    }

    //metodo responsavel por implementar todos os eventos de botoes
    private void cadastraEventos(){
        anteriorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaMes(-1);
            }
        });

        proximoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaMes(1);
            }
        });

        novoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // EventosDB db = new EventosDB(MainActivity.this);
               // db.insereEventos();

               // Toast.makeText(MainActivity.this, db.getDatabaseName(), Toast.LENGTH_LONG).show();

            }
        });

        entradaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaActivity = new Intent(MainActivity.this, VisualizarEventos.class);
                trocaActivity.putExtra("acao", 0);
                //pedimos para executar a Activity passada como parametro
                startActivity(trocaActivity);
            }
        });

        saidaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaActivity = new Intent(MainActivity.this, VisualizarEventos.class);
                trocaActivity.putExtra("acao", 1);
                startActivity(trocaActivity);
            }
        });
    }


}