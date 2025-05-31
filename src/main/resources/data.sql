DELETE FROM usuario_roles;
DELETE FROM usuario;
DELETE FROM area_comum;
DELETE FROM condominium;

INSERT INTO condominium (id, nome, endereco) VALUES
(1, 'Condomínio Vila das Flores', 'Rua das Palmeiras, 100');

INSERT INTO area_comum (id, nome, regras_uso, condominium_id) VALUES
(1, 'Salão de Festas Principal', 'Reservar com 2 dias de antecedência. Limite de 50 pessoas. Som permitido até 22h.', 1),
(2, 'Churrasqueira Gourmet', 'Uso permitido das 10h às 22h. Limpar após o uso. Trazer próprio carvão.', 1),
(3, 'Piscina Adulto e Infantil', 'Horário de funcionamento: 9h às 18h. Obrigatório exame médico.', 1);

-- Morador (João) - ID 1
INSERT INTO usuario (id, nome, email, senha, tipo_usuario, telefone, cpf, unidade, bloco_responsavel, setor, condominio_id)
VALUES (1, 'João Morador Silva', 'joao@condo.com', '123', 'MORADOR', '11987654321', '111.222.333-44', 'Apt 101', NULL, NULL, 1);

-- Síndico (Maria) - ID 2 (também é moradora)
INSERT INTO usuario (id, nome, email, senha, tipo_usuario, telefone, cpf, unidade, bloco_responsavel, setor, condominio_id)
VALUES (2, 'Maria Síndica Oliveira', 'maria@condo.com', '123', 'SINDICO', '21912345678', '222.333.444-55', 'Apt 202', 'Todos', NULL, 1);

-- Administrador/Funcionário (Carlos) - ID 3
INSERT INTO usuario (id, nome, email, senha, tipo_usuario, telefone, cpf, unidade, bloco_responsavel, setor, condominio_id)
VALUES (3, 'Carlos Admin Souza', 'carlos@condo.com', '123', 'FUNCIONARIO', '31956781234', '333.444.555-66', NULL, NULL, 'Financeiro', 1);

-- Morador (Ana) - ID 4
INSERT INTO usuario (id, nome, email, senha, tipo_usuario, telefone, cpf, unidade, bloco_responsavel, setor, condominio_id)
VALUES (4, 'Ana Moradora Pereira', 'ana@condo.com', '123', 'MORADOR', '41988887777', '444.555.666-77', 'Casa 05', NULL, NULL, 1);

-- João (Morador)
INSERT INTO usuario_roles (usuario_id, role) VALUES (1, 'MORADOR');

-- Maria (Síndica e também Moradora)
INSERT INTO usuario_roles (usuario_id, role) VALUES (2, 'SINDICO');
INSERT INTO usuario_roles (usuario_id, role) VALUES (2, 'MORADOR');

-- Carlos (Funcionário)
INSERT INTO usuario_roles (usuario_id, role) VALUES (3, 'FUNCIONARIO');

-- Ana (Moradora)
INSERT INTO usuario_roles (usuario_id, role) VALUES (4, 'MORADOR');


-- O valor para RESTART WITH deve ser MAIOR que o maior ID inserido manualmente na respectiva tabela
ALTER TABLE condominium ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM condominium);
ALTER TABLE area_comum ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM area_comum);
ALTER TABLE usuario ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM usuario);
