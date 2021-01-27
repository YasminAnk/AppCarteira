package ferramentas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import modelo.Evento;

public class EventosDB extends SQLiteOpenHelper {

    private Context contexto;

    public EventosDB(Context context){
        super(context, "evento", null, 1);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String criaTb = "CREATE TABLE IF NOT EXISTS evento(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT," +
                " valor REAL, imagem TEXT, dataocorreu DATE, datacadastro DATE, datavalida DATE)";

        db.execSQL(criaTb);
    }

    public void updateEvento(Evento eventoAtualizado) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues valores = new ContentValues();
            valores.put("nome", eventoAtualizado.getNome());
            valores.put("valor", eventoAtualizado.getValor());
            valores.put("imagem", eventoAtualizado.getCaminhoFoto());
            valores.put("dataocorreu", eventoAtualizado.getOcorreu().getTime());
            valores.put("datavalida", eventoAtualizado.getValida().getTime());

            db.update("evento", valores, "id = ?", new String[]{eventoAtualizado.getId() + ""});

        } catch (SQLiteException ex) {
            System.err.println("erro na atualização do evento");
            ex.printStackTrace();
        }
    }

    public Evento buscaEvtId(int id) {
        String sql = "SELECT * FROM  evento WHERE id = " + id;
        Evento resultado = null;

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            //executando a sql
            Cursor tupla = db.rawQuery(sql, null);

            //extraindo as informaçoes do evento
            if (tupla.moveToFirst()) {

                String nome = tupla.getString(1);
                double valor = tupla.getDouble(2);
                if (valor < 0) {
                    valor *= -1;
                }
                String urlfoto = tupla.getString(3);
                Date dataocorreu = new Date(tupla.getLong(4));
                Date datacadastro = new Date(tupla.getLong(5));
                Date datavalida = new Date(tupla.getLong(6));


                resultado = new Evento(id, nome, valor, datacadastro, datavalida, dataocorreu, urlfoto);
            }

        } catch (SQLiteException ex) {
            System.err.println("erro na busca do evento pelo id");
            ex.printStackTrace();
        }
        return resultado;
    }

    public void insereEventos(Evento novoEvento) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues valores = new ContentValues();
            valores.put("nome", novoEvento.getNome());
            valores.put("valor", novoEvento.getValor());
            valores.put("imagem", novoEvento.getCaminhoFoto());
            valores.put("dataocorreu", novoEvento.getOcorreu().getTime());
            valores.put("datacadastro", new Date().getTime());
            valores.put("datavalida", novoEvento.getValida().getTime());



            db.insert("evento", null, valores);

        }catch (SQLiteException ex){

            ex.printStackTrace();
        }


    }



    public ArrayList<Evento> buscaEventos(int op, Calendar date) {

        ArrayList<Evento> resultado = new ArrayList<>();

        // primeiro dia do mes
        Calendar dia1 = Calendar.getInstance();
        dia1.setTime(date.getTime());
        dia1.set(Calendar.DAY_OF_MONTH, 1);
        dia1.set(Calendar.HOUR, -12);
        dia1.set(Calendar.MINUTE, 0);
        dia1.set(Calendar.SECOND, 0);


        //ultimo dia do mes
        Calendar dia2 = Calendar.getInstance();
        dia2.setTime(date.getTime());
        dia2.set(Calendar.DAY_OF_MONTH, dia2.getActualMaximum(Calendar.DAY_OF_MONTH));
        dia2.set(Calendar.HOUR, 23);
        dia2.set(Calendar.MINUTE, 59);
        dia2.set(Calendar.MILLISECOND, 999);
        dia2.set(Calendar.SECOND, 59);


        String sql = "SELECT * FROM evento WHERE ((datavalida < " + dia2.getTime().getTime() +
                " AND datavalida >= " + dia1.getTime().getTime() + ") OR (dataocorreu <= +" + dia2.getTime().getTime() +
                " AND datavalida >= " + dia1.getTime().getTime() + ") )";
        sql += " AND valor ";

        if (op == 0) {
            //estamos atras das entradas
            sql += ">= 0";
        } else {
            //saídas (valor negativo)
            sql += "< 0";
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor cursor = db.rawQuery(sql, null);

            //avaliar a leitura do cursor
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String nome = cursor.getString(1);
                    double valor = cursor.getDouble(2);
                    if (valor < 0) {
                        valor *= -1;
                    }
                    String urlfoto = cursor.getString(3);
                    Date dataocorreu = new Date(cursor.getLong(4));
                    Date datacadastro = new Date(cursor.getLong(5));
                    Date datavalida = new Date(cursor.getLong(6));

                    Evento temporario = new Evento((long) id, nome, valor, datacadastro, datavalida, dataocorreu, urlfoto);

                    resultado.add(temporario);
                } while (cursor.moveToNext());
            }


        } catch (SQLException EX) {
            System.err.println("erro na consulta ao banco");
            EX.printStackTrace();
        }

        return resultado;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //ficará parado ate a atualização da Activity de update (funcionalidade)
    }
}
