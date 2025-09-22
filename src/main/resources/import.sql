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
    'Hayao Miyazaki é co-fundador do Studio Ghibli e um dos mais influentes diretores de animação da história. Nascido em Tóquio, começou sua carreira na Toei Animation em 1963. Suas obras são conhecidas pela rica narrativa visual, temas ambientais, pacifismo e personagens femininas fortes. Fundou o Studio Ghibli em 1985 junto com Isao Takahata.',
    'Co-fundador do Studio Ghibli, mestre da animação japonesa',
    'Oscar de Melhor Filme de Animação (A Viagem de Chihiro), Urso de Ouro (Berlim), Leone d''Oro alla Carriera (Veneza), Ordem da Cultura do Japão'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Isao Takahata foi co-fundador do Studio Ghibli e mentor de Miyazaki. Conhecido por suas narrativas profundamente humanas e realistas, mesmo em contextos fantásticos. Começou na Toei Animation e posteriormente trabalhou na Tokyo Movie Shinsha antes de fundar o Ghibli. Era famoso por sua abordagem meticulosa e emocional ao cinema.',
    'Co-fundador do Studio Ghibli, diretor de narrativas humanistas',
    'Prêmio da Academia Japonesa, Festival de Cannes (seleção oficial), Annie Awards'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Gorō Miyazaki é diretor japonês e filho de Hayao Miyazaki. Inicialmente trabalhou como paisagista antes de ingressar no mundo da animação. Sua entrada no Studio Ghibli gerou controvérsias familiares, mas estabeleceu seu próprio estilo narrativo focado em aventuras épicas e desenvolvimento de personagens jovens.',
    'Filho de Hayao Miyazaki, diretor da segunda geração Ghibli',
    'Indicações em festivais internacionais de animação'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Hiromasa Yonebayashi é um dos diretores mais promissores da nova geração do Studio Ghibli. Começou como animador no estúdio e foi promovido a diretor por Miyazaki. Suas obras focam em histórias íntimas sobre crescimento pessoal e relacionamentos familiares, mantendo a tradição visual Ghibli.',
    'Diretor da nova geração Ghibli, especialista em dramas íntimos',
    'Annie Awards, Japan Academy Prize, reconhecimento em Cannes'
);

insert into BiografiaDiretor (textoCompleto, resumo, premiosRecebidos) values(
    'Yoshifumi Kondō foi um talentoso diretor e animador do Studio Ghibli, considerado sucessor de Miyazaki e Takahata. Trabalhou como animador-chefe em vários filmes Ghibli antes de dirigir. Sua morte prematura em 1998 foi uma grande perda para o estúdio e para a indústria da animação japonesa.',
    'Ex-diretor Ghibli, considerado sucessor de Miyazaki',
    'Japan Academy Prize, reconhecimento póstumo da indústria'
);

insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Hayao Miyazaki', '1941-01-05', 'Japonesa', 1);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Isao Takahata', '1935-10-29', 'Japonesa', 2);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Gorō Miyazaki', '1967-01-21', 'Japonesa', 3);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Hiromasa Yonebayashi', '1973-07-10', 'Japonesa', 4);
insert into diretor (nome, nascimento, nacionalidade, biografia_id) values('Yoshifumi Kondō', '1950-03-31', 'Japonesa', 5);

