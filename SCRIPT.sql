CREATE DATABASE DataFans;



CREATE SCHEMA Usuario;
CREATE SCHEMA Adquisicion;
CREATE SCHEMA Administrativo;

CREATE TABLE Usuario.Fan
(
    ID_fan          BIGSERIAL       NOT NULL,
    NombreFan       VARCHAR(100)    NOT NULL,
    Telefono        VARCHAR(13)     NOT NULL,
    Email           VARCHAR(200)    NOT NULL,
    Fecha_registro  DATE            NOT NULL,
    CONSTRAINT PK_Fan PRIMARY KEY (ID_fan)
);

CREATE TABLE Usuario.Artista
(
    ID_artista      BIGSERIAL       NOT NULL,
    Nombre_artista  VARCHAR(100)    NOT NULL,
    Pais            VARCHAR(50)     NOT NULL,
    Fecha_registro  DATE            NOT NULL,
    CONSTRAINT PK_Artista PRIMARY KEY (ID_artista)
);

CREATE TABLE Adquisicion.Suscripcion
(
    ID_suscripcion  BIGSERIAL   NOT NULL,
    ID_fan          BIGINT      NOT NULL,
    ID_artista      BIGINT      NOT NULL,
    CONSTRAINT PK_Suscripcion       PRIMARY KEY (ID_suscripcion),
    CONSTRAINT FK_Suscripcion_Fan   FOREIGN KEY (ID_fan)
        REFERENCES Usuario.Fan (ID_fan),
    CONSTRAINT FK_Suscripcion_Art   FOREIGN KEY (ID_artista)
        REFERENCES Usuario.Artista (ID_artista)
);

CREATE TABLE Adquisicion.Membresia
(
    ID_membresia    BIGSERIAL       NOT NULL,
    ID_suscripcion  BIGINT          NOT NULL,
    NombreMembresia VARCHAR(100)    NOT NULL,
    Precio          NUMERIC(12,2)   NOT NULL,
    Duracion        VARCHAR(30)     NOT NULL
        CONSTRAINT CHK_Duracion_Cadena CHECK (Duracion IN ('Mensual', 'Anual')),
    Fecha_inicio    DATE            NULL,
    Fecha_fin       DATE            NULL,
    Estado          VARCHAR(100)    NOT NULL DEFAULT 'Pago Pendiente',
    CONSTRAINT PK_Membresia             PRIMARY KEY (ID_membresia),
    CONSTRAINT FK_Membresia_Suscripcion FOREIGN KEY (ID_suscripcion)
        REFERENCES Adquisicion.Suscripcion (ID_suscripcion)
);

CREATE TABLE Adquisicion.Beneficio
(
    ID_beneficio    BIGSERIAL       NOT NULL,
    NombreBeneficio VARCHAR(100)    NOT NULL,
    TipoBeneficio   VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_Beneficio PRIMARY KEY (ID_beneficio)
);

CREATE TABLE Adquisicion.MembresiaBeneficio
(
    ID_membresia    BIGINT  NOT NULL,
    ID_beneficio    BIGINT  NOT NULL,
    CONSTRAINT FK_MB_Membresia  FOREIGN KEY (ID_membresia)
        REFERENCES Adquisicion.Membresia (ID_membresia),
    CONSTRAINT FK_MB_Beneficio  FOREIGN KEY (ID_beneficio)
        REFERENCES Adquisicion.Beneficio (ID_beneficio),
    CONSTRAINT UQ_MembresiaBeneficio UNIQUE (ID_membresia, ID_beneficio)
);

CREATE TABLE Adquisicion.Contenido
(
    ID_contenido    BIGSERIAL       NOT NULL,
    ID_artista      BIGINT          NOT NULL,
    Titulo          VARCHAR(200)    NOT NULL,
    TipoContenido   VARCHAR(100)    NOT NULL,
    Exclusivo       VARCHAR(100)    NOT NULL,
    CONSTRAINT PK_Contenido         PRIMARY KEY (ID_contenido),
    CONSTRAINT FK_Contenido_Artista FOREIGN KEY (ID_artista)
        REFERENCES Usuario.Artista (ID_artista)
);

