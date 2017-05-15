Create table Persona
(
	cf char(16) primary key,
	nome varchar(20) not null,
	cognome varchar(20) not null,
	indirizzo_residenza varchar(30),
	citta_residenza varchar(30)
);

Create table cliente
(
	cf char(16) primary key references persona on delete cascade on update cascade,
	email varchar(50),
	numero_carta char(6) unique
);

Create table dipendente
(
	Matricola varchar(6) primary key,
	cf varchar(16) unique not null references persona on delete cascade on update cascade,
	ruolo varchar(14) not null check(ruolo='inserviente' or ruolo='cassiere' or ruolo='cuoco' or ruolo='responsabile' or ruolo='amministratore'),
	matricola_capo varchar(6) references dipendente on delete set null on update cascade
);

Create table stabilimento
(
        Nome varchar(50) primary key,
        citta varchar(50) not null,
        indirizzo varchar(30) not null,
        numero_forni Numeric(2,0) check(numero_forni>0),
        numero_bagni Numeric(2,0) check(numero_bagni>0),
        numero_casse Numeric(2,0) check(numero_casse>0),
        unique(citta,indirizzo)
);


Create table ordine
(
        data date,
        codice Integer check(codice>0),
        nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
	cf varchar(16) references cliente on delete cascade on update cascade,
        primary key(data,codice,nome_stabilimento)
);

create table prodotto
(
        nome varchar(20) primary key,
        kcal smallint check (kcal>=0) default null
);

Create table bevanda
(
        nome varchar(20) primary key references prodotto on delete cascade on update cascade
);

Create table ingrediente
(
        nome varchar(20) primary key references prodotto on delete cascade on update cascade
);



Create table telefono
(
	telefono varchar(15) primary key,
	cf varchar(16) not null references persona on delete cascade on update cascade
);

Create table turno
(
	matricola varchar(6) references dipendente on delete cascade on update cascade,
	data date,
	ore Numeric(2,0) not null check(ore>0 and ore<24),
	nome_stabilimento varchar(50) default 'eliminato' references stabilimento on delete set default on update cascade,
	primary key(matricola,data,nome_stabilimento)  
);

create table fornitore
(
        p_iva varchar(11) primary key,
        nome_ditta varchar(30) unique not null,
	citta varchar(30) not null,
        indirizzo varchar(30) not null,
	unique(citta,indirizzo)
);

create table fornitura
(
        data date,
        nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
        p_iva varchar(11) references fornitore on delete cascade on update cascade,
        primary key(data,nome_stabilimento,p_iva)
);


Create table cibo
(
        nome varchar(20) primary key
);


Create table cibo_ordine
(
	codice Integer,
	data date,
	nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
	nome_cibo varchar(20) references cibo on delete cascade on update cascade,
	quantita integer not null check(quantita >0),
	primary key(codice,data,nome_cibo),
	foreign key (codice,data,nome_stabilimento) references ordine(codice,data,nome_stabilimento) on delete cascade on update cascade
);

Create table bevanda_ordine
(
	codice integer,
	data date,
	nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
	nome_bevanda varchar(20) references bevanda on delete cascade on update cascade,
	quantita integer not null check(quantita >0),
	primary key(codice,data,nome_bevanda),
	foreign key(codice,data,nome_stabilimento) references ordine(codice,data,nome_stabilimento) on delete cascade on update cascade
);

create table composizione_cibo
(
	nome_cibo varchar(20) references cibo on delete cascade on update cascade,
	nome_ingrediente varchar(20) references ingrediente on delete cascade on update cascade,
	primary key(nome_cibo,nome_ingrediente)
);

create table rifornimento
(
	data date,
	nome_stabilimento varchar(50),
	p_iva varchar(11),
	nome_prodotto varchar(20) references prodotto on delete cascade on update cascade,
	quantita smallint check(quantita >0) default null,
	spesa real check(spesa>0),
	data_scadenza date check(data_scadenza>data),
	foreign key (data,nome_stabilimento,p_iva) references fornitura (data,nome_stabilimento,p_iva)
	on delete cascade on update cascade
);

create table inventario_fornitore
(
	p_iva varchar(11) references fornitore on delete cascade on update cascade,
	nome_prodotto varchar(20) references prodotto on delete cascade on update cascade,
	primary key(p_iva,nome_prodotto)
);

create table listino_cibo
(
	nome_cibo varchar(20) references cibo on delete cascade on update cascade,
	nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
	prezzo real check(prezzo>=0) default 0,
	primary key(nome_cibo,nome_stabilimento)
);	

create table listino_bevande
(
	nome_bevanda varchar(20) references bevanda on delete cascade on update cascade,
	nome_stabilimento varchar(50) references stabilimento on delete cascade on update cascade,
	prezzo real check(prezzo>=0) default 0,
	primary key(nome_bevanda,nome_stabilimento)
);

create view Menu_completo as 
select S.nome,C.nome_cibo as alimento
        from stabilimento as S, listino_cibo as C
        where S.nome=C.nome_stabilimento
        UNION
        select S.nome,B.nome_bevanda
        from stabilimento as S, listino_bevande as B
        where S.nome=B.nome_stabilimento;


	
--INSERT PERSONA--
insert into persona values('RSSMRA78C04F257Z','Mario','Rossi','via Togliatti 4','Modena');
insert into persona values('BRTLRT80M08F257B','Alberto','Berti','via Piave 4','Castelfranco Emilia');
insert into persona values('TRZNDA72D53D969H','Nadia','Tarozzi','via Fratelli bandiera 3','Castelfranco Emilia');
insert into persona values('LBRLCU78M28A944R','Luca','Alberghini','via Agnini 48','San Cesario sul Panaro');
insert into persona values('NGRMSM91R23A944R','Massimo','Negrini','via Tarozzi 7','Bologna');
insert into persona values('MROBBR87L60C351L','Barbara','Mora','via Commenda 15','Castelnuovo Rangone');
insert into persona values('NDRMHL84B59A561J','Michela','Anderlini','via Gramsci 73','Modena');
insert into persona values('RTCNDR98M13A053H','Andrea','Ritacco','via Monte Bianco 2','Bologna');
insert into persona values('PCCGLI95T55F257O','Giulia','Piccinini','via Fratelli Moscardini 1','Modena');
insert into persona values('CCLMRZ91L17F839S','Maurizio','Accolla','via Peschiera 42','Castelfranco Emilia');
insert into persona values('CRSDNL95R23F257N','Daniele','Cristoni','via Agnini 16','San Cesario sul Panaro');


--INSERT FORTNIORE--
insert into fornitore values('03133770366','Buffetti','Castelfranco Emilia','via dei Sarti 2/4');
insert into fornitore values('03331721203','witafood','Valsamoggia','via Emilia 71');
insert into fornitore values('00162810360','Cremonini S.P.A.','Castelvetro','via Modena 53');

--INSERT CIBO--
insert into cibo values('double chicken');
insert into cibo values('angus wrap');
insert into cibo values('angus');
insert into cibo values('spicy burgher');
insert into cibo values('cheesburgher');
insert into cibo values('hamburgher');


--INSERT PRODOTTO--
insert into prodotto values('coca cola');
insert into prodotto values('fanta');
insert into prodotto values('coca cola zero');
insert into prodotto values('acqua');
insert into prodotto values('acqua frizzante');
insert into prodotto values('sprite');
insert into prodotto values('the pesca');
insert into prodotto values('the limone');
 -- TRIGGER



 -- PROCEDURE --
 







