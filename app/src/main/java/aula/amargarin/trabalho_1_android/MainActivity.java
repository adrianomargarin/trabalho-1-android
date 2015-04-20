package aula.amargarin.trabalho_1_android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    private long idLinha;
    private TextView nomeCateg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new CarregaTodasCategorias().execute(idLinha);
    }

    // Executa a consulta em uma thead separada
    private class CarregaTodasCategorias extends AsyncTask<Long, Object, Cursor>{
        DBAdapter databaseConnector = new DBAdapter(MainActivity.this);

        @Override
        protected Cursor doInBackground(Long... params){
            databaseConnector.open();
            return databaseConnector.getTodosCateg();
        }
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

             if(result.moveToNext()){
                 result.moveToFirst();
                 int nomeCategIndex = result.getColumnIndex("nomeCategIndex");
                 nomeCateg.setText(result.getString(nomeCategIndex));
             }

            result.close();
            databaseConnector.close();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent("aula.amargarin.trabalho_1_android.AddNovaCategoria"));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