CREATE TABLE Adquisicion.MembresiaContenido
(
    ID_membresia    BIGINT  NOT NULL,
    ID_contenido    BIGINT  NOT NULL,
    CONSTRAINT FK_MC_Membresia  FOREIGN KEY (ID_membresia)
        REFERENCES Adquisicion.Membresia (ID_membresia),
    CONSTRAINT FK_MC_Contenido  FOREIGN KEY (ID_contenido)
        REFERENCES Adquisicion.Contenido (ID_contenido)
);

CREATE TABLE Adquisicion.Producto
(
    ID_producto     BIGSERIAL       NOT NULL,
    ID_artista      BIGINT          NOT NULL,
    Nombre_producto VARCHAR(200)    NOT NULL,
    Tipo_producto   VARCHAR(100)    NOT NULL,
    Precio          NUMERIC(12,2)   NOT NULL,
    Stock           INT             NOT NULL
        CONSTRAINT CHK_Stock_Numerico CHECK (Stock >= 0),
    CONSTRAINT PK_Producto          PRIMARY KEY (ID_producto),
    CONSTRAINT FK_Producto_Artista  FOREIGN KEY (ID_artista)
        REFERENCES Usuario.Artista (ID_artista)
);

CREATE TABLE Administrativo.Pago
(
    ID_pago         BIGSERIAL       NOT NULL,
    ID_membresia    BIGINT          NOT NULL,
    Fecha_pago      DATE            NOT NULL,
    NumTarjeta      BIGINT          NOT NULL,
    Banco           VARCHAR(100)    NOT NULL,
    Cvv             INT             NOT NULL,
    CONSTRAINT PK_Pago              PRIMARY KEY (ID_pago),
    CONSTRAINT FK_Pago_Membresia    FOREIGN KEY (ID_membresia)
        REFERENCES Adquisicion.Membresia (ID_membresia)
);

CREATE TABLE Administrativo.Compra
(
    ID_compra       BIGSERIAL       NOT NULL,
    ID_fan          BIGINT          NOT NULL,
    Fecha_compra    DATE            NOT NULL,
    Total_compra    NUMERIC(12,2)   NOT NULL DEFAULT 0,
    CONSTRAINT PK_Compra        PRIMARY KEY (ID_compra),
    CONSTRAINT FK_Compra_Fan    FOREIGN KEY (ID_fan)
        REFERENCES Usuario.Fan (ID_fan)
);

CREATE TABLE Administrativo.DetalleCompra
(
    ID_producto BIGINT          NOT NULL,
    ID_compra   BIGINT          NOT NULL,
    Cantidad    INT             NOT NULL,
    Subtotal    NUMERIC(12,2)   NOT NULL DEFAULT 0,
    CONSTRAINT FK_Detalle_Producto  FOREIGN KEY (ID_producto)
        REFERENCES Adquisicion.Producto (ID_producto),
    CONSTRAINT FK_Detalle_Compra    FOREIGN KEY (ID_compra)
        REFERENCES Administrativo.Compra (ID_compra)
);

-------------Triggers---------
-#1
CREATE OR REPLACE FUNCTION Administrativo.fn_CalcularFechaInicio()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Adquisicion.Membresia
    SET Fecha_inicio = NEW.Fecha_pago
    WHERE ID_membresia = NEW.ID_membresia;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-#2

CREATE TRIGGER trg_CalcularFechaInicio
AFTER INSERT ON Administrativo.Pago
FOR EACH ROW
EXECUTE FUNCTION Administrativo.fn_CalcularFechaInicio();

CREATE OR REPLACE FUNCTION Adquisicion.fn_CalcularFechaFin_y_Estado()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.Fecha_inicio IS DISTINCT FROM OLD.Fecha_inicio THEN
        NEW.Fecha_fin :=
            CASE
                WHEN NEW.Duracion = 'Mensual' THEN NEW.Fecha_inicio + INTERVAL '1 month'
                WHEN NEW.Duracion = 'Anual'   THEN NEW.Fecha_inicio + INTERVAL '1 year'
                ELSE NEW.Fecha_inicio
            END;
        NEW.Estado :=
            CASE
                WHEN NEW.Fecha_inicio IS NULL        THEN 'Pago Pendiente'
                WHEN NEW.Fecha_fin >= CURRENT_DATE   THEN 'Activa'
                ELSE 'Vencida'
            END;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-#3

CREATE TRIGGER trg_CalcularFechaFin_y_Estado
BEFORE UPDATE ON Adquisicion.Membresia
FOR EACH ROW
EXECUTE FUNCTION Adquisicion.fn_CalcularFechaFin_y_Estado();

