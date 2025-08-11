INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('레벨 5 달성', '레벨 5에 도달하기', 'LEVEL', 5, 500, 500, 1, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('레벨 10 달성', '레벨 10에 도달하기', 'LEVEL', 10, 500, 500, 2, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('레벨 15 달성', '레벨 15에 도달하기', 'LEVEL', 15, 500, 500, 3, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('레벨 20 달성', '레벨 20에 도달하기', 'LEVEL', 20, 500, 500, 4, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('첫 인증 도전!', '인증글 1회 작성하기', 'MISSION_PROOF', 1, 100, 50, 5, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('꾸준한 탐험가', '인증글 5회 작성하기', 'MISSION_PROOF', 5, 300, 150, 6, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('열정의 기록자', '인증글 10회 작성하기', 'MISSION_PROOF', 10, 500, 300, 7, false, NOW(), NOW(), NULL);

INSERT INTO quest (title, description, quest_condition_type, acquired_condition, reward_point, reward_exp, title_id,
                   deleted, created_at, updated_at, deleted_at)
VALUES ('탐험의 정점', '인증글 20회 작성하기', 'MISSION_PROOF', 20, 1000, 600, 8, false, NOW(), NOW(), NULL);
