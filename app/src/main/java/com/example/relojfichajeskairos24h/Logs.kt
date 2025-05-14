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
 *
 *
 *
 */