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

insert into diretor (nome, nascimento, nacionalidade, biografia) values('Miyazaki', '1940-03-14', 'Japones', 'Miyazaki biografia');
insert into diretor (nome, nascimento, nacionalidade, biografia) values('Lucas', '1970-09-23', 'Estado Unidense', 'Lucas biografia');
insert into diretor (nome, nascimento, nacionalidade, biografia) values('Takashi', '1987-06-03', 'Japones', 'Takashi biografia');
insert into diretor (nome, nascimento, nacionalidade, biografia) values('Philip', '1999-02-17', 'Canadense', 'Philip biografia');

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