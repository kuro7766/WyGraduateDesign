class Block {
  int __lastTime = 0;
  int gap = 0;

  //constructor
  Block(this.gap);

  void run(void Function() work, {void Function() onDeny}) {
    var now = DateTime.now().millisecondsSinceEpoch;
    if (now - __lastTime > gap) {
      __lastTime = now;
      work?.call();
    } else {
      if (onDeny != null) {
        onDeny?.call();
      }
    }
  }
}
