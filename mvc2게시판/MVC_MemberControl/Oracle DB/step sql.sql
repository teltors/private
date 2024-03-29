SELECT LEVEL,
		articleNO,
		parentNO,
		LPAD('', 4*(LEVLE-1)) || title title,
		content,
		writeDate,
		id
FROM t_board
START WITH parentNO=0
CONNECT BY PRIOR articleNO=parentNO
ORDER SIBLINGS BY articleNO DESC;