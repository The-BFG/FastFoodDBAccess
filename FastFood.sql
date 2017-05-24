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
	matricola_capo varchar(6)default '000001' references dipendente on delete set default on update cascade
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
        nome varchar(20) primary key,
	kcal smallint default 0 check(kcal>=0) 
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
	costo real,
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
		select S.nome as stabilimento,C.nome_cibo as alimento, C.prezzo 
        from stabilimento as S, listino_cibo as C
        where S.nome=C.nome_stabilimento
        UNION
        select S.nome as stabilimento ,B.nome_bevanda as alimento, B.prezzo
        from stabilimento as S, listino_bevande as B
        where S.nome=B.nome_stabilimento;


--
-- italian_codes -- SQL definitions
--
-- Copyright (C) 2011 Daniele Varrazzo <daniele.varrazzo@gmail.com>
--

create function _cf_check(s text) returns boolean as
$$
-- Return True if *s* is a valid Codice Fiscale
-- else raise a 'check_violation' exception with a description of the problem.
declare
    error text := codice_fiscale_error(s);
begin
    if error is null then
        return true;
    else
        raise 'Codice Fiscale non valido: %', error
            using errcode = 'check_violation';
    end if;
end
$$ immutable strict language plpgsql;

create domain codice_fiscale as text
    check (_cf_check(VALUE));

comment on domain codice_fiscale is
'A valid Italian Codice Fiscale.';


create function codice_fiscale_normalize(s text) returns text as
$$
select upper(regexp_replace($1, '[[:space:]]', '', 'g'));
$$
immutable strict language sql;

comment on function codice_fiscale_normalize(text) is
'Normalize a string representing an Italian Codice Fiscale.';


create function codice_fiscale(s text) returns codice_fiscale as
$$
select codice_fiscale_normalize($1)::codice_fiscale;
$$
language sql immutable strict;

comment on function codice_fiscale(text) is
'Normalize and return a string into a codice_fiscale domain.';


create function codice_fiscale_error(s text) returns text as
$$
begin
    if length(s) = 16 then
        return _cf_error_16(s);

    elsif length(s) = 11 then
        return _cf_error_11(s);

    else
        return 'lunghezza errata: ' || length(s)::text
            || '; attesi 11 o 16 caratteri';
    end if;

    return null;
end
$$ immutable strict language plpgsql;

comment on function codice_fiscale_error(text) is
'Return the error message for a wrong Codice Fiscale, else NULL.';


create function _pi_check(s text) returns boolean as
$$
-- Return True if *s* is a valid Partita IVA
-- else raise a 'check_violation' exception with a description of the problem.
declare
    error text := partita_iva_error(s);
begin
    if error is null then
        return true;
    else
        raise 'Partita IVA non valida: %', error
            using errcode = 'check_violation';
    end if;
end
$$ immutable strict language plpgsql;

create domain partita_iva as text
    check (_pi_check(VALUE));

comment on domain partita_iva is
'A valid Italian Partita IVA.';


create function partita_iva_normalize(s text) returns text as
$$
select upper(regexp_replace($1, '[[:space:]]', '', 'g'));
$$
immutable strict language sql;

comment on function partita_iva_normalize(text) is
'Normalize a string representing an Italian Partita IVA.';


create function partita_iva(s text) returns partita_iva as
$$
select partita_iva_normalize($1)::partita_iva;
$$
language sql immutable strict;

comment on function partita_iva(text) is
'Normalize and return a string into a partita_iva domain.';


create function partita_iva_error(s text) returns text as
$$
begin
    if length(s) = 11 then
        return _cf_error_11(s);

    else
        return 'lunghezza errata: ' || length(s)::text
            || '; attesi 11 caratteri';
    end if;

    return null;
end
$$ immutable strict language plpgsql;

comment on function partita_iva_error(text) is
'Return the error message for a wrong Partita IVA, else NULL.';


