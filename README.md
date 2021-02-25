# MPSY

マップとポイントを操作するやつ(Mappu to Pointo o Sousasuru Yatsu)

XOPSのマップとポイントに対して拡大や回転などの処理を行うプログラムです。

## 使い方

### オプション一覧

|      オプション       |            意味             |                引数                 |
| :-------------------: | :-------------------------: | :---------------------------------: |
| -bi, --bd1FilepathIn  | 入力するBD1ファイルへのパス |            ファイルパス             |
| -pi, --pd1FilepathIn  | 入力するPD1ファイルへのパス |            ファイルパス             |
| -bo, --bd1FilepathOut | 出力するBD1ファイルへのパス |            ファイルパス             |
| -po, --pd1FilepathOut | 出力するPD1ファイルへのパス |            ファイルパス             |
|   -t, --translation   |            移動             |          移動量 [x, y, z]           |
|      -s, --scale      |         拡大・縮小          |          拡大率 [x, y, z]           |
|      -rx, --rotX      |        X軸回りの回転        |               角度(°)               |
|      -ry, --rotY      |        Y軸回りの回転        |               角度(°)               |
|      -rz, --rotZ      |        Z軸回りの回転        |               角度(°)               |
|       -r, --rot       |      任意軸回りの回転       | 角度および回転軸 [角度(°), x, y, z] |
|     -z, --invertZ     |          Z軸を反転          |                  -                  |
|      -h, --help       |        ヘルプを表示         |                  -                  |
|     -v, --version     |    バージョン情報を表示     |                  -                  |

### 使用例

#### マップとポイントの操作

```
mpsy.exe ^
	-bi map.pd1 ^
	-pi points.pd1 ^
	-bo map2.bd1 ^
	-po points2.pd1 ^
	-t 50.0 50.0 50.0 ^
	-s 2.0 2.0 2.0 ^
	-rx 45 ^
	-ry 45 ^
	-rz 45 ^
	-r 45 1.0 1.0 1.0 ^
	-z
```

すべてのオプションを設定する必要はありません。
行いたい処理のオプションのみを設定してください。

#### ヘルプの表示

```
mpsy.exe -h
```

## 使用上の注意

マップおよびポイントに対する操作は、
**移動**→**拡大・縮小**→**X軸回りの回転**→**Y軸回りの回転**→**Z軸回りの回転**→**任意軸回りの回転**→**Z軸反転**
という順番で行われます。
このため、移動や回転を同時に行うと、想定通りの結果が得られない場合があります。

------

このプログラムには[JXM (Java XOPSManipulator)](https://github.com/Dabasan/jxm)が使用されています。
JavaがインストールされていないWindowsマシンでも動作するように、Windows用のJREが同封されています。

リリースに含まれる`mpsy.exe`は内部で`mpsy.jar`を実行しています。

Windows以外の環境を使用している方は、`mpsy.jar`を直接実行してください。
この場合には、Java 11以上が必要になります。

## プログラム情報

### 作者

駄場

### バージョン

1.0.0-rc2

### ライセンス

JREのライセンスについては、JRE/legalを参照してください。
その他のファイルはGPLライセンスの下に公開されています。

