package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchFilmeResponse {
    public List<Filme> Filmes = new ArrayList<>();
    public long TotalFilmes;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
