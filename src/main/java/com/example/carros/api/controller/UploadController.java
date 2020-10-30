package com.example.carros.api.controller;


import com.example.carros.domain.upload.FirebaseStorageService;
import com.example.carros.domain.upload.UploadInput;
import com.example.carros.domain.upload.UploadOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

     //Cria a instancia do FireBase, vai ser gerenciada pelo Spring
    @Autowired
    private FirebaseStorageService uploadService;

    //Faz um poste aqui e manda o arquivo direto para o upload do firebase
    @PostMapping
    public ResponseEntity upload(@RequestBody UploadInput uploadInput){

        String url = uploadService.upload(uploadInput);

        return ResponseEntity.ok(new UploadOutput(url));

    }
















}