create function _cf_error_16(s text) returns text as
$$
begin
    -- Check the basic pattern. If it doesn't match, slow check for errors.
    if s !~ '^[A-Z]{6}'
            '[0-9L-NP-V]{2}[A-Z][0-9L-NP-V]{2}'
            '[A-Z][0-9L-NP-V]{3}[A-Z]$' then
        for i in 1 .. 16 loop
            declare
                t text := substring('CCCCCCNNCNNCNNNC', i, 1);
                c text := substring(s, i, 1);
            begin
                if t = 'C' then
                    if c not between 'A' and 'Z' then
                        return 'carattere non valido in posizione ' || i
                            || ': attesa una lettera';
                    end if;
                else
                    if 0 = position(c in '0123456789LMNPQRSTUV') then
                        return 'carattere non valido in posizione ' || i
                            || ': atteso un numero';
                            -- Not strictly true: it could be omocodia
                    end if;
                end if;
            end;
        end loop;

        -- You shouldn't be there
        raise 'assert failed in codice_fiscale_error with input %',
            s;
    end if;

    -- Check the date
    declare
        year integer := _cf_int(substring(s from 7 for 2));
        month integer := position(substring(s from 9 for 1)
            in 'ABCDEHLMPRST');
        day integer := _cf_int(substring(s from 10 for 2));
        month_lens constant integer[] :=
            array[31,28,31,30,31,30,31,31,30,31,30,31];
        month_len integer;
    begin
        if month = 0 then
            return 'carattere per il mese sbagliato in posizione 9';
        end if;

        month_len := month_lens[month];
        if month = 2 and year % 4 = 0 then
            -- we can't tell apart 1900 from 2000, so we consider
            -- all years divisible by 4 as leap.
            month_len := month_len + 1;
        end if;

        -- women add 40 to birth day
        if day > 40 then
            day := day - 40;
        end if;

        if day not between 1 and month_len then
            return 'giorno di nascita sbagliato in posizione 7-8';
        end if;
    end;

    -- Check the control code
    if _cf_check_char(substring(s from 1 for 15))
            != substring(s from 16 for 1) then
        return 'codice di controllo sbagliato in posizione 16';
    end if;

    -- All fine
    return null;
end
$$ immutable strict language plpgsql;

create function _cf_error_11(s text) returns text as
$$
begin
    -- Check the basic pattern. If it doesn't match, slow check for errors.
    if s !~ '^[0-9]{11}$' then
        for i in 1 .. 11 loop
            declare
                c text := substring(s, i, 1);
            begin
                if c not between '0' and '9' then
                    return 'carattere non valido in posizione ' || i
                        || ': atteso un numero';
                end if;
            end;
        end loop;

        -- You shouldn't be there
        raise 'assert failed in codice_fiscale_error with input %',
            s;
    end if;

    -- Check the control digit
    if _cf_check_digit(substring(s from 1 for 10))
            != substring(s from 11 for 1) then
        return 'cifra di controllo sbagliata in posizione 11';
    end if;

    -- All fine
    return null;
end
$$ immutable strict language plpgsql;

create function _cf_check_char(s text) returns text as
$$
-- Return the control char of a string.
-- Assume the string only contains chars valid in a Codice Fiscale.
declare
    acc int := 0;
    c text;
    odd_chars constant integer[] :=
        array[1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23];

begin
    -- Even chars
    for i in 2 .. length(s) by 2 loop
        c := substring(s from i for 1);
        if c between 'A' and 'Z' then
            acc := acc + (ascii(c) - ascii('A'));
        else
            acc := acc + (ascii(c) - ascii('0'));
        end if;
    end loop;

    -- Odd chars
    for i in 1 .. length(s) by 2 loop
        c := substring(s from i for 1);
        if c between 'A' and 'Z' then
            acc := acc + odd_chars[1 + (ascii(c) - ascii('A'))];
        else
            acc := acc + odd_chars[1 + (ascii(c) - ascii('0'))];
        end if;
    end loop;

    return chr(ascii('A') + acc % 26);
end
$$ immutable strict language plpgsql;

