import 'package:flutter_wine/util/async_mutex.dart';

class AppTool {
  static AppTool _instance;

  static AppTool get instance => _instance ??= AppTool._internal();

  factory AppTool() => instance;

  AppTool._internal();

  AsyncMutex extremelyHeavyTaskMutex = AsyncMutex();

  AsyncMutex fetchCurveMutex = new AsyncMutex();

}
