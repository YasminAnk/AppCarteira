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
        contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String criaTabela ="CREATE TABLE IF NOT EXISTS eventos(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " nome TEXT, valor REAL, imagem TEXT, dataocorreu DATE, datacadastro DATE" +
                ", datavalida DATE )";

        db.execSQL(criaTabela);
    }

    public void insereEventos(Evento novoEvento){

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues valores = new ContentValues();

            valores.put("nome", novoEvento.getNome());
            valores.put("valor", novoEvento.getValor());
            valores.put("imagem", novoEvento.getCaminhoFoto());
            valores.put("dataocorreu", novoEvento.getOcorreu().getTime());
            valores.put("datacadastro", novoEvento.getCadastro().getTime());
            valores.put("datavalida", novoEvento.getValida().getTime());



            db.insert("evento", null, valores);

        }catch (SQLiteException ex){

            ex.printStackTrace();
        }


    }

    public void atualizaEventos() {

    }

    public ArrayList<Evento> buscaEventos(int op, Calendar date) {

        ArrayList<Evento> resultado = new ArrayList<>();

        // primeiro dia do mes
        Calendar dia1 = Calendar.getInstance();
        dia1.setTime(date.getTime());
        dia1.set(Calendar.DAY_OF_MONTH, 1);

        //ultimo dia do mes
        Calendar dia2 = Calendar.getInstance();
        dia2.setTime(date.getTime());
        dia2.set(Calendar.DAY_OF_MONTH, dia2.getActualMaximum(Calendar.DAY_OF_MONTH));


        String sql = "SELECT * FROM evento WHERE dataocorreu < " + dia2.getTime().getTime() +
                " AND dataocorreu >= " + dia1.getTime().getTime();
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

                    Evento temporario = new Evento((long) id, nome, valor, dataocorreu, datacadastro, datavalida, urlfoto);

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
