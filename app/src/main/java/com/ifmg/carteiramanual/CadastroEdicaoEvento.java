package com.ifmg.carteiramanual;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ferramentas.EventosDB;
import modelo.Evento;

public class CadastroEdicaoEvento extends AppCompatActivity {

    private DatePickerDialog calendarioUsuario;
    private TextView tituloTxt;
    private EditText nomeTxt;
    private EditText valorTxt;
    private TextView dataTxt;
    private CheckBox repeteBtn;
    private Button fotoBtn;
    private Button salvarBtn;
    private Button cancelarBtn;
    private ImageView foto;

    private Calendar calendarioTemp;

    // 0- cadast entrada 1- cadast saida, 2 - edicao entrada 3-edicao saida
    private int acao = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_edicao_evento);

        tituloTxt = (TextView) findViewById(R.id.tituloTxt);
        nomeTxt = (EditText) findViewById(R.id.nomeCadastroTxt);
        valorTxt = (EditText) findViewById(R.id.valorCadastroTxt);
        dataTxt = (TextView) findViewById(R.id.dataCadastroTxt);
        repeteBtn = (CheckBox) findViewById(R.id.repeteBtn);
        fotoBtn = (Button) findViewById(R.id.fotoBtn);
        foto = (ImageView) findViewById(R.id.fotoCadastro);
        salvarBtn = (Button) findViewById(R.id.salvarCadastroBtn);
        cancelarBtn = (Button) findViewById(R.id.cancelarCadastroBtn);

        Intent intencao = getIntent();
        acao = intencao.getIntExtra("acao", -1);

        ajustaPorAcao();
        cadastraEventos();

}

private void ajustaPorAcao(){
        // recuperando a data de hoje
    Calendar hoje = Calendar.getInstance();
    SimpleDateFormat formatador = new SimpleDateFormat("dd/mm/yyyy");
    dataTxt.setText((formatador.format(hoje.getTime())));

    switch (acao){
        case 0:{

            tituloTxt.setText("Cadast. Entrada");
        }break;
        case 1:{

            tituloTxt.setText("Cadast. Saída");
        }break;
        case 2:{

            tituloTxt.setText("Edição Entrada");
        }break;
        case 3:{

            tituloTxt.setText("Edição Saída");
        }break;
        default:{

        }
    } }

    private void cadastraEventos(){

        calendarioTemp = Calendar.getInstance();
        calendarioUsuario = new DatePickerDialog(CadastroEdicaoEvento.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                calendarioTemp.set(ano, mes, dia);
                dataTxt.setText(dia+"/"+(mes+1)+"/"+ano);
            }
        }, calendarioTemp.get(Calendar.YEAR), calendarioTemp.get(Calendar.MONTH), calendarioTemp.get(Calendar.DAY_OF_MONTH));
        dataTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarioUsuario.show();
            }
        });

        salvarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarNovoEvento();
            }
        });
    }


    private void cadastrarNovoEvento(){
        String nome = nomeTxt.getText().toString();
        double valor = Double.parseDouble(valorTxt.getText().toString());

        if(acao ==1 || acao ==3){
            valor *= -1;
        }
        SimpleDateFormat formatador = new SimpleDateFormat("dd/mm/yyyy");
        String dataStr = dataTxt.getText().toString();

        try{
            Date diaEvento = formatador.parse(dataStr);

            // um novo calendario para calcular a data limite
            Calendar dataLimite = Calendar.getInstance();
            dataLimite.setTime(calendarioTemp.getTime());

            //verificando se este evento irá repetir por alguns meses
            if(repeteBtn.isChecked()){
                //por enquanto estamos considerando apenas um mes
            }

            //setando para o ultimo dia do mes limite
            dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

            Evento novoEvento = new Evento(nome, valor, new Date(), dataLimite.getTime(), diaEvento, null );

            //inserir esse evento no BD
            EventosDB bd = new EventosDB(CadastroEdicaoEvento.this);
            bd.insereEventos(novoEvento);

            Toast.makeText(CadastroEdicaoEvento.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();
            finish();

        }catch (ParseException ex){
            System.err.println("erro no formato data");
        }


    }
}
