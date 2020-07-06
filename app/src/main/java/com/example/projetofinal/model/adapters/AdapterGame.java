package com.example.projetofinal.model.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.projetofinal.R;
import com.example.projetofinal.control.controls.GameListControl;
import com.example.projetofinal.model.dao.GameDao;
import com.example.projetofinal.model.vo.Game;
import com.iambedant.text.OutlineTextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterGame extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Game> itens;
    //Fiz um map associando cada jogo a uma checkbox, nao consegui pegar o checkbox de um item
    //da listview de uma maneira menos feia
    private Map<CheckBox, Game> cbGameMap;
    private GameDao dao;
    private GameListControl control;
    private Context context;

    //Adicionei control como parametro, pois precisava botar um listener no CheckBox (botão de favoritar) de cada jogo
    //O método para adicionar o listener está no control
    public AdapterGame(Context context, List<Game> itens, GameListControl control) {
        this.context = context;
        this.itens = itens;
        this.control = control;
        inflater = LayoutInflater.from(context);
        cbGameMap = new HashMap<>();
        dao = new GameDao(context);
    }

    public void add(Game g) {
        itens.add(g);
        notifyDataSetChanged();
    }

    public void remove(Game g) {
        itens.remove(g);
        notifyDataSetChanged();
    }

    public void removeAll() {
        itens.clear();
        cbGameMap.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Object getItem(int position) {
        return itens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itens.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Game game = itens.get(position);
        convertView = inflater.inflate(R.layout.item_game_listview, null);

        //OutlineTextView é uma TextView custom de um projeto github, que adiciona bordas às letras,
        //Pra deixar o texto mais legível em cima de imagens
        OutlineTextView tvGameName = convertView.findViewById(R.id.tvGameName);
        OutlineTextView tvGameRelease = convertView.findViewById(R.id.tvGameRelease);
        OutlineTextView tvGameRating = convertView.findViewById(R.id.tvGameRating);
        ImageView ivGameBackground = convertView.findViewById(R.id.ivGameBackground);
        final CheckBox cbGameFavorito = convertView.findViewById(R.id.cbGameFavorito);

        //Associando CheckBox com o game em um Map, pra saber qual game pegar
        // quando o usuario clicar em um checkbox
        cbGameMap.put(cbGameFavorito, game);

        //Marca checkbox se o jogo ja estiver no banco de dados
        try {
            if (dao.getDao().idExists(game.getId())) {
                cbGameFavorito.setChecked(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Adiciona imagem como fundo se ela existir, senao bota um fundo preto
        Drawable drawable = getImageFromGame(game);
        if (drawable != null) {
            ivGameBackground.setImageDrawable(drawable);
        } else {
            ivGameBackground.setBackgroundColor(Color.rgb(0, 0, 0));
        }

        tvGameName.setText(game.getNome());
        if (game.getLancamento() != null) {
            tvGameRelease.setText(context.getString(R.string.data_lancamento_string) + new SimpleDateFormat("dd/MM/yyyy").format(game.getLancamento()));
        } else {
            tvGameRelease.setText(R.string.data_lancamento_desconhecida_string);
        }
        tvGameRating.setText(context.getString(R.string.nota_jogo) + game.getNota().toString() + "/" + game.getNotaMaxima().toString());

        //Adiciona listener no checkbox passado como parametro
        control.setCheckBoxListener(cbGameFavorito);
        return convertView;
    }

    //Retorna imagem salva no sdcard se existir
    //Caso nao exista, pega imagem Drawable do objeto Game
    //Se for nulo, retornará null
    private Drawable getImageFromGame(Game g) {
        if (g.getImagePath() != null && !g.getImagePath().equals("")) {
            Drawable d = BitmapDrawable.createFromPath(g.getImagePath());
            return d;
        } else {
            return g.getImage();
        }
    }

    public Map<CheckBox, Game> getCbGameMap() {
        return cbGameMap;
    }
}
