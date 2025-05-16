/**
 * *******************************************
 * *********** logica_BB_DD.kt ***************
 * *******************************************
 *
 * // Log para verificar que se va a insertar un nuevo fichaje en la base de datos local
 * Log.d("SQLite", "Insertando en tabla l_informados: L_INFORMADO=$lInformado, xFichaje=$xFichaje")
 *
 * // Log que indica que se ha añadido la nueva columna 'cEmpCppExt' a la tabla l_informados
 * Log.d("modificacionBBDD", "Columna cEmpCppExt añadida a l_informados")
 *
 * // Log que indica que la tabla l_informados no existía y ha sido creada en onUpgrade
 * Log.d("modificacionBBDD", "Tabla l_informados no existía, creada desde onUpgrade")
 *
 * // Log que indica que se ha iniciado correctamente la lógica de reintento automático
 * Log.d("FichajeApp", "Lógica de reintento automático iniciada correctamente.")
 *
 * // Log que informa que se va a intentar reenviar un fichaje no informado
 * Log.d("ReintentoFichaje", "Preparando reenvío de fichaje con ID=$id")
 *
 * // Log que muestra la URL generada para reenviar un fichaje pendiente
 * Log.d("ReintentoFichaje", "Invocando URL: $url")
 *
 * // Log que muestra la respuesta del servidor tras intentar reenviar un fichaje
 * Log.d("ReintentoFichaje", "Respuesta recibida: $body")
 *
 * // Log que informa que un fichaje ha sido actualizado como informado (L_INFORMADO = "S")
 * Log.d("ReintentoFichaje", "L_INFORMADO = S → Actualizando ID=$id a informado")
 *
 * // Log que informa que el archivo CSV fue generado correctamente con la ruta del archivo
 * Log.d("EXPORTACION", "Archivo generado en: ${archivo.absolutePath}")
 *
 * // Log que informa si hubo un error exportando la tabla
 * Log.e("EXPORTACION", "Error al exportar la tabla $tabla: ${e.message}")
 *
 *
 * *******************************************
 * *********** MainActivity.kt ***************
 * *******************************************
 *
 * // Log que indica que la lógica de reintento automático ha sido activada al iniciar la app
 * Log.d("MainActivity", "Lógica de reintento automático iniciada correctamente.")
 *
 * // Log que muestra la URL que se ha generado para realizar el fichaje (entrada o salida)
 * Log.d("FichajeApp", "URL generada para fichaje: $url")
 *
 * // Log que indica que se está invocando la URL del servidor para registrar el fichaje
 * Log.d("FichajeApp", "Invocando URL al servidor: $url")
 *
 * // Log que imprime la respuesta completa del servidor tras intentar fichar
 * Log.d("FichajeApp", "Respuesta del servidor: $responseText")
 *
 * // Log que confirma que se ha insertado un registro en SQLite, con los datos devueltos por el servidor
 * Log.d("SQLite", "Registro insertado: xFichaje=${jsonResponse.optString("xFichaje")}, cTipFic=${jsonResponse.optString("cTipFic")}")
 *
 * // Log que se activa si no hay conexión a Internet y el fichaje no se puede enviar (se guardaría localmente)
 * Log.d("FichajeApp", "No hay conexión. Fichaje guardado localmente.")
 *
 * // Log que informa que no se ha encontrado el archivo de audio especificado para reproducir
 * Log.e("Audio", "No se encontró el archivo de audio: $nombreArchivo")
 *
 * // Log que informa que informa de la base de datos, los regitros que tiene (Tiene que ejecutarse en modo debbug)
 * Log.e("DB_DUMP")
 *
 *
 *
 *
 *
 *
 */