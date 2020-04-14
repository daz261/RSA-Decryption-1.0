# RSA Decryption for Barcode and Text Recognition
The RSA Decryption app demonstrates how to use RSA decryption in order to decrypt RSA-encrypted text in plain or barcode format. The app is structured into two modules or steps:
<ol>
  <li>Scanning of the RSA private key in QR code format</li>
  <li>Scanning of the encrypted text</li>
</ol>

<h2>Libraries</h2>
<ul>
  <li>The app uses <a href="https://github.com/zxing/zxing">ZXing ("Zebra Crossing")</a> barcode scanning library to scan the QR codes. </li>  
  <li>The app uses <a href="https://github.com/firebase/quickstart-android/blob/master/mlkit/README.md">MLKit Vision</a> API for live camera analysis to detect barcodes and text. </li>
</ul>

<h2>Use</h2>
Download the repository, open it with Android Studio and press run. The app was meant to be deployed on an Android device, because of the scanning capabilities that are more difficult to simulate on the Android emulator. If you want to change the name of the 

In the app-level build.gradle change the name of the app from 'com.server
  
# Barcode Detection
![](barcode.gif)

# Text Detection
![](ocr.gif)
