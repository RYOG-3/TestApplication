# TestApplication
タブレット上で使用を想定したネットワーク構成図を描画できるAndroidアプリ

## 研究概要
ネットワークエンジニアの主たる業務の一つにネットワークの設計と構築がある. ネットワークの設計では,
物理構成図や論理構成図といったネットワーク構成図などの資料を作成する. そして, 作成した資料を基にネットワーク機器に施すべき設定を確認し, 発行するコマンドを準備する.
ネットワークの構築では, 設計時に作成した資料を基にネットワーク機器同士を結線し, コマンドを発行することでネットワークを構築する.
これらの業務は手作業で実施するため, 作業ミスといったネットワーク障害の一因となるヒューマンエラーが発生する可能性がある.
そして, ヒューマンエラーを完全に防ぐことは不可能であり, 実際の作業現場では, 多くのネットワーク機器に対して設定コマンドを発行する必要があるため, 作業ミスを無くすことは難しい.
ヒューマンエラーの防止策として, 作業の自動化が有効である. これにより, 作業時間の短縮も可能となる.
そこで本研究では, ネットワーク構築の設定コマンド発行時における作業ミスの防止と作業時間の短縮を図ること目的に, 
ネットワーク構成図から生成した設定情報のネットワーク機器への自動設定を可能とする機能を開発する.
この機能を用いることで, ネットワークエンジニアがネットワークを構築する際の作業負担を軽減することが期待できる.

## システム構成図
システム構成図を以下に示す. 本システムは, タブレットと管理サーバ, 対象のネットワーク機器から構成する. 
タブレットは開発したAndroidアプリを操作するために利用する. Androidアプリはネットワーク構成図描画機能, メモ機能, そして設定情報反映機能が搭載されている.
タッチやスワイプでGUIを操作し, 機能を利用する. 管理サーバはタブレットとHTTP通信してJSONデータを受け取り, 保存する.
その後NETCONFを用いて, 対象のネットワーク機器に設定データを送信する.
NETCONF は, IETF (Internet Engineering Task Force) において標準化が進められている, ネットワーク機器を制御するためのプロトコルである.
このプロトコルを用いて送信されたデータをネットワーク機器が受け取り，自動で機能設定する.


![System Diagram drawio](https://user-images.githubusercontent.com/65248588/213059164-e06ac083-c6eb-4ac2-a00c-39f88c07bf27.png)


## デモ動画
PC上で動作させたタブレット用Androidエミュレータでのデモ動画

### ネットワーク機器の配置と結線
左上の物理構成図をタッチするとモードが切り替わり, 何も無いところをタッチするとルータが設置される. 続けてタッチすると, スイッチ, ホストに切り替わる. 
機器同士をなぞるようにスワイプすると結線できる. 
https://user-images.githubusercontent.com/65248588/213067377-36ddc10a-c06e-4e04-a0ec-9bb71107f524.mp4


### 設定情報反映機能の説明
タブレット上で描画が終わると，右上の発行ボタンをタッチすることで実機のネットワーク機器に自動設定が行われる. 
右のウィンドウは実機のルータにコンソール接続した画面
https://user-images.githubusercontent.com/65248588/213059242-00054cc0-bb54-4dfc-bb37-d9b9d461965b.mp4

