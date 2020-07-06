package com.example.projetofinal.control.bo;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.projetofinal.model.adapters.AdapterGameInfiniteScroll;
import com.example.projetofinal.model.vo.Game;
import com.example.projetofinal.model.dto.GameDTO;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GameBO {

    private Activity activity;

    public GameBO(Activity activity) {
        this.activity = activity;
    }

    //Irá converter DTO para objeto Game normal.
    //Adicionei como parametro o adapter porque precisava atualizar o view a medida que baixasse as imagens
    //No fim da conversao, chama task assincrona para baixar imagem da url que veio como atributo da DTO
    public Game parseGameDTO(GameDTO gameDTO, AdapterGameInfiniteScroll adapter) {
        Game game = new Game();

        game.setId(gameDTO.getId());
        game.setDescricao(gameDTO.getDescription_raw());
        try {
            if (gameDTO.getReleased() != null) {
                game.setLancamento(new SimpleDateFormat("yyyy-MM-dd").parse(gameDTO.getReleased()));
            }
            game.setNome(gameDTO.getName());
            game.setNota(gameDTO.getRating());
            game.setNotaMaxima(gameDTO.getRating_top());
            game.setImageUrl(gameDTO.getBackground_image());
            DownloadTask task = new DownloadTask(game, gameDTO, adapter);
            task.execute();
            return game;
        } catch (ParseException e) {
            e.printStackTrace();
            return game;
        }
    }

    //Task assincrona que baixa as imagens das urls e vai atualizando o adapter quando termina de baixar
    private class DownloadTask extends AsyncTask<String, Void, Drawable> {
        private Game game;
        private GameDTO gameDTO;
        private AdapterGameInfiniteScroll adapter;

        public DownloadTask(Game game, GameDTO gameDTO, AdapterGameInfiniteScroll adapterGame) {
            this.game = game;
            this.gameDTO = gameDTO;
            this.adapter = adapterGame;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(gameDTO.getBackground_image());
                return new BitmapDrawable(activity.getResources(), Picasso.get().load(url.toString()).resize(1000, 1000).centerCrop().get());
                //return BitmapDrawable.createFromStream(url.openStream(), game.getNome());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable result){
            if(result!=null){
                game.setImage(result);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
