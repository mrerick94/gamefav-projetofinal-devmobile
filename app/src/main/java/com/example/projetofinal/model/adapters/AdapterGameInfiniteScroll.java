package com.example.projetofinal.model.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class AdapterGameInfiniteScroll extends RecyclerView.Adapter<AdapterGameInfiniteScroll.ViewHolder> {

    private List<Game> itens;
    private Map<CheckBox, Game> cbGameMap;
    private GameDao dao;
    private GameListControl control;
    private Context context;

    public AdapterGameInfiniteScroll(Context context, List<Game> itens, GameListControl control) {
        this.context = context;
        this.itens = itens;
        this.control = control;
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

    public Object getItem(int position) {
        return itens.get(position);
    }

    public List<Game> getItems() {
        return itens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.item_game_listview, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = itens.get(position);
        cbGameMap.put(holder.cbGameFavorito, game);

        try {
            if (dao.getDao().idExists(game.getId())) {
                holder.cbGameFavorito.setChecked(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Drawable drawable = getImageFromGame(game);
        if (drawable != null) {
            holder.ivGameBackground.setImageDrawable(drawable);
        } else {
            holder.ivGameBackground.setBackgroundColor(Color.rgb(0, 0, 0));
        }

        holder.tvGameName.setText(game.getNome());
        if (game.getLancamento() != null) {
            holder.tvGameRelease.setText(context.getString(R.string.data_lancamento_string) + new SimpleDateFormat("dd/MM/yyyy").format(game.getLancamento()));
        } else {
            holder.tvGameRelease.setText(R.string.data_lancamento_desconhecida_string);
        }
        holder.tvGameRating.setText(context.getString(R.string.nota_jogo) + game.getNota().toString() + "/" + game.getNotaMaxima().toString());

        control.setCheckBoxListener(holder.cbGameFavorito);
    }

    @Override
    public long getItemId(int position) {
        return itens.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        OutlineTextView tvGameName;
        OutlineTextView tvGameRelease;
        OutlineTextView tvGameRating;
        ImageView ivGameBackground;
        final CheckBox cbGameFavorito;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvGameName = itemView.findViewById(R.id.tvGameName);
            tvGameRelease = itemView.findViewById(R.id.tvGameRelease);
            tvGameRating = itemView.findViewById(R.id.tvGameRating);
            ivGameBackground = itemView.findViewById(R.id.ivGameBackground);
            cbGameFavorito = itemView.findViewById(R.id.cbGameFavorito);
        }
    }
}
