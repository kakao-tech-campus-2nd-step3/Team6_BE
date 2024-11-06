INSERT INTO `groups` (id, group_name, description, group_image_url, created_At, modified_at)
SELECT 1, 'COMMON', 'commonquestion.', 'group/default.png', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM `groups` WHERE id=1 AND group_name='COMMON');
