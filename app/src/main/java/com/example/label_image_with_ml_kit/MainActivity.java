package com.example.label_image_with_ml_kit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Button button,button2;
    InputImage inputImage;
    LocalModel localModel;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1)
        {
            imageView.setImageURI(data.getData());

            try {
                inputImage = InputImage.fromFilePath(getApplicationContext(), data.getData());
                 }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Varsayılan seçenekleri kullanmak için
//            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

            // Veya gereken minimum güveni ayarlamak için:
             ImageLabelerOptions options =
              new ImageLabelerOptions.Builder()
              .setConfidenceThreshold(0.5f)
                   .build();
            ImageLabeler labeler = ImageLabeling.getClient(options);
            labeler.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            for (int i=0;i<labels.size();i++) {
                                String text = labels.get(i).getText();
                                float confidence = labels.get(i).getConfidence();
                                int index = labels.get(i).getIndex();
                                if(i==0)
                                {
                                    textView.append("En yüksek tahmin: "+text+":"+confidence+"\n\n");
                                }
                                else if (confidence>0.90f)
                                {
                                    textView.append("Detaylar: "+text+":"+confidence+"\n");
                                }
                                else if (i<4)
                                {
                                    textView.append(text+":"+confidence+"\n");
                                }

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }



        if (requestCode==2)
        {
            imageView.setImageURI(data.getData());

            try {
                inputImage = InputImage.fromFilePath(getApplicationContext(), data.getData());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            localModel = new LocalModel.Builder()
                            .setAssetFilePath("lite-model_disease-classification_1.tflite")
                            // or .setAbsoluteFilePath(absolute file path to model file)
                            // or .setUri(URI to model file)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(2)
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
            labeler.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            for (ImageLabel label : labels) {
                                String text = label.getText();
                                float confidence = label.getConfidence();
                                int index = label.getIndex();
                                textView.append(text+":"+confidence+"\n");
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Görsel Seçin"),1);

            }
        });
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Görsel Seçin"),2);

            }
        });

    }

}
