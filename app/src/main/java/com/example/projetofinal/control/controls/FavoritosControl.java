package com.example.projetofinal.control.controls;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projetofinal.R;
import com.example.projetofinal.model.adapters.AdapterGame;
import com.example.projetofinal.model.dao.GameDao;
import com.example.projetofinal.model.vo.Game;
import com.example.projetofinal.view.FavoritosActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FavoritosControl implements GameListControl {

    private FavoritosActivity activity;
    private GameDao gameDao;
    private List<Game> gamesList;
    private AdapterGame adapterGame;

    @SuppressLint("HandlerLeak")
    private final Handler handlerToast = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.obj instanceof String) {
                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public FavoritosControl(FavoritosActivity activity) {
        this.activity = activity;
        configInicial();
    }

    private void configInicial() {
        gameDao = new GameDao(activity);
        try {
            gamesList = gameDao.getDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapterGame = new AdapterGame(activity, gamesList, this);
        activity.getLvFavGames().setAdapter(adapterGame);
        activity.getToolbarTitle().setText(R.string.favoritos_actionbar_title);
    }

    //Recebe como parametro um CheckBox
    //Com o checkbox, consigo pegar o game deste checkbox especifico por meio do Map
    //criado no adapter
    public void setCheckBoxListener(final CheckBox cbGameFavorito) {
        cbGameFavorito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Game game = adapterGame.getCbGameMap().get(cbGameFavorito);
                try {
                    //Se o checkbox estiver checado, salva o jogo
                    //Se nao, deleta o jogo
                    if (isChecked) {
                        saveGameAndImageFromDrawableOfGame(game);
                    } else {
                        Game gameFromDatabase = gameDao.getDao().queryForId(game.getId());
                        if (deleteImageFromGame(gameFromDatabase)) {
                            gameDao.getDao().delete(gameFromDatabase);
                            adapterGame.getCbGameMap().remove(cbGameFavorito);
                            adapterGame.remove(game);
                            Toast.makeText(activity, R.string.jogo_removido_favoritos, Toast.LENGTH_SHORT).show();
                        } else {
                            //Caso algo de errado no delete da imagem, salva tudo de volta
                            saveGameAndImageFromDrawableOfGame(game);
                            Toast.makeText(activity, R.string.nao_possivel_remover_jogo_favoritos, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    };

    //Thread em paralelo para salvar o jogo nos favoritos caso clique no checkbox e o checkbox esteja marcado
    //Salva primeiro a imagem, e verifica se ela foi salva
    //Se tiver sido salva, o jogo também é salvo
    //Este método está aqui porque embora caso o usuário desmarque o checkbox, o item saia instantaneamente da lista
    //Algo ainda pode dar errado no delete, e eu precise salvar tudo de novo como rollback
    private void saveGameAndImageFromDrawableOfGame(final Game g) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) g.getImage();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    File folder = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/ProjetoFinal/");
                    System.out.println(folder.getPath());
                    if (!folder.exists())
                        folder.mkdirs();
                    File f = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "ProjetoFinal" + File.separator + g.getNome() + ".png");
                    System.out.println("--------------------------------- " + f.getPath());
                    g.setImagePath(f.getPath());
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    bytes.close();
                    if (g.getImagePath() != null && !g.getImagePath().equals("")) {
                        salvarGame(g);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //Método usado pela thread em paralelo para salvar o jogo
    //Verifica se o jogo foi salvo, e mostra um Toast por meio do handler se foi salvo ou nao
    private void salvarGame(Game game) {
        try {
            Message msg = new Message();
            gameDao.getDao().createIfNotExists(game);
            if (gameDao.getDao().idExists(game.getId())) {
                msg.obj = activity.getString(R.string.jogo_adicionado_favoritos);
                handlerToast.sendMessage(msg);
            } else {
                msg.obj = activity.getString(R.string.erro_salvar_jogo);
                handlerToast.sendMessage(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Metodo usado no listener do checkbox para deletar imagem do sdcard
    private boolean deleteImageFromGame(Game g) {
        System.out.println("Deletando imagem de: " + g.getNome() + " | Local: " + g.getImagePath());
        File file = new File(g.getImagePath());
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    //Finaliza a activity atual e volta para a anterior
    public void voltar() {
        activity.finish();
    }
}