create function _cf_int(s text) returns integer as
$$
-- Convert a string of numberic chars or replacement chars into an integer.
-- Assume only valid characters in *s*.
declare
    acc integer := 0;
    c text;
    repl_chars text := 'LMNPQRSTUV';

begin
    for i in 1 .. length(s) loop
        acc := acc * 10;
        c := substring(s from i for 1);
        if c between '0' and '9' then
            acc := acc + (ascii(c) - ascii('0'));
        else
            acc := acc + (position(c in repl_chars) - 1);
        end if;
    end loop;

    return acc;
end
$$ immutable strict language plpgsql;

create function _cf_check_digit(s text) returns text as
$$
-- Return the control digit of a Codice Fiscale per Persone Giuridiche.
-- Assume the string only contains valid chars (digits).
declare
    acc int := 0;
    c text;
    even_values constant integer[] := array[0,2,4,6,8,1,3,5,7,9];

begin
    -- Odd chars
    for i in 1 .. length(s) by 2 loop
        c := substring(s from i for 1);
        acc := acc + (ascii(c) - ascii('0'));
    end loop;

    -- Even chars
    for i in 2 .. length(s) by 2 loop
        c := substring(s from i for 1);
        acc := acc + even_values[1 + (ascii(c) - ascii('0'))];
    end loop;

    return chr(ascii('0') + (10 - (acc % 10)) % 10);
end
$$ immutable strict language plpgsql;

CREATE OR REPLACE FUNCTION controllo_codice_fiscale_partita_iva() RETURNS trigger as $$
DECLARE
	risultato text;
	CF_PIVA_normalizzato text;
	CF_PIVA varchar(16);
BEGIN
	
	IF pg_typeof(NEW)::text = 'persona' 
	THEN
		CF_PIVA=NEW.cf;
	ELSE
		CF_PIVA=NEW.p_iva;
	END IF; 
	select into CF_PIVA_normalizzato codice_fiscale_normalize(CF_PIVA::text);
	select into risultato codice_fiscale_error(CF_PIVA_normalizzato);
	IF risultato IS NOT NULL THEN RAISE EXCEPTION'codice fiscale o partita iva non valido/a: %',risultato; END IF;
	
	IF pg_typeof(NEW)::text = 'persona'
        THEN
                NEW.cf:=CF_PIVA_normalizzato::char(16);
        ELSE
                NEW.p_iva=CF_PIVA_normalizzato::char(11);
        END IF;


RETURN NEW;
END
$$LANGUAGE plpgsql;


CREATE TRIGGER trigger_controllo_codice_fiscale
BEFORE INSERT OR UPDATE on persona
FOR EACH ROW EXECUTE PROCEDURE controllo_codice_fiscale_partita_iva();

CREATE TRIGGER trigger_controllo_partita_iva
BEFORE INSERT OR UPDATE on fornitore                                        
FOR EACH ROW EXECUTE PROCEDURE controllo_codice_fiscale_partita_iva();

CREATE OR REPLACE FUNCTION controllo_disponibilita_stabilimento() RETURNS trigger as $$
DECLARE
	nuovoRuolo varchar(14);
	postiDisponibili numeric(2,0);
	postiOccupati numeric(2,0);
	posto char(12);
BEGIN
	
	SELECT INTO nuovoRuolo ruolo FROM dipendente WHERE matricola=NEW.matricola;
		
	IF nuovoRuolo='cassiere' THEN
		SELECT INTO postiDisponibili numero_casse FROM stabilimento WHERE nome=NEW.nome_stabilimento; END IF;
	IF nuovoRuolo='inserviente' THEN 
		SELECT INTO postiDisponibili numero_bagni FROM stabilimento WHERE nome=NEW.nome_stabilimento; END IF;
	IF nuovoRuolo='cuoco' THEN 
		SELECT INTO postiDisponibili numero_forni FROM stabilimento WHERE nome=NEW.nome_stabilimento; END IF;
	IF nuovoRuolo='responsabile' THEN
		postiDisponibili=1; END IF;
	SELECT INTO postiOccupati count(*) FROM turno AS T,dipendente AS D
	WHERE T.data=NEW.data AND T.Matricola=D.Matricola AND ruolo=nuovoRuolo; 
	
	IF postiOccupati>postiDisponibili THEN RAISE EXCEPTION 
	'Lo stabilimento % in data % ha i posti per il ruolo % al completo',
	NEW.nome_stabilimento,NEW.data,nuovoRuolo; END IF;
