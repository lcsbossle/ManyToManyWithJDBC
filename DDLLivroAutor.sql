CREATE TABLE autor(
id int(4) AUTO_INCREMENT,
nome varchar(45) NOT NULL,
constraint pk_Autor PRIMARY KEY (id)
);


CREATE TABLE livro
(id int(4) AUTO_INCREMENT,
titulo varchar(45),
constraint pk_Livro PRIMARY KEY (id));

CREATE TABLE Livro_Autor
(idLivro int(4),
idAutor int(4),
constraint pk_LivroAutor primary key(idLivro, idAutor),
constraint fk_LivroAutor_pk_Livro foreign key(idLivro) references livro(id) on update cascade,
constraint fk_LivroAutor_pk_Autor foreign key(idAutor) references autor(id) on update cascade);