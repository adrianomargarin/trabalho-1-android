package aula.amargarin.trabalho_1_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNovaCategoria extends Activity {

    private long idLinha;
    private EditText txtTitulo;
    private Button btnSalvar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nova_categoria);

        txtTitulo = (EditText) findViewById(R.id.txtTitulo);

        Bundle extras = getIntent().getExtras();

        // Se há extras, usa os valores para preencher a tela
        if (extras != null){
            idLinha = extras.getLong("_id");
            txtTitulo.setText(extras.getString("nome"));
        }

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(salvarCategButtonClicked);
    }

    View.OnClickListener salvarCategButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            if (txtTitulo.getText().length() != 0){
                AsyncTask<Object, Object, Object> salvaCategTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaCateg(); // Salva a categoria na base de dados
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result){
                        finish(); // Fecha a atividade
                    }
                };

                // Salva a Categoria no BD usando uma thread separada
                salvaCategTask.execute();
            } // end if
            else {
                // Cria uma caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNovaCategoria.this);
                builder.setTitle(R.string.tituloErro);
                builder.setMessage(R.string.mensagemErro);
                builder.setPositiveButton(R.string.botaoErro, null);
                builder.show();
            }
        }
    };

    // Salva a Categoria na base de dados
    private void salvaCateg(){
        try{
            DBAdapter databaseConnector = new DBAdapter(this);
            databaseConnector.open();
            if (getIntent().getExtras() == null){
                databaseConnector.insereCateg(txtTitulo.getText().toString());
            }
            databaseConnector.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_nova_categoria, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
