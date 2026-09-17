INSERT INTO users (
    username,
    email,
    password,
    manager_id
)
VALUES
(
    'admin01',
    'admin01@example.com',
    '$2a$15$Oqa/fbmfppLjQ7Lt3YwnluMHIlAlZajt6poWl2lrfF3DqHnl7DJSu',
    NULL
),
(
    'manager01',
    'manager01@example.com',
    '$2a$15$jdoLDn.KeyNBi2UT2IgRHezLoV9C87XdO1Phmj.qaqsiVmFcoa.ae',
    NULL
),
(
    'manager02',
    'manager02@example.com',
    '$2a$15$fZT/CziF5Uh4R.kKnC7uz.lvGJaTnOUy8mOQg7OihJCPENym4JXXG',
    NULL
),
(
    'user01',
    'user01@example.com',
    '$2a$15$6OolljjsXUI7E6gILJYyouArql3BXjOxkpmbnNJb8CGdCD2Sk8t7q',
    NULL
),
(
    'user02',
    'user02@example.com',
    '$2a$15$rcnnZlrnAQ5FNqgJvExXueJ1.WdTZiUt1s3oQ9OzbxVBS2jfdoGwy',
    NULL
),
(
    'user03',
    'user03@example.com',
    '$2a$15$22M0ZxySSdNkv9qjAJMgN.vfb24CsPXq4dM3.x968jo5EIi6yoPl2',
    NULL
);

UPDATE users
SET manager_id = (
    SELECT id
    FROM users
    WHERE username = 'admin01'
)
WHERE username IN (
    'manager01',
    'manager02'
);

UPDATE users
SET manager_id = (
    SELECT id
    FROM users
    WHERE username = 'manager01'
)
WHERE username IN (
    'user01',
    'user02'
);

UPDATE users
SET manager_id = (
    SELECT id
    FROM users
    WHERE username = 'manager02'
)
WHERE username = 'user03';