package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchBookResponse {

    // Entidade para fazer os HTO's

    public List<Book> Books = new ArrayList<>();
    public long TotalBooks;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}
