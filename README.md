# Sistema de Gestión - Taller Mecánico

Este proyecto es una aplicación de escritorio diseñada para la administración integral de talleres automotrices, permitiendo el control de clientes, vehículos, facturación y generación de reportes profesionales.

## 🚀 Características

* **Gestión de Clientes:** Registro completo de propietarios con validación de datos personales [cite: ClienteController.java, Cliente.java].
* **Control de Vehículos:** Soporte para múltiples tipos de vehículos mediante herencia (Motos, Carros, Diesel) con campos específicos por categoría [cite: Vehiculo.java, Moto.java, Carro.java, Diesel.java].
* **Facturación Transaccional:** Módulo para la creación de facturas que integra mano de obra y repuestos en una sola operación atómica [cite: FacturacionController.java, FacturaRepository.java].
* **Reportes PDF:** Generación automática de facturas en formato PDF con diseño profesional utilizando iText [cite: PdfExportService.java].
* **Seguridad:** Acceso protegido a la configuración del sistema mediante contraseña de administrador [cite: MainController.java, ConfigController.java].
* **Interfaz Moderna:** Tema oscuro (Dark Mode) optimizado para la experiencia del usuario [cite: dark-theme.css, App.java].

## 🛠️ Tecnologías Utilizadas

* **Java 25:** Lenguaje de programación base [cite: pom.xml].
* **JavaFX:** Framework para el desarrollo de la interfaz gráfica [cite: pom.xml].
* **Maven:** Gestor de dependencias y construcción del proyecto [cite: pom.xml].
* **H2 Database:** Motor de base de datos embebido para persistencia de datos local [cite: pom.xml, DatabaseConnection.java].
* **iText 7:** Librería para la exportación de documentos a PDF [cite: pom.xml, PdfExportService.java].

## 📂 Estructura del Proyecto

El código está organizado siguiendo el patrón **MVC (Modelo-Vista-Controlador)**:

* `com.taller.models`: Definición de las entidades del negocio (Records y Clases) [cite: uml_diagram.mmd].
* `com.taller.controllers`: Lógica de control para la interacción con las vistas FXML [cite: uml_diagram.mmd].
* `com.taller.repository`: Capa de persistencia y comunicación con la base de datos SQL [cite: uml_diagram.mmd].
* `com.taller.services`: Servicios especializados como la generación de PDF [cite: PdfExportService.java].
* `resources`: Vistas FXML, estilos CSS y activos gráficos [cite: main.fxml, dark-theme.css].

## ⌨️ Atajos de Teclado

El sistema incluye aceleradores para agilizar la navegación [cite: MainController.java]:
* `Ctrl + 1`: Gestión de Clientes.
* `Ctrl + 2`: Gestión de Vehículos.
* `Ctrl + 3`: Nueva Factura.
* `Ctrl + 4`: Historial de Facturas.
* `Ctrl + H`: Panel de Inicio (Dashboard).

---
*Desarrollado por Brandon Capacho*
