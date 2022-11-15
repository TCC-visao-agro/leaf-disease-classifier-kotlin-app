package com.example.leafdiseaseclassificationkotlin

class DiseaseInformation() {
    var currentDiseaseName = "Healthy"
    var currentDiseaseInformation = "loren ipsun"

    private val diseaseList = arrayOf(
        "Tomato_Bacterial_spot",
        "Tomato_Early_blight",
        "Tomato_Late_blight",
        "Tomato_Leaf_Mold",
        "Tomato_Septoria_leaf_spot",
        "Tomato_Spider_mites_Two_spotted_spider_mite",
        "TomatoTarget_Spot",
        "TomatoTomato_YellowLeafCurl_Virus",
        "TomatoTomato_mosaic_virus",
        "Tomato_healthy"
    )

    private val translatedDiseaseList = arrayOf(
        "Mancha Bacteriana",
        "Praga em estado inicial",
        "Praga em estado avançado",
        "Bolor",
        "Mancha de folha septorial",
        "Acaro",
        "Mancha da folha",
        "Virus de folha amarelada",
        "Virus mosaico",
        "Planta Saudavel"
    )

    fun getDiseaseInformation(diseaseName: String){
        for(i in  0..9){
            if (diseaseName == diseaseList[i]){
                currentDiseaseName = translatedDiseaseList[i]
            }
        }
        print(currentDiseaseName)
    }
}