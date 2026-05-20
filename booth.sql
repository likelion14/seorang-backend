SET SQL_SAFE_UPDATES = 0;

START TRANSACTION;

UPDATE booth
SET name = CASE name
    WHEN '미산융' THEN '미래산업융합대학'
    WHEN '융전' THEN '미래산업융합자유전공'
    WHEN '경영' THEN '경영학과'
    WHEN '패산' THEN '패션산업학과'
    WHEN '디미' THEN '디지털미디어학과'
    WHEN '정보' THEN '지능정보보학부'
    WHEN '소융' THEN '소프트웨어학과'
    WHEN '데사' THEN '데이터사이언스학과'
    WHEN '산디' THEN '산업디자인학과'

    WHEN '아디' THEN '아트앤디자인스쿨'
    WHEN '현미' THEN '현대미술전공'
    WHEN '공예' THEN '공예전공'
    WHEN '시디' THEN '시각디자인전공'
    WHEN '첨디' THEN '첨단미디어디자인전공'

    WHEN '과기융' THEN '과학기술융합대학'
    WHEN '과전' THEN '과학기술융합자유전공'
    WHEN '수학' THEN '수학과'
    WHEN '화학' THEN '화학과'
    WHEN '생공' THEN '생명환경공학과'
    WHEN '바헬' THEN '바이오헬스융합학과'
    WHEN '원조' THEN '원예생명조경학과'
    WHEN '식공' THEN '식품생명공학과'
    WHEN '식영' THEN '식품영양학과'

    WHEN '사대' THEN '사회과학대학'
    WHEN '사전' THEN '사회과학자유전공'
    WHEN '경제' THEN '경제학과'
    WHEN '문정' THEN '문헌정보학과'
    WHEN '사복' THEN '사회복지학과'
    WHEN '아동' THEN '아동학과'
    WHEN '행정' THEN '행정학과'
    WHEN '언영' THEN '언론영상학부'
    WHEN '심과' THEN '심리인지과학학부'
    WHEN '스과' THEN '스포츠운동과학과'

    WHEN '인대' THEN '인문대학'
    WHEN '인전' THEN '인문자유전공'
    WHEN '메타' THEN '메타버스융합콘텐츠전공'
    WHEN '프문' THEN '프랑스문화콘텐츠전공'
    WHEN '독문' THEN '독일문화콘텐츠전공'
    WHEN '국문' THEN '국어국문학과'
    WHEN '영문' THEN '영어영문학과'
    WHEN '중문' THEN '중어중문학과'
    WHEN '일문' THEN '일어일문학과'
    WHEN '사학' THEN '사학과'
    WHEN '기독' THEN '기독교학과'

    WHEN '자유전공' THEN '자유전공학부'
    ELSE name
END
WHERE name IN (
    '미산융', '융전', '경영', '패산', '디미', '정보', '소융', '데사', '산디',
    '아디', '현미', '공예', '시디', '첨디',
    '과기융', '과전', '수학', '화학', '생공', '바헬', '원조', '식공', '식영',
    '사대', '사전', '경제', '문정', '사복', '아동', '행정', '언영', '심과', '스과',
    '인대', '인전', '메타', '프문', '독문', '국문', '영문', '중문', '일문', '사학', '기독',
    '자유전공'
);

SELECT id, name, day_1_open, day_2_open, day_3_open
FROM booth
ORDER BY id;

COMMIT;

SET SQL_SAFE_UPDATES = 1;