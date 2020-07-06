package com.example.projetofinal.control.controls;

import android.widget.CheckBox;


//Interface criada para permitir pegar mais de um tipo de control no AdapterGame em uma variável só
//Apenas para control que esteja controlando uma activity com lista de jogos
public interface GameListControl {

    void setCheckBoxListener(final CheckBox cbGameFavorito);
}
