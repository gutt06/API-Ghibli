-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into book (id, titulo, autor, editora, anoLancamento, estaDisponivel) values(1, 'Dom Casmurro', 'Machado de Assis', 'Editora Saraiva', 1899, true);
insert into book (id, titulo, autor, editora, anoLancamento, estaDisponivel) values(2, 'Harry Potter', 'JK Rolling', 'Editora Livro', 2011, false);
insert into book (id, titulo, autor, editora, anoLancamento, estaDisponivel) values(3, 'Diario Indestrutivel', 'Agata Runin', 'Studio Par', 2021, true);
insert into book (id, titulo, autor, editora, anoLancamento, estaDisponivel) values(4, 'Capitao America', 'Stuart Rom', 'Marvel', 1999, false);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Hayao Miyazaki é um dos mais renomados diretores de animação japoneses, conhecido mundialmente por suas obras que misturam fantasia, natureza e elementos culturais japoneses.',
    'Diretor japonês de animação mundialmente reconhecido',
    'Urso de Ouro, Oscar de Melhor Filme de Animação'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'George Lucas revolucionou o cinema com efeitos especiais digitais.',
    'Diretor americano',
    'Lifetime Achievement Award, Irving G. Thalberg Memorial Award'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Takashi Miike é conhecido por sua filmografia prolífica e diversificada, explorando diversos gêneros cinematográficos.',
    'Diretor japonês de cinema experimental',
    'Diversos prêmios em festivais internacionais'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Philip Kaufman é um diretor canadense conhecido por suas adaptações cinematográficas únicas.',
    'Diretor canadense de adaptações literárias',
    'Indicações ao Globo de Ouro'
);

insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Miyazaki', '1940-03-14', 'Japones', 1);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Lucas', '1970-09-23', 'Estado Unidense', 2);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Takashi', '1987-06-03', 'Japones', 3);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Philip', '1999-02-17', 'Canadense', 4);

insert into genero (nome, descricao) values('Fantasia', 'Filmes fantasiosos');
insert into genero (nome, descricao) values('Aventura', 'Filmes de aventura');
insert into genero (nome, descricao) values('Familia', 'Filmes para a familia');

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values('A viagem de Chihiro', 'Chihiro sinopse', 2010, 9.5, 12, 1);
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values('Meu vizinho Totoro', 'Totoro sinopse', 1999, 9, 0, 2 );
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values('O castelo animado', 'Castelo animado sinopse', 2014, 8.7, 14, 3);
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values('O servico de entregas da Kiki', 'Kiki sinopse', 1990, 8, 0, 1);

-- A viagem de Chihiro → Fantasia + Aventura
insert into filme_genero (filme_id, genero_id) values (1, 1), (1, 2);

-- Meu vizinho Totoro → Fantasia + Familia
insert into filme_genero (filme_id, genero_id) values (2, 1), (2, 3);

-- O castelo animado → Fantasia + Aventura
insert into filme_genero (filme_id, genero_id) values (3, 1), (3, 2);

-- O serviço de entregas da Kiki → Fantasia + Familia + Aventura
insert into filme_genero (filme_id, genero_id) values (4, 1), (4, 2), (4, 3);