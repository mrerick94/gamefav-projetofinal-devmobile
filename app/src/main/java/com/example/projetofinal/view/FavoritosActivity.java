package com.example.projetofinal.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.projetofinal.R;
import com.example.projetofinal.control.controls.FavoritosControl;

public class FavoritosActivity extends AppCompatActivity {

    private FavoritosControl control;
    private ListView lvFavGames;
    private Toolbar toolbar;
    private ImageButton btVoltar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        inicializar();
        control = new FavoritosControl(this);
        setSupportActionBar(toolbar);
    }

    private void inicializar() {
        lvFavGames = findViewById(R.id.lvFavGames);
        toolbar = findViewById(R.id.toolbar);
        btVoltar = findViewById(R.id.backButton);
        toolbarTitle = findViewById(R.id.tituloToolbar);

        btVoltar.setOnClickListener(v -> control.voltar());
    }

    public ListView getLvFavGames() {
        return lvFavGames;
    }

    public void setLvFavGames(ListView lvFavGames) {
        this.lvFavGames = lvFavGames;
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(TextView toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }
}