return NEW;
END
$$LANGUAGE plpgsql;

CREATE TRIGGER controllo_inserimento_turno
AFTER INSERT OR UPDATE on turno 
FOR EACH ROW EXECUTE PROCEDURE controllo_disponibilita_stabilimento();


CREATE OR REPLACE FUNCTION creazione_fornitura(nome_stabilimento varchar(50), piva_fornitore char(11))
RETURNS void AS $$
DECLARE
	oggi date;

BEGIN
	oggi=now();
	INSERT INTO fornitura values(oggi,nome_stabilimento,piva_fornitore);
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION creazione_ordine(CF char(16), nome_stab varchar(50)) RETURNS Integer as $$
DECLARE
	cod Integer;
	oggi date;
BEGIN
	oggi=now();

	SELECT INTO cod MAX(O.codice) FROM ordine as O
	WHERE O.data=oggi AND O.nome_stabilimento=nome_stab;
	
	IF cod IS NULL THEN cod=0; END IF;
		
	INSERT INTO ordine VALUES(oggi,cod+1,nome_stab,CF);
-- il +1 garantisce l'ultimo codice inserito
RETURN cod+1;
END;
 $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fornitura(nome_stabilimento varchar(50),piva_fornitore char(11),
				     lista_prodotti varchar(30)[]) RETURNS void AS $$
DECLARE
	spesa real;
	quantita smallint;
	check_prodotto varchar(20);
BEGIN

	PERFORM	creazione_fornitura(nome_stabilimento, piva_fornitore);
	

	FOR i IN 1..array_length(lista_prodotti,1) BY 3
        LOOP
		SELECT INTO check_prodotto nome FROM prodotto where lista_prodotti[i] IN (SELECT nome_prodotto FROM inventario_fornitore WHERE p_iva=piva_fornitore);
		
		IF check_prodotto IS NULL THEN 
		  RAISE NOTICE 'il fornitore % non possiede il prodotto %',piva_fornitore,lista_prodotti[i];
		ELSE   
		
		quantita=lista_prodotti[i+1]::SMALLINT;
		SELECT INTO spesa costo*quantita FROM inventario_fornitore WHERE p_iva=piva_fornitore;
                INSERT INTO rifornimento VALUES(now(),nome_stabilimento,piva_fornitore,lista_prodotti[i],
					     quantita,spesa,lista_prodotti[i+2]::date);
        	END IF;
	END LOOP;


END 
$$LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ordine(CF char(16), nome_stab varchar(50), lista_cibo varchar(50)[],lista_bevande varchar(50)[]) RETURNS void as $$
DECLARE
	cod integer;
BEGIN
	select into cod creazione_ordine(CF, nome_stab);
	raise notice 'codice : %',cod;
	FOR i IN 1..array_length(lista_cibo,1) BY 2 
	LOOP
		insert into cibo_ordine values(cod,now(),nome_stab,lista_cibo[i],lista_cibo[i+1]::INTEGER);
	END LOOP;	

	FOR i IN 1..array_length(lista_bevande,1) BY 2
       
	LOOP
                insert into bevanda_ordine values(cod,now(),nome_stab,lista_bevande[i],lista_bevande[i+1]::INTEGER);
        END LOOP;

END;
 $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION generazione_matricola() RETURNS char(6) AS $$ 
DECLARE
	matr INTEGER;
BEGIN
	SELECT INTO matr MAX(matricola)::INTEGER from dipendente;
	IF matr IS NULL OR matr<100000 THEN matr=99999; END IF;