CREATE OR REPLACE FUNCTION Administrativo.fn_VerificarStock()
RETURNS TRIGGER AS $$
DECLARE
    Stock_disponible INT;
BEGIN
    SELECT Stock INTO Stock_disponible
    FROM Adquisicion.Producto
    WHERE ID_producto = NEW.ID_producto;

    IF Stock_disponible < NEW.Cantidad THEN
        RAISE EXCEPTION 'Stock insuficiente para el producto %. Stock disponible: %, solicitado: %',
            NEW.ID_producto, Stock_disponible, NEW.Cantidad;
    END IF;

    UPDATE Adquisicion.Producto
    SET Stock = Stock - NEW.Cantidad
    WHERE ID_producto = NEW.ID_producto;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-#4

CREATE TRIGGER trg_VerificarStock
BEFORE INSERT ON Administrativo.DetalleCompra
FOR EACH ROW
EXECUTE FUNCTION Administrativo.fn_VerificarStock();

CREATE OR REPLACE FUNCTION Administrativo.fn_CalcularSubtotal()
RETURNS TRIGGER AS $$
DECLARE
    Precio_producto NUMERIC(12,2);
BEGIN
    SELECT Precio INTO Precio_producto
    FROM Adquisicion.Producto
    WHERE ID_producto = NEW.ID_producto;

    NEW.Subtotal := NEW.Cantidad * Precio_producto;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-#5

CREATE TRIGGER trg_CalcularSubtotal
BEFORE INSERT ON Administrativo.DetalleCompra
FOR EACH ROW
EXECUTE FUNCTION Administrativo.fn_CalcularSubtotal();

CREATE OR REPLACE FUNCTION Administrativo.fn_CalcularTotalCompra()
RETURNS TRIGGER AS $$
DECLARE
    v_ID_compra BIGINT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_ID_compra := OLD.ID_compra;
    ELSE
        v_ID_compra := NEW.ID_compra;
    END IF;

    UPDATE Administrativo.Compra
    SET Total_compra = (
        SELECT COALESCE(SUM(Subtotal), 0)
        FROM Administrativo.DetalleCompra
        WHERE ID_compra = v_ID_compra
    )
    WHERE ID_compra = v_ID_compra;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-#6
CREATE TRIGGER trg_CalcularTotalCompra
AFTER INSERT OR UPDATE OR DELETE ON Administrativo.DetalleCompra
FOR EACH ROW
EXECUTE FUNCTION Administrativo.fn_CalcularTotalCompra();

------------------------Usuarios --------------
CREATE USER Admin_datafans WITH PASSWORD 'Admin2026!';
GRANT ALL PRIVILEGES ON DATABASE datafans TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA Usuario        TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA Adquisicion    TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA Administrativo TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA Usuario        TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA Adquisicion    TO admin_datafans;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA Administrativo TO admin_datafans;

CREATE USER Editor_datafans WITH PASSWORD 'Editor2026!';
GRANT CONNECT ON DATABASE datafans TO editor_datafans;
GRANT USAGE ON SCHEMA Usuario, Adquisicion, Administrativo TO editor_datafans;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA Usuario        TO editor_datafans;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA Adquisicion    TO editor_datafans;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA Administrativo TO editor_datafans;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA Usuario        TO editor_datafans;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA Adquisicion    TO editor_datafans;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA Administrativo TO editor_datafans;

CREATE USER Lector_datafans WITH PASSWORD 'Lector2026!';
GRANT CONNECT ON DATABASE datafans TO lector_datafans;
GRANT USAGE ON SCHEMA Usuario, Adquisicion, Administrativo TO lector_datafans;
GRANT SELECT ON ALL TABLES IN SCHEMA Usuario        TO lector_datafans;
GRANT SELECT ON ALL TABLES IN SCHEMA Adquisicion    TO lector_datafans;
GRANT SELECT ON ALL TABLES IN SCHEMA Administrativo TO lector_datafans;
REVOKE INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA Usuario        FROM lector_datafans;
REVOKE INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA Adquisicion    FROM lector_datafans;
REVOKE INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA Administrativo FROM lector_datafans;

