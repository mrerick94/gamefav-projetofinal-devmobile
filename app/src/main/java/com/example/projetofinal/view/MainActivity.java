package com.example.projetofinal.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.projetofinal.R;
import com.example.projetofinal.control.controls.MainControl;

public class MainActivity extends AppCompatActivity {

    private RecyclerView lvGames;
    private EditText editTextTermoPesquisa;
    private ImageButton btPesquisar;
    private ImageButton btFavoritos;
    private ImageButton btUploadImage;
    private MainControl control;
    private ProgressBar progressBar;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializar();
        control = new MainControl(this);
        setSupportActionBar(toolbar);
    }

    private void inicializar() {
        lvGames = findViewById(R.id.lvGames);
        editTextTermoPesquisa = findViewById(R.id.editTextTermoPesquisa);
        btFavoritos = findViewById(R.id.btFavoritos);
        btPesquisar = findViewById(R.id.btPesquisar);
        btUploadImage = findViewById(R.id.btUploadImage);
        progressBar = findViewById(R.id.progress_bar);
        toolbar = findViewById(R.id.toolbar_main);


        btFavoritos.setOnClickListener(v -> control.chamarFavoritosActivity());
        btPesquisar.setOnClickListener(v -> control.pesquisarGames());
        btUploadImage.setOnClickListener(v -> control.chamarUploadImageActivity());

        lvGames.setHasFixedSize(true);
    }

    public RecyclerView getLvGames() {
        return lvGames;
    }

    public void setLvGames(RecyclerView lvGames) {
        this.lvGames = lvGames;
    }

    public EditText getEditTextTermoPesquisa() {
        return editTextTermoPesquisa;
    }

    public void setEditTextTermoPesquisa(EditText editTextTermoPesquisa) {
        this.editTextTermoPesquisa = editTextTermoPesquisa;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


}
