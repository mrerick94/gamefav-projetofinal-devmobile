package com.example.projetofinal.model.vo;

import android.graphics.drawable.Drawable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

@DatabaseTable(tableName = "game")
public class Game {

    @DatabaseField(id = true, columnName = "id")
    private Integer id;
    @DatabaseField(columnName = "nome", canBeNull = false)
    private String nome;
    @DatabaseField(columnName = "descricao")
    private String descricao;
    @DatabaseField(columnName = "lancamento")
    private Date lancamento;
    //Não é salvo no banco, é apenas para o background
    //Caso o game seja favoritado, a imagem será salva no sdcard, e o imagePath será salvo
    private Drawable image;
    @DatabaseField(columnName = "imageurl")
    private String imageUrl;
    @DatabaseField(columnName = "imagepath")
    private String imagePath;
    @DatabaseField(columnName = "nota")
    private Double nota;
    @DatabaseField(columnName = "notamaxima")
    private Integer notaMaxima;

    public Game() {
    }

    public Game(Integer id, String nome, String descricao, Date lancamento, Drawable image, String imageUrl, String imagePath, Double nota, Integer notaMaxima) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.lancamento = lancamento;
        this.image = image;
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.nota = nota;
        this.notaMaxima = notaMaxima;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Integer getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(Integer notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
