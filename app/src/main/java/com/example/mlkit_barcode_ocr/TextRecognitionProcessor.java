// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.mlkit_barcode_ocr;

import android.graphics.Bitmap;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.example.mlkit_barcode_ocr.utility.CameraImageGraphic;
import com.example.mlkit_barcode_ocr.utility.FrameMetadata;
import com.example.mlkit_barcode_ocr.utility.GraphicOverlay;
import com.example.mlkit_barcode_ocr.VisionProcessorBase;

import java.io.IOException;
import java.util.List;

/**
 * Processor for the text recognition demo.
 */
public class TextRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

    private static final String TAG = "TextRecProc";

    private final FirebaseVisionTextRecognizer detector;
    String encrypted;
    String decrypted;
    private FirebaseVisionText.TextBlock decr;

    public TextRecognitionProcessor() {
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
        }
    }

    @Override
    protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
        return detector.processImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull FirebaseVisionText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay,
                    originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            GraphicOverlay.Graphic textGraphic3 = new TextGraphicBlock (graphicOverlay, blocks.get(i));

            encrypted = blocks.get(i).getText().replace("\n", "").
                    replace("o", "0").
                    replace("O", "0").
                    replaceAll("\\s", "").
                    replace("l", "1").
                    replace("Z", "3");
            Log.i("encrypted", encrypted);
            if (RSA.TestHex(encrypted)) {
                Log.i("tested", encrypted);
                //tested with 1024 RSA keys, the encrypted string in hex is 256 characters long
                if (encrypted.length() == 256) {
                    Log.i("string", encrypted);
                    //decrypt
                    try {
                        decrypted = RSA.decrypt_ocr(encrypted);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //replace text in the bounding box with decrypted text
                if (decrypted == null) {
                    graphicOverlay.add(textGraphic3);
                    Log.i("decrypted", "nothing to decrypt");
                }
                else{
                    //it works
                    //the only problem is how to display it
                    Log.i("decrypted", decrypted);
                  //  decrypted = decr.toString();
                    GraphicOverlay.Graphic textGraphic4 = new TextGraphicDecrypted (graphicOverlay, blocks.get(i), decrypted);
                    graphicOverlay.add(textGraphic4);
                }
            }
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
               // GraphicOverlay.Graphic textGraphic2 = new TextGraphicLine (graphicOverlay, lines.get(j));
                //graphicOverlay.add(textGraphic2);
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
                    //graphicOverlay.add(textGraphic);
                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}