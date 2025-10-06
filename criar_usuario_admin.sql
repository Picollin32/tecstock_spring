-- Script para criar o usuário ADMIN no banco de dados
-- Nível de Acesso: 0 (Admin - acesso completo e irrestrito)
-- O Admin NÃO precisa estar vinculado a um funcionário/consultor

-- Inserir o Usuário Admin independente (SEM vínculo com funcionário)
INSERT INTO usuario (nome_usuario, senha, nome_completo, nivel_acesso, consultor_id)
VALUES (
    'admin',                                                                    -- nome_usuario
    '$2a$10$vwqj75VWFeOz76fH7MZOl.DcXBwX0RhhKYv5QmM3NI.ZAru09Q4/G',          
    'Administrador do Sistema',                                                 -- nome_completo
    0,                                                                          -- nivel_acesso (0 = Admin)
    NULL                                                                        -- consultor_id (NULL = não vinculado a funcionário)
);

