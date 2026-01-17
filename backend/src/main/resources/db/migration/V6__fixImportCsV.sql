-- V6__fixImportCsv.sql

-- 0) Nettoyage : supprime les lignes orphelines
DELETE FROM tableauventes
WHERE user_id IS NULL;

-- 1) FK : si elle existe déjà, on la drop (migration partielle / déjà faite)
ALTER TABLE tableauventes
  DROP CONSTRAINT IF EXISTS fk_tableauventes_user;

-- 2) user_id obligatoire
ALTER TABLE tableauventes
  ALTER COLUMN user_id SET NOT NULL;

-- 3) FK propre
ALTER TABLE tableauventes
  ADD CONSTRAINT fk_tableauventes_user
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON DELETE CASCADE;
