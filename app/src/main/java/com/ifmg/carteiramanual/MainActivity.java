package com.ifmg.carteiramanual;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

import ferramentas.EventosDB;
import modelo.Evento;

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

        atualizaValores();

        configuraPermissoes();


    }

    private void configuraPermissoes() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);


        }
    }

    private void mostraDataApp() {
        //0-janeiro, 1-fevereiro .. 11-dezembro
        String nomeMes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"};

        int mes = dataApp.get(Calendar.MONTH);
        int ano = dataApp.get(Calendar.YEAR);

        titulo.setText(nomeMes[mes] + "/" + ano);
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
        atualizaValores();
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
                startActivityForResult(trocaActivity, 0);
            }
        });

        saidaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaActivity = new Intent(MainActivity.this, VisualizarEventos.class);
                trocaActivity.putExtra("acao", 1);
                startActivityForResult(trocaActivity, 1);
            }
        });
    }

    private void atualizaValores() {


        EventosDB db = new EventosDB(MainActivity.this);
        ArrayList<Evento> saidas = db.buscaEventos(1, dataApp);
        ArrayList<Evento> entradas = db.buscaEventos(0, dataApp);

        //somando todos os valores dos eventos
        double entradattl = 0.0;
        double saidattl = 0.0;

        for (int i = 0; i < entradas.size(); i++) {
            entradattl += entradas.get(i).getValor();
        }
        for (int i = 0; i < saidas.size(); i++) {
            saidattl += saidas.get(i).getValor();
        }

        //exibindo os valores
        double saldoTotal = entradattl - saidattl;

        entrada.setText(String.format("%.2f", entradattl));
        saida.setText(String.format("%.2f", saidattl));
        saldo.setText(String.format("%.2f", saldoTotal));

    }

    protected void onActivityResult(int cod, int result, Intent data) {

        super.onActivityResult(cod, result, data);

        atualizaValores();
    }


}