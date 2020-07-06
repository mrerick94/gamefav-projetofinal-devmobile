package com.example.projetofinal.model.vo;

import com.example.projetofinal.model.dto.GameDTO;

import java.util.List;

public class ResultadoGameList {

    private Integer count;
    private String next;
    private String previous;
    private List<GameDTO> results;

    public ResultadoGameList() {
    }

    public ResultadoGameList(Integer count, String next, String previous, List<GameDTO> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<GameDTO> getResults() {
        return results;
    }

    public void setResults(List<GameDTO> results) {
        this.results = results;
    }
}
