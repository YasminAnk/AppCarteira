package com.ifmg.carteiramanual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import modelo.Evento;

//classe que define comportamento e informações de cada um dos itens da lista de eventos
public class itemListaEventos extends ArrayAdapter<Evento> {


    private Context contextoPai;
    private ArrayList<Evento> eventos;

    public itemListaEventos(Context contexto, ArrayList<Evento> dados) {
        super(contexto, R.layout.itemlista_eventos, dados);

        this.contextoPai = contexto;
        this.eventos = dados;
    }

    @NonNull
    @Override

    //passa pela lista indicando oq tem e onde ta armazenado (ArrayList)
    public View getView(int indice, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(indice, convertView, parent); precisamos retornar uma view

        Evento eventoAtual = eventos.get(indice);//selecionamos o evento que mostraremos para o usuario
        ViewHolder noveView;

        //resultado para ser retornado
        final View resultado;

        //lista sendo montada pela primeira vez (View nula)
        if (convertView == null) {
            noveView = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());//layout que cria novos layouts
            convertView = inflater.inflate(R.layout.itemlista_eventos, parent, false);//indica que deve seguir o layout do "itemlista_eventos"


            //linkando com os componentes do xml
            noveView.dataHTxt = (TextView) convertView.findViewById(R.id.dataItemTxt);
            noveView.fotoTxt = (TextView) convertView.findViewById(R.id.fotoItemTxt);
            noveView.nomeTxt = (TextView) convertView.findViewById(R.id.nomeItemTxt);
            noveView.repeteTxt = (TextView) convertView.findViewById(R.id.repeteItemTxt);
            noveView.valorTxt = (TextView) convertView.findViewById(R.id.valorItemTxt);

            resultado = convertView;
            convertView.setTag(noveView);

        } else {
            //item existe mas foi modificado
            noveView = (ViewHolder) convertView.getTag();
            resultado = convertView;
        }

        //setar os valores de cada campo -> armazenados no evento atual
        noveView.nomeTxt.setText(eventoAtual.getNome());
        noveView.valorTxt.setText(eventoAtual.getValor() + "");
        noveView.fotoTxt.setText(eventoAtual.getCaminhoFoto() == null ? "Não" : "Sim");
        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
        noveView.dataHTxt.setText(formataData.format(eventoAtual.getOcorreu()));


        //verificar se o evento repete

        Calendar data = Calendar.getInstance();
        data.setTime(eventoAtual.getOcorreu());

        Calendar data2 = Calendar.getInstance();
        data2.setTime(eventoAtual.getOcorreu());

        if (data.get(Calendar.MONTH) != data2.get(Calendar.MONTH)) {
            noveView.repeteTxt.setText("Sim");
        } else {
            noveView.repeteTxt.setText("Não");
        }

        return resultado;

    }

    private static class ViewHolder {
        private TextView nomeTxt;
        private TextView valorTxt;
        private TextView dataHTxt;
        private TextView repeteTxt;
        private TextView fotoTxt;
    }
}
