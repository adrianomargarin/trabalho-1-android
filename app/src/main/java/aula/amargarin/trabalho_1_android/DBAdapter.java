package aula.amargarin.trabalho_1_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.Settings.Global.getString;

/**
 * Created by amargarin on 19/04/15.
 */

public class DBAdapter {

    private static final String DATABASE_NAME = "DB";

    static final String TABELA_CATEG="categ";
    static final String KEY_CATEG_ID="_id";
    static final String KEY_CATEG_NOME="nome";

    static final String TABELA_ORCA="orca";
    static final String KEY_ORCA_ID="_idOrca";
    static final String KEY_ORCA_LOJA="loja";
    static final String KEY_ORCA_VALOR="valor";
    static final String KEY_ORCA_PAGTO="pagto";
    static final String KEY_ORCA_OBSERV="observ";
    static final String KEY_ORCA_PICTURE="picture";
    static final String KEY_ORCA_IDCATEG="_idCateg";

    static final String VIEW = "ViewOrca";

    private static final int DATABASE_VERSION = 2;


    static final String CRIA_TAB_CATEG = "CREATE TABLE " + TABELA_CATEG +
            " (" + KEY_CATEG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CATEG_NOME + " TEXT NOT NULL);";

    static final String CRIA_TAB_ORCA =  "CREATE TABLE " + TABELA_ORCA +
            " (" + KEY_ORCA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " +
            KEY_ORCA_LOJA + " TEXT, " + KEY_ORCA_VALOR + " DOUBLE, " +
            KEY_ORCA_PAGTO + " VARCHAR NOT NULL ," +
            KEY_ORCA_OBSERV + " VARCHAR ," +
            KEY_ORCA_PICTURE + " VARCHAR ," +
            KEY_ORCA_IDCATEG + " INTEGER ," +
            "FOREIGN KEY (" + KEY_ORCA_IDCATEG + ") REFERENCES " + TABELA_CATEG +" (" + KEY_CATEG_ID + "));";


    static final String CRIA_VIEW = "CREATE VIEW "+ VIEW +
            " AS SELECT " + TABELA_ORCA + "." + KEY_ORCA_ID + " AS _id," +
            TABELA_ORCA + "." + KEY_ORCA_LOJA + ", " +
            TABELA_ORCA + "."+ KEY_ORCA_PAGTO + ", " +
            TABELA_ORCA + "."+ KEY_ORCA_OBSERV + ", " +
            TABELA_ORCA + "."+ KEY_ORCA_PICTURE + ", " +
            TABELA_CATEG + "."+ KEY_CATEG_NOME +
            " FROM " + TABELA_ORCA +
            " JOIN " + TABELA_CATEG +
            " ON "+ TABELA_ORCA + "." + KEY_ORCA_IDCATEG +
            " = " + TABELA_CATEG + "." + KEY_CATEG_ID;


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context); //classe interna que herda de SQLiteOpenHelper
    }

    //classe interna que manipula o banco
    //SQLiteOpenHelper ? uma classe abstrata.
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onOpen(SQLiteDatabase db) {//for?a integridade referencial
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            try{

                db.execSQL(CRIA_TAB_CATEG);
                db.execSQL(CRIA_TAB_ORCA);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABELA_CATEG);
            db.execSQL("DROP TABLE IF EXISTS "+TABELA_ORCA);
            db.execSQL("DROP VIEW IF EXISTS "+ VIEW);
            onCreate(db);
        }
    }

    // *******************************************************************************
    //--- abre a base de dados ---
    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //--- fecha a base de dados ---
    public void close(){
        DBHelper.close();
    }

    //---insere um Categoria na base da dados ---
    public long insereCateg(String nome){
        ContentValues cv=new ContentValues();
        cv.put(KEY_CATEG_NOME, nome);
        return db.insert(TABELA_CATEG, KEY_CATEG_ID, cv);
    }

    //---insere um Orcamento na base da dados ---
    public long insereOrcamento(String loja, double valor, String pagto, String observ, String picture, int idCateg){
        ContentValues cv=new ContentValues();
        cv.put(KEY_ORCA_LOJA, loja);
        cv.put(KEY_ORCA_VALOR, valor);
        cv.put(KEY_ORCA_PAGTO, pagto);
        cv.put(KEY_ORCA_OBSERV, observ);
        cv.put(KEY_ORCA_PICTURE, picture);
        cv.put(KEY_ORCA_IDCATEG, idCateg);
        return db.insert(TABELA_ORCA, null, cv);
    }

    public Cursor getCategoria(String nome){
        return db.rawQuery("select * from categ where nome = ?", new String[] { nome });
    }

    //--- devolve todos as Categoria ---
    public Cursor getTodosCateg(){
        String colunas[] = {KEY_CATEG_ID, KEY_CATEG_NOME};
        return db.query(TABELA_CATEG, colunas, null, null, null, null, null);
    }

    //--- devolve todos os orcamentos---
    public Cursor getTodosOrca(){
        String colunas[] = {KEY_ORCA_ID, KEY_ORCA_LOJA, KEY_ORCA_VALOR, KEY_ORCA_PAGTO, KEY_ORCA_OBSERV, KEY_ORCA_PICTURE, KEY_ORCA_IDCATEG };
//        return db.query(VIEW, colunas, null, null, null, null, null);
        return db.query(TABELA_ORCA, colunas, null, null, null, null, null);
    }

    public Cursor getOrcamentoById(String idCateg){
        return db.rawQuery("select * from orca where _idCateg = ?", new String [] {idCateg} );
    }
}