RETURN (matr+1)::char(6); 
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION inserimento_dipendente(CF char(16), ruolo varchar(14),matricola_capo char(6)) RETURNS void AS $$
DECLARE
	matr char(6);
	
BEGIN
	SELECT INTO matr generazione_matricola();
	INSERT INTO dipendente VALUES(matr,CF,ruolo,matricola_capo);
	
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION timbro_cartellino(matr char(6),nome_stabilimento varchar(50),ore Numeric(2,0)) RETURNS void AS $$

BEGIN

	insert into turno values(matricola,now(),ore,nome_stabilimento);

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ricetta(nome_cibo varchar(20), lista_ingredienti varchar(20)[]) RETURNS void AS $$
DECLARE
	ingrediente varchar(20);
	alimento varchar(20);
BEGIN
	SELECT INTO alimento nome FROM cibo WHERE nome=nome_cibo;
	
	IF alimento IS NULL THEN
		RAISE NOTICE'Creazione del nuovo alimento %...',nome_cibo;
		INSERT INTO cibo VALUES(nome_cibo);
	ELSE
		RAISE NOTICE'Aggiornamento dell alimento %...',alimento;
	END IF;
	
	FOR i IN 1..array_length(lista_ingredienti,1)
        LOOP
		SELECT INTO ingrediente nome FROM ingrediente where nome=lista_ingredienti[i];
		
		IF  ingrediente IS NOT NULL THEN
			INSERT INTO composizione_cibo VALUES(nome_cibo,ingrediente);
        	ELSE
			RAISE EXCEPTION '% non è un ingrediente',lista_ingredienti[i];
		END IF;
	END LOOP;



END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION aggiornamento_kcal() RETURNS trigger AS $$
DECLARE
	totale smallint;
BEGIN
	SELECT INTO totale SUM(P.kcal) FROM composizione_cibo AS C, prodotto AS P
	WHERE C.nome_ingrediente=P.nome AND C.nome_cibo=NEW.nome_cibo;	
	

	UPDATE cibo SET kcal=totale WHERE nome=NEW.nome_cibo;
RETURN NEW;
END
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_aggiornamento_kcal
AFTER INSERT OR UPDATE on composizione_cibo
FOR EACH ROW EXECUTE PROCEDURE aggiornamento_kcal();

CREATE OR REPLACE FUNCTION fedele(codice_fiscale char(16)) RETURNS CHAR(6) AS $$
DECLARE
	codice CHAR(6);
BEGIN
	--prima di tutto controllo se il cliente possiede gia una carta fedelta
	SELECT INTO codice numero_carta FROM cliente WHERE cf=codice_fiscale;
	IF codice IS NOT NULL THEN

		--questa e' la notifica che puoi cancellare... se lo fai cambia l'if in IF NULL THEN e togli l' ELSE
		RAISE NOTICE'Il cliente con cf % possiede gia la carta fedelta numerata: %',codice_fiscale,codice;
	
	ELSE
	SELECT INTO codice MAX(numero_carta) FROM cliente;
	
	IF codice IS NULL THEN codice='000000'; END IF;	

	codice=(codice::smallint)+1;

	--Formatto il codice in modo corretto
	codice=lpad(codice, 6,'0');
		

	UPDATE cliente 
	SET numero_carta=codice
	WHERE cf=codice_fiscale;
	END IF;
RETURN codice;
END
$$ LANGUAGE plpgsql;


