package com.example.projetofinal.control.controls;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projetofinal.R;
import com.example.projetofinal.control.service.HttpService;
import com.example.projetofinal.model.vo.ResultadoUploadImage;
import com.example.projetofinal.view.UploadImageActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifDrawable;

public class UploadImageControl {

    private UploadImageActivity activity;
    private GifDrawable imagemSelecionada;
    private Uri uri;

    public UploadImageControl(UploadImageActivity activity) {
        this.activity = activity;
        configInicial();
    }

    private void configInicial() {
        activity.getToolbarTitle().setText(R.string.upload_actionbar_title);
    }

    //Método chamado na activity para informar uri da imagem a ser upada
    //Após ser setado a uri na variavel global, boto ela no GifImageView
    //GifImageView é uma ImageView customizada para incluir a possibilidade de exibiçao de gifs
    //O gif é pego da uri e posto no objeto GifDrawable e inserido no GifImageView
    //Informei a dependencia no documento pdf, e também esta nas dependencias do gradle
    public void setImageURI(Uri uri) {
        this.uri = uri;
        try {
            //imagemSelecionada = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
            imagemSelecionada = new GifDrawable(activity.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        activity.getIvImagemSelecionada().setImageDrawable(imagemSelecionada);
        activity.getBtSubirImagem().setEnabled(true);
    }

    //Verifica se tem permissao de leitura e escrita no external storare se o android for marshmallow ou posterior
    //Pede a permissão e após isso chama metodo pra selecionar imagem na galeria
    public void verificarPermissaoAndSelecionarImagemFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                activity.requestPermissions(permissions, UploadImageActivity.getPermissionCode());
            } else {
                selecionarImagemFromGallery();
            }
        } else {
            selecionarImagemFromGallery();
        }

    }

    //Seleciona imagem da galeria de imagens do celular do usuario
    //permite selecionar PNGs, JPGs e GIFs
    public void selecionarImagemFromGallery() {
        Intent it = new Intent(Intent.ACTION_PICK);
        it.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "image/gif"};
        it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activity.startActivityForResult(it, UploadImageActivity.getGalleryRequestCode());
    }

    //Finaliza activity atual e volta para a anterior
    public void voltar() {
        activity.finish();
    }

    //Cria um inputstream da imagem salva no sdcard
    //Insere num array de bytes, e encoda para uma String Base64
    //Chama o método postImage passando a string base64 da imagem como parametro
    public void uploadBitmap() {
        activity.getProgressBar().setVisibility(View.VISIBLE);
        try {
            InputStream is = activity.getContentResolver().openInputStream(uri);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            bytes = output.toByteArray();
            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
            is.close();
            output.close();
            postImage(encodedString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Thread em paralelo para fazer a request de upload de imagem, fazendo o upload da imagem em base64
    //Depois passa para o handler a resposta com o link da imagem upada
    private void postImage(String encodedImage) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.obj = HttpService.postImage(encodedImage);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //Se o upload tiver sido sucesso, pega a url da imagem passado pela response
    //e seta a url no editText, sendo possivel copiar o texto
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
        Gson gson = new Gson();
        ResultadoUploadImage resultado = gson.fromJson((String) msg.obj, ResultadoUploadImage.class);
        if (resultado.getStatus() == 200) {
            activity.getEditTextLinkGerado().setText(resultado.getData().getImage().getUrl());
            activity.getEditTextLinkGerado().setTextIsSelectable(true);
        } else {
            Toast.makeText(activity, activity.getString(R.string.http_status) + resultado.getStatus(), Toast.LENGTH_SHORT).show();
        }
        activity.getProgressBar().setVisibility(View.GONE);
        }
    };
}
