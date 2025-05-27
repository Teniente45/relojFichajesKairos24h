package com.example.relojfichajeskairos24h

object BuildURL {
    const val HOST = "https://rincontragabuche.kairos24h.es/"
    const val ACTION = "index.php?r=citaRedWeb/crearFichajeExterno"
    const val PARAMS = "&xEntidad=1005" +
                        "&cKiosko=TABLET1" +
                        "&cEmpCppExt=" +
                        "&cTipFic=" +
                        "&cFicOri=PUEFIC" +
                        "&tGPSLat=" +
                        "&tGPSLon="

    const val SETFICHAJE = HOST + ACTION + PARAMS
}

/*
 * El objeto Imagenes centraliza toda la configuración visual de los logos que se muestran en la app.
 *
 * LOGO_CLIENTE y LOGO_DESARROLLADORA: indican los nombres de las imágenes que deben estar en res/drawable.
 * PropiedadesImagen: define cómo se deben mostrar esas imágenes (ancho, alto, gravedad).
 * Vertical y Horizontal: especifican cómo deben comportarse visualmente según la orientación de la pantalla.
 *
 * Esto permite cambiar el comportamiento visual de los logos sin modificar el código de MainActivity.kt.
 *
 * Ejemplo:
 *   - Para cambiar la imagen del cliente: cambiar LOGO_CLIENTE = "nuevo_logo"
 *   - Para modificar su altura en horizontal: cambiar Horizontal.LOGO_CLIENTE.height = "100dp"
 *
 * Si se desea controlar más propiedades visuales (margins, padding, visibility, scaleType...),
 * este modelo puede extenderse fácilmente desde aquí.
 */
object Imagenes {
    // Nombres de recursos en drawable
    const val LOGO_CLIENTE = "tragabuche"
    const val LOGO_DESARROLLADORA = "logo_desarrolladora"

    data class PropiedadesImagen(
        val width: String,
        val height: String,
        val gravity: String,
        val marginTop: String = "0sp",
        val marginBottom: String = "0sp"
    )

    object Vertical {
        val LOGO_CLIENTE = PropiedadesImagen(
            width = "match_parent",
            height = "200dp",
            gravity = "center_horizontal",
            marginTop = "10sp",
            marginBottom = "10sp"
        )
        val LOGO_DESARROLLADORA = PropiedadesImagen("match_parent", "wrap_content", "center_horizontal")
    }

    object Horizontal {
        val LOGO_CLIENTE = PropiedadesImagen(
            width = "wrap_content",
            height = "150dp",
            gravity = "center_horizontal",
            marginTop = "8dp",
            marginBottom = "8dp"
        )
        val LOGO_DESARROLLADORA = PropiedadesImagen(
            width = "wrap_content",
            height = "70dp",
            gravity = "center_horizontal",
            marginTop = "4dp",
            marginBottom = "4dp"
        )
    }
}
