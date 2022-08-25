import 'dart:convert';
import 'dart:math';

import 'package:crypto/crypto.dart';
import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;

typedef ResponseCallbackForSimpleHttp<T> = void Function(T json);

bool __showRawJson = true;

class ResponseContent<T> {
  bool success;
  T data;

  ResponseContent({this.success, this.data});

  ResponseContent.success(T responseData) {
    success = true;
    data=responseData;
  }

  ResponseContent.fail(){
    success=false;
  }
}

