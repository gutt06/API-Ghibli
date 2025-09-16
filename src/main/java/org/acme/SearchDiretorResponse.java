package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchDiretorResponse {
    public List<Diretor> Diretores = new ArrayList<>();
    public long TotalDiretores;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
