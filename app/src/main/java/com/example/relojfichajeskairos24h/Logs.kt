/**
 * *******************************************
 * *********** logica_BB_DD.kt ***************
 * *******************************************
 * // Log que indica que se está preparando el reenvío de un fichaje con su ID
 * Log.d("ReintentoFichaje", "Preparando reenvío de fichaje con ID=$id")
 *
 * // Log que muestra la URL que será invocada para reenviar el fichaje
 * Log.d("ReintentoFichaje", "Invocando URL: $url")
 *
 * // Log que imprime el cuerpo de la respuesta recibida del servidor tras invocar la URL
 * Log.d("ReintentoFichaje", "Respuesta recibida: $body")
 *
 * // Log que confirma que el servidor ha marcado el fichaje como informado (L_INFORMADO distinto de "N") y por tanto se elimina de la base de datos
 * Log.d("ReintentoFichaje", "L_INFORMADO != N → Eliminando ID=$id de la tabla informado")
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
 *
 *
 *
 *
 *
 *
 *
 */