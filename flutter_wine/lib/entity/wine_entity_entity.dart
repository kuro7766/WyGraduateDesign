import 'package:flutter_wine/generated/json/base/json_convert_content.dart';

class WineEntity with JsonConvert<WineEntity> {
	int id;
	String name;
	String desc;
	String img;
}
