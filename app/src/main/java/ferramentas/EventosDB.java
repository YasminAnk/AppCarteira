package ferramentas;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

    public void insereEventos(){

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String sql = "INSERT into evento (nome, valor) VALUES ('evento 1', 89)";
            db.execSQL(sql);

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
