package com.example.carros.domain.upload;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class FirebaseStorageService {


    @PostConstruct
    private void init() throws IOException {

        if (FirebaseApp.getApps().isEmpty()) { //Verifica se o app esta vazio ou nao

            InputStream serviceAccount = FirebaseStorageService.class.getResourceAsStream("/serviceAccountKey.json"); //busca nosso arquivo com as credenciais

            System.out.println(serviceAccount);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("carros-upload.appspot.com") //Seleciona nosso Bucket
                    .setDatabaseUrl("https://carros-upload.firebaseio.com") //Se fossemos mexer com a nuvem
                    .build();

            FirebaseApp.initializeApp(options);
        }

    }


    //Faz upload do arquivo
    public String upload(UploadInput uploadInput){

        //Carega uma instancia do nosso banco de dados, o que foi configurado anteriomente
        Bucket bucket = StorageClient.getInstance().bucket();

        System.out.println(bucket);

        //pega a base64, para codificar nossa imagem, jogoa para um arrays de bytes
        byte[] bytes = Base64.getDecoder().decode(uploadInput.getBase64());

        //Pega o nome do arquivo
        String filename = uploadInput.getFilename();

        //Envia para o banco de dados, os dados do upload, temos que colocar o nome do arquivo, a nossa base64 e por fim o tipo do dado.
        Blob blob = bucket.create(filename,bytes,uploadInput.getMimeType());

        //Deixa a Url public
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(),Acl.Role.READER));


        return String.format("https://storage.googleapis.com/%s/%s", bucket.getName(),filename);


    }



}