insert into persona values('RSSMRA78C04F257Z','Mario','Rossi','via Togliatti 4','Modena');
insert into persona values('BRTLRT80M08F257B','Alberto','Berti','via Piave 4','Castelfranco Emilia');
insert into persona values('TRZNDA72D53D969H','Nadia','Tarozzi','via Fratelli bandiera 3','Castelfranco Emilia');
insert into persona values('LBRLCU78M28A944R','Luca','Alberghini','via Agnini 48','San Cesario sul Panaro');
insert into persona values('NGRMSM91M05A944B','Massimo','Negrini','via Tarozzi 7','Bologna');
insert into persona values('MROBBR87L60C351L','Barbara','Mora','via Commenda 15','Castelnuovo Rangone');
insert into persona values('NDRMHL84B59A561J','Michela','Anderlini','via Gramsci 73','Modena');
insert into persona values('RTCNDR98M13A053H','Andrea','Ritacco','via Monte Bianco 2','Bologna');
insert into persona values('PCCGLI95T55F257O','Giulia','Piccinini','via Fratelli Moscardini 1','Modena');
insert into persona values('CCLMRZ91L17F839S','Maurizio','Accolla','via Peschiera 42','Castelfranco Emilia');
insert into persona values('CRSDNL95R23F257N','Daniele','Cristoni','via Agnini 16','San Cesario sul Panaro');
insert into persona values('PCLYRU96S10E506E','Yuri','Apicella','via Carso 31','Anzola');
insert into persona values('GRDMTT92A01L885D','Mattia','Giordani','via Piella 11','Vignola');
insert into persona values('LZZMRN64S50D969R','Marina','Lizzoli','via Salvatore Rosa 41','Sassuolo');

insert into cliente values('TRZNDA72D53D969H');
insert into cliente values('GRDMTT92A01L885D','Mattia.giordani@yahoo.it');
insert into cliente values('PCLYRU96S10E506E','apicella96@gmail.com');
insert into cliente values('BRTLRT80M08F257B','alberto.berti@gmail.com');
insert into cliente values('NGRMSM91M05A944B','massimomax@hotmail.it');
insert into cliente values('RSSMRA78C04F257Z');

insert into dipendente values('000001','CRSDNL95R23F257N','amministratore',null);
insert into dipendente values('000020','NDRMHL84B59A561J','responsabile','000001');
insert into dipendente values('000021','LZZMRN64S50D969R','responsabile','000001');
insert into dipendente values('000022','BRTLRT80M08F257B','responsabile','000001');
select inserimento_dipendente('LBRLCU78M28A944R','cassiere','000021');
select inserimento_dipendente('RSSMRA78C04F257Z','inserviente','000021');
select inserimento_dipendente('MROBBR87L60C351L','cuoco','000020');
select inserimento_dipendente('RTCNDR98M13A053H','cassiere','000020');
select inserimento_dipendente('PCCGLI95T55F257O','cuoco','000022');
select inserimento_dipendente('CCLMRZ91L17F839S','inserviente','000022');

insert into fornitore values('03133770366','Buffetti','Castelfranco Emilia','via dei Sarti 2/4');
insert into fornitore values('03331721203','witafood','Valsamoggia','via Emilia 71');
insert into fornitore values('00162810360','Cremonini S.P.A.','Castelvetro','via Modena 53');

insert into stabilimento values('La Madonnina','Modena','via Emilia ovest 417',3,2,4);
insert into stabilimento values('SanGer','San Cesario sul Panaro','corso Liberta 44',2,1,2);
insert into stabilimento values('la Torre','Piumazzo','via dei Mille 195',3,1,2);
insert into stabilimento values('la Stazione','Bologna','via Medaglie d oro 7',5,3,4);

insert into cibo values('double chicken');
insert into cibo values('angus wrap');
insert into cibo values('angus');
insert into cibo values('spicy burgher');
insert into cibo values('cheesburger');
insert into cibo values('hamburger');

insert into prodotto values('coca cola',185);
insert into prodotto values('fanta',200);
insert into prodotto values('coca cola zero',0);
insert into prodotto values('acqua',0);
insert into prodotto values('acqua frizzante',0);
insert into prodotto values('sprite',185);
insert into prodotto values('the pesca',220);
insert into prodotto values('the limone',225);
insert into prodotto values('pane',240);
insert into prodotto values('pane integrale', 200);
insert into prodotto values('patata', 92);
insert into prodotto values('lattuga',15);
insert into prodotto values('hamburger di manzo',300);
insert into prodotto values('hamburger di pollo',566);
insert into prodotto values('pomodoro',19);
insert into prodotto values('cipolla',25);

