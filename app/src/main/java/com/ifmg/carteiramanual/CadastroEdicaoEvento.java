package com.ifmg.carteiramanual;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private Evento eventoSelecionado;

    private String nomeFoto;

    private Spinner mesRepeticao;

    private Calendar calendarioTemp;

    // 0- cadast entrada 1- cadast saida, 2 - edicao entrada 3-edicao saida
    private int acao = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_edicao_evento);

        tituloTxt = (TextView) findViewById(R.id.tituloCadastroTxt);
        nomeTxt = (EditText) findViewById(R.id.nomeCadastroTxt);
        valorTxt = (EditText) findViewById(R.id.valorCadastroTxt);
        dataTxt = (TextView) findViewById(R.id.dataCadastroTxt);
        repeteBtn = (CheckBox) findViewById(R.id.repeteBtn);
        fotoBtn = (Button) findViewById(R.id.fotoBtn);
        foto = (ImageView) findViewById(R.id.fotoCadastro);
        salvarBtn = (Button) findViewById(R.id.salvarCadastroBtn);
        cancelarBtn = (Button) findViewById(R.id.cancelarCadastroBtn);
        mesRepeticao = (Spinner) findViewById(R.id.mesesSpinner);

        Intent intencao = getIntent();
        acao = intencao.getIntExtra("acao", -1);

        ajustaPorAcao();
        cadastraEventos();
        confSpinner();

    }


    private void confSpinner() {
        List<String> meses = new ArrayList<>();

        //permitiremos a repetição de apenas 24 meses de um evento
        for (int i = 1; i <= 24; i++) {
            meses.add(i + "");
        }
        ArrayAdapter<String> listaAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, meses);

        mesRepeticao.setAdapter(listaAdapter);
        mesRepeticao.setEnabled(false);

    }

    private void ajustaPorAcao() {
        // recuperando a data de hoje
        Calendar hoje = Calendar.getInstance();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/mm/yyyy");
        dataTxt.setText((formatador.format(hoje.getTime())));

        switch (acao) {
            case 0: {

                tituloTxt.setText("Cadast. Entrada");
            }
            break;
            case 1: {

                tituloTxt.setText("Cadast. Saída");
            }
            break;
            case 2: {

                tituloTxt.setText("Edição Entrada");
                ajusteEdicao();
            }
            break;
            case 3: {

                tituloTxt.setText("Edição Saída");
                ajusteEdicao();
            }
            break;
            default: {

            }
        }
    }


    private void ajusteEdicao() {
        cancelarBtn.setText("excluir");
        salvarBtn.setText("atualizar");

        //carregando a info do bd
        int id = Integer.parseInt(getIntent().getStringExtra("id"));
        if (id != 0) {
            EventosDB db = new EventosDB(CadastroEdicaoEvento.this);
            eventoSelecionado = db.buscaEvtId(id);

            SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");
            //carregando as informaçoes dos campos vindos do banco
            nomeTxt.setText(eventoSelecionado.getNome());
            valorTxt.setText(eventoSelecionado.getValor() + "");
            dataTxt.setText(formatar.format(eventoSelecionado.getOcorreu()));
            nomeFoto = eventoSelecionado.getCaminhoFoto();
            CarregarImg();

            Calendar d1 = Calendar.getInstance();
            d1.setTime(eventoSelecionado.getValida());

            Calendar d2 = Calendar.getInstance();
            d2.setTime(eventoSelecionado.getOcorreu());
            repeteBtn.setChecked(d1.get(Calendar.MONTH) != d2.get(Calendar.MONTH) ? true : false);

            if (repeteBtn.isChecked()) {
                mesRepeticao.setEnabled(true);

                //calculo da diferença do mes do cadastro e mes valido
                mesRepeticao.setSelection(d1.get(Calendar.MONTH) - d2.get(Calendar.MONTH) - 1);
            }
        }
    }

    private void cadastraEventos() {

        calendarioTemp = Calendar.getInstance();
        calendarioUsuario = new DatePickerDialog(CadastroEdicaoEvento.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                calendarioTemp.set(ano, mes, dia);
                dataTxt.setText(dia + "/" + (mes + 1) + "/" + ano);
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
                if (acao < 2) {
                    cadastrarNovoEvento();
                } else {
                    //faremos um update do evento
                    updateEvento();
                }
            }
        });

        repeteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeteBtn.isChecked()) {
                    mesRepeticao.setEnabled(true);
                } else {
                    mesRepeticao.setEnabled(false);
                }
            }
        });

        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (acao < 2) {


                    //termina a exec de uma activity e retorna a anterior
                    finish();
                } else {
                    //será chamado o metodo de delete do bd
                }
            }
        });

        fotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 100);
            }
        });
    }

    private void salvarimagem(Bitmap img) {
        Random gerador = new Random();
        Date instante = new Date();

        //definindo o nome do arqu8ivo foto
        String nome = gerador.nextInt() + "" + instante.getTime() + ".png";
        nomeFoto = nome;
        File sd = Environment.getExternalStorageDirectory();
        File fotoArquivo = new File(sd, nome);

        //gravando armazenamento do dispositivo
        try {
            FileOutputStream gravador = new FileOutputStream(fotoArquivo);
            img.compress(Bitmap.CompressFormat.PNG, 100, gravador);
            gravador.flush();
            gravador.close();

        } catch (Exception ex) {
            System.err.println("Erro ao armazenar foto");
            ex.printStackTrace();

        }
    }


    protected void onActivityResult(int RequestCode, int resultCode, Intent data) {
        super.onActivityResult(RequestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Bitmap imagemUser = (Bitmap) data.getExtras().get("data");
            foto.setImageBitmap(imagemUser);
            foto.setBackground(null);

            salvarimagem(imagemUser);
        }
    }

    private void updateEvento() {
        eventoSelecionado.setNome(nomeTxt.getText().toString());
        eventoSelecionado.setValor(Double.parseDouble(valorTxt.getText().toString()));

        if (acao == 3) {
            eventoSelecionado.setValor(eventoSelecionado.getValor() * -1);
        }

        eventoSelecionado.setOcorreu(calendarioTemp.getTime());

        // um novo calendario para calcular a data limite
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        //verificando se este evento irá repetir por alguns meses
        if (repeteBtn.isChecked()) {
            //por enquanto estamos considerando apenas um mes
            String stro = (String) mesRepeticao.getSelectedItem();
            dataLimite.add(Calendar.MONTH, Integer.parseInt(stro));

        }
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        eventoSelecionado.setValida(dataLimite.getTime());
        eventoSelecionado.setCaminhoFoto(nomeFoto);

        EventosDB db = new EventosDB(CadastroEdicaoEvento.this);
        db.updateEvento(eventoSelecionado);
        finish();

    }

    private void cadastrarNovoEvento() {
        String nome = nomeTxt.getText().toString();
        double valor = Double.parseDouble(valorTxt.getText().toString());

        if (acao == 1 || acao == 3) {
            valor *= -1;
        }
        //SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        //ææString dataStr = dataTxt.getText().toString();

        //try{
        Date diaEvento = calendarioTemp.getTime();

        // um novo calendario para calcular a data limite
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        //verificando se este evento irá repetir por alguns meses
        if (repeteBtn.isChecked()) {
            //por enquanto estamos considerando apenas um mes
            String stro = (String) mesRepeticao.getSelectedItem();
            dataLimite.add(Calendar.MONTH, Integer.parseInt(stro));

        }

        //setando para o ultimo dia do mes limite
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        Evento novoEvento = new Evento(nome, valor, new Date(), dataLimite.getTime(), diaEvento, nomeFoto);

        //inserir esse evento no BD
        EventosDB bd = new EventosDB(CadastroEdicaoEvento.this);
        bd.insereEventos(novoEvento);

        Toast.makeText(CadastroEdicaoEvento.this, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show();
        finish();

        //}catch (ParseException ex){
        System.err.println("erro no formato data");
    }

    //método chamado durante a edição de algum evento
    private void CarregarImg() {
        if (nomeFoto != null) {
            File sd = Environment.getExternalStorageDirectory();
            File arquivoLeitura = new File(sd, nomeFoto);
            try {
                FileInputStream leitor = new FileInputStream(arquivoLeitura);
                Bitmap img = BitmapFactory.decodeStream(leitor);

                foto.setImageBitmap(img);
                foto.setBackground(null);

            } catch (Exception ex) {
                System.err.println("Erro na leitura da foto");
            }
        }
    }


}

