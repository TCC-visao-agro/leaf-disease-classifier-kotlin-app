package com.example.leafdiseaseclassificationkotlin

data class Classification(val highestProb: String,
                          val classification: String,
                          val picture: String
) {
    override fun toString(): String {
        return "Category [highestProb: ${this.highestProb}, classification: ${this.classification}, picture: ${this.picture}]"
    }
}
