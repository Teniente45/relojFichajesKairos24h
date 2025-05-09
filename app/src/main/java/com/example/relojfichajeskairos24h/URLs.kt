package com.example.relojfichajeskairos24h

object BuildURL {
    const val HOST = "https://setfichaje.kairos24h.es/"
    const val DEMO = "https://demosetfichaje.kairos24h.es/"
    const val ACTION = "index.php?r=citaRedWeb/crearFichajeExterno"
    const val PARAMS = "&xEntidad=1005" +
                        "&cKiosko=TABLET1" +
                        "&cEmpCppExt=" +
                        "&cTipFic=" +
                        "&cFicOri=APP"

    const val SETFICHAJE = HOST + ACTION + PARAMS
}


