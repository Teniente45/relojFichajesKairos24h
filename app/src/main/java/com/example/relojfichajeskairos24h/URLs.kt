package com.example.relojfichajeskairos24h

object BuildURL {
    //https://setfichaje.kairos24h.es/index.php?r=citaRedWeb/crearFichajeExterno&xEntidad=1002&cKiosko=TABLET1&cEmpCppExt=&cTipFic=&cFicOri=
    //https://demosetfichaje.kairos24h.es/index.php?r=citaRedWeb/crearFichajeExterno&xEntidad=1002&cKiosko=TABLET1&cEmpCppExt=1111&cTipFic=ENTRADA&cFicOri=PUEFIC
    //https://demosetfichaje.kairos24h.es/index.php?r=citaRedWeb/crearFichajeExterno&xEntidad=1002&cKiosko=TABLET1&cEmpCppExt=1111&cTipFic=ENTRADA&cFicOri=PUEFIC


    const val host = "https://setfichaje.kairos24h.es/"
    const val demo = "https://demosetfichaje.kairos24h.es/"
    const val action = "index.php?r=citaRedWeb/crearFichajeExterno"
    const val params = "&xEntidad=1002&cKiosko=TABLET1&cEmpCppExt=&cTipFic=&cFicOri=APP"


    const val setfichaje = demo + action + params
}


