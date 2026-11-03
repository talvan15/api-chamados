INSERT INTO setores (nome, sigla) VALUES
('Coordenação de Tecnologia da Informação e Comunicação', 'COTIC'),
('Manutenção e Infraestrutura', 'MANUT');

INSERT INTO categorias (nome, descricao, setor_id) VALUES
('Computadores e periféricos', 'Computadores, teclados, mouses e impressoras', 1),
('Rede e internet', 'Wi-Fi, rede cabeada e acesso à internet', 1),
('Projetores e audiovisual', 'Projetores, telas e equipamentos de áudio e vídeo', 1),
('Instalações físicas', 'Iluminação, mobiliário e infraestrutura predial', 2);

INSERT INTO usuarios (nome,email,matricula,tipo,setor_id) VALUES
('Solicitante Demonstração','solicitante@ifrn.edu.br','DEMO-SOL-001','SOLICITANTE',NULL),
('Aluno Demonstração','aluno@ifrn.edu.br','DEMO-SOL-002','SOLICITANTE',NULL),
('Técnico COTIC','tecnico.cotic@ifrn.edu.br','DEMO-TEC-001','TECNICO',1),
('Técnico Manutenção','tecnico.manut@ifrn.edu.br','DEMO-TEC-002','TECNICO',2);
