package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchGeneroResponse {
    public List<Genero> Generos = new ArrayList<>();
    public long TotalGeneros;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
