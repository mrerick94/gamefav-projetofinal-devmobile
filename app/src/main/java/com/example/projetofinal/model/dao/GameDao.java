package com.example.projetofinal.model.dao;

import android.content.Context;

import com.example.projetofinal.model.helpers.DaoHelper;
import com.example.projetofinal.model.vo.Game;

public class GameDao extends DaoHelper<Game> {

    public GameDao(Context c) {
        super(c, Game.class);
    }
}
