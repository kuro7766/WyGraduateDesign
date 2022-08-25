import 'package:flutter/material.dart';
import 'package:sklite/SVM/SVM.dart';
import 'package:sklite/utils/io.dart';
import 'dart:convert';
class SkliteRunner{
  Future run(){
    loadModel("assets/svcmnist.json").then((x) {
      var svc = SVC.fromMap(json.decode(x));

    });
  }
}