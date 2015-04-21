package aula.amargarin.trabalho_1_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class AddNovoOrcamento extends Activity {

    private static final int IMAGEM_SELECIONADA = 1;

    private long idLinha;
    private Button btnSalvar;

    private String nameCateg;
    private int idCateg;

    private EditText txtCategoria;
    private EditText idCategoria;
    private EditText txtLoja;
    private EditText txtValor;
    private EditText txtPagamento;
    private EditText txtObservacao;
    private ImageView imageView;

    private String caminhoImagem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_novo_orcamento);

        txtLoja = (EditText) findViewById(R.id.txtLoja);
        txtValor = (EditText) findViewById(R.id.txtValor);
        txtPagamento = (EditText) findViewById(R.id.txtPagamento);
        txtObservacao = (EditText) findViewById(R.id.txtObservacao);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(uploadImage);

        Bundle extras = getIntent().getExtras();

        // Se há extras, usa os valores para preencher a tela
        if (extras != null){
            idLinha = extras.getLong("_id");
            nameCateg = extras.getString("nameCateg").toString();
            idCateg = extras.getInt("idCateg");
        }

        txtCategoria = (EditText) findViewById(R.id.txtCategoria);
        txtCategoria.setText(nameCateg);
        txtCategoria.setEnabled(false);

        idCategoria = (EditText) findViewById(R.id.idCategoria);
        idCategoria.setId(idCateg);
        idCategoria.setVisibility(View.INVISIBLE);

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(salvarOrcaButtonClicked);
    }

    View.OnClickListener uploadImage = new View.OnClickListener(){
        public void onClick(View v){
            Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(it, IMAGEM_SELECIONADA);
        }
    };

    View.OnClickListener salvarOrcaButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            if(txtLoja.getText().length() != 0 &&
               txtValor.getText().length() != 0 &&
               txtPagamento.getText().length() != 0 &&
               txtObservacao.getText().length() != 0){

                AsyncTask<Object, Object, Object> salvaOrcaTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaOrca();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result){
                        finish();
                    }
                };

                salvaOrcaTask.execute();
            }else{
                // Cria uma caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNovoOrcamento.this);
                builder.setTitle(R.string.tituloErro);
                builder.setMessage(R.string.mensagemErro);
                builder.setPositiveButton(R.string.botaoErro, null);
                builder.show();
            }
        }
    };

    // Salva a Categoria na base de dados
    private void salvaOrca(){
        try{
            DBAdapter db = new DBAdapter(this);
            db.open();
            if (getIntent().getExtras() == null){

                double valor = Double.parseDouble(txtValor.getText().toString());
                int idCategAux = Integer.parseInt(idCategoria.getText().toString());

                db.insereOrcamento(
                   txtLoja.getText().toString(),
                   valor,
                   txtPagamento.getText().toString(),
                   txtObservacao.getText().toString(),
                   caminhoImagem,
                   idCategAux
                );
            }
            db.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri imagemSelecionada = intent.getData();
                    String[] caminhoArquivoColuna = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(imagemSelecionada, caminhoArquivoColuna, null, null, null);
                    cursor.moveToFirst();

                    int indiceColuna = cursor.getColumnIndex(caminhoArquivoColuna[0]);
                    String caminhoImagemSelecionada = cursor.getString(indiceColuna);
                    cursor.close();

                    imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(caminhoImagemSelecionada),
                            imageView.getWidth(), imageView.getHeight(), true));

                    caminhoImagem = caminhoImagemSelecionada;
                }
        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_novo_orcamento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent it = new Intent(AddNovoOrcamento.this, MainActivity.class);
        startActivity(it);

        return super.onOptionsItemSelected(item);
    }
}
