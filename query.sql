--1
SELECT * 
FROM bevande AS b, stabilimento AS s 
WHERE b.nome_stabilimento=s.nome_stabilimento AND b.nome_stabilimento=?;

SELECT * 
FROM cibo AS c, stabilimento AS s 
WHERE c.nome_stabilimento=s.nome_stabilimento AND c.nome_stabilimento=?;

--2
