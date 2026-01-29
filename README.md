# 💼 Sistema de Pago de Trabajadores - CDC S.A

Sistema de gestión empresarial para administrar el pago de trabajadores, calcular salarios basados en proyectos certificados, gestionar claves de control y generar informes detallados.

## 📋 Características Principales

- ✅ **Gestión completa de trabajadores** (CRUD)
- ✅ **Cálculo automático de pagos** basado en tarifas y proyectos
- ✅ **Control de claves** (269, 271, 278) que afectan el derecho a cobro
- ✅ **Gestión de proyectos** certificados y no certificados
- ✅ **Interfaz gráfica** (Swing) e **interfaz de consola**
- ✅ **Persistencia de datos** con archivos de texto
- ✅ **Registro de cambios** (log de auditoría)
- ✅ **Generación de resúmenes** exportables a texto
- ✅ **Agrupación por departamentos**
- ✅ **Validaciones** de datos y combinaciones tarifarias

## 🏗️ Arquitectura del Proyecto

## 🏗️ Estructura del Proyecto

| Directorio/Archivo | Tipo | Descripción |
|-------------------|------|-------------|
| **src/** | Directorio | Código fuente principal |
| ├── controller/ | Directorio | Controladores del sistema |
| │   └── SistemaPagoTrabajadoresCDC.java | Java | Controlador principal |
| ├── model/ | Directorio | Modelos de datos |
| │   ├── Trabajador.java | Java | Modelo de trabajador |
| │   └── Proyecto.java | Java | Modelo de proyecto |
| ├── utils/ | Directorio | Utilidades |
| │   ├── GestorPersistencia.java | Java | Gestión de almacenamiento |
| │   └── RegistroCambios.java | Java | Log de auditoría |
| └── viewer/ | Directorio | Interfaces de usuario |
|     ├── GUI.java | Java | Interfaz gráfica |
| **data/** | Directorio | Datos y logs |
| ├── trabajadores.dat | Datos | Base de datos serializada |
| └── historial_cambios.log | Log | Registro de cambios |
| **Resumen.txt** | Texto | Informes generados |
| **README.md** | Markdown | Documentación |

## 📊 Modelo de Datos

### Trabajador
class Trabajador {
    int codigo;
    String nombre;
    String depto;
    String cargo;
    String grupoEscala;      // II, IV, V, VI, VII, VIII, X, XI, XIV, XV, XVI, XVII, XVIII, XIX, XX, XXI, XXII
    String ocupBarco;        // "D" o "DA"
    boolean esBaja;
    boolean tieneClave271;
    boolean tieneClave278;
    int cantidadClaves269;   // 2 o más = sin derecho a cobro
    ArrayList<Proyecto> proyectos;
    double pagoTotal;
    String fechaUltimaModificacion;
}

### Proyecto
class Proyecto {
    int numeroProyecto;
    boolean esCertificado;   // Solo proyectos certificados pagan
    double horasTrab;
    double horasExtra;
}


## ⚙️ Reglas de Negocio

### Derecho a Cobro
Un trabajador **NO** tiene derecho a cobro si:
1. ✅ Todos sus proyectos están **no certificados**
2. ✅ Está dado de **baja**
3. ✅ Tiene **clave 271**
4. ✅ Tiene **clave 278**
5. ✅ Tiene **2 o más claves 269**

### Cálculo de Tarifas
Las tarifas se calculan según:
- **Grupo Escala** (II a XXII)
- **Ocupación Barco** ("D" o "DA")

Ejemplos:
- Grupo II + D = 35 CUP/hora
- Grupo IV + D = 37 CUP/hora
- Grupo II + DA = 33 CUP/hora
- Grupo XXII + DA = 60 CUP/hora

### Cálculo de Pago Total

PagoTotal = (Σ horas_trabajadas_proyectos_certificados × tarifa) 
            + (Σ horas_extra_proyectos_certificados × tarifa)

## 🖥️ Interfaces Disponibles

### 1. Interfaz Gráfica (GUI.java)
- **Menú principal** con navegación por pestañas
- **Gestión visual** de trabajadores y proyectos
- **Vistas organizadas** por departamento
- **Paneles de detalles** completos
- **Exportación** de resúmenes a archivo

### 2. Interfaz de Consola (Console version.zip)
- **Menú interactivo** por línea de comandos
- **Funcionalidades equivalentes** a la GUI
- **Ideal** para ejecución en servidores o sistemas sin GUI

## 🚀 Funcionalidades por Módulo

### Módulo de Gestión
- ✅ **Alta** de nuevos trabajadores
- ✅ **Modificación** de datos existentes
- ✅ **Baja** lógica (eliminación)
- ✅ **Agregar/eliminar/modificar** proyectos

### Módulo de Consultas
- ✅ **Listar trabajadores** agrupados por departamento
- ✅ **Filtrar** por derecho a cobro
- ✅ **Ver detalles completos** de cada trabajador
- ✅ **Mostrar estado de pago** con motivos

### Módulo de Reportes
- ✅ **Resumen general** del sistema
- ✅ **Cálculo de montos** por ocupación
- ✅ **Total de horas** trabajadas certificadas
- ✅ **Exportación** a archivo de texto
- ✅ **Historial de cambios** (auditoría)

## 💾 Persistencia de Datos

### Archivos Generados:
1. **trabajadores.dat** - Datos serializados de trabajadores
2. **historial_cambios.log** - Registro de operaciones (ALTA, BAJA, MODIFICACIÓN)
3. **Resumen.txt** - Reportes exportados

### Formato de Serialización:
codigo|nombre|depto|cargo|grupoEscala|ocupBarco|esBaja|clave269|clave271|clave278|fechaModificacion|[proyectos]


## 🛠️ Tecnologías Utilizadas

- **Java 8+** - Lenguaje principal
- **Swing** - Interfaz gráfica
- **Java Collections** - Estructuras de datos
- **Java I/O** - Manejo de archivos
- **MVC** - Patrón arquitectónico

## 📦 Compilación y Ejecución

### Compilación:
javac -d bin src/controller/*.java src/model/*.java src/utils/*.java src/viewer/*.java

### Ejecución GUI:
java -cp bin sistemapagotrabajadorescdc.viewer.GUI


## 🧪 Validaciones Implementadas

1. **Código único** por trabajador
2. **Combinación válida** Grupo Escala + Ocupación Barco
3. **Valores numéricos** en campos requeridos
4. **Ocupación Barco** solo "D" o "DA"
5. **Horas no negativas** en proyectos
6. **Confirmación** antes de eliminaciones

## 📈 Métricas Calculadas

### Por Departamento:
- Total trabajadores con/sin derecho a cobro
- Monto total por pagar
- Horas trabajadas certificadas

### Por Ocupación:
- Monto total personal "D"
- Monto total personal "DA"
- Distribución porcentual

## 🔧 Mantenimiento

### Limpieza de Datos:
- **Eliminar base de datos** completa
- **Limpiar historial** de cambios
- **Reiniciar aplicación** con datos limpios

### Backup y Restauración:
- Los datos se guardan automáticamente al cerrar
- Se cargan automáticamente al iniciar
- Formato de texto plano para fácil recuperación

## 📋 Estado del Proyecto

### ✅ Completado:
- [x] Modelo de datos completo
- [x] Lógica de negocio
- [x] Persistencia con archivos
- [x] Interfaz gráfica completa
- [x] Sistema de log/auditoría
- [x] Generación de reportes

### 🔄 Pendiente:
- [ ] Sistema de backup automático
- [ ] Importación desde Excel/CSV
- [ ] Reportes estadísticos avanzados
- [ ] Sistema de usuarios y permisos

## 📄 Licencia

Este proyecto está desarrollado para fines educativos y de gestión empresarial. Libre uso y modificación con atribución.

---

**Versión:** 1.0.0  
**Última actualización:** 2024  
**Autores:** Equipo de Desarrollo CDC S.A

## 🔗 Archivos Incluidos en el Repositorio

Los archivos Java principales del sistema son:

1. **SistemaPagoTrabajadoresCDC.java** - Controlador principal del sistema
2. **Trabajador.java** - Modelo de datos del trabajador
3. **Proyecto.java** - Modelo de datos del proyecto
4. **RegistroCambios.java** - Utilidad para logging y auditoría
5. **GestorPersistencia.java`** - Gestión de almacenamiento en archivos
6. **GUI.java** - Interfaz gráfica de usuario (completa)

## 🎯 Uso Empresarial

Este sistema está diseñado específicamente para:
- Empresas con **pago por proyectos**
- Control de **asistencias y bajas**
- Gestión de **bonificaciones y penalizaciones** (claves)
- **Auditoría completa** de movimientos
- **Cálculo automatizado** de nóminas

## ⚠️ Notas Importantes

- Las **tarifas** son configurables en el código (método calcularTarifa)
- Las **reglas de negocio** están codificadas en tieneDerechoACobro()
- Los **archivos de datos** se crean en el directorio de ejecución
- El **sistema es portable** y no requiere base de datos externa
