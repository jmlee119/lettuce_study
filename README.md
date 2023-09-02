## Lettuce_study

---

> http://www.lettuces.co.kr/#firstPage
> 

당근마켓을 보고 영감을 받아 동네 사람들끼리 같이 공부를 하는 웹을 만들면 어떨까 싶어서 만들었습니다

### 사용한 언어

---

- JDK 17
- Spring Boot
- Java Script
- Html
- Thymeleaf
- MYSQL
- AWS

### 파일구조

`
lettuce_study
    ├── README.md
    ├── build.gradle
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── gradlew.bat
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── lettuce
        │   │       └── demo
        │   │           ├── Config
        │   │           │   └── SecurityConfig.java
        │   │           ├── Controller
        │   │           │   ├── AdminController.java
        │   │           │   ├── ApiController.java
        │   │           │   ├── CommentController.java
        │   │           │   ├── HomeController.java
        │   │           │   ├── LoginController.java
        │   │           │   ├── MailController.java
        │   │           │   ├── MemberController.java
        │   │           │   ├── MyPageController.java
        │   │           │   ├── PostController.java
        │   │           │   └── ReplyController.java
        │   │           ├── DemoApplication.java
        │   │           ├── Entity
        │   │           │   ├── Comment.java
        │   │           │   ├── Declaration.java
        │   │           │   ├── Mail.java
        │   │           │   ├── Member.java
        │   │           │   ├── Post.java
        │   │           │   └── Reply.java
        │   │           ├── Member
        │   │           │   ├── AuthForm.java
        │   │           │   ├── LoginForm.java
        │   │           │   └── MemberDetails.java
        │   │           ├── Repository
        │   │           │   ├── CommentRepository.java
        │   │           │   ├── DeclarationRepository.java
        │   │           │   ├── MailRepository.java
        │   │           │   ├── MemberRepository.java
        │   │           │   ├── PostRepository.java
        │   │           │   └── ReplyRepository.java
        │   │           └── Service
        │   │               ├── MailService.java
        │   │               └── MemberSecurityService.java
        │   └── resources
        │       ├── application.properties
        │       ├── static
        │       │   ├── css
        │       │   │   ├── fullpage.css
        │       │   │   ├── fullpage.js
        │       │   │   ├── javascript.fullPage.css
        │       │   │   ├── javascript.fullPage.js
        │       │   │   ├── jquery.fullPage.css
        │       │   │   ├── jquery.fullPage.js
        │       │   │   ├── mailcss.css
        │       │   │   └── menucss.css
        │       │   └── images
        │       │       ├── 1p.jpg
        │       │       ├── 2p.jpg
        │       │       ├── 3p.jpg
        │       │       ├── 4p.jpg
        │       │       ├── declaration.png
        │       │       ├── error-image.png
        │       │       ├── free-icon-edit-info-9424666.png
        │       │       ├── free-icon-github-sign-25657.png
        │       │       ├── free-icon-house-black-silhouette-without-door-20176.png
        │       │       ├── free-icon-instagram-174855.png
        │       │       ├── free-icon-sticky-note-7688466.png
        │       │       ├── lettuce.png
        │       │       ├── location.png
        │       │       └── noimage.webp
        │       └── templates
        │           ├── Admin
        │           │   ├── MainAdminPage.html
        │           │   ├── declaration.html
        │           │   └── detaildeclaration.html
        │           ├── Mail
        │           │   ├── detail.html
        │           │   ├── list.html
        │           │   └── send.html
        │           ├── Post
        │           │   ├── createPost.html
        │           │   ├── detail.html
        │           │   ├── extendslist.html
        │           │   ├── postList.html
        │           │   └── updatePost.html
        │           ├── errorPage.html
        │           ├── fragments
        │           │   ├── MailCom.html
        │           │   └── common.html
        │           ├── index.html
        │           ├── login
        │           │   └── login.html
        │           ├── mail.html
        │           ├── members
        │           │   ├── addMemberForm.html
        │           │   ├── authForm.html
        │           │   ├── findIdForm.html
        │           │   ├── findIdResult.html
        │           │   ├── findPassword.html
        │           │   └── findPasswordResult.html
        │           ├── myPage
        │           │   ├── edit.html
        │           │   ├── mylist.html
        │           │   └── mypage.html
        │           └── test.html
        └── test
            └── java
                └── lettuce
                    └── demo
                        └── DemoApplicationTests.java
`
