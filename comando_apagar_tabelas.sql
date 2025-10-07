DO $$ 
DECLARE
    r RECORD;
BEGIN
    -- Loop por todas as tabelas no schema público, exceto a tabela que você quer manter
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename != 'tipo_pagamento') LOOP
        EXECUTE 'DROP TABLE IF EXISTS public.' || r.tablename || ' CASCADE';
    END LOOP;
END $$;
