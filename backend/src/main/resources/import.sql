-- Default Super Admin user
-- IMPORTANT: Update email and firebase_uid to match a real Firebase user in your project.
INSERT INTO user_account (
    id,
    firebase_uid,
    email,
    display_name,
    role,
    status,
    created_at,
    updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'FIREBASE_UID_SUPER_ADMIN',
    'superadmin@example.com',
    'Super Admin',
    'SUPER_ADMIN',
    'ACTIVE',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

