package com.example.projetofinal.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.projetofinal.R;
import com.example.projetofinal.control.controls.UploadImageControl;

import pl.droidsonroids.gif.GifImageView;

public class UploadImageActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private UploadImageControl control;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton btVoltar;
    private Button btEscolherImagem;
    private Button btSubirImagem;
    private EditText editTextLinkGerado;
    private GifImageView ivImagemSelecionada;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        inicializar();
        control = new UploadImageControl(this);
        setSupportActionBar(toolbar);
    }

    private void inicializar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.tituloToolbar);
        btVoltar = findViewById(R.id.backButton);
        btEscolherImagem = findViewById(R.id.btEscolherImagem);
        btSubirImagem = findViewById(R.id.btSubirImagem);
        editTextLinkGerado = findViewById(R.id.editTextLinkGerado);
        ivImagemSelecionada = findViewById(R.id.ivImagemSelecionada);
        progressBar = findViewById(R.id.progressBarUploadImage);

        progressBar.bringToFront();

        btVoltar.setOnClickListener(v -> control.voltar());
        btEscolherImagem.setOnClickListener(v -> control.verificarPermissaoAndSelecionarImagemFromGallery());
        btSubirImagem.setOnClickListener(v -> control.uploadBitmap());
    }

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(TextView toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    public EditText getEditTextLinkGerado() {
        return editTextLinkGerado;
    }

    public void setEditTextLinkGerado(EditText editTextLinkGerado) {
        this.editTextLinkGerado = editTextLinkGerado;
    }

    public GifImageView getIvImagemSelecionada() {
        return ivImagemSelecionada;
    }

    public void setIvImagemSelecionada(GifImageView ivImagemSelecionada) {
        this.ivImagemSelecionada = ivImagemSelecionada;
    }

    public Button getBtSubirImagem() {
        return btSubirImagem;
    }

    public static int getGalleryRequestCode() {
        return GALLERY_REQUEST_CODE;
    }

    public static int getPermissionCode() {
        return PERMISSION_CODE;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri uri = data.getData();
                    control.setImageURI(uri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    control.selecionarImagemFromGallery();
                }
            }
        }
    }
}
