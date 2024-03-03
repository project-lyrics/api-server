# Project Lyrics - API Server
> 프로젝트 `Lyrics`의 API 서버 프로젝트입니다.

---

## Profile Setup
해당 프로젝트의 Profile은 아래 3개로 분류됩니다.

* `local`: 로컬 개발 환경을 위한 profile입니다.
* `stage`: `production` 레벨 배포 전 테스트를 위한 profile입니다.
* `production`: 실 제품에 사용되는 profile입니다.

---

## Requirements
이 문단에서는 프로젝트 실행을 위한 사전 준비에 대해 서술합니다.

### SDK
이 프로젝트는 JDK21을 사용하고 있습니다. JDK21이 설치되어 있는지 확인해주세요.

### Env variables
이 프로젝트는 AWSpring을 사용하고, Profile이 구분되어 있습니다. 따라서, 프로젝트 실행 전 아래 환경변수가 설정되어야 합니다.
```shell
export AWS_ACCESS_KEY_ID={AWS Access key id}
export AWS_SECRET_ACCESS_KEY={AWS Secret access key}
export AWS_REGION=ap-northeast-2
export SPRING_PROFILES_ACTIVE={profile}
```
환경 변수의 간편한 Setup을 위하여 [direnv](https://direnv.net/)의 사용을 권장합니다.

### AWS CLI
프로젝트 내의 일부 Shell Script 실행을 위해서는 AWS CLI가 설치되어 있어야 합니다.

---

## Setup Formatter

이 프로젝트는 formatter로 checkstyle을 사용하고, google style을 약간 수정하여 사용합니다.

커밋 전 반드시 `./gradlew checkstyleMain`을 실행해주세요.

아래 명령을 실행하면 깃 커밋 전 자동으로 checkstyle을 실행시킬 수 있습니다.
```shell
echo "./gradlew checkstyleMain; git update-index --again" > .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```
