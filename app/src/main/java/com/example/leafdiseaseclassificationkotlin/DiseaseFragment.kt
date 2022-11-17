package com.example.leafdiseaseclassificationkotlin

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.*
import java.time.LocalDateTime

class DiseaseFragment : Fragment() {
    private var currentDiseaseName = "Healthy"
    private var currentDiseaseInformation = "loren ipsun"
    var navController: NavController? = null
    private lateinit var confidence: TextView
    private lateinit var result: TextView
    private lateinit var imageView: ImageView
    private lateinit var information: TextView
    private lateinit var highestProb: String
    private lateinit var classification: String
    private lateinit var picture: String
    lateinit var outputStream: OutputStream

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime = LocalDateTime.now()


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

    private val diseaseSymptomsList = arrayOf(
        "Uma infecção por Xanthomonas campestris pv. vesicatoria causa manchas em plantas maduras e mudas. A mancha bacteriana é semelhante à mancha bacteriana e muitas vezes é diagnosticada erroneamente. Uma imagem horizontal de perto de uma planta de tomate que sofre de mancha bacteriana, fazendo com que a folhagem fique marrom ou amarela e murche. A mancha bacteriana persiste de uma estação para outra em tomates voluntários, ervas daninhas como cereja moída e outras solanáceas e em restos de culturas. Esta doença frequentemente se espalha através de sementes infectadas. Uma vez que as bactérias infectam um jardim ou campo, elas podem ser difíceis de controlar. Mudas que são atacadas podem perder suas folhas. Plantas maduras geralmente apresentam sintomas nas folhas mais velhas. A doença se manifesta como áreas da folhagem que parecem estar encharcadas de água. As plantas podem desenvolver grandes manchas, principalmente nas margens das folhas. Sprays preventivos de cobre e controles culturais podem ser usados \u200B\u200Bpara controlar a propagação da doença. Não regue com aspersores, pois as bactérias podem ser espirradas em outras plantas. E certifique-se de selecionar sementes e transplantes certificados como livres da doença, se isso tiver sido um problema para você no passado. Algumas cepas também podem infectar pimentas. A rotação de seus tomates com culturas que não são da família Solanaceae ajudará a prevenir infecções subsequentes por manchas bacterianas.",
        "Esta doença fúngica comum, causada por Alternaria solani, resulta em pequenas manchas marrons ou pretas nas folhas, caules e frutos. As manchas nas folhas e frutos geralmente têm um padrão clássico de anéis concêntricos. planta de tomate que sofre de uma doença que fez com que as folhas se tornassem marrons e necróticas, retratada em um fundo de foco suave. A requeima é principalmente um problema quando o tempo está chuvoso. Os esporos podem ser espirrados nas plantas pela água ou podem ser O fungo sobrevive no tecido de tomates infectados, ervas daninhas e batatas. Destruir esses hospedeiros em potencial é uma maneira de minimizar a chance de infecção. Não adicione partes de plantas doentes à sua pilha de compostagem. Pode ser necessário aplicar um fungicida se uma infecção é grave. No entanto, a infecção pode diminuir se o tempo ficar seco.",
        "O mofo da água Phytophthora infestans pode causar infecções graves em tomates e batatas. Este é o organismo que foi responsável pela fome da batata irlandesa, resultando na morte ou emigração de milhões de pessoas. Um close-up de um tomate maduro que sofre de uma infecção mortal chamada requeima que também pode infectar batatas, retratado em um fundo de foco suave. Os primeiros sintomas são áreas das folhas que parecem encharcadas de água. Aumentam rapidamente para formar manchas roxas oleosas. Anéis de micélio branco-acinzentado podem aparecer nos lados inferiores das folhas.",
        "O mofo branco não é tão comum, mas pode ser uma doença grave quando ocorre. Os fungos que a causam (Sclerotinia spp.) frequentemente atacam as flores em decomposição que caíram e se alojaram nos caules. Uma imagem horizontal aproximada de plantas de tomate crescendo no jardim mostrando sintomas de podridão de madeira de esclerotinia, fazendo com que elas murchem e morram. Foto de Don Ferrin, Centro Agrícola da Universidade Estadual da Louisiana, Bugwood.org, via CC BY-SA. Os sintomas começam com lesões que parecem encharcadas de água que se espalham ao longo dos caules, que depois secam e parecem branqueadas – daí o nome alternativo para esta doença, podridão da madeira. Também conhecido como Sclerotinia caule podridão, o patógeno mais comum para causar esta doença é chamado S. sclerotiorum, que se refere às estruturas de repouso conhecidas como escleródios que podem abundar no solo. Em condições úmidas e frias, os escleródios podem germinar para formar corpos frutíferos que produzem milhões de esporos que são espalhados pelo vento. Infelizmente, simplesmente praticar uma boa higienização em torno de seus tomateiros não ajudará a controlar o mofo branco, devido ao grande volume de esporos que são produzidos. Uma forma de prevenir esta doença é evitar a molhagem e a secagem repetidas da superfície do solo usando irrigação por gotejamento subsuperficial. Os escleródios são muito menos propensos a germinar se a superfície do solo permanecer seca.",
        "A mancha foliar de Septoria, também conhecida como cancro foliar, é causada pelo fungo Septoria lycopersici. Além de infectar tomates, também pode infectar outras solanáceas. Uma imagem horizontal de perto das folhas de uma planta de tomate que sofre de uma doença chamada Septoria, retratada em um fundo verde de foco suave. Os tomateiros podem frequentemente ser infectados com mancha foliar e requeima simultaneamente. Esta doença se manifesta primeiro como pequenas manchas circulares que parecem estar encharcadas de água. As manchas aumentam e muitas vezes se fundem para formar manchas. A septoria produz estruturas pequenas, escuras e semelhantes a espinhas no centro das manchas. Estas são estruturas de frutificação que produzem esporos. Observá-los é uma maneira de distinguir uma infecção por mancha foliar de Septoria da requeima, que não produz tais estruturas. Esses esporos são facilmente espalhados pelo vento, chuva, roupas, insetos e ferramentas ou equipamentos de cultivo. Certifique-se de sempre limpar e higienizar seu equipamento de jardinagem, entre as plantas e entre os usos. O fungo pode hibernar nos restos de plantas doentes e ervas daninhas como erva-moura, jimsonweed ou cereja moída. As medidas de controle geralmente envolvem a eliminação de fontes de esporos. Certifique-se de sempre fazer um trabalho completo de limpeza do jardim no final da temporada e descarte quaisquer detritos que possam estar infectados no lixo. Você pode aplicar fungicidas se uma infecção for grave.",
        "Os ácaros vermelhos do tomate são laranja pálido a vermelho e se alimentam na parte inferior das folhas. Eles não podem ser vistos facilmente a olho nu. Os danos de alimentação causados pela sucção da seiva aparecem como muitas marcas amarelas pálidas brilhantes no topo da folha de tomate (veja a foto inferior). Eventualmente, as folhas ficam marrons e morrem ou caem. Ataque severo leva à formação de teias na planta (ver foto). Os ácaros vermelhos são difíceis, mas não impossíveis de controlar. Os ácaros vermelhos alimentam-se principalmente de plantas intimamente relacionadas e da mesma família do tomate. Por exemplo: solanáceas pretas (managu), batatas, berinjelas (biringanya) e ervas daninhas como maçã sodom. Os ácaros da aranha chegam a todos os lugares. Você não pode controlá-los no tomate sem considerar outras plantas. Os ácaros aumentam rapidamente em número durante condições quentes e secas. O ácaro vermelho causa mais danos e perdas em tomates de estufa por causa das temperaturas mais altas e condições mais secas.",
        "O fungo do ponto alvo pode infectar todas as partes acima do solo do tomateiro. As plantas são mais suscetíveis como mudas e pouco antes e durante a frutificação. Os sintomas foliares iniciais são manchas pequenas e encharcadas de água na superfície superior da folha. As manchas evoluem para pequenas lesões necróticas com centros castanho-claros e margens escuras. Esses sintomas podem ser confundidos com sintomas de mancha bacteriana. As lesões aumentam de tamanho, tornam-se circulares com centros cinza a marrom pálido. À medida que as lesões aumentam, elas podem desenvolver círculos concêntricos mais escuros, daí o nome de mancha alvo. Os círculos concêntricos são semelhantes aos observados nas lesões iniciais da requeima. Halos amarelos podem se formar ao redor das lesões em algumas variedades. As lesões podem coalescer, formando grandes áreas manchadas nos folíolos, e as folhas infectadas podem cair prematuramente. As infecções no local-alvo geralmente começam nas folhas mais velhas e inferiores no dossel interno. Assim, os sintomas iniciais podem não ser percebidos pelo produtor, dificultando a detecção precoce. A doença progride para cima, causando desfolhamento do dossel interno, uma condição conhecida como “derretimento”.",
        "Tomato yellow leaf curl virus (TYLCV) pode infectar mais de 30 tipos diferentes de plantas, mas é conhecido principalmente por causar perdas devastadoras de até 100% na produção de tomates. Ambos os tomates cultivados em campo e em estufa são suscetíveis. O TYLCV é vetorizado pela mosca-branca Silverleaf (Bemisia tabaci). Após detecções recentes, a mosca-branca silverleaf é agora considerada estabelecida em Victoria. TYLCV é um begomovírus, o que implica que possui uma ampla gama de hospedeiros em dicotiledôneas. Existem duas cepas de TYLCV, uma cepa leve e uma cepa grave. A cepa suave foi considerada responsável pela detecção recente de TYLCV em plantas de tomate e ervas daninhas no norte de Victoria (David Lovelace pers. comm). A cepa grave infecta feijão e tomate.",
        "Esta doença é geralmente fatal e afeta principalmente plantas de tomate que são cultivadas perto de alfafa. A maioria dos campos comerciais de alfafa nos EUA está infectada com esse vírus, e os pulgões transmitem facilmente a doença para plantas de tomate próximas. Uma imagem horizontal de perto de uma seção de uma folha que sofre de um vírus de mosaico retratado em um fundo de foco suave. Foto da Divisão de Indústria de Plantas da Flórida, Departamento de Agricultura e Serviços ao Consumidor da Flórida, Bugwood.org, via CC BY-SA. Os sintomas típicos incluem folhas amarelas com algumas manchas e frutas com anéis circulares de tecido morto. O floema do sistema vascular – tecido responsável pelo transporte de nutrientes das plantas – também morre e fica marrom. Não há controles químicos para esta infecção, e o uso de inseticidas para controlar os pulgões não ajuda a controlar esse vírus. Uma estratégia de manejo é colocar coberturas de polietileno refletivo de prata nos canteiros antes do plantio para repelir os pulgões. Sua melhor aposta para evitar esta doença é evitar plantar tomates perto de alfafa.",
        "A planta de tomate está saudavel, sem nenhuma doença aparente."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //argumentos passados da ultima fragment!!!
        highestProb = arguments?.getString("highest_prob")!!
        classification = arguments?.getString("classification")!!
        picture = arguments?.getString("picture")!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disease, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        //decodando a string para virar bitmap e poder ser exibida
        val imageBytes = Base64.decode(picture, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val test = (highestProb.toFloat() * 100).toString() + "%"

        confidence = view.findViewById(R.id.confidence)
        result = view.findViewById(R.id.result)
        imageView = view.findViewById(R.id.imageView)
        information = view.findViewById(R.id.diseaseInformation)

        for (i in 0..9) {
            if (classification == diseaseList[i]) {
                currentDiseaseName = translatedDiseaseList[i]
                currentDiseaseInformation = diseaseSymptomsList[i]
            }
        }

        confidence.text = test
        result.text = currentDiseaseName
        information.text = currentDiseaseInformation
        imageView.setImageBitmap(image)

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                WRITE_EXTERNAL_STORAGE
            ) -> {
                saveImage()
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    permissionLauncher.launch(
                        WRITE_EXTERNAL_STORAGE,
                    )
                }
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permission Granted:", isGranted.toString())
        } else {
            Log.d("Permission Granted:", isGranted.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveImage() {
        val dir = File(context?.filesDir, "LeafClassification")
        if (!dir.exists()) {
            Log.d("Unavailable:", dir.absolutePath);
            Log.d("Can write?:", dir.canWrite().toString())
            dir.mkdirs()
        }
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val file = File(dir, "$currentDateTime.jpg")
        try {
            outputStream = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Toast.makeText(context, "Imagem salva com sucesso.", Toast.LENGTH_SHORT).show()
        try {
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}
