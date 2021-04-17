package com.example.label_image_with_ml_kit
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.IOException
class MainActivity : AppCompatActivity() {
    var imageView: ImageView? = null
    var textView: TextView? = null
    var button: Button? = null
    var button2: Button? = null
    var inputImage: InputImage? = null
    var localModel: LocalModel? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            imageView!!.setImageURI(data!!.data)
            try {
                inputImage = InputImage.fromFilePath(applicationContext, data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // Varsayılan seçenekleri kullanmak için
//            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
            // Veya gereken minimum güveni ayarlamak için:
            val options = ImageLabelerOptions.Builder()
                    .setConfidenceThreshold(0.5f)
                    .build()
            val labeler = ImageLabeling.getClient(options)
            labeler.process(inputImage)
                    .addOnSuccessListener { labels ->
                        for (i in labels.indices) {
                            val text = labels[i].text
                            val confidence = labels[i].confidence
                            val index = labels[i].index
                            if (i == 0) {
                                textView!!.append("En yüksek tahmin: $text:$confidence\n\n")
                            } else if (confidence > 0.90f) {
                                textView!!.append("Detaylar: $text:$confidence\n")
                            } else if (i < 4) {
                                textView!!.append("$text:$confidence\n")
                            }
                        }
                    }
                    .addOnFailureListener { }
        }
        if (requestCode == 2) {
            imageView!!.setImageURI(data!!.data)
            try {
                inputImage = InputImage.fromFilePath(applicationContext, data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            localModel = LocalModel.Builder()
                    .setAssetFilePath("lite-model_disease-classification_1.tflite") // or .setAbsoluteFilePath(absolute file path to model file)
                    // or .setUri(URI to model file)
                    .build()
            val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
                    .setConfidenceThreshold(0.5f)
                    .setMaxResultCount(2)
                    .build()
            val labeler = ImageLabeling.getClient(customImageLabelerOptions)
            labeler.process(inputImage)
                    .addOnSuccessListener { labels ->
                        for (label in labels) {
                            val text = label.text
                            val confidence = label.confidence
                            val index = label.index
                            textView!!.append("$text:$confidence\n")
                        }
                    }
                    .addOnFailureListener { }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)
        button = findViewById(R.id.button)
        button.setOnClickListener(View.OnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Görsel Seçin"), 1)
        })
        button2 = findViewById(R.id.button2)
        button2.setOnClickListener(View.OnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Görsel Seçin"), 2)
        })
    }
}