insert into genero (nome, descricao) values('Fantasia', 'Filmes com elementos mágicos, espíritos e mundos fantásticos');
insert into genero (nome, descricao) values('Aventura', 'Jornadas épicas, descobertas e desafios pessoais');
insert into genero (nome, descricao) values('Família', 'Histórias sobre laços familiares e crescimento pessoal');
insert into genero (nome, descricao) values('Drama', 'Narrativas emocionais profundas sobre condição humana');
insert into genero (nome, descricao) values('Romance', 'Histórias de amor e relacionamentos interpessoais');
insert into genero (nome, descricao) values('Guerra', 'Filmes que abordam conflitos bélicos e suas consequências');

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'A Viagem de Chihiro',
    'Chihiro, uma menina de 10 anos, se aventura em um mundo mágico habitado por espíritos, bruxas e dragões. Deve trabalhar em uma casa de banho para espíritos para salvar seus pais transformados em porcos.',
    2001, 9.3, 0, 1
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'Meu Vizinho Totoro',
    'Duas irmãs, Satsuki e Mei, se mudam para o campo para ficarem próximas da mãe hospitalizada e descobrem criaturas mágicas da floresta chamadas Totoros.',
    1988, 8.9, 0, 1
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'O Castelo Animado',
    'Sophie, uma jovem chapeleira, é amaldiçoada por uma bruxa e transformada em uma velha. Ela busca refúgio no castelo móvel do mago Howl para quebrar a maldição.',
    2004, 8.7, 0, 1
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'Princesa Mononoke',
    'Ashitaka, um jovem guerreiro, se envolve na luta entre deuses da floresta e humanos que consomem seus recursos naturais.',
    1997, 9.0, 12, 1
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'O Serviço de Entregas da Kiki',
    'Kiki, uma jovem bruxa de 13 anos, deixa sua cidade natal para viver de forma independente por um ano, usando seus poderes para criar um serviço de entregas.',
    1989, 8.5, 0, 1
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'Túmulo dos Vagalumes',
    'Durante a Segunda Guerra Mundial, dois irmãos órfãos lutam para sobreviver nos últimos meses do conflito no Japão.',
    1988, 8.8, 14, 2
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'Tales from Earthsea',
    'Em um mundo fantástico, um jovem príncipe foge de seu destino e encontra o mago Ged em uma jornada para restaurar o equilíbrio do mundo.',
    2006, 6.8, 10, 3
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'O Mundo dos Pequeninos',
    'Arrietty é uma jovem de 10 centímetros que vive sob o assoalho de uma casa com sua família, emprestando pequenos objetos dos "seres humanos" que vivem acima.',
    2010, 7.8, 0, 4
);

insert into filme (titulo, sinopse, anoLancamento, nota, idadeIndicativa, diretor_id) values(
    'Sussurros do Coração',
    'Shizuku, uma estudante do ensino médio apaixonada por livros, conhece Seiji, um jovem que sonha em ser luthier, e descobre sua própria paixão pela escrita.',
    1995, 8.3, 0, 5
);

-- Associações filme-gênero (Many-to-Many) baseadas nas características reais dos filmes

-- A Viagem de Chihiro: Fantasia + Aventura + Família + Drama
insert into filme_genero (filme_id, genero_id) values (1, 1), (1, 2), (1, 3), (1, 4);

-- Meu Vizinho Totoro: Fantasia + Família + Drama
insert into filme_genero (filme_id, genero_id) values (2, 1), (2, 3), (2, 4);

-- O Castelo Animado: Fantasia + Aventura + Romance + Drama
insert into filme_genero (filme_id, genero_id) values (3, 1), (3, 2), (3, 4), (3, 5);

-- Princesa Mononoke: Fantasia + Aventura + Drama + Guerra
insert into filme_genero (filme_id, genero_id) values (4, 1), (4, 2), (4, 4), (4, 6);

-- O Serviço de Entregas da Kiki: Fantasia + Aventura + Família
insert into filme_genero (filme_id, genero_id) values (5, 1), (5, 2), (5, 3);

-- Túmulo dos Vagalumes: Drama + Guerra + Família
insert into filme_genero (filme_id, genero_id) values (6, 3), (6, 4), (6, 6);

-- Tales from Earthsea: Fantasia + Aventura + Drama
insert into filme_genero (filme_id, genero_id) values (7, 1), (7, 2), (7, 4);

-- O Mundo dos Pequeninos: Fantasia + Família + Aventura
insert into filme_genero (filme_id, genero_id) values (8, 1), (8, 2), (8, 3);

-- Sussurros do Coração: Romance + Drama + Família
insert into filme_genero (filme_id, genero_id) values (9, 3), (9, 4), (9, 5);