insert into bevanda values('coca cola');
insert into bevanda values('fanta');
insert into bevanda values('coca cola zero');
insert into bevanda values('acqua');
insert into bevanda values('acqua frizzante');
insert into bevanda values('sprite');
insert into bevanda values('the pesca');
insert into bevanda values('the limone');

insert into ingrediente values('pane');
insert into ingrediente values('pane integrale');
insert into ingrediente values('patata');
insert into ingrediente values('lattuga');
insert into ingrediente values('hamburger di manzo');
insert into ingrediente values('hamburger di pollo');
insert into ingrediente values('pomodoro');
insert into ingrediente values('cipolla');


insert into inventario_fornitore values('03133770366','coca cola',0.90);
insert into inventario_fornitore values('03133770366','fanta',0.90);
insert into inventario_fornitore values('03133770366','acqua',0.10);
insert into inventario_fornitore values('03133770366','acqua frizzante',0.10);
insert into inventario_fornitore values('03133770366','the pesca',0.70);
insert into inventario_fornitore values('03133770366','sprite',0.90);
insert into inventario_fornitore values('03133770366','pane',0.20);
insert into inventario_fornitore values('03133770366','hamburger di manzo',0.80);
insert into inventario_fornitore values('03133770366','cipolla',0.30);

insert into inventario_fornitore values('00162810360','acqua',0.20);
insert into inventario_fornitore values('00162810360','acqua frizzante',0.20);
insert into inventario_fornitore values('00162810360','the pesca',0.80);
insert into inventario_fornitore values('00162810360','sprite',0.80);
insert into inventario_fornitore values('00162810360','pomodoro',0.15);
insert into inventario_fornitore values('00162810360','hamburger di pollo',0.60);

insert into inventario_fornitore values('03331721203','coca cola',0.75);
insert into inventario_fornitore values('03331721203','fanta',0.75);
insert into inventario_fornitore values('03331721203','acqua',0.15);
insert into inventario_fornitore values('03331721203','lattuga',0.05);
insert into inventario_fornitore values('03331721203','pane integrale',0.20);
insert into inventario_fornitore values('03331721203','patata',0.20); 

insert into listino_cibo values('cheesburger','La Madonnina',1.10);
insert into listino_cibo values('hamburger','La Madonnina',1.00);
insert into listino_cibo values('angus','La Madonnina',3.40);
insert into listino_bevande values('coca cola','La Madonnina',1.70);
insert into listino_bevande values('acqua','La Madonnina',1.00);
insert into listino_bevande values('acqua frizzante','La Madonnina',1.00);
insert into listino_bevande values('the pesca','La Madonnina',1.80);

insert into listino_cibo values('double chicken','SanGer',1.20);
insert into listino_cibo values('angus wrap','SanGer',4.10);
insert into listino_cibo values('angus','SanGer',3.70);
insert into listino_cibo values('hamburger','SanGer',1.20);
insert into listino_bevande values('coca cola','SanGer',1.80);
insert into listino_bevande values('fanta','SanGer',1.80);
insert into listino_bevande values('acqua','SanGer',1.10);
insert into listino_bevande values('the limone','SanGer',2.00);

insert into listino_cibo values('hamburger','la Torre',1.10);
insert into listino_cibo values('cheesburger','la Torre',1.20);
insert into listino_bevande values('acqua','la Torre',1.00);
insert into listino_bevande values('acqua frizzante','la Torre',1.00);

insert into listino_cibo values('spicy burgher','la Stazione',4.50);
insert into listino_cibo values('cheesburger','la Stazione',1.50);
insert into listino_cibo values('hamburger','la Stazione',1.40);
insert into listino_bevande values('acqua','la Stazione',1.20);
insert into listino_bevande values('acqua frizzante','la Stazione',1.20);
insert into listino_bevande values('coca cola','la Stazione',2.00);
insert into listino_bevande values('fanta','la Stazione',2.00);
insert into listino_bevande values('sprite','la Stazione',2.00);
