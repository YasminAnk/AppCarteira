package ferramentas;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

    public void atualizaEventos(){

    }
    public void buscaEventos(){

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //ficará parado ate a atualização da Activity de update (funcionalidade)
    }
}
