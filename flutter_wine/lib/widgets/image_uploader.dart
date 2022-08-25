import 'package:flutter/cupertino.dart';
import 'package:flutter_wine/util/files.dart';
import 'package:flutter_wine/util/simple_http.dart';
import 'package:flutter_wine/util/simple_http_builder.dart';

class ImageUploader extends StatefulWidget {
  String id;

  ImageUploader({Key key}) : super(key: key);

  @override
  _ImageUploaderState createState() => _ImageUploaderState();
}

class _ImageUploaderState extends State<ImageUploader> {
  @override
  Widget build(BuildContext context) {
    return SimpleFutureBuilder(
      future: () async {
        PersistentFiles persistentFile=PersistentFiles(fileId:widget.id);
        if(await persistentFile.exists()) {
          return ResponseContent.success(await persistentFile.readCurve());
        }else{
          return ResponseContent.success(null);
        }
      }(),
      builder: (data) {
        if (data == null) {
          return Image.asset('empty.png');
        } else {
          return Image.memory(data);
        }
      },
    );
    // return Container();
  }
}