INSERT INTO Usuario.Fan (NombreFan, Telefono, Email, Fecha_registro) VALUES
    ('Ana García López',     '4441234567', 'ana.garcia@email.com',  CURRENT_DATE),
    ('Carlos Martínez Ruiz', '4449876543', 'carlos.mr@email.com',   CURRENT_DATE),
    ('Sofía Pérez Torres',   '4447654321', 'sofia.perez@email.com', CURRENT_DATE);

INSERT INTO Usuario.Artista (Nombre_artista, Pais, Fecha_registro) VALUES
    ('Bad Bunny',    'Puerto Rico',    CURRENT_DATE),
    ('Taylor Swift', 'Estados Unidos', CURRENT_DATE),
    ('Peso Pluma',   'México',         CURRENT_DATE);

INSERT INTO Adquisicion.Suscripcion (ID_fan, ID_artista) VALUES
    (1, 1), (1, 3), (2, 2), (3, 1);

INSERT INTO Adquisicion.Beneficio (NombreBeneficio, TipoBeneficio) VALUES
    ('Descuento en merchandising',            'Descuento'),
    ('Acceso anticipado a entradas',          'Acceso'),
    ('Contenido exclusivo detrás de cámaras', 'Contenido'),
    ('Chat privado con el artista',           'Interacción');

INSERT INTO Adquisicion.Membresia (ID_suscripcion, NombreMembresia, Precio, Duracion, Fecha_inicio, Fecha_fin, Estado) VALUES
    (1, 'Fan Básico',   150.00, 'Mensual', NULL, NULL, 'Pago Pendiente'),
    (2, 'Fan Premium',  500.00, 'Anual',   NULL, NULL, 'Pago Pendiente'),
    (3, 'Seguidor VIP', 300.00, 'Mensual', NULL, NULL, 'Pago Pendiente');

INSERT INTO Adquisicion.Contenido (ID_artista, Titulo, TipoContenido, Exclusivo) VALUES
    (1, 'Detrás de cámaras - Tour 2025', 'Video',  'Sí'),
    (1, 'Sesión de fotos exclusiva',     'Imagen', 'Sí'),
    (2, 'Ensayo previo al concierto',    'Video',  'Sí'),
    (3, 'Mini documental',               'Video',  'No');

INSERT INTO Adquisicion.Producto (ID_artista, Nombre_producto, Tipo_producto, Precio, Stock) VALUES
    (1, 'Camiseta Tour 2025', 'Ropa',          450.00, 100),
    (1, 'Poster Firmado',     'Coleccionable', 250.00,  50),
    (2, 'Gorra Oficial',      'Ropa',          350.00,  75),
    (3, 'Álbum Autografiado', 'Música',        200.00,  60);

INSERT INTO Administrativo.Pago (ID_membresia, Fecha_pago, NumTarjeta, Banco, Cvv) VALUES
    (1, CURRENT_DATE, 4111111111111111, 'BBVA',      123),
    (2, CURRENT_DATE, 5500005555555559, 'Banamex',   456),
    (3, CURRENT_DATE, 4012888888881881, 'Santander', 789);

INSERT INTO Administrativo.Compra (ID_fan, Fecha_compra, Total_compra) VALUES
    (1, CURRENT_DATE, 0),
    (2, CURRENT_DATE, 0);

INSERT INTO Administrativo.DetalleCompra (ID_producto, ID_compra, Cantidad, Subtotal) VALUES
    (1, 1, 2, 0),
    (2, 1, 1, 0),
    (3, 2, 1, 0);

INSERT INTO Adquisicion.MembresiaBeneficio (ID_membresia, ID_beneficio) VALUES
    (1, 1), (1, 2), (2, 1), (2, 2), (2, 3), (3, 4);

INSERT INTO Adquisicion.MembresiaContenido (ID_membresia, ID_contenido) VALUES
    (1, 1), (2, 1), (2, 2), (3, 3);

SELECT * FROM Usuario.Fan;
SELECT * FROM Usuario.Artista;
SELECT * FROM Adquisicion.Suscripcion;
SELECT * FROM Adquisicion.Membresia;
SELECT * FROM Adquisicion.Beneficio;
SELECT * FROM Adquisicion.MembresiaBeneficio;
SELECT * FROM Adquisicion.Contenido;
SELECT * FROM Adquisicion.MembresiaContenido;
SELECT * FROM Adquisicion.Producto;
SELECT * FROM Administrativo.Pago;
SELECT * FROM Administrativo.Compra;
SELECT * FROM Administrativo.DetalleCompra;