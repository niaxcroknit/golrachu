# 골라츄 안드로이드 1.0.3 (Node 없이 안드로이드 스튜디오만)

Node.js, npm, Expo 전부 필요 없어요. **안드로이드 스튜디오 하나**만 있으면 돼요.

- 화면은 `app/src/main/assets/index.html` 한 파일이에요. 웹 버전과 똑같이 생겼어요.
- 글꼴이 앱 안에 들어 있어서 인터넷이 없어도 똑같이 보여요.
- 고를 때 톡톡, 싫어는 툭, 좋아는 딱 하고 **진동**이 와요.
- 폰이 다크 모드면 앱도 어두운 화면으로 바뀌어요.
- 화면 맨 아래에 **AdMob 배너 광고**가 붙어요. 키보드가 올라와 있는 동안에는 숨어요.

---

## 1. 열기

1. 안드로이드 스튜디오 실행 → **Open** → 압축을 푼 `golrachu-android` 폴더 선택
2. 처음 열면 오른쪽 아래에 **Gradle sync**가 돌아요. 필요한 것들을 인터넷에서 받느라 몇 분 걸려요. 끝날 때까지 기다려 주세요.
3. "Android Gradle Plugin을 업그레이드할까요?" 같은 알림이 뜨면 **업그레이드해도 되고 무시해도 돼요.** 둘 다 동작해요.

## 2. 폰에서 켜 보기

**실제 폰으로 (진동까지 확인하려면 이쪽)**
1. 폰 **설정 → 휴대전화 정보 → 소프트웨어 정보 → 빌드번호**를 7번 눌러 개발자 모드 켜기
2. **설정 → 개발자 옵션 → USB 디버깅** 켜기
3. 폰을 USB로 연결 → 폰에 뜨는 "USB 디버깅 허용" 누르기
4. 안드로이드 스튜디오 위쪽에서 내 폰이 선택된 걸 확인하고 ▶ (Run) 누르기

**에뮬레이터로**
위쪽 기기 목록 → **Device Manager** → 가상 기기 만들기 → ▶. (에뮬레이터는 진동이 안 느껴져요.)

## 3. 플레이스토어에 올릴 파일 만들기

### 앱 고유 ID
`app/build.gradle`에 **`com.nia.golrachu`**로 넣어 뒀어요. 플레이스토어에 한 번 올리면 바꿀 수 없으니, 바꾸고 싶으면 올리기 전에 바꿔 주세요.

### 서명된 파일(.aab) 만들기
1. 메뉴 **Build → Generate Signed App Bundle or APK…**
2. **Android App Bundle** 선택 → Next
3. Key store path 옆 **Create new…** → 저장 위치와 비밀번호 입력 → OK
   - ⚠️ 이 **키 파일(.jks)과 비밀번호는 절대 잃어버리면 안 돼요.** 나중에 업데이트할 때 꼭 필요해요. 이 폴더 말고 안전한 곳에 따로 백업하세요.
4. **release** 선택 → Create
5. 끝나면 `app/release/app-release.aab` 파일이 생겨요. 이걸 Play Console에 올리면 돼요.

### 업데이트할 때
지금 버전은 **1.0.3 (versionCode 4)**이에요. 다음에 또 올릴 때는 `app/build.gradle`에서 `versionCode`를 1 올리고(4 → 5), `versionName`도 바꿔요(1.0.3 → 1.0.4). 그다음 위의 서명 순서를 다시 하면 돼요.
- 업데이트는 **처음 만든 그 키 파일(.jks)**로 서명해야 해요. 새로 만들면 안 돼요.
- Play Console에서 "버전 코드가 이미 사용됨"이라고 나오면 `versionCode`를 하나 더 올리면 돼요.

## 4. Play Console 등록

1. https://play.google.com/console → 앱 만들기 → 이름 **골라츄**
2. **테스트 → 비공개 테스트**에 `.aab` 올리기
3. 개인 계정은 **테스터 12명이 14일 동안** 테스트해야 정식 출시를 신청할 수 있어요.
4. 스토어 등록 정보에 필요한 것
   - 앱 아이콘: `store/앱-아이콘-512.png`
   - 개인정보처리방침 주소: `https://niaxcroknit.github.io/golrachu/privacy.html` (GitHub Pages, 아래 README 참고)
   - 데이터 보안 설문: 광고 때문에 **기기 ID(광고 ID)** 등을 수집함으로 답해야 해요 (1.0.3부터)
   - 광고 포함: **예** (1.0.3부터)
   - 카테고리: **라이프스타일**

## 광고 (AdMob)

- 앱 ID `ca-app-pub-2486517051650098~9481436943` → `app/src/main/AndroidManifest.xml`
- 배너 광고 단위 `ca-app-pub-2486517051650098/2634927181` → `MainActivity.java` 맨 위 `AD_UNIT_REAL`
- **안드로이드 스튜디오에서 ▶로 실행하면 "Test Ad"라고 적힌 구글 테스트 광고**가 나와요. 내 광고를 내가 누르면 AdMob 계정이 정지될 수 있어서 일부러 그렇게 해 뒀어요.
- **서명해서 만든 .aab(스토어용)에서만 진짜 광고**가 나와요. 테스트할 때도 진짜 광고는 절대 누르지 마세요.
- AdMob에서 앱 상태가 "준비 중 / 검토 중"이면 진짜 광고가 안 나오거나 조금만 나와요. 앱이 플레이스토어에 **정식 공개**되고 AdMob 검토가 끝나야 제대로 나와요.

## 고치고 싶을 때

| 바꾸고 싶은 것 | 고칠 곳 |
|---|---|
| 화면 글자, 색, 싫어 누를 때 나오는 말 | `app/src/main/assets/index.html` |
| 앱 이름 | `app/src/main/res/values/strings.xml` |
| 진동 세기·종류 | `MainActivity.java` 맨 아래 `feedbackFor` |
| 광고 단위 ID | `MainActivity.java` 맨 위 `AD_UNIT_REAL` |
| 아이콘 | `app/src/main/res/mipmap-*` 폴더의 그림들 |

## 자주 막히는 곳

| 증상 | 해결 |
|---|---|
| Gradle sync가 실패해요 | 인터넷 연결 확인 후 위쪽 코끼리 모양 🐘 **Sync Project** 다시 누르기 |
| "SDK location not found" | **File → Project Structure → SDK Location**에서 Android SDK 경로 지정 (보통 자동으로 잡혀요) |
| 폰이 목록에 안 보여요 | USB 케이블을 데이터 전송용으로 바꾸거나, 폰 알림에서 USB 모드를 "파일 전송"으로 |
| 진동이 안 와요 | 폰 **설정 → 소리 및 진동 → 진동 세기 → 터치 피드백**이 꺼져 있지 않은지 확인 |
