# 出欠席管理システム (Attendance System)

## 概要
このシステムは、学生と教師が出欠席を管理するためのWebアプリケーションです。

## 機能

### ログイン機能
1. **スタートページ** (`/`) - システムの開始ページ
2. **ログイン画面** (`/login`) - ユーザーIDとパスワードでログイン
3. **ロール別メニュー表示**:
   - 生徒 (STUDENT): `/students/menu` にリダイレクト
   - 教師 (TEACHER): `/teachers/menu` にリダイレクト

## テストユーザー

データベース初期化時に以下のテストユーザーが作成されます:

### 生徒アカウント
- **ユーザーID**: `student1`
- **パスワード**: `password`
- **ロール**: STUDENT

### 教師アカウント
- **ユーザーID**: `teacher1`
- **パスワード**: `password`
- **ロール**: TEACHER

## 技術スタック
- **フレームワーク**: Spring Boot 3.5.5
- **テンプレートエンジン**: Thymeleaf
- **セキュリティ**: Spring Security
- **データベース**: 
  - 本番環境: MySQL
  - テスト環境: H2 (インメモリ)
- **ビルドツール**: Gradle

## 実行方法

### 前提条件
- Java 21
- MySQL サーバー (localhost:65534)

### アプリケーションの起動
```bash
./gradlew bootRun
```

アプリケーションは `http://localhost:8080` で起動します。

### テストの実行
```bash
./gradlew test
```

## プロジェクト構造
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # セキュリティ設定
│   │   ├── controller/      # コントローラー
│   │   ├── model/           # エンティティモデル
│   │   ├── repository/      # データアクセス層
│   │   ├── security/        # カスタムセキュリティハンドラー
│   │   └── service/         # サービス層
│   └── resources/
│       ├── templates/       # Thymeleafテンプレート
│       │   ├── layout/      # レイアウトフラグメント
│       │   ├── students/    # 生徒用ページ
│       │   └── teachers/    # 教師用ページ
│       ├── static/          # 静的リソース (CSS, JS)
│       └── data.sql         # 初期データ
└── test/
    └── java/com/example/demo/
        └── controller/      # コントローラーテスト
```

## セキュリティ
- パスワードはBCryptでハッシュ化されて保存されます
- ロールベースのアクセス制御 (RBAC) を実装
- 生徒用と教師用のエンドポイントは適切なロールでのみアクセス可能
