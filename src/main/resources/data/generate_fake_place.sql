DROP TEMPORARY TABLE IF EXISTS tmp_emds;
SET @r := 0;
CREATE TEMPORARY TABLE tmp_emds AS
SELECT emd_cd, (@r := @r + 1) AS rn
FROM eupmyeondong
ORDER BY emd_cd;

SET @TOTAL_EMDS := (SELECT COUNT(*) FROM tmp_emds);

    -- -- -- place 데이터와 address 데이터를 각각 10만 개 만든 후 연관관계 매핑하여 저장

SELECT COALESCE(MAX(CAST(RIGHT(title,7) AS UNSIGNED)), 0) + 1
INTO @START_SEQ
FROM place
WHERE title LIKE '[FAKE] %';

SET @n := @START_SEQ - 1;
INSERT INTO place (
    user_id, title, content, proof_method, status, view_count, created_at, updated_at
)
SELECT
    1,
    CONCAT('[FAKE] Place #', LPAD(@n := @n + 1, 7, '0')),
    CONCAT('테스트용 가짜 콘텐츠 seq=', @n),
    IF(@n % 2 = 0, 'PHOTO', 'QR'),
    'APPROVED',
    FLOOR(RAND(@n) * 1000),
    NOW(), NOW()
FROM information_schema.COLUMNS c1
         CROSS JOIN information_schema.COLUMNS c2
    LIMIT 100000;

INSERT INTO address (
    created_at, updated_at, deleted_at,
    address_type_cd, detail, emd_cd, sd_cd, sgg_cd, lat, lng, target_id
)
SELECT
    NOW(), NOW(), NULL,
    'PLACE',
    CONCAT('가짜 주소 ', LPAD(CAST(RIGHT(p.title, 7) AS UNSIGNED), 7, '0')),
    e.emd_cd,
    LEFT(e.emd_cd, 2),         -- 시/도 코드 (앞 2자리)
    LEFT(e.emd_cd, 5),         -- 시군구 코드 (앞 5자리)
    ROUND(33.000000 + (RAND(CAST(RIGHT(p.title,7) AS UNSIGNED)) * 5.600000), 6),
    ROUND(124.000000 + (RAND(CAST(RIGHT(p.title,7) AS UNSIGNED)) * 7.500000), 6),
    p.id
FROM place p
    JOIN tmp_emds e
ON e.rn = ((CAST(RIGHT(p.title,7) AS UNSIGNED) - 1) % @TOTAL_EMDS) + 1
WHERE p.title LIKE '[FAKE] %'
  AND CAST(RIGHT(p.title,7) AS UNSIGNED)
    BETWEEN @START_SEQ AND @START_SEQ + 100000 - 1;

UPDATE place p
    JOIN address a
ON a.target_id = p.id
    AND a.address_type_cd = 'PLACE'
    SET p.address_id = a.id
WHERE p.title LIKE '[FAKE] %'
  AND CAST(RIGHT(p.title,7) AS UNSIGNED)
    BETWEEN @START_SEQ AND @START_SEQ + 100000 - 1;