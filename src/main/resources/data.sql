INSERT INTO users (user_id, username, password, email, role)
VALUES (1, 'testuser', 'pw', 'test@example.com', 'USER');

INSERT INTO profile (
    profile_id, user_id, nickname, birth_date, gender, region,
    face_features, face_preference, absolute_score,
    description_key, description_text, target_gender
) VALUES (
    1, 1, '테스트유저', '1995-01-01', TRUE, '서울',
    '0000000000', '0000000000', 50,
    NULL, NULL, NULL
);
