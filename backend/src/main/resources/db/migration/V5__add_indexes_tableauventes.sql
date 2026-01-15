-- V4__add_indexes_tableauventes.sql
-- Objectif : accélérer les requêtes les plus fréquentes (stats + "recent" + stock)
-- Aucun impact sur les données : uniquement perf.

-- 1) Stats par période (filtre user + date_vente)
-- Exemple de requête : WHERE user_id = ? AND date_vente BETWEEN ? AND ?
CREATE INDEX IF NOT EXISTS idx_tableauventes_user_datevente
  ON public.tableauventes (user_id, date_vente);

-- 2) Derniers items ajoutés (tri DESC + LIMIT)
-- Exemple : WHERE user_id = ? ORDER BY created_at DESC LIMIT 7
-- Le DESC est utile car tu tries toujours dans ce sens.
CREATE INDEX IF NOT EXISTS idx_tableauventes_user_createdat_desc
  ON public.tableauventes (user_id, created_at DESC);

-- 3) Items en stock (requête très fréquente)
-- Exemple : WHERE user_id = ? AND date_vente IS NULL
-- Index partiel : plus petit, plus rapide, car ne contient QUE les lignes "en stock".
CREATE INDEX IF NOT EXISTS idx_tableauventes_user_stock
  ON public.tableauventes (user_id)
  WHERE date_vente IS NULL;

-- 4) (Optionnel mais safe) Filtre stats/écrans basés sur date_achat
-- Exemple : WHERE user_id = ? ORDER BY date_achat DESC ou BETWEEN...
CREATE INDEX IF NOT EXISTS idx_tableauventes_user_dateachat
  ON public.tableauventes (user_id, date_achat);
