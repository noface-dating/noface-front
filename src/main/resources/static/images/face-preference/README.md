## face-preference 이미지 넣는 위치 (male/female 폴더 방식)

현재 `questions.json` 및 퀴즈 화면(폴백 질문)은 아래 경로를 기준으로 이미지를 불러옵니다.

### 저장 폴더

```
src/main/resources/static/images/face-preference/
```

### 폴더/파일명 규칙

각 질문당 **좌/우 2장(총 20장/성별)**이 필요합니다.

```
female/c1-1.jpg
female/c1-2.jpg
female/c2-1.jpg
female/c2-2.jpg
...
female/c10-1.jpg
female/c10-2.jpg

male/c1-1.jpg
male/c1-2.jpg
male/c2-1.jpg
male/c2-2.jpg
...
male/c10-1.jpg
male/c10-2.jpg
```

### 이미지가 없으면?

이미지 로드에 실패하면 카드에 기본 아이콘(👤)이 표시됩니다.

