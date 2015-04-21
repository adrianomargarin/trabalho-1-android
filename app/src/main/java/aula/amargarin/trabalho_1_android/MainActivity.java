package aula.amargarin.trabalho_1_android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private long idLinha;
    private String nomeCateg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ListaCategoriasInterface();
    }

    public void ListaCategoriasInterface(){
        setContentView(R.layout.activity_main);
        CarregaArrayAdapter();
    }

    public void CarregaArrayAdapter(){
        ListView lista = (ListView) findViewById(R.id.listView);
        String[] strings = new String[] {};

        ArrayList<String> lista2 = new ArrayList<String>();

        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor cursor = db.getTodosCateg();
        for(int i=0; i <cursor.getCount(); i++){
            cursor.moveToPosition(i);
            int index = cursor.getColumnIndex("nome");
            lista2.add(cursor.getString(index));
        }
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista2);

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Intent it = new Intent(MainActivity.this, AddNovoOrcamento.class);

            TextView textView = (TextView) view;

            DBAdapter db = new DBAdapter(MainActivity.this);
            db.open();
            Cursor cursor = db.getCategoria(textView.getText().toString());

            it.putExtra("nameCateg", textView.getText());
            it.putExtra("idCateg", cursor.getColumnIndex("_id"));
            db.close();

            startActivity(it);

            return false;
            }
        });


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir lojas aqui
            }
        });

        lista.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent it = new Intent(MainActivity.this, AddNovaCategoria.class);
        startActivity(it);

        return super.onOptionsItemSelected(item);
    }
}
