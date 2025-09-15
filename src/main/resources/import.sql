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

insert into movie (id, titulo, autor, anoLancamento, nota) values(1, 'A viagem de Chihiro', 'Miyazaki', 2003, 10);
insert into movie (id, titulo, autor, anoLancamento, nota) values(2, 'Meu vizinho Totoro', 'Miyazaki', 1999, 9.3);
insert into movie (id, titulo, autor, anoLancamento, nota) values(3, 'O castelo animado', 'Miyazaki', 2006, 9.5);
insert into movie (id, titulo, autor, anoLancamento, nota) values(4, 'O servico de entregas da Kiki', 'Miyazaki', 1995, 8.4);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa) values('A viagem de Chihiro', 'Chihiro sinopse', 2010, 9.5, 12);
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa) values('Meu vizinho Totoro', 'Totoro sinopse', 1999, 9, 0);
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa) values('O castelo animado', 'Castelo animado sinopse', 2014, 8.7, 14);
insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa) values('O servico de entregas da Kiki', 'Kiki sinopse', 1990, 8, 0);