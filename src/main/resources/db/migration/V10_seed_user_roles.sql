INSERT INTO user_roles (user_id, role_id)
SELECT
    u.id,
    r.id
FROM users u
CROSS JOIN roles r
WHERE
    (u.username IN ('user01', 'user02', 'user03')
        AND r.name = 'USER')
 OR
    (u.username IN ('manager01', 'manager02')
        AND r.name IN ('USER', 'MANAGER'))
 OR
    (u.username = 'admin01'
        AND r.name IN ('USER', 'ADMIN'));