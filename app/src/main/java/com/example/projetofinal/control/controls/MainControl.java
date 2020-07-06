package com.example.projetofinal.control.controls;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projetofinal.R;
import com.example.projetofinal.control.bo.GameBO;
import com.example.projetofinal.control.service.HttpService;
import com.example.projetofinal.model.adapters.AdapterGameInfiniteScroll;
import com.example.projetofinal.model.dao.GameDao;
import com.example.projetofinal.model.vo.Game;
import com.example.projetofinal.model.dto.GameDTO;
import com.example.projetofinal.model.vo.ResultadoGameList;
import com.example.projetofinal.view.FavoritosActivity;
import com.example.projetofinal.view.MainActivity;
import com.example.projetofinal.view.UploadImageActivity;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainControl implements GameListControl {

    //Numero de jogos consultados em cada request
    private static final int MAX_ITEMS_PER_REQUEST = 20;
    //Numero de jogos disponíveis na request, para saber quando parar de fazer request no infiniteScroll
    private static int NUMBER_OF_ITEMS;

    private MainActivity activity;
    private MainControl control = this;
    private List<Game> gamesList;
    private List<Game> gameListResult;
    private AdapterGameInfiniteScroll adapterGame;
    private LinearLayoutManager manager;
    private GameDao gameDao;
    private ResultadoGameList ultimoResultado;
    //Quantas paginas ja foram requeridas
    private int page;
    //boolean para saber se a proxima requisiçao é de uma proxima pagina ou nao
    //se nao for, a lista será esvaziada para ser feita uma nova lista
    private boolean isProximaPagina;
    //Boolean para controlar o loading e nao permitir que seja feita mais de uma request por loading
    private boolean isLoading = false;


    //Converte a response da request para um objeto de ResultadoGameList
    //Entao, pega a lista de GameDTO e converte um por um em Game, e adiciona no adapter
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Gson gson = new Gson();
            ResultadoGameList resultado = gson.fromJson((String) msg.obj, ResultadoGameList.class);
            ultimoResultado = resultado;
            NUMBER_OF_ITEMS = resultado.getCount();
            GameBO gameBO = new GameBO(activity);
            if (!isProximaPagina) {
                adapterGame.removeAll();
                page = 0;
                for (GameDTO dto : resultado.getResults()) {
                    adapterGame.add(gameBO.parseGameDTO(dto, adapterGame));
                }
            } else {
                for (GameDTO dto : resultado.getResults()) {
                    gameListResult.add(gameBO.parseGameDTO(dto, adapterGame));
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler handlerToast = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.obj instanceof String) {
                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public MainControl(MainActivity activity) {
        this.activity = activity;
        configInicial();
    }

    private void configInicial() {
        gamesList = new ArrayList<>();
        gameListResult = new ArrayList<>();
        gameDao = new GameDao(activity);
        adapterGame = new AdapterGameInfiniteScroll(activity, gamesList, this);
        manager = new LinearLayoutManager(activity);
        activity.getLvGames().setLayoutManager(manager);
        activity.getLvGames().setAdapter(adapterGame);
        getGamesRequest(null, null);
        //Adiciona o listener InfiniteScroll no RecyclerView
        activity.getLvGames().addOnScrollListener(createInfiniteScrollListener());
        this.isProximaPagina = false;
    }

    //Realiza uma nova request por uma lista de jogos
    //Os parametros podem ser passados como null
    //Se forem nulos, fará uma pesquisa geral
    //Se pesquisa nao for nula, e proximaPagina for nula, fará uma pesquisa nova
    //Se proximaPagina nao for nula, pegará a proxima página, que é recebida na response pro objeto ResultadoGameList
    private void getGamesRequest(String pesquisa, String proximaPagina) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.obj = HttpService.getGameList(pesquisa, proximaPagina);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    //Cria o listener para carregar mais itens a medida que rola a lista
    //Primeiro verifica se ja atingiu o numero de itens maximo da pesquisa
    //Se nao atingiu, faz uma nova lista, pega os itens do adapter atual, e adiciona os novos itens na nova lista
    //cria um novo adapter com a nova lista, e bota o novo adapter no RecyclerView
    @NonNull
    private InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(MAX_ITEMS_PER_REQUEST, manager) {
            @Override
            public void onScrolledToEnd(final int firstVisibleItemPosition) {
                if (isLoading) {
                    return;
                }
                int start = ++page * MAX_ITEMS_PER_REQUEST;
                final boolean allItemsLoaded = start >= NUMBER_OF_ITEMS;
                if (!allItemsLoaded) {
                    isLoading = true;
                    gameListResult = new ArrayList<>();
                    gameListResult.addAll(adapterGame.getItems());
                    LoadNextPage asyncLoad = new LoadNextPage();
                    AdapterGameInfiniteScroll novoAdapter = new AdapterGameInfiniteScroll(activity, gameListResult, control);
                    adapterGame = novoAdapter;
                    asyncLoad.execute(ultimoResultado.getNext());
                    refreshView(activity.getLvGames(), novoAdapter, firstVisibleItemPosition);
                } else {
                    activity.getProgressBar().setVisibility(View.GONE);
                }
            }
        };
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
                    if (isChecked) {
                        saveGameAndImageFromDrawableOfGame(game);
                    } else {
                        Game gameFromDatabase = gameDao.getDao().queryForId(game.getId());
                        if (deleteImageFromGame(gameFromDatabase)) {
                            gameDao.getDao().delete(gameFromDatabase);
                            System.out.println(activity.getString(R.string.jogo_removido_sucesso_main));
                        } else {
                            saveGameAndImageFromDrawableOfGame(game);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Thread em paralelo para salvar o jogo nos favoritos caso clique no checkbox e o checkbox esteja marcado
    //Salva primeiro a imagem, e verifica se ela foi salva
    //Se tiver sido salva, o jogo também é salvo
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
                msg.obj = activity.getString(R.string.erro_salvar_jogo_main);
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

    //Faz uma nova requisiçao de pesquisa, com o texto presente no editText de pesquisa
    public void pesquisarGames() {
        String pesquisa = activity.getEditTextTermoPesquisa().getText().toString();
        getGamesRequest(pesquisa, null);
    }

    //Abre tela de favoritos
    public void chamarFavoritosActivity() {
        Intent it = new Intent(activity, FavoritosActivity.class);
        activity.startActivity(it);
    }

    //Abre tela de upload de imagem
    public void chamarUploadImageActivity() {
        Intent it = new Intent(activity, UploadImageActivity.class);
        activity.startActivity(it);
    }

    //Task assincrona responsavel por carregar mais itens para lista infinita
    //É aqui que adiciono os novos itens na lista criada la no listener do InfiniteScroll
    private class LoadNextPage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            activity.getProgressBar().setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... proximaPagina) {
            try {
                System.out.println("------------------------------------------------ ASYNC TASK ------------------------------------------------");
                String response = HttpService.getGameList(null, proximaPagina[0]);
                Gson gson = new Gson();
                ResultadoGameList resultado = gson.fromJson(response, ResultadoGameList.class);
                ultimoResultado = resultado;
                NUMBER_OF_ITEMS = resultado.getCount();
                GameBO gameBO = new GameBO(activity);
                for (GameDTO dto : resultado.getResults()) {
                    gameListResult.add(gameBO.parseGameDTO(dto, adapterGame));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            activity.getProgressBar().setVisibility(View.GONE);
            adapterGame.notifyDataSetChanged();
            isLoading = false;
        }
    }
}
