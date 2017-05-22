--1
SELECT nome, citta, indirizzo
FROM stabilimento;
SELECT c.nome_cibo, c.prezzo
FROM listino_cibo AS c, stabilimento AS s 
WHERE c.nome_stabilimento=s.nome AND c.nome_stabilimento=?;
SELECT b.nome_bevanda, b.prezzo
FROM listino_bevande AS b, stabilimento AS s 
WHERE b.nome_stabilimento=s.nome AND b.nome_stabilimento=?;
--2
select mc.alimento  
from menu_completo as mc  
where nome = ?;
--

