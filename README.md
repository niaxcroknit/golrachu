# 골라츄 (CHOOSY)

고민되는 선택지를 넣으면 대신 하나를 골라 주는 앱이에요.
마음에 안 들면 **싫어**를 누르면 그 선택지는 빼고 남은 것 중에서 다시 골라 줘요.

- 패키지 이름: `com.nia.golrachu`
- 현재 버전: **1.0.3** (versionCode 4)
- 하단 AdMob 배너 광고 포함

## 폴더 구성

| 폴더 | 내용 |
|---|---|
| `app/`, `gradle/`, `build.gradle` 등 | 안드로이드 앱 (안드로이드 스튜디오로 바로 열기). 자세한 건 [ANDROID.md](ANDROID.md) |
| `docs/` | 웹 버전과 **개인정보처리방침**(`privacy.html`). GitHub Pages로 공개돼요. 자세한 건 [docs/WEB.md](docs/WEB.md) |
| `store/` | Play Console 스토어 등록정보: 아이콘, 그래픽 이미지, 스크린샷, 설명 문구, 출시 노트 |

## 빠르게 시작하기

1. 안드로이드 스튜디오 → **File → New → Project from Version Control** → 이 저장소 주소 붙여 넣기
   (또는 저장소를 내려받아 폴더째 **Open**)
2. Gradle sync가 끝나면 폰을 연결하고 ▶
   - ▶로 실행하면 광고는 구글 **테스트 광고**가 나와요. 진짜 광고는 서명한 스토어용 파일에서만 나와요.
3. 스토어용 파일: **Build → Generate Signed App Bundle** → 처음 만든 키 파일(.jks)로 서명

## 링크

- 개인정보처리방침: https://niaxcroknit.github.io/golrachu/privacy.html
- 웹 버전: https://niaxcroknit.github.io/golrachu/

## 화면 수정

화면(글자, 색, 동작)은 전부 `app/src/main/assets/index.html` 한 파일이에요.
같은 화면을 웹 버전에도 쓰고 있어서, 고치면 `docs/index.html`도 같이 맞춰 주세요. (앱 쪽 파일에만 진동·광고 연결 코드가 조금 더 있어요.)

## 주의

- 서명 키(`.jks`)와 서명된 파일(`.aab`, `.apk`)은 `.gitignore`로 막아 두었어요. **키 파일은 절대 저장소에 올리지 마세요.